package com.cherylfong.airlineapp;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {


    public static void insertTestData(SQLiteDatabase db){

        if(db == null){

            Log.e("TestUtil", "Error: db null");
            return;
        }

        Log.d("TestUtil", "insertTestAccounts called");

        //create a list of test accounts
        List<ContentValues> accList = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(AccountContract.AccountEntry.COLUMN_USERNAME, "@Alice5");
        cv.put(AccountContract.AccountEntry.COLUMN_PASSWORD, "@aL100");
        accList.add(cv);

        cv = new ContentValues();
        cv.put(AccountContract.AccountEntry.COLUMN_USERNAME, "$BriAn7");
        cv.put(AccountContract.AccountEntry.COLUMN_PASSWORD, "123aBc##");
        accList.add(cv);

        cv = new ContentValues();
        cv.put(AccountContract.AccountEntry.COLUMN_USERNAME, "z@ch3");
        cv.put(AccountContract.AccountEntry.COLUMN_PASSWORD, "zZz3!");
        accList.add(cv);

        //insert all accounts in one transaction
        try
        {
            db.beginTransaction();

            //clear the table
            db.delete(AccountContract.AccountEntry.TABLE_NAME,null,null);

            //add cv on the list
            for(ContentValues c:accList){

                db.insert(AccountContract.AccountEntry.TABLE_NAME, null, c);

            }

            db.setTransactionSuccessful();

            Log.d("TestUtil", "create dummy accounts data successful");
        }
        catch (SQLException e) {

            Log.e("TestUtil", "Error: " + e.getMessage());
        }
        finally
        {
            db.endTransaction();
            Log.d("TestUtil", "create dummy accounts data done");
        }



        //////////////////////////////////////////////////////////////////////
        Log.d("TestUtil", "insertTestLogs called");
        /////////////////////////////////////////////////////////////////////

        //create a list of test accounts
        List<ContentValues> logList = new ArrayList<ContentValues>();

        cv = new ContentValues();
        cv.put(SystemLogsContract.LogEntry.COLUMN_TYPE, "X0");
        cv.put(SystemLogsContract.LogEntry.COLUMN_DETAILS, "....");
        logList.add(cv);

        cv = new ContentValues();
        cv.put(SystemLogsContract.LogEntry.COLUMN_TYPE, "X1");
        cv.put(SystemLogsContract.LogEntry.COLUMN_DETAILS, "....");
        logList.add(cv);

        //insert all logs in one transaction
        try
        {
            db.beginTransaction();

            //clear the table
            db.delete(SystemLogsContract.LogEntry.TABLE_NAME,null,null);

            //add cv on the list
            for(ContentValues v:logList){

                db.insert(SystemLogsContract.LogEntry.TABLE_NAME, null, v);

            }

            db.setTransactionSuccessful();

            Log.d("TestUtil", "create dummy log data successful");
        }
        catch (SQLException e) {

            Log.e("TestUtil", "Error: " + e.getMessage());
        }
        finally
        {
            db.endTransaction();
            Log.d("TestUtil", "create dummy log data done");
        }


        //////////////////////////////////////////////////////////////////////
        Log.d("TestUtil", "insertTestFLIGHTS called");
        /////////////////////////////////////////////////////////////////////

        //create a list of test accounts
        List<ContentValues> flightList = new ArrayList<ContentValues>();

        cv = new ContentValues();
        cv.put(FlightContract.FlightEntry.COLUMN_DESIGNATOR, "Zephyr101");
        cv.put(FlightContract.FlightEntry.COLUMN_DEPART, "San Francisco");
        cv.put(FlightContract.FlightEntry.COLUMN_ARRIVE, "Los Angeles");
        cv.put(FlightContract.FlightEntry.COLUMN_TAKEOFF_TIME, "10:30");
        cv.put(FlightContract.FlightEntry.COLUMN_CAPACITY, 10);
        cv.put(FlightContract.FlightEntry.COLUMN_PRICE, 150.00);
        flightList.add(cv);

        cv = new ContentValues();
        cv.put(FlightContract.FlightEntry.COLUMN_DESIGNATOR, "Zephyr102");
        cv.put(FlightContract.FlightEntry.COLUMN_DEPART, "Los Angeles");
        cv.put(FlightContract.FlightEntry.COLUMN_ARRIVE, "San Francisco");
        cv.put(FlightContract.FlightEntry.COLUMN_TAKEOFF_TIME, "13:00");
        cv.put(FlightContract.FlightEntry.COLUMN_CAPACITY, 10);
        cv.put(FlightContract.FlightEntry.COLUMN_PRICE, 150.00);
        flightList.add(cv);

        cv = new ContentValues();
        cv.put(FlightContract.FlightEntry.COLUMN_DESIGNATOR, "Zephyr201");
        cv.put(FlightContract.FlightEntry.COLUMN_DEPART, "San Francisco");
        cv.put(FlightContract.FlightEntry.COLUMN_ARRIVE, "Seattle");
        cv.put(FlightContract.FlightEntry.COLUMN_TAKEOFF_TIME, "11:00");
        cv.put(FlightContract.FlightEntry.COLUMN_CAPACITY, 5);
        cv.put(FlightContract.FlightEntry.COLUMN_PRICE, 200.50);
        flightList.add(cv);

        cv = new ContentValues();
        cv.put(FlightContract.FlightEntry.COLUMN_DESIGNATOR, "Zephyr205");
        cv.put(FlightContract.FlightEntry.COLUMN_DEPART, "San Francisco");
        cv.put(FlightContract.FlightEntry.COLUMN_ARRIVE, "Seattle");
        cv.put(FlightContract.FlightEntry.COLUMN_TAKEOFF_TIME, "15:45");
        cv.put(FlightContract.FlightEntry.COLUMN_CAPACITY, 15);
        cv.put(FlightContract.FlightEntry.COLUMN_PRICE, 150.00);
        flightList.add(cv);

        cv = new ContentValues();
        cv.put(FlightContract.FlightEntry.COLUMN_DESIGNATOR, "Zephyr202");
        cv.put(FlightContract.FlightEntry.COLUMN_DEPART, "Seattle");
        cv.put(FlightContract.FlightEntry.COLUMN_ARRIVE, "San Francisco");
        cv.put(FlightContract.FlightEntry.COLUMN_TAKEOFF_TIME, "14:10");
        cv.put(FlightContract.FlightEntry.COLUMN_CAPACITY, 5);
        cv.put(FlightContract.FlightEntry.COLUMN_PRICE, 200.50);
        flightList.add(cv);

        //insert all flights in one transaction
        try
        {
            db.beginTransaction();

            //clear the table
            db.delete(FlightContract.FlightEntry.TABLE_NAME,null,null);

            //add cv on the list
            for(ContentValues v:flightList){

                db.insert(FlightContract.FlightEntry.TABLE_NAME, null, v);

            }

            db.setTransactionSuccessful();

            Log.d("TestUtil", "create dummy flight data successful");
        }
        catch (SQLException e) {

            Log.e("TestUtil", "Error: " + e.getMessage());
        }
        finally
        {
            db.endTransaction();
            Log.d("TestUtil", "create dummy flight data done");
        }

        //////////////////////////////////////////////////////////////////////
        Log.d("TestUtil", "insertTestReservations called");
        /////////////////////////////////////////////////////////////////////

        //create a list of test accounts
        List<ContentValues> reservationsList = new ArrayList<ContentValues>();

        cv = new ContentValues();
        cv.put(ReserveContract.ReserveEntry.COLUMN_DESIGNATOR, "Test111");
        cv.put(ReserveContract.ReserveEntry.COLUMN_DEPART, "depart from");
        cv.put(ReserveContract.ReserveEntry.COLUMN_ARRIVE, "arrive at");
        cv.put(ReserveContract.ReserveEntry.COLUMN_TAKEOFF_TIME, "25:00");
        cv.put(ReserveContract.ReserveEntry.COLUMN_TICKETS, 3);
        cv.put(ReserveContract.ReserveEntry.COLUMN_PRICE, 150.00);
        cv.put(ReserveContract.ReserveEntry.COLUMN_BY_USER, "testUser");
        reservationsList.add(cv);

        //insert all flights in one transaction
        try
        {
            db.beginTransaction();

            //clear the table
            db.delete(ReserveContract.ReserveEntry.TABLE_NAME,null,null);

            //add cv on the list
            for(ContentValues v:reservationsList){

                db.insert(ReserveContract.ReserveEntry.TABLE_NAME, null, v);

            }

            db.setTransactionSuccessful();

            Log.d("TestUtil", "create dummy reservation data successful");
        }
        catch (SQLException e) {

            Log.e("TestUtil", "Error: " + e.getMessage());
        }
        finally
        {
            db.endTransaction();
            Log.d("TestUtil", "create dummy reservation data done");
        }


    } // function bracket


}
