package com.kukitriplan.smartquizapp.skripsi.services;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kukitriplan.smartquizapp.skripsi.data.db.NotificationsHelper;
import com.kukitriplan.smartquizapp.skripsi.data.model.Notifications;
import com.kukitriplan.smartquizapp.skripsi.data.shared.SharedPrefFirebase;
import com.kukitriplan.smartquizapp.skripsi.ui.home.DetailKuisActivity;
import com.kukitriplan.smartquizapp.skripsi.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private Notifications notifications = new Notifications();
    private NotificationsHelper mHelper;

    @Override
    public void onNewToken(String s) {
        Log.d(TAG, "Refreshed token: " + s);
        storeToken(s);
    }

    private void storeToken(String token) {
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

    private void sendPushNotification(@NonNull JSONObject json) {
        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            NotificationUtils mNotificationManager = new NotificationUtils(getApplicationContext());
            mHelper = new NotificationsHelper(getApplicationContext());
            mHelper.open();

            JSONObject data = json.getJSONObject("data");

            String tipeNotif = data.getString("tipe");

            if (tipeNotif.equals("aktivasiKuisAuthor")) {
                String title = data.getString("title");
                String subtitle = data.getString("subtitle");
                String message = data.getString("message");

                notifications.setTitle(title);
                notifications.setSubtitle(subtitle);
                notifications.setMessage(message);
                notifications.setDate(getCurrentDate());

                mHelper.insert(notifications);

                mNotificationManager.NotificationAktivasiKuisAuthor(title,subtitle,message);

                mHelper.close();
            } else if (tipeNotif.equals("kuisTerbaru")) {
                String namaKuis = data.getString("namaKuis");
                String slug = data.getString("slug");
                Intent intent = new Intent(getApplicationContext(), DetailKuisActivity.class)
                        .putExtra("nama", namaKuis)
                        .putExtra("slug", slug)
                        .putExtra("tipe", "notif");
                String title = data.getString("title");
                String subtitle = data.getString("subtitle");
                String message = data.getString("message");
                String url = data.getString("image");

                notifications.setTitle(title);
                notifications.setSubtitle(subtitle);
                notifications.setMessage(message);
                notifications.setDate(getCurrentDate());

                mHelper.insert(notifications);

                mNotificationManager.NotificationAktivasiKuis(title,subtitle,message,url,intent);

                mHelper.close();
            } else if (tipeNotif.equals("lengkapiProfile")) {
                String title = data.getString("title");
                String subtitle = data.getString("subtitle");
                String message = data.getString("message");

                notifications.setTitle(title);
                notifications.setSubtitle(subtitle);
                notifications.setMessage(message);
                notifications.setDate(getCurrentDate());

                mHelper.insert(notifications);

                mNotificationManager.NotificationProfile(title,subtitle,message);

                mHelper.close();
            } else if (tipeNotif.equals("topUpSaldo")) {
                String title = data.getString("title");
                String subtitle = data.getString("subtitle");
                String message = data.getString("message");

                notifications.setTitle(title);
                notifications.setSubtitle(subtitle);
                notifications.setMessage(message);
                notifications.setDate(getCurrentDate());

                mHelper.insert(notifications);

                mNotificationManager.NotificationTopUp(title,subtitle,message);

                mHelper.close();
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
