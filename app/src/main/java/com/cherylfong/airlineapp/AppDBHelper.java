package com.cherylfong.airlineapp;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cherylfong.airlineapp.AccountContract.*;

public class AppDBHelper extends SQLiteOpenHelper{

    // app's database name
    private static final String DATABASE_NAME = "flight-reservations-app.db";

    // If change database schema, must increment the database version
    private static final int DATABASE_VERSION = 2;

    // constructor
    public AppDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){

        Log.d("AppDBHelper", "onCreate()");

        String SQL_CREATE_ACCOUNTS_TABLE = "";

        try{

            // create table to hold user account data
            SQL_CREATE_ACCOUNTS_TABLE = "CREATE TABLE " + AccountEntry.TABLE_NAME + " ("
                    + AccountEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + AccountEntry.COLUMN_USERNAME + " TEXT NOT NULL, "
                    + AccountEntry.COLUMN_PASSWORD + " TEXT NOT NULL, "
                    + AccountEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + "); ";

            sqLiteDatabase.execSQL(SQL_CREATE_ACCOUNTS_TABLE);

        }catch (SQLException e){

            Log.d("AppDBHelper", "onCreate() ERROR: " + e.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){

        Log.d("AppDBHelper", "onUpgrade()");

        // simply drop the table and create a new one.
        // if DATABASE_VERSION changed then the tables will be dropped.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AccountEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}