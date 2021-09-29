package edu.temple.convoy;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import edu.temple.convoy.databinding.ActivityLoginBinding;
import edu.temple.convoy.utils.Constants;
import edu.temple.convoy.utils.SharedPrefs;

public class LoginActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // First, check the shared preferences to see if a user is logged in...
        // If so, redirect to the map view.
        redirectIfLoggedIn();

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

    private void redirectIfLoggedIn() {
        SharedPrefs sp = new SharedPrefs(this);
        if (!sp.getLoggedInUser().equals(Constants.SHARED_PREFS_DEFAULT_STRING) &&
                !sp.getSessionKey().equals(Constants.SHARED_PREFS_DEFAULT_STRING)) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
    }

}