package edu.temple.convoy.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import edu.temple.convoy.utils.Constants;
import edu.temple.convoy.utils.SharedPrefs;

public class FcmService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.i(Constants.LOG_TAG, "Received new FCM message: "
                + remoteMessage.getData().get("payload"));
        // TODO - parse out list of user locations
        // TODO - ignore my location, since I already know it
        // TODO - send out broadcast with filtered list of locations
    }

    @Override
    public void onNewToken(String token) {
        Log.i(Constants.LOG_TAG, "Received new FCM token: " + token);
        SharedPrefs.setFcmToken(this, token);
    }

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

}