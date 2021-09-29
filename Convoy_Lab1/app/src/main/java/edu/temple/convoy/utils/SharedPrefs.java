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

}