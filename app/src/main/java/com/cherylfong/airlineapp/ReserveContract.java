package com.cherylfong.airlineapp;

import android.provider.BaseColumns;

public class ReserveContract {

    public static final class ReserveEntry implements BaseColumns {

        public static final String TABLE_NAME = "reservations";
        public static final String COLUMN_BY_USER = "user";
        public static final String COLUMN_DESIGNATOR = "designation";
        public static final String COLUMN_DEPART = "depart";
        public static final String COLUMN_ARRIVE = "arrival";
        public static final String COLUMN_TAKEOFF_TIME = "takeoff";
        public static final String COLUMN_TICKETS = "tickets";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
