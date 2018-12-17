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
    private static final int DATABASE_VERSION = 4;

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


    private static final String FLIGHTS_TABLE = "CREATE TABLE " + FlightContract.FlightEntry.TABLE_NAME + " ("
            + FlightContract.FlightEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + FlightContract.FlightEntry.COLUMN_DESIGNATOR + " TEXT NOT NULL, "
            + FlightContract.FlightEntry.COLUMN_DEPART + " TEXT NOT NULL, "
            + FlightContract.FlightEntry.COLUMN_ARRIVE + " TEXT NOT NULL, "
            + FlightContract.FlightEntry.COLUMN_TAKEOFF_TIME + " TEXT NOT NULL, "
            + FlightContract.FlightEntry.COLUMN_CAPACITY + " INTEGER NOT NULL, "
            + FlightContract.FlightEntry.COLUMN_PRICE + " DOUBLE NOT NULL, "
            + FlightContract.FlightEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
            + " UNIQUE (" + FlightContract.FlightEntry.COLUMN_DESIGNATOR + ") ON CONFLICT REPLACE"
            + "); ";

    // TODO
    // https://sqlite.org/lang_conflict.html
    // FAIL
    // use try catch when adding new flight in system

    private static final String RESERVATIONS_TABLE = "CREATE TABLE " + ReserveContract.ReserveEntry.TABLE_NAME + " ("
            + ReserveContract.ReserveEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ReserveContract.ReserveEntry.COLUMN_BY_USER + " TEXT NOT NULL, "
            + ReserveContract.ReserveEntry.COLUMN_DESIGNATOR + " TEXT NOT NULL, "
            + ReserveContract.ReserveEntry.COLUMN_DEPART + " TEXT NOT NULL, "
            + ReserveContract.ReserveEntry.COLUMN_ARRIVE + " TEXT NOT NULL, "
            + ReserveContract.ReserveEntry.COLUMN_TAKEOFF_TIME + " TEXT NOT NULL, "
            + ReserveContract.ReserveEntry.COLUMN_TICKETS + " INTEGER NOT NULL, "
            + ReserveContract.ReserveEntry.COLUMN_PRICE + " DOUBLE NOT NULL, "
            + ReserveContract.ReserveEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
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

            String SQL_CREATE_FLIGHT_TABLE = FLIGHTS_TABLE;
            sqLiteDatabase.execSQL(SQL_CREATE_FLIGHT_TABLE);

            String SQL_CREATE_RESERVATIONS_TABLE = RESERVATIONS_TABLE;
            sqLiteDatabase.execSQL(SQL_CREATE_RESERVATIONS_TABLE);

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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FlightContract.FlightEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReserveContract.ReserveEntry.TABLE_NAME);

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

    // returns true if username and password match
    public boolean isCorrectUserPass(String username, String password){

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = AccountEntry.COLUMN_USERNAME + " = ? AND "
                + AccountEntry.COLUMN_PASSWORD + " = ?";

        String[] selectArg = {
                username,
                password
        };

        Cursor cursor = db.query(
                AccountEntry.TABLE_NAME,
                null,
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
//        db.close();

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

        Log.d("AppDBHelper addLogEntry", "DONE");

    }

    public void addReservation(String user, String depart, String arrive, String desig, String tickets,
                               String takeOff, String price){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ReserveContract.ReserveEntry.COLUMN_BY_USER, user);
        cv.put(ReserveContract.ReserveEntry.COLUMN_DEPART,depart);
        cv.put(ReserveContract.ReserveEntry.COLUMN_ARRIVE,arrive);
        cv.put(ReserveContract.ReserveEntry.COLUMN_DESIGNATOR,desig);
        cv.put(ReserveContract.ReserveEntry.COLUMN_TICKETS,tickets);
        cv.put(ReserveContract.ReserveEntry.COLUMN_TAKEOFF_TIME, takeOff);
        cv.put(ReserveContract.ReserveEntry.COLUMN_PRICE,price);

        try{
            db.insert(ReserveContract.ReserveEntry.TABLE_NAME, null, cv);
        } catch (SQLException e){
            Log.d("AppDB addReservation", "Error: " + e.getMessage());
        }
//        db.close();

        Log.d("AppDB addReservation", "DONE");

    }
}