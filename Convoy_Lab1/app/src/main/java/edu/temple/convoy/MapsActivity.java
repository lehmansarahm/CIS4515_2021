package edu.temple.convoy;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.temple.convoy.api.AccountAPI;
import edu.temple.convoy.databinding.ActivityMapsBinding;
import edu.temple.convoy.fragments.LoginFragment;
import edu.temple.convoy.services.LocationService;
import edu.temple.convoy.utils.Constants;
import edu.temple.convoy.utils.SharedPrefs;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Intent locationServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonStart.setOnClickListener(view -> {
            // submit a "start convoy" API request

            // start the location service
            locationServiceIntent = new Intent(this, LocationService.class);
            startService(locationServiceIntent);
        });

        binding.buttonEnd.setOnClickListener(view -> {
            // submit an "end convoy" API request

            // stop the location service
            stopService(locationServiceIntent);
        });

        binding.buttonJoin.setOnClickListener(view -> {
            // TODO - will be implemented later
        });

        binding.buttonLeave.setOnClickListener(view -> {
            // TODO - will be implemented later
        });

        binding.buttonLogout.setOnClickListener(view -> {
            SharedPrefs sp = new SharedPrefs(MapsActivity.this);
            AccountAPI.ResultListener listener = new AccountAPI.ResultListener() {
                @Override
                public void onSuccess(String sessionKey) {
                    Log.i(Constants.LOG_TAG, "Logout attempt successful! Returning to login screen.");
                    sp.clear();     // wipe everything in shared prefs
                    finish();       // return to login screen
                }

                @Override
                public void onFailure(String message) {
                    Log.e(Constants.LOG_TAG, "Login attempt has failed with message: " + message);
                    Toast.makeText(MapsActivity.this,
                            "Login attempt has failed.  Check LogCat for message.",
                            Toast.LENGTH_LONG).show();
                }
            };

            // Call the "Logout" API
            AccountAPI accountAPI = new AccountAPI(MapsActivity.this);
            accountAPI.logout(sp.getLoggedInUser(), sp.getSessionKey(), listener);
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}