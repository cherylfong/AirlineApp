package com.cherylfong.airlineapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

    private static final String ACCOUNTS_TABLE = "CREATE TABLE " + AccountEntry.TABLE_NAME + " ("
            + AccountEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + AccountEntry.COLUMN_USERNAME + " TEXT NOT NULL, "
            + AccountEntry.COLUMN_PASSWORD + " TEXT NOT NULL, "
            + AccountEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            + "); ";

    private static final String SYSTEMLOGS_TABLE = "CREATE TABLE " + SystemLogsContract.LogEntry.TABLE_NAME + " ("
            + SystemLogsContract.LogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + SystemLogsContract.LogEntry.COLUMN_TYPE + " TEXT NOT NULL, "
            + SystemLogsContract.LogEntry.COLUMN_DETAILS + " TEXT NOT NULL, "
            + SystemLogsContract.LogEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            + "); ";



    // constructor
    public AppDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){

        Log.i("AppDBHelper", "onCreate()");

        try{

            // create table to hold user account data
            String SQL_CREATE_ACCOUNTS_TABLE = ACCOUNTS_TABLE;
            sqLiteDatabase.execSQL(SQL_CREATE_ACCOUNTS_TABLE);

            String SQL_CREATE_LOGS_TABLE = SYSTEMLOGS_TABLE;
            sqLiteDatabase.execSQL(SQL_CREATE_LOGS_TABLE);

            TestUtil.insertTestData(sqLiteDatabase);

        }catch (SQLException e){

            Log.d("AppDBHelper", "onCreate() ERROR: " + e.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){

        Log.d("AppDBHelper", "onUpgrade()");

        // simply drop the table and create a new one.
        // if DATABASE_VERSION changed then the tables will be dropped.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AccountEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SystemLogsContract.LogEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }


    // returns true if user exists
    public boolean isUser(String username){

        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                AccountEntry._ID
        };

        String selection = AccountEntry.COLUMN_USERNAME + " = ?";

        String[] selectArg = {username};

        Cursor cursor = db.query(
                AccountEntry.TABLE_NAME,
                columns,
                selection,
                selectArg,
                null,
                null,
                null
        );

        int cusorCount = cursor.getCount();
        cursor.close();
        db.close();

        if(cusorCount> 0){
            return true;
        }

        return false;
    }

    // add user to account table
    public void addUser(Account user){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(AccountEntry.COLUMN_USERNAME, user.getUsername());
        cv.put(AccountEntry.COLUMN_PASSWORD, user.getPassword());

       try{
           db.insert(AccountEntry.TABLE_NAME, null, cv);
       } catch (SQLException e){
           Log.d("AppDBHelper addUser", "Error: " + e.getMessage());
       }
        db.close();

        Log.d("AppDBHelper addUser", "DONE");
    }

    // add entry to system log
    public void addLogEntry(String type, String details){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(SystemLogsContract.LogEntry.COLUMN_TYPE, type);
        cv.put(SystemLogsContract.LogEntry.COLUMN_DETAILS, details);

        try{
            db.insert(SystemLogsContract.LogEntry.TABLE_NAME, null, cv);
        } catch (SQLException e){
            Log.d("AppDBHelper addLogEntry", "Error: " + e.getMessage());
        }
        db.close();

        Log.d("AppDBHelper addLogEntry", "DONE");


    }
}