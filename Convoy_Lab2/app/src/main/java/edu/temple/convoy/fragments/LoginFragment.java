package edu.temple.convoy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import edu.temple.convoy.MapsActivity;
import edu.temple.convoy.R;
import edu.temple.convoy.api.AccountAPI;
import edu.temple.convoy.databinding.FragmentLoginBinding;
import edu.temple.convoy.utils.Constants;
import edu.temple.convoy.utils.SharedPrefs;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText usernameText = view.findViewById(R.id.login_username_input);
        EditText passwordText = view.findViewById(R.id.login_password_input);

        /*
            Lambda notation ... takes the place of:
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
         */
        binding.loginButton.setOnClickListener(view1 -> {
            Log.d(Constants.LOG_TAG, "Login button has been clicked.");

            // Retrieve the username and password from the login form
            String username = String.valueOf(usernameText.getText());
            String password = String.valueOf(passwordText.getText());

            // Make sure all fields are populated before we continue ...
            if (!isInputFormValid(username, password)) {
                return;
            }

            // Tell the API class what we want to do when the API call is finished
            AccountAPI.ResultListener listener = new AccountAPI.ResultListener() {
                @Override
                public void onSuccess(String sessionKey) {
                    SharedPrefs sp = new SharedPrefs(LoginFragment.this.getContext());
                    sp.setLoggedInUser(username);
                    sp.setSessionKey(sessionKey);

                    Log.i(Constants.LOG_TAG, "Login attempt successful! Load the map view.");
                    Intent intent = new Intent(LoginFragment.this.getContext(), MapsActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(String message) {
                    Log.e(Constants.LOG_TAG, "Login attempt has failed with message: " + message);
                    Toast.makeText(LoginFragment.this.getContext(),
                            "Login attempt has failed.  Check LogCat for message.",
                            Toast.LENGTH_LONG).show();
                }
            };

            // Call the API
            AccountAPI accountAPI = new AccountAPI(LoginFragment.this.getContext());
            accountAPI.login(username, password, listener);
        });

        binding.registerButton.setOnClickListener(intervalView -> {
            Log.d(Constants.LOG_TAG, "Register button has been clicked.  Navigating to Registration fragment.");
            NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_goto_register);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public boolean isInputFormValid(String username, String password) {
        // form is invalid if username is empty or null
        if (username == null || username.equals(""))
            return false;

        // form is invalid if password is empty or null
        if (password == null || password.equals(""))
            return false;

        // if we've gotten this far, it means the form is valid
        return true;
    }

}