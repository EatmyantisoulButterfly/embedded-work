package com.example.embedded.data;

import android.net.Uri;

public class ContentContract {
    public static final String CONTENT_AUTHORITY = "com.example.embedded";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String DB_NAME = "submitDB";
    public static final String TABLE_DATA = "data";
    public static class TableData{
        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_TEMPERATURE = "temperature";
        public static final String COLUMN_AROUND_INJECTION = "around_injection";
        public static final String COLUMN_TIME="time";
    }
    public static final Uri DATA_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(TABLE_DATA)
            .build();
    public static final String TABLE_USER = "user";
    public static class TableUser{
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_PASSWORD = "password";
    }
    public static final Uri USER_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(TABLE_USER)
            .build();


    public static final String COLUMN_LAST_CLOCK_IN_TIME="last_time";

}
