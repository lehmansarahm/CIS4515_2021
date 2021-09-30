package edu.temple.convoy.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.temple.convoy.utils.Constants;

public class ConvoyAPI extends BaseAPI {

    public ConvoyAPI(Context initialContext) {
        super(initialContext);
    }

    @Override
    protected String getApiSuffix() {
        return "convoy.php";
    }

    /**
     * Create a new convoy using the remote API
     *
     * @param listener
     */
    public void create(String username, String sessionKey, ResultListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put(Constants.API_KEY_ACTION, Constants.API_ACTION_CREATE);
        params.put(Constants.API_KEY_USERNAME, username);
        params.put(Constants.API_KEY_SESSION_KEY, sessionKey);

        String apiName = "CreateConvoyAPI";
        post(params, getListenerForAPI(apiName, listener));
    }

    /**
     * End an existing convoy using the remote API
     * 
     * @param listener
     */
    public void end(String username, String sessionKey, String convoyID, ResultListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put(Constants.API_KEY_ACTION, Constants.API_ACTION_END);
        params.put(Constants.API_KEY_USERNAME, username);
        params.put(Constants.API_KEY_SESSION_KEY, sessionKey);
        params.put(Constants.API_KEY_CONVOY_ID, convoyID);

        String apiName = "EndConvoyAPI";
        post(params, getListenerForAPI(apiName, listener));
    }

    /**
     * Submits a request for the indicated user to join the indicated convoy
     *
     * @param username
     * @param sessionKey
     * @param convoyID
     * @param listener
     */
    public void join(String username, String sessionKey, String convoyID, ResultListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put(Constants.API_KEY_ACTION, Constants.API_ACTION_JOIN);
        params.put(Constants.API_KEY_USERNAME, username);
        params.put(Constants.API_KEY_SESSION_KEY, sessionKey);
        params.put(Constants.API_KEY_CONVOY_ID, convoyID);

        String apiName = "JoinConvoyAPI";
        post(params, getListenerForAPI(apiName, listener));
    }

    /**
     * Submits a request for the indicated user to leave the indicated convoy
     *
     * @param username
     * @param sessionKey
     * @param convoyID
     * @param listener
     */
    public void leave(String username, String sessionKey, String convoyID, ResultListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put(Constants.API_KEY_ACTION, Constants.API_ACTION_LEAVE);
        params.put(Constants.API_KEY_USERNAME, username);
        params.put(Constants.API_KEY_SESSION_KEY, sessionKey);
        params.put(Constants.API_KEY_CONVOY_ID, convoyID);

        String apiName = "LeaveConvoyAPI";
        post(params, getListenerForAPI(apiName, listener));
    }

    /**
     * Updates the current user's location with the backend server using the associated API
     *
     * @param username
     * @param sessionKey
     * @param convoyID
     * @param latitude
     * @param longitude
     * @param listener
     */
    public void updateLocation(String username, String sessionKey, String convoyID,
                               String latitude, String longitude, ResultListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put(Constants.API_KEY_ACTION, Constants.API_ACTION_UPDATE);
        params.put(Constants.API_KEY_USERNAME, username);
        params.put(Constants.API_KEY_SESSION_KEY, sessionKey);
        params.put(Constants.API_KEY_CONVOY_ID, convoyID);
        params.put(Constants.API_KEY_LATITUDE, latitude);
        params.put(Constants.API_KEY_LONGITUDE, longitude);

        String apiName = "UpdateLocationConvoyAPI";
        post(params, getListenerForAPI(apiName, listener));
    }

    /**
     * Generate a response listener for the provided API Name and results listener
     *
     * @param apiName
     * @param resultListener
     * @return
     */
    protected Response.Listener<String> getListenerForAPI(String apiName,
                                                          AccountAPI.ResultListener resultListener) {
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
                    resultListener.onSuccess(response.getString(Constants.API_KEY_CONVOY_ID));
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