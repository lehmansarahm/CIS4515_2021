package edu.temple.convoy;

import androidx.fragment.app.FragmentActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.temple.convoy.api.AccountAPI;
import edu.temple.convoy.api.ConvoyAPI;
import edu.temple.convoy.databinding.ActivityMapsBinding;
import edu.temple.convoy.services.LocationService;
import edu.temple.convoy.utils.Constants;
import edu.temple.convoy.utils.SharedPrefs;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int DEFAULT_ZOOM = 15;
    private static final String CONVOY_PREFIX = "Current Convoy: ";

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private Intent locationServiceIntent;
    private BroadcastReceiver locationReceiver;

    private String username;
    private String sessionKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerLocationReceiver();

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPrefs sp = new SharedPrefs(MapsActivity.this);
        username = sp.getLoggedInUser();
        sessionKey = sp.getSessionKey();

        binding.buttonLogout.setOnClickListener(view -> logout(username, sessionKey));
        binding.buttonStart.setOnClickListener(view -> createNewConvoy(username, sessionKey));
        binding.buttonEnd.setOnClickListener(view -> {
            String convoyID = sp.getConvoyID();
            endActiveConvoy(username, sessionKey, convoyID);
        });

        binding.buttonJoin.setOnClickListener(view -> {
            // TODO - will be implemented later
        });

        binding.buttonLeave.setOnClickListener(view -> {
            // TODO - will be implemented later
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, DEFAULT_ZOOM));
    }

    private void registerLocationReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BROADCAST_LOCATION_UPDATE);
        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_LOCATION_UPDATE)) {
                    // parse out lat-lon and update the map
                    LatLng newPosition = new LatLng(intent.getDoubleExtra(Constants.BROADCAST_KEY_LAT, 0.0d),
                            intent.getDoubleExtra(Constants.BROADCAST_KEY_LON, 0.0d));
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(newPosition).title(username));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, DEFAULT_ZOOM));
                }
            }
        };
        registerReceiver(locationReceiver, filter);
    }

    private void createNewConvoy(String username, String sessionKey) {
        Context ctx = MapsActivity.this;
        ConvoyAPI.ResultListener listener = new ConvoyAPI.ResultListener() {
            @Override
            public void onSuccess(String convoyID) {
                // update the current convoy ID in shared prefs and start the location service
                Toast.makeText(ctx, "You have joined a convoy!", Toast.LENGTH_LONG).show();
                Log.d(Constants.LOG_TAG, "User has created convoy: " + convoyID
                        + ", starting location tracking.");
                (new SharedPrefs(ctx)).setConvoyID(convoyID);
                binding.currentConvoyID.setText(CONVOY_PREFIX + convoyID);
                locationServiceIntent = new Intent(ctx, LocationService.class);
                startService(locationServiceIntent);
            }

            @Override
            public void onFailure(String message) {
                // inform the user that the operation has failed
                Log.e(Constants.LOG_TAG, "Attempt to create a convoy has failed with message: " + message);
                Toast.makeText(ctx, "Attempt to create a convoy"
                        + " has failed.  Check LogCat for message.", Toast.LENGTH_LONG).show();
            }
        };

        // submit a "start convoy" API request
        ConvoyAPI convoyAPI = new ConvoyAPI(ctx);
        convoyAPI.create(username, sessionKey, listener);
    }

    private void endActiveConvoy(String username, String sessionKey, String convoyID) {
        Context ctx = MapsActivity.this;
        ConvoyAPI.ResultListener listener = new ConvoyAPI.ResultListener() {
            @Override
            public void onSuccess(String convoyID) {
                // clear the current convoy ID in shared prefs and stop the location service
                Toast.makeText(ctx, "You have left convoy: " + convoyID, Toast.LENGTH_LONG).show();
                Log.d(Constants.LOG_TAG, "Convoy: " + convoyID
                        + " has been destroyed.  Stopping location updates.");
                binding.currentConvoyID.setText(CONVOY_PREFIX);
                (new SharedPrefs(ctx)).clearConvoyID();
                stopService(locationServiceIntent);
            }

            @Override
            public void onFailure(String message) {
                // inform the user that the operation has failed
                Log.e(Constants.LOG_TAG, "Attempt to create a convoy has failed with message: " + message);
                Toast.makeText(ctx, "Attempt to create a convoy"
                        + " has failed.  Check LogCat for message.", Toast.LENGTH_LONG).show();
            }
        };

        // submit an "end convoy" API request
        ConvoyAPI convoyAPI = new ConvoyAPI(ctx);
        convoyAPI.end(username, sessionKey, convoyID, listener);
    }

    private void logout(String username, String sessionKey) {
        Context ctx = MapsActivity.this;
        AccountAPI.ResultListener listener = new AccountAPI.ResultListener() {
            @Override
            public void onSuccess(String sessionKey) {
                // wipe everything in shared prefs, and return to the login screen
                Log.i(Constants.LOG_TAG, "Logout attempt successful! Returning to login screen.");
                (new SharedPrefs(ctx)).clearAllUserSettings();
                finish();
            }

            @Override
            public void onFailure(String message) {
                // inform the user that the operation has failed
                Log.e(Constants.LOG_TAG, "Login attempt has failed with message: " + message);
                Toast.makeText(ctx, "Login attempt has failed.  Check LogCat for message.",
                        Toast.LENGTH_LONG).show();
            }
        };

        // Call the "Logout" API
        AccountAPI accountAPI = new AccountAPI(ctx);
        accountAPI.logout(username, sessionKey, listener);
    }

}