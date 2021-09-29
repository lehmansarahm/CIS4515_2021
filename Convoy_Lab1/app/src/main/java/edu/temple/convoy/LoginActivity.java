package edu.temple.convoy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import edu.temple.convoy.databinding.ActivityLoginBinding;
import edu.temple.convoy.services.LocationService;
import edu.temple.convoy.utils.Constants;
import edu.temple.convoy.utils.SharedPrefs;

public class LoginActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 999;
    private static final String[] REQUESTED_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE
    };

    private AppBarConfiguration appBarConfiguration;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // First, verify that we have the necessary permissions
        if (allPermissionsGranted(REQUESTED_PERMISSIONS)) {
            // Next, check the shared preferences to see if a user is logged in...
            // If so, redirect to the map view.
            redirectIfLoggedIn();
        } else {
            // If permissions are not granted, explicitly request them before continuing
            ActivityCompat.requestPermissions(LoginActivity.this,
                    REQUESTED_PERMISSIONS, PERMISSION_REQUEST_CODE);
        }


        // Inflate the default LoginActivity layout ... populate the toolbar
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        /*
            Use a navigation controller to handle the transitions between fragments.
            This logic is provided by adding an activity using the "Basic" template
         */
        NavController navController = Navigation.findNavController(this, R.id.frag_login_register_nav);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        /*
            This function was also provided automatically by the "Basic" activity template.
            No need to change this.
         */
        NavController navController = Navigation.findNavController(this, R.id.frag_login_register_nav);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                redirectIfLoggedIn();
            }
        }
    }

    private boolean allPermissionsGranted(String[] permissions) {
        boolean permissionsGranted = true;
        for (String permission : permissions) {
            permissionsGranted &=
                    (ContextCompat.checkSelfPermission(LoginActivity.this, permission)
                            == PackageManager.PERMISSION_GRANTED);
        }
        return permissionsGranted;
    }

    private void redirectIfLoggedIn() {
        SharedPrefs sp = new SharedPrefs(this);
        if (!sp.getLoggedInUser().equals(Constants.SHARED_PREFS_DEFAULT_STRING) &&
                !sp.getSessionKey().equals(Constants.SHARED_PREFS_DEFAULT_STRING)) {
            // start the location service
            Intent locationIntent = new Intent(this, LocationService.class);
            startService(locationIntent);

            // redirect to the maps view
            Intent mapIntent = new Intent(this, MapsActivity.class);
            startActivity(mapIntent);
        }
    }

}