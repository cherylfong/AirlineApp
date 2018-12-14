package com.cherylfong.airlineapp;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {


    public static void insertTestAccounts(SQLiteDatabase db){

        if(db == null){

            Log.e("TestUtil", "Error: db null");
            return;
        }

        Log.d("TestUtil", "insertTestAccounts called");

        //create a list of test accounts
        List<ContentValues> accList = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(AccountContract.AccountEntry.COLUMN_USERNAME, "A@lice5");
        cv.put(AccountContract.AccountEntry.COLUMN_PASSWORD, "@cSit100");
        accList.add(cv);

        cv = new ContentValues();
        cv.put(AccountContract.AccountEntry.COLUMN_USERNAME, "$BriAn7");
        cv.put(AccountContract.AccountEntry.COLUMN_PASSWORD, "123aBc##");
        accList.add(cv);

        cv = new ContentValues();
        cv.put(AccountContract.AccountEntry.COLUMN_USERNAME, "!chriS12!");
        cv.put(AccountContract.AccountEntry.COLUMN_PASSWORD, "CHrIS12!!");
        accList.add(cv);

        cv = new ContentValues();
        cv.put(AccountContract.AccountEntry.COLUMN_USERNAME, "!admiM2");
        cv.put(AccountContract.AccountEntry.COLUMN_PASSWORD, "!admiM2");
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







    }
}
