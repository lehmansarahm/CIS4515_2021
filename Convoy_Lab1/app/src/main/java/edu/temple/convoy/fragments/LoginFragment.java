package edu.temple.convoy.fragments;

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

import edu.temple.convoy.R;
import edu.temple.convoy.api.AccountAPI;
import edu.temple.convoy.databinding.FragmentLoginBinding;

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
            Log.d("LoginFragment", "Login button has been clicked.");

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
                    Log.i("LoginFragment", "Login attempt successful! Load the map view.");
                }

                @Override
                public void onFailure(String message) {
                    Log.e("LoginFragment", "Login attempt has failed with message: " + message);
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
            Log.d("LoginFragment", "Register button has been clicked.  Navigating to Registration fragment.");
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