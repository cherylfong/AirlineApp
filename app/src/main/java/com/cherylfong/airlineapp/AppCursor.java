package com.cherylfong.airlineapp;

import android.database.CursorWrapper;
import android.database.Cursor;

// TODO (4) find a way to use this class otherwise remove it and account class.

public class  AppCursor extends CursorWrapper {

    public AppCursor(Cursor cursor){
        super(cursor);
    }

    // retrieve column values from cursor and make a Account object
    public Account getAccount(){

        String username = getString(getColumnIndex(AccountContract.AccountEntry.COLUMN_USERNAME));
        String password = getString(getColumnIndex(AccountContract.AccountEntry.COLUMN_PASSWORD));

        Account a = new Account(username,password);
        return a;
    }

}
