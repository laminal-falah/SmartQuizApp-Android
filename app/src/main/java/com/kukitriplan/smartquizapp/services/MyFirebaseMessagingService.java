package com.kukitriplan.smartquizapp.services;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kukitriplan.smartquizapp.data.shared.SharedPrefFirebase;
import com.kukitriplan.smartquizapp.ui.home.HomeActivity;
import com.kukitriplan.smartquizapp.ui.home.NotificationActivity;
import com.kukitriplan.smartquizapp.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(String s) {
        Log.d(TAG, "Refreshed token: " + s);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        storeToken(s);
    }

    private void storeToken(String token) {
        //we will save the token in sharedpreferences later
        SharedPrefFirebase.getInstance(getApplicationContext()).saveDeviceToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void sendPushNotification(@NonNull JSONObject json) {
        //optionally we can display the json into log
        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            //getting the json data
            JSONObject data = json.getJSONObject("data");

            //parsing json data
            String title = data.getString("title");
            String message = data.getString("message");
            //String imageUrl = data.getString("image");

            //creating MyNotificationManager object
            NotificationUtils mNotificationManager = new NotificationUtils(getApplicationContext());

            //creating an intent for the notification
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);

            //if there is no image
            //if(imageUrl.equals("null")){
                //displaying small notification
                mNotificationManager.showSmallNotification(title, message, intent);
            //}else{
                //if there is an image
                //displaying a big notification
                //mNotificationManager.showBigNotification(title, message, imageUrl, intent);
            //}
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}
