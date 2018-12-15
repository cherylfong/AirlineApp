package com.cherylfong.airlineapp;

import android.provider.BaseColumns;

public class SystemLogsContract {

    public static final class LogEntry implements BaseColumns {

        public static final String TABLE_NAME = "logs";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_DETAILS = "details";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
