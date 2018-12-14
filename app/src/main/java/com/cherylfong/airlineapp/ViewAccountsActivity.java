package com.cherylfong.airlineapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ViewAccountsActivity extends AppCompatActivity {

    private AccountListAdapter mAdapter;
    private SQLiteDatabase mDb;

    private EditText mNewUsernameEditText;
    private EditText mNewPasswordEditText;

    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_accounts);

        RecyclerView accountListRecylerView;

        // set local attributes to corresponding views
        accountListRecylerView = (RecyclerView) this.findViewById(R.id.all_accounts_list_view);

        mNewUsernameEditText = (EditText) this.findViewById(R.id.username_edit_text);
        mNewPasswordEditText = (EditText) this.findViewById(R.id.password_edit_text);

        // set layout for RecyclerView, as set as Linear Layout
        accountListRecylerView.setLayoutManager(new LinearLayoutManager(this));

        // create DB helper to create the DB if run for first time
        AppDBHelper dbHelper = new AppDBHelper(this);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // since will be adding users
        mDb = dbHelper.getWritableDatabase();

        // Get all guest info from the database and save in a cursor
        Cursor cursor = getAllAccounts();

        // create an adapter for that cursor to display data
        mAdapter = new AccountListAdapter(this, cursor);

        // link adapter to Recyclerview
        accountListRecylerView.setAdapter(mAdapter);
    }

    /**
     * This method is called when user clicks on the Add Account button
     *
     * @param view The calling view (button)
     */
    public void addUserAccount(View view) {

        // Check if any of the EditTexts are empty, otherwise return/exit
        if (mNewUsernameEditText.getText().length() == 0 ||
                mNewPasswordEditText.getText().length() == 0) {
            return;
        }


        String password = "";

       // TODO (1) Update exception for password
        try {
            //mNewPartyCountEditText inputType="number", so this should always work
            password = mNewPasswordEditText.getText().toString();

        } catch (Exception e) {

            Log.e(LOG_TAG, "Password in add account error: " + e.getMessage());
        }

        // add new account with username and password
        // Add username info to mDb
        addNewUser(mNewUsernameEditText.getText().toString(), password);

        // to update the cursor in the adapter to trigger UI to display the new list
        mAdapter.swapCursor(getAllAccounts());

        // to make the UI look nice,
        // clear UI text fields
        mNewPasswordEditText.clearFocus();
        mNewUsernameEditText.getText().clear();
        mNewPasswordEditText.getText().clear();
    }

    /**
     * Query the mDb and get all users from accounts table
     *
     * @return Cursor containing the list of accounts (users)
     */
    private Cursor getAllAccounts() {
        return mDb.query(
                AccountContract.AccountEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                AccountContract.AccountEntry.COLUMN_TIMESTAMP
        );
    }

    /**
     * Adds a new user to the mDb including password and the current timestamp
     *
     * @param username account's username
     * @param password username's password
     * @return id of new record added
     */
    private long addNewUser(String username, String password) {

        ContentValues cv = new ContentValues();

        cv.put(AccountContract.AccountEntry.COLUMN_USERNAME, username);

        cv.put(AccountContract.AccountEntry.COLUMN_PASSWORD, password);

        Toast.makeText(getApplicationContext(), "User account created.", Toast.LENGTH_SHORT).show();

        return mDb.insert(AccountContract.AccountEntry.TABLE_NAME, null, cv);
    }


}
