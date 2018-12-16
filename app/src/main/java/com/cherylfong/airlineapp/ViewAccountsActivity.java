package com.cherylfong.airlineapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ViewAccountsActivity extends AppCompatActivity {

    private AccountListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private AppDBHelper dbHelper;

    private EditText mNewUsernameEditText;
    private EditText mNewPasswordEditText;

    private final static String LOG_TAG = ViewAccountsActivity.class.getSimpleName();

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
        dbHelper = new AppDBHelper(this);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // since will be adding users
        mDb = dbHelper.getWritableDatabase();

        // Get all guest info from the database and save in a cursor
        Cursor cursor = getAllAccounts();

        // create an adapter for that cursor to display data
        mAdapter = new AccountListAdapter(this, cursor);

        // link adapter to Recyclerview
        accountListRecylerView.setAdapter(mAdapter);



        // A new ItemTouchHelper with a SimpleCallback that handles both LEFT and RIGHT swipe directions
        // To handle swiping items off the list
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {



            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //do nothing, since only want swiping
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                //get the id of the item being swiped
                int id = (int) viewHolder.itemView.getTag();

                Log.d("ItemTouchHelper", "value of id: " + id);

                AppDBHelper mdbHelper = new AppDBHelper(getApplicationContext());

                String details =  getInfoFromID(id, mdbHelper);

                // add to system log
                mdbHelper.addLogEntry("delete account",details);
//              mdbHelper.addLogEntry("delete account", "test");

                removeAccount(id);

                //update the list
                mAdapter.swapCursor(getAllAccounts());

                Toast.makeText(getApplicationContext(), "Account removed.", Toast.LENGTH_SHORT).show();

            }

            // attach the ItemTouchHelper to the RecyclerView
        }).attachToRecyclerView(accountListRecylerView);

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

            Toast.makeText(getApplicationContext(), "All Fields must be filled!", Toast.LENGTH_SHORT).show();
            return;
        }


        String password = "";

       // TODO (1) Update exception for password
        try {
            //mNewPartyCountEditText inputType="number", so this should always work
            password = mNewPasswordEditText.getText().toString().trim();

        } catch (Exception e) {

            Log.e(LOG_TAG, "Password in add account error: " + e.getMessage());
        }

        String username = mNewUsernameEditText.getText().toString().trim();
        // add new account with username and password
        // Add username info to mDb
        addNewUser(username, password);

        dbHelper.addLogEntry("sys: new account", "usrnm=" + username + " pword=" + password);

        // to update the cursor in the adapter to trigger UI to display the new list
        mAdapter.swapCursor(getAllAccounts());

        // to make the UI look nice,
        // clear UI text fields
        mNewPasswordEditText.clearFocus();
        mNewUsernameEditText.getText().clear();
        mNewPasswordEditText.getText().clear();

        Toast.makeText(getApplicationContext(), "New account added.", Toast.LENGTH_SHORT).show();

        // dbHelper.addLogEntry("new account system", "test");

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

    // TODO remove this function perhaps

    /**
     * Query the mDb and get all users from accounts table
     *
     * @return a list datatype of accounts (users)
     */
    private List<Account> getAllAccountsList(){

        List<Account> accList = new ArrayList<Account>();

        Cursor cursor = getAllAccounts();


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Account user = new Account();

                user.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(AccountContract.AccountEntry._ID))));
                user.setUsername(cursor.getString(cursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_USERNAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_PASSWORD)));

                // Adding user record to list
                accList.add(user);
            } while (cursor.moveToNext());
        }

        return accList;
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


    /**
     * Removes the record with the specified id
     *
     * @param id the DB id to be removed
     * @return True: if removed successfully, False: failed
     */
    private boolean removeAccount(int id) {

        return mDb.delete(AccountContract.AccountEntry.TABLE_NAME, AccountContract.AccountEntry._ID + "=" + id, null) > 0;
    }


    private Cursor getRowData(int id, AppDBHelper mDBHelper){

        SQLiteDatabase mmDb = mDBHelper.getWritableDatabase();

        String selection = AccountContract.AccountEntry._ID + " = ?";


        String[] selectArg = {
                String.valueOf(id)
        };

        return mmDb.query(
                AccountContract.AccountEntry.TABLE_NAME,
                null,
                selection,
                selectArg,
                null,
                null,
                null
        );

    }

    private String getInfoFromID(int id, AppDBHelper mDBHelper){

        Cursor cursor = getRowData(id, mDBHelper);

        if (cursor != null){

            cursor.moveToFirst();

            String u = cursor.getString(cursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_USERNAME));
            String p = cursor.getString(cursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_PASSWORD));
            String t = cursor.getString(cursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_TIMESTAMP));
            String mid = cursor.getString(cursor.getColumnIndex(AccountContract.AccountEntry._ID));

            return "id=" + mid + " usrnm=" + u + " pword=" + p + " createdAt=" + t;

        }else{

            Toast.makeText(getApplicationContext(), "Something went wrong. - ViewAccountsActivity", Toast.LENGTH_LONG);
            return "ERROR getInfoFromID - ViewAccountsActivity";
        }

    }


}
