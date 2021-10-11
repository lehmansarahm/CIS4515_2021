package edu.temple.convoy.utils;

public class Constants {

    public static final String LOG_TAG = "ConvoyV3";

    public static final String BASE_API_URL = "https://kamorris.com/lab/convoy/";

    public static final String API_KEY_ACTION = "action";
    public static final String API_KEY_FIRSTNAME = "firstname";
    public static final String API_KEY_LASTNAME = "lastname";
    public static final String API_KEY_USERNAME = "username";
    public static final String API_KEY_PASSWORD = "password";
    public static final String API_KEY_FCM_TOKEN = "fcm_token";

    public static final String API_KEY_STATUS = "status";
    public static final String API_KEY_SESSION_KEY = "session_key";
    public static final String API_KEY_CONVOY_ID = "convoy_id";
    public static final String API_KEY_LATITUDE = "latitude";
    public static final String API_KEY_LONGITUDE = "longitude";
    public static final String API_KEY_MESSAGE = "message";

    public static final String API_ACTION_REGISTER = "REGISTER";
    public static final String API_ACTION_LOGIN = "LOGIN";
    public static final String API_ACTION_LOGOUT = "LOGOUT";
    public static final String API_ACTION_CREATE = "CREATE";
    public static final String API_ACTION_END = "END";
    public static final String API_ACTION_JOIN = "JOIN";
    public static final String API_ACTION_LEAVE = "LEAVE";
    public static final String API_ACTION_UPDATE = "UPDATE";

    public static final String API_STATUS_SUCCESS = "SUCCESS";
    public static final String API_STATUS_ERROR = "ERROR";

    public static final String SHARED_PREFS_NAME = "CIS4515_ConvoyApp";
    public static final String SHARED_PREFS_DEFAULT_STRING = "None";
    public static final boolean SHARED_PREFS_DEFAULT_BOOL = false;

    public static final String SHARED_PREFS_USERNAME = "username";
    public static final String SHARED_PREFS_SESSION_KEY = "session_key";
    public static final String SHARED_PREFS_ACTIVE_CONVOY_ID = "active_convoy_id";
    public static final String SHARED_PREFS_DID_START_ACTIVE_CONVOY = "did_start_active_convoy";
    public static final String SHARED_PREFS_FCM_TOKEN = "fcm_token";

    public static final String BROADCAST_LOCATION_UPDATE = "edu.temple.convoy.broadcast.location_update";
    public static final String BROADCAST_KEY_CURR_USER_LOC = "current_user_loc";
    public static final String BROADCAST_KEY_CONVOY_LOCS = "convoy_locations";

}