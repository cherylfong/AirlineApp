package com.cherylfong.airlineapp;

import android.provider.BaseColumns;

public class FlightContract {

    public static final class FlightEntry implements BaseColumns {

        public static final String TABLE_NAME = "flights";
        public static final String COLUMN_DESIGNATOR = "designation";
        public static final String COLUMN_DEPART = "depart";
        public static final String COLUMN_ARRIVE = "arrival";
        public static final String COLUMN_TAKEOFF_TIME = "takeoff";
        public static final String COLUMN_CAPACITY = "capacity";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
