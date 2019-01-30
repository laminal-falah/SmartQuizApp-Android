package com.kukitriplan.smartquizapp.skripsi.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.data.db.NotificationsHelper;
import com.kukitriplan.smartquizapp.skripsi.data.model.Notifications;
import com.kukitriplan.smartquizapp.skripsi.data.shared.SharedLoginManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtils {

    private static final int ID_BIG_NOTIFICATION = 234;
    private static final int ID_SMALL_NOTIFICATION = 235;
    private static String CHANNEL_ID = "CHANNEL_ID";
    private static CharSequence CHANNEL_NAME = "SMARTQUIZAPP CHANNEL";
    private final static String GROUP_KEY_KUIS = "GROUP_BY_KUIS";
    private final static int NOTIF_REQUEST_CODE = 200;
    private int idNotif = 0;
    private int maxNotif = 1;

    private Context mContext;
    private Uri uri;

    private SharedLoginManager prefManager;

    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private NotificationChannel mNotificationChannel;
    private NotificationCompat.Builder mBuilder;
    private NotificationCompat.InboxStyle mInboxStyle;
    private NotificationCompat.BigPictureStyle mBigPictureStyle;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
        prefManager = new SharedLoginManager(mContext);
    }

    public void NotificationAktivasiKuis(String title, String subtitle, String message, String url, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, NOTIF_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBigPictureStyle = new NotificationCompat.BigPictureStyle();
        mBigPictureStyle.setBigContentTitle(title);
        mBigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        mBigPictureStyle.bigPicture(getBitmapFromURL(url));
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources() ,R.drawable.ic_notifications_white_24dp))
                .setContentTitle(title)
                .setSubText(subtitle)
                .setContentText(message)
                .setSound(uri)
                .setStyle(mBigPictureStyle)
                .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            mBuilder.setChannelId(CHANNEL_ID);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mNotificationChannel);
            }
        }

        mNotification = mBuilder.build();
        if (mNotificationManager != null) {
            mNotificationManager.notify(ID_BIG_NOTIFICATION, mNotification);
        }
    }

    public void NotificationAktivasiKuisAuthor(String title, String subtitle, String message, Intent intent) {

    }

    public void NotificationProfile(String title, String subtitle, String message, Intent intent) {

    }
    /*
    public void showBigNotification(String title, String message, String url, Intent intent) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        ID_BIG_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(getBitmapFromURL(url));
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setStyle(bigPictureStyle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setContentText(message)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_BIG_NOTIFICATION, notification);
    }
    */
    //the method will show a small notification
    //parameters are title for message title, message for message text and an intent that will open
    //when you will tap on the notification
    public void showSmallNotification(String title, String message, Intent intent) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        ID_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext,"id")
                .setSmallIcon(R.drawable.ic_notifications_white_24dp) //your app icon
                .setBadgeIconType(R.drawable.ic_notifications_white_24dp) //your app icon
                .setChannelId("id")
                .setContentTitle(title)
                .setAutoCancel(true).setContentIntent(resultPendingIntent)
                .setNumber(1)
                .setColor(255)
                .setContentText(message)
                .setWhen(System.currentTimeMillis());
        notificationManager.notify(1, notificationBuilder.build());
        /*NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.drawable.ic_notifications_white_24dp).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                //.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_notifications_white_24dp))
                .setContentText(message)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);*/
        /*NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, "101")
                .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = title;
            String description = message;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("101", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }*/
    }

    //The method will return Bitmap from an image URL
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
