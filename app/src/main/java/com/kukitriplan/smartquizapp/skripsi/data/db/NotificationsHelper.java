package com.kukitriplan.smartquizapp.skripsi.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kukitriplan.smartquizapp.skripsi.data.model.Notifications;

import java.sql.SQLException;
import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.kukitriplan.smartquizapp.skripsi.data.db.DatabaseContract.NotificationColumns.DATE;
import static com.kukitriplan.smartquizapp.skripsi.data.db.DatabaseContract.NotificationColumns.MESSAGE;
import static com.kukitriplan.smartquizapp.skripsi.data.db.DatabaseContract.NotificationColumns.SUBTITLE;
import static com.kukitriplan.smartquizapp.skripsi.data.db.DatabaseContract.NotificationColumns.TABLE_NOTIFICATION;
import static com.kukitriplan.smartquizapp.skripsi.data.db.DatabaseContract.NotificationColumns.TITLE;
import static com.kukitriplan.smartquizapp.skripsi.data.db.DatabaseHelper.DATABASE_NAME;

public class NotificationsHelper {
    private static String DATABASE_TABLE = TABLE_NOTIFICATION;
    private static String DATABASE = DATABASE_NAME;
    private Context mContext;
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    public NotificationsHelper(Context mContext) {
        this.mContext = mContext;
    }

    public NotificationsHelper open() throws SQLException {
        mDatabaseHelper = new DatabaseHelper(mContext);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDatabaseHelper.close();
    }

    public ArrayList<Notifications> query() {
        ArrayList<Notifications> notificationsArrayList = new ArrayList<>();
        Cursor cursor = mSqLiteDatabase.query(DATABASE_TABLE, null, null, null, null, null, _ID + " DESC", null);
        cursor.moveToFirst();
        Notifications notifications;
        if (cursor.getCount() > 0) {
            do {
                notifications = new Notifications();
                notifications.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                notifications.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                notifications.setSubtitle(cursor.getString(cursor.getColumnIndexOrThrow(SUBTITLE)));
                notifications.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE)));
                notifications.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DATE)));
                notificationsArrayList.add(notifications);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return notificationsArrayList;
    }

    public long insert(Notifications notifications) {
        ContentValues values = new ContentValues();
        values.put(TITLE, notifications.getTitle());
        values.put(SUBTITLE, notifications.getSubtitle());
        values.put(MESSAGE, notifications.getMessage());
        values.put(DATE, notifications.getDate());
        return mSqLiteDatabase.insert(DATABASE_TABLE, null, values);
    }

    public int delete(int id) {
        return mSqLiteDatabase.delete(TABLE_NOTIFICATION, _ID + " = '" + id + "'", null);
    }

    public static void drop(Context mContext) {
        mContext.deleteDatabase(DATABASE_NAME);
    }
}
