package edu.temple.convoy;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.temple.convoy.api.AccountAPI;
import edu.temple.convoy.api.ConvoyAPI;
import edu.temple.convoy.databinding.ActivityMapsBinding;
import edu.temple.convoy.fragments.JoinConvoyDialogFragment;
import edu.temple.convoy.services.FcmService;
import edu.temple.convoy.services.LocationService;
import edu.temple.convoy.utils.Constants;
import edu.temple.convoy.utils.SharedPrefs;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        JoinConvoyDialogFragment.JoinConvoyDialogListener {

    private static final int DEFAULT_ZOOM = 15;
    private static final String CONVOY_PREFIX = "Current Convoy: ";

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private Context ctx;
    private Intent locationServiceIntent;
    private BroadcastReceiver locationReceiver;

    private String username;
    private String sessionKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // setting a class-level property for the activity context for easier access
        // inside nested event handlers, etc.
        ctx = MapsActivity.this;

        // register our broadcast receiver for location updates
        registerLocationReceiver();

        // grab the important info from shared prefs
        username = SharedPrefs.getLoggedInUser(ctx);
        sessionKey = SharedPrefs.getSessionKey(ctx);

        // assign the button on-click listeners
        binding.buttonLogout.setOnClickListener(view -> logout());
        binding.buttonStart.setOnClickListener(view -> createNewConvoy());
        binding.buttonEnd.setOnClickListener(view -> endActiveConvoy());
        binding.buttonLeave.setOnClickListener(view -> leaveConvoy());

        // enable, disable the buttons as appropriate
        enableDisableButtons();

        // tell the "join convoy" button to show the appropriate dialog window
        binding.buttonJoin.setOnClickListener(view -> {
            DialogFragment dialog = new JoinConvoyDialogFragment();
            dialog.show(getSupportFragmentManager(), "JoinConvoyDialogFragment");
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // ================================================================================
    //      EVENT HANDLERS FOR GOOGLE MAP FRAGMENT
    // ================================================================================

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // get user's last location and plot it
        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng newPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(newPosition).title(username));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, DEFAULT_ZOOM));
                    }
                });
    }

    // ================================================================================
    //      EVENT HANDLERS FOR "JOIN DIALOG" RESPONSES
    // ================================================================================

    @Override
    public void onDialogPositiveClick(String newConvoyID) {
        // result listener to respond to operation success / failure
        ConvoyAPI.ResultListener listener = new ConvoyAPI.ResultListener() {
            @Override
            public void onSuccess(String convoyID) {
                Toast.makeText(ctx, "You have joined convoy: " + convoyID,
                        Toast.LENGTH_LONG).show();
                Log.d(Constants.LOG_TAG, "User has joined convoy: " + convoyID
                        + ", starting location tracking.");

                // update the current ConvoyID in shared prefs / enable, disable buttons
                SharedPrefs.setActiveConvoyID(ctx, newConvoyID);
                enableDisableButtons();

                // update the ConvoyID label in the maps view
                binding.currentConvoyID.setText(CONVOY_PREFIX + convoyID);

                // start the location update service
                locationServiceIntent = new Intent(ctx, LocationService.class);
                startService(locationServiceIntent);

                // subscribe to the convoy topic on FCM
                // FcmService.subscribeToTopic(ctx, convoyID);
            }

            @Override
            public void onFailure(String message) {
                // inform the user that the operation has failed
                Log.e(Constants.LOG_TAG, "Attempt to join a convoy has failed with message: " + message);
                Toast.makeText(ctx, "Attempt to join a convoy"
                        + " has failed.  Check LogCat for message.", Toast.LENGTH_LONG).show();
            }
        };

        // submit a "join convoy" API request
        ConvoyAPI convoyAPI = new ConvoyAPI(ctx);
        convoyAPI.join(username, sessionKey, newConvoyID, listener);
    }

    @Override
    public void onDialogNegativeClick() {
        Log.d(Constants.LOG_TAG, "User cancelled out of the 'Join Convoy' dialog.");
    }

    // ================================================================================
    //      HANDLING THE LOCATION UPDATE BROADCAST RECEIVER
    // ================================================================================

    /**
     * Register the location update broadcast receiver with the system
     */
    private void registerLocationReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BROADCAST_LOCATION_UPDATE);
        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_LOCATION_UPDATE)) {
                    // parse out lat-lon and update the map
                    double lat = intent.getDoubleExtra(Constants.BROADCAST_KEY_LAT, 0.0d);
                    double lon = intent.getDoubleExtra(Constants.BROADCAST_KEY_LON, 0.0d);

                    // update the map
                    mMap.clear();
                    LatLng newPosition = new LatLng(lat, lon);
                    mMap.addMarker(new MarkerOptions().position(newPosition).title(username));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, DEFAULT_ZOOM));

                    // send an "update location" API request to server
                    sendLocationUpdate(lat, lon);
                }
            }
        };
        registerReceiver(locationReceiver, filter);
    }

    /**
     * Event handler to respond to broadcast receiver by forwarding new user location
     * to the remote API
     *
     * @param lat
     * @param lon
     */
    private void sendLocationUpdate(double lat, double lon) {
        // check to make sure we have an assigned convoy before we continue with loc updates
        if (!SharedPrefs.isActiveConvoyIdSet(ctx)) {
            Log.e(Constants.LOG_TAG, "Can't send location updates without an associated convoy!");
            return;
        }

        // result listener to respond to operation success / failure
        ConvoyAPI.ResultListener listener = new ConvoyAPI.ResultListener() {
            @Override
            public void onSuccess(String convoyID) {
                // inform the user that the operation was successful
                Toast.makeText(ctx, "You have updated your location with convoy: "
                        + convoyID, Toast.LENGTH_LONG).show();
                Log.d(Constants.LOG_TAG, "You have updated your location with convoy: "
                        + convoyID);
            }

            @Override
            public void onFailure(String message) {
                // inform the user that the operation has failed
                Log.e(Constants.LOG_TAG, "Attempt to update location has failed with message: "
                        + message);
                Toast.makeText(ctx, "Attempt to update location as failed.  "
                        + "Check LogCat for message.", Toast.LENGTH_LONG).show();
            }
        };

        // submit an "update location" API request
        ConvoyAPI convoyAPI = new ConvoyAPI(ctx);
        convoyAPI.updateLocation(username, sessionKey, SharedPrefs.getActiveConvoyID(ctx),
                String.valueOf(lat), String.valueOf(lon), listener);
    }

    // ================================================================================
    //      BUTTON ON-CLICK EVENT HANDLERS
    // ================================================================================

    /**
     * OnClick event handler to log the current user out of the system
     */
    private void logout() {
        AccountAPI.ResultListener listener = new AccountAPI.ResultListener() {
            @Override
            public void onSuccess(String sessionKey) {
                // wipe everything in shared prefs, and return to the login screen
                Log.i(Constants.LOG_TAG, "Logout attempt successful! Returning to login screen.");
                SharedPrefs.clearAllUserSettings(ctx);
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

    /**
     * OnClick event handler to create a new convoy
     */
    private void createNewConvoy() {
        // result listener to respond to operation success / failure
        ConvoyAPI.ResultListener listener = new ConvoyAPI.ResultListener() {
            @Override
            public void onSuccess(String convoyID) {
                Toast.makeText(ctx, "You have joined a convoy!", Toast.LENGTH_LONG).show();
                Log.d(Constants.LOG_TAG, "User has created convoy: " + convoyID
                        + ", starting location tracking.");

                // update the current ConvoyID in shared prefs / enable, disable buttons
                SharedPrefs.setActiveConvoyID(ctx, convoyID);
                SharedPrefs.setDidStartActiveConvoy(ctx, true);
                enableDisableButtons();

                // update the ConvoyID label in the maps view
                binding.currentConvoyID.setText(CONVOY_PREFIX + convoyID);

                // start the location update service
                locationServiceIntent = new Intent(ctx, LocationService.class);
                startService(locationServiceIntent);

                // subscribe to the convoy topic on FCM
                // FcmService.subscribeToTopic(ctx, convoyID);
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

    /**
     * OnClick event handler to end an active convoy
     */
    private void endActiveConvoy() {
        // result listener to respond to operation success / failure
        ConvoyAPI.ResultListener listener = new ConvoyAPI.ResultListener() {
            @Override
            public void onSuccess(String convoyID) {
                Toast.makeText(ctx, "You have left convoy: " + convoyID, Toast.LENGTH_LONG).show();
                Log.d(Constants.LOG_TAG, "Convoy: " + convoyID
                        + " has been destroyed.  Stopping location updates.");

                // clear the ConvoyID from the persistent label in the maps view
                binding.currentConvoyID.setText(CONVOY_PREFIX);

                // clear the convoyID from shared prefs / enable, disable buttons
                SharedPrefs.clearActiveConvoyID(ctx);
                SharedPrefs.clearDidStartActiveConvoy(ctx);
                enableDisableButtons();

                // stop the location updates service
                stopService(locationServiceIntent);
            }

            @Override
            public void onFailure(String message) {
                // inform the user that the operation has failed
                Log.e(Constants.LOG_TAG, "Attempt to end a convoy has failed with message: " + message);
                Toast.makeText(ctx, "Attempt to end a convoy"
                        + " has failed.  Check LogCat for message.", Toast.LENGTH_LONG).show();
            }
        };

        // submit an "end convoy" API request
        ConvoyAPI convoyAPI = new ConvoyAPI(ctx);
        convoyAPI.end(username, sessionKey, SharedPrefs.getActiveConvoyID(ctx), listener);
    }

    /**
     * OnClick event handler to leave a previously joined convoy
     */
    private void leaveConvoy() {
        // result listener to respond to operation success / failure
        ConvoyAPI.ResultListener listener = new ConvoyAPI.ResultListener() {
            @Override
            public void onSuccess(String convoyID) {
                Toast.makeText(ctx, "You have left convoy: " + convoyID,
                        Toast.LENGTH_LONG).show();
                Log.d(Constants.LOG_TAG, "You have left convoy: " + convoyID
                        + ".  Stopping location updates.");

                // clear the ConvoyID from the persistent label in the maps view
                binding.currentConvoyID.setText(CONVOY_PREFIX);

                // clear the convoyID from shared prefs / enable, disable buttons
                SharedPrefs.clearActiveConvoyID(ctx);
                enableDisableButtons();

                // stop the location updates service
                if (locationServiceIntent != null) stopService(locationServiceIntent);
            }

            @Override
            public void onFailure(String message) {
                // inform the user that the operation has failed
                Log.e(Constants.LOG_TAG, "Attempt to leave the convoy has failed with message: " + message);
                Toast.makeText(ctx, "Attempt to leave the convoy"
                        + " has failed.  Check LogCat for message.", Toast.LENGTH_LONG).show();
            }
        };

        // submit a "leave convoy" API request
        ConvoyAPI convoyAPI = new ConvoyAPI(ctx);
        convoyAPI.leave(username, sessionKey, SharedPrefs.getActiveConvoyID(ctx), listener);
    }

    // ================================================================================
    //      UPDATE ENABLED STATE OF BUTTONS BASED ON CONVOY STATUS
    // ================================================================================

    private void enableDisableButtons() {
        boolean isActiveConvoySet = SharedPrefs.isActiveConvoyIdSet(ctx);
        boolean didStartActiveConvoy = SharedPrefs.getDidStartActiveConvoy(ctx);

        binding.buttonStart.setEnabled(!isActiveConvoySet);
        binding.buttonEnd.setEnabled(isActiveConvoySet && didStartActiveConvoy);
        binding.buttonJoin.setEnabled(!isActiveConvoySet);
        binding.buttonLeave.setEnabled(isActiveConvoySet && !didStartActiveConvoy);
    }
}