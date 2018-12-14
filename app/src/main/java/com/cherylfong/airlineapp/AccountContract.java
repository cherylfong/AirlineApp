package com.cherylfong.airlineapp;

import android.provider.BaseColumns;

public class AccountContract {

    public static final class AccountEntry implements BaseColumns {

        public static final String TABLE_NAME = "accounts";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}