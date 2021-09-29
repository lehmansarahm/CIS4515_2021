package edu.temple.convoy.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.temple.convoy.utils.Constants;

public class AccountAPI extends BaseAPI {

    // ================================================================================
    //      API RESULT LISTENER
    // ================================================================================

    public interface ResultListener {
        void onSuccess(String sessionKey);
        void onFailure(String message);
    }

    // ================================================================================

    public AccountAPI(Context initialContext) {
        super(initialContext);
    }

    @Override
    protected String getApiSuffix() {
        return "account.php";
    }

    /**
     * Register a new user by calling the remote API
     *
     * @param firstName
     * @param lastName
     * @param username
     * @param password
     * @param resultListener
     */
    public void register(String firstName, String lastName, String username, String password,
                         ResultListener resultListener) {
        Map<String, String> params = new HashMap<>();
        params.put(Constants.API_KEY_ACTION, Constants.API_ACTION_REGISTER);
        params.put(Constants.API_KEY_USERNAME, username);
        params.put(Constants.API_KEY_FIRSTNAME, firstName);
        params.put(Constants.API_KEY_LASTNAME, lastName);
        params.put(Constants.API_KEY_PASSWORD, password);

        String apiName = "RegistrationAPI";
        post(params, getListenerForAPI(apiName, resultListener));
    }

    /**
     * Login a previously-registered user using the remote API
     *
     * @param username
     * @param password
     * @param resultListener
     */
    public void login(String username, String password, ResultListener resultListener) {
        Map<String, String> params = new HashMap<>();
        params.put(Constants.API_KEY_ACTION, Constants.API_ACTION_LOGIN);
        params.put(Constants.API_KEY_USERNAME, username);
        params.put(Constants.API_KEY_PASSWORD, password);

        String apiName = "LoginAPI";
        post(params, getListenerForAPI(apiName, resultListener));
    }

    /**
     * Log the current user out
     *
     * @param resultListener
     */
    public void logout(String username, String sessionKey, ResultListener resultListener) {
        Map<String, String> params = new HashMap<>();
        params.put(Constants.API_KEY_ACTION, Constants.API_ACTION_LOGOUT);
        params.put(Constants.API_KEY_USERNAME, username);
        params.put(Constants.API_KEY_SESSION_KEY, sessionKey);

        String apiName = "LogoutAPI";
        post(params, getListenerForAPI(apiName, resultListener));
    }

    /**
     * Generate a response listener for the provided API Name and results listener
     *
     * @param apiName
     * @param resultListener
     * @return
     */
    private Response.Listener<String> getListenerForAPI(String apiName,
                                                              ResultListener resultListener) {
        /*
            Lambda notation ... takes the place of:
                new Response.Listener<String>() {
                        @Override
                        public void onResponse(String origResponse) {
         */
        Response.Listener<String> listener = origResponse -> {
            try {
                Log.d(Constants.LOG_TAG, apiName + " - Received response: " + origResponse);
                JSONObject response = new JSONObject(origResponse);
                String status = response.getString(Constants.API_KEY_STATUS);

                if (status.equals(Constants.API_STATUS_SUCCESS)) {
                    resultListener.onSuccess(response.getString(Constants.API_KEY_SESSION_KEY));
                } else if (status.equals(Constants.API_STATUS_ERROR)) {
                    resultListener.onFailure(response.getString(Constants.API_KEY_MESSAGE));
                } else {
                    Log.e(Constants.LOG_TAG, apiName + " - Unrecognized result status: " + status);
                }
            } catch (JSONException e) {
                Log.e(Constants.LOG_TAG, apiName + " - Unable to parse results from JSON Object");
                e.printStackTrace();
            }
        };
        return listener;
    }

}