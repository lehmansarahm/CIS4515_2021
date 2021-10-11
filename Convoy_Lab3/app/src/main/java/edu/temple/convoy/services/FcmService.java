package edu.temple.convoy.services;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.temple.convoy.LoginActivity;
import edu.temple.convoy.Traveler;
import edu.temple.convoy.api.AccountAPI;
import edu.temple.convoy.api.BaseAPI;
import edu.temple.convoy.utils.Constants;
import edu.temple.convoy.utils.SharedPrefs;

public class FcmService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String rawPayload = remoteMessage.getData().get("payload");
        if (rawPayload == null || rawPayload.equals("")) {
            Log.e(Constants.LOG_TAG, "Received new FCM message but payload was empty.");
            return;
        }

        try {
            Log.i(Constants.LOG_TAG, "Received new FCM message with payload: " + rawPayload);
            JSONObject parsedPayload = new JSONObject(rawPayload);
            JSONArray dataArray = parsedPayload.getJSONArray("data");

            // data = JSON array with format:
            // {"username":"sarah5",
            //      "firstname":"Sarah",
            //      "lastname":"Lehman",
            //      "latitude":"40.036",
            //      "longitude":"-75.2203"}

            // parse out list of locations for fellow convoy travelers
            List<Traveler> travelers = new ArrayList<>();
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObject = dataArray.getJSONObject(i);
                if (!dataObject.getString(Constants.API_KEY_USERNAME)
                        .equals(SharedPrefs.getLoggedInUser(this))) {
                    // only forward locations for those travelers that AREN'T me
                    Traveler traveler = new Traveler(
                            dataObject.getString(Constants.API_KEY_USERNAME),
                            dataObject.getString(Constants.API_KEY_FIRSTNAME),
                            dataObject.getString(Constants.API_KEY_LASTNAME),
                            dataObject.getDouble(Constants.API_KEY_LATITUDE),
                            dataObject.getDouble(Constants.API_KEY_LONGITUDE)
                    );

                    Log.i(Constants.LOG_TAG, "Tracking traveler: " + traveler);
                    travelers.add(traveler);
                }
            }

            Intent intent = new Intent();
            intent.setAction(Constants.BROADCAST_LOCATION_UPDATE);
            intent.putExtra(Constants.BROADCAST_KEY_CONVOY_LOCS, (Serializable) travelers);

            Log.i(Constants.LOG_TAG, "Sending convoy broadcast for " + travelers.size()
                    + " fellow travelers");
            sendBroadcast(intent);
        } catch (JSONException e) {
            Log.e(Constants.LOG_TAG, "Something went wrong while parsing the JSON payload: "
                    + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *
     * @param token
     */
    @Override
    public void onNewToken(String token) {
        Log.i(Constants.LOG_TAG, "New FCM token received: " + token + "\n\t Storing in SharedPrefs");
        SharedPrefs.setFcmToken(this, token);
    }

    /*
    public static void subscribeToTopic(Context context, String topicLabel) {
        Log.d(Constants.LOG_TAG, "Attempting to subscribe to topic: " + topicLabel);
        FirebaseMessaging.getInstance().subscribeToTopic(topicLabel)
                .addOnCompleteListener(task -> {
                    String message = task.isSuccessful()
                            ? "Successfully subscribed to FCM topic for convoy: "
                            : "Failed to subscribe to FCM topic for convoy: ";
                    message += topicLabel;
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    Log.d(Constants.LOG_TAG, message);
                });
    }

    public static void unsubscribeFromTopic(Context context, String topicLabel) {
        Log.d(Constants.LOG_TAG, "Attempting to subscribe to topic: " + topicLabel);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topicLabel)
                .addOnCompleteListener(task -> {
                    String message = task.isSuccessful()
                            ? "Successfully UN-subscribed from FCM topic for convoy: "
                            : "Failed to unsubscribe from FCM topic for convoy: ";
                    message += topicLabel;
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    Log.d(Constants.LOG_TAG, message);
                });
    }
    */

}