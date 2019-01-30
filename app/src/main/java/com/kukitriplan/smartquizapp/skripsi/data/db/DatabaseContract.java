package com.kukitriplan.smartquizapp.skripsi.data.db;

import android.provider.BaseColumns;

public class DatabaseContract {

    public static final class NotificationColumns implements BaseColumns {
        public static String TABLE_NOTIFICATION = "notifications";
        static String TITLE = "TITLE";
        static String SUBTITLE = "SUBTITLE";
        static String MESSAGE = "MESSAGE";
        static String DATE = "DATE";
    }
}
