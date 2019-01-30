package com.kukitriplan.smartquizapp.skripsi.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kukitriplan.smartquizapp.skripsi.data.db.DatabaseContract.NotificationColumns;
import static com.kukitriplan.smartquizapp.skripsi.data.db.DatabaseContract.NotificationColumns.TABLE_NOTIFICATION;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "db_smartquizapp";

    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE_NOTIFICATION = String.format(
            "CREATE TABLE %s" +
                    " (%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            TABLE_NOTIFICATION,
            NotificationColumns._ID,
            NotificationColumns.TITLE,
            NotificationColumns.SUBTITLE,
            NotificationColumns.MESSAGE,
            NotificationColumns.DATE
    );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_NOTIFICATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        onCreate(db);
    }
}
