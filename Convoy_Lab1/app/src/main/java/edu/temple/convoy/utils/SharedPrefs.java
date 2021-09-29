package edu.temple.convoy.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    private Context context;
    private SharedPreferences sharedPrefs;

    public SharedPrefs(Context initialContext) {
        context = initialContext;
        sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFS_NAME,
                Context.MODE_PRIVATE);
    }

    public void clearAllUserSettings() {
        clearLoggedInUser();
        clearSessionKey();
        clearConvoyID();
    }

    // ================================================================================
    //      USERNAME FOR CURRENTLY LOGGED IN USER
    // ================================================================================

    public void setLoggedInUser(String username) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constants.API_KEY_USERNAME, username);
        editor.apply();
    }

    public String getLoggedInUser() {
        return sharedPrefs.getString(Constants.API_KEY_USERNAME,
                Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    protected void clearLoggedInUser() {
        setLoggedInUser(Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    // ================================================================================
    //      SESSION KEY FOR CURRENTLY LOGGED IN USER
    // ================================================================================

    public void setSessionKey(String sessionKey) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constants.API_KEY_SESSION_KEY, sessionKey);
        editor.apply();
    }

    public String getSessionKey() {
        return sharedPrefs.getString(Constants.API_KEY_SESSION_KEY,
                Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    protected void clearSessionKey() {
        setSessionKey(Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    // ================================================================================
    //      CONVOY ID FOR CURRENTLY LOGGED IN USER
    // ================================================================================

    public void setConvoyID(String convoyID) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constants.API_KEY_CONVOY_ID, convoyID);
        editor.apply();
    }

    public String getConvoyID() {
        return sharedPrefs.getString(Constants.API_KEY_CONVOY_ID,
                Constants.SHARED_PREFS_DEFAULT_STRING);
    }

    public void clearConvoyID() {
        setConvoyID(Constants.SHARED_PREFS_DEFAULT_STRING);
    }

}