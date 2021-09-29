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
import edu.temple.convoy.databinding.FragmentRegisterBinding;
import edu.temple.convoy.utils.Constants;
import edu.temple.convoy.utils.SharedPrefs;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText firstNameText = view.findViewById(R.id.register_firstName_input);
        EditText lastNameText = view.findViewById(R.id.register_lastName_input);
        EditText usernameText = view.findViewById(R.id.register_username_input);
        EditText passwordText = view.findViewById(R.id.register_password_input);

        /*
            Lambda notation ... takes the place of:
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
         */
        binding.buttonSubmit.setOnClickListener(view1 -> {
            Log.d(Constants.LOG_TAG, "Register button has been clicked.");

            // Retrieve the information from the registration form
            String firstName = String.valueOf(firstNameText.getText());
            String lastName = String.valueOf(lastNameText.getText());
            String username = String.valueOf(usernameText.getText());
            String password = String.valueOf(passwordText.getText());

            // Make sure all fields are populated before we continue ...
            if (!isInputFormValid(firstName, lastName, username, password)) {
                return;
            }

            // Tell the API class what we want to do when the API call is finished
            AccountAPI.ResultListener listener = new AccountAPI.ResultListener() {
                @Override
                public void onSuccess(String sessionKey) {
                    SharedPrefs sp = new SharedPrefs(RegisterFragment.this.getContext());
                    sp.setLoggedInUser(username);
                    sp.setSessionKey(sessionKey);

                    Log.i(Constants.LOG_TAG, "Registration attempt successful! Load the map view.");
                    Intent intent = new Intent(RegisterFragment.this.getContext(), MapsActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(String message) {
                    Log.e(Constants.LOG_TAG, "Registration attempt has failed "
                            + "with message: " + message);
                    Toast.makeText(RegisterFragment.this.getContext(),
                            "Registration attempt has failed.  Check LogCat for message.",
                            Toast.LENGTH_LONG).show();
                }
            };

            // Call the API
            AccountAPI accountAPI = new AccountAPI(RegisterFragment.this.getContext());
            accountAPI.register(firstName, lastName, username, password, listener);
        });

        binding.buttonCancel.setOnClickListener(view1 -> {
            // return the user to the login fragment
            NavHostFragment.findNavController(RegisterFragment.this)
                    .navigate(R.id.action_goto_login);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public boolean isInputFormValid(String firstName, String lastName, String username,
                                    String password) {
        // form is invalid if first name is empty or null
        if (firstName == null || firstName.equals(""))
            return false;

        // form is invalid if last name is empty or null
        if (lastName == null || lastName.equals(""))
            return false;

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