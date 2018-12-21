package com.cherylfong.airlineapp;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CancelReservationActivity extends AppCompatActivity {

    private static final String LOG_TAG = CancelReservationActivity.class.getSimpleName();

    private ReserveListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private AppDBHelper dbHelper;

    private EditText mLoginUsernameEditText;
    private EditText mLoginPasswordEditText;

    private String mUser = "";

    private ViewReservedFlightsActivity vRFActivity = new ViewReservedFlightsActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_reservation);

        RecyclerView cancelReservedFlightsRecyclerView;

        cancelReservedFlightsRecyclerView = this.findViewById(R.id.show_account_reservation_view);
        cancelReservedFlightsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new AppDBHelper(this);

        mDb = dbHelper.getWritableDatabase();

        Cursor cursor = getAllReservedFlights();

        mAdapter = new ReserveListAdapter(this, cursor);

        cancelReservedFlightsRecyclerView.setAdapter(mAdapter);

        mLoginUsernameEditText = (EditText) this.findViewById(R.id.cancel_reserve_username_edit_text);
        mLoginPasswordEditText = (EditText) this.findViewById(R.id.cancel_reserve_password_edit_text);

        findViewById(R.id.cancel_reserve_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if any of the EditTexts are empty, otherwise return/exit
                if (mLoginUsernameEditText.getText().length() == 0 ||
                        mLoginPasswordEditText.getText().length() == 0) {

                    Toast.makeText(getApplicationContext(), "All Fields must be filled!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String password = ((EditText) findViewById(R.id.cancel_reserve_password_edit_text)).getText().toString().trim();
                String username = ((EditText) findViewById(R.id.cancel_reserve_username_edit_text)).getText().toString().trim();

                if( !dbHelper.isUser(username) ){

                    Toast.makeText(getApplicationContext(), "Invalid username!", Toast.LENGTH_LONG).show();
                    return;

                } else {

                    if (!dbHelper.isCorrectUserPass(username, password)) {

                        Toast.makeText(getApplicationContext(), "Invalid password!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();

                    Cursor mCursor = getUserReservations(username);


                    // query reservation
                    if(mCursor != null){
                        mCursor.moveToFirst();

                        try{

                            mUser = mCursor.getString(mCursor.getColumnIndex(ReserveContract.ReserveEntry.COLUMN_BY_USER));

                            // show "logged in as" on screen
                            TextView loginAs = findViewById(R.id.cancel_reserve_login_username_textView);
                            loginAs.setText("Logged in as: " + mUser);
                            loginAs.setVisibility(View.VISIBLE);

                            // update the list
                            // un-invisible the recycler view
                            mAdapter.swapCursor(mCursor);
                            findViewById(R.id.show_account_reservation_view).setVisibility(View.VISIBLE);


                        }catch (CursorIndexOutOfBoundsException e){


                            // User has no reservations.
                            Toast.makeText(getApplicationContext(), "User has no reservations!", Toast.LENGTH_LONG ).show();

//                            setResult(RESULT_OK);
//                            finish();

                        }
                    }


                }


            }
        });

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

                Cursor mCursor = getRowDataFromID(id);

                // query reservation
                if(mCursor != null){
                    mCursor.moveToFirst();

                    try{

                        String mTicket= mCursor.getString(mCursor.getColumnIndex(ReserveContract.ReserveEntry.COLUMN_TICKETS));
                        String mDepart= mCursor.getString(mCursor.getColumnIndex(ReserveContract.ReserveEntry.COLUMN_DEPART));
                        String mArrive = mCursor.getString(mCursor.getColumnIndex(ReserveContract.ReserveEntry.COLUMN_ARRIVE));
                        String mTakeOff = mCursor.getString(mCursor.getColumnIndex(ReserveContract.ReserveEntry.COLUMN_TAKEOFF_TIME));
                        String mPrice = mCursor.getString(mCursor.getColumnIndex(ReserveContract.ReserveEntry.COLUMN_PRICE));
                        String mUser = mCursor.getString(mCursor.getColumnIndex(ReserveContract.ReserveEntry.COLUMN_BY_USER));
                        String mTime = mCursor.getString(mCursor.getColumnIndex(ReserveContract.ReserveEntry.COLUMN_TIMESTAMP));
                        String mFlightCode = mCursor.getString(mCursor.getColumnIndex(ReserveContract.ReserveEntry.COLUMN_DESIGNATOR));

                        int imTicket = Integer.parseInt(mTicket);

                        int flightID = getFlightIDfromReservedFlightCode(mFlightCode);


                        // UPDATE LOG
                        dbHelper.addLogEntry("cancel reservation", "by-usrnm=" + mUser + " depart="
                                + mDepart + " arrive= " + mArrive + " flightCode= " + mFlightCode + " ticketNum=" + mTicket
                                + " takeoffAt=" + mTakeOff + " totalPrice=" + String.valueOf(Integer.parseInt(mTicket)
                                *Double.parseDouble(mPrice)));

                        // UPDATE CAPACITY (add to current capacity
                        dbHelper.updateFlightCapacity(flightID, false, imTicket);

                        //remove from DB
                        removeReservedFlight(id);

                        Toast.makeText(getApplicationContext(), "Reserved flight removed.", Toast.LENGTH_SHORT).show();


                    } catch (Exception e) {
                        Log.d(LOG_TAG, "ERROR:" + e.getMessage());

                    }
                }


                Cursor mmCursor = getUserReservations(mUser);

//                Log.d(LOG_TAG, "HEEEYYYY");

                // query reservation
                if(mmCursor != null){
                    mmCursor.moveToFirst();

                    try{

                        int cursorCount = mmCursor.getCount();

                       if(cursorCount<=0){

                            // User has no reservations.
                            Toast.makeText(getApplicationContext(), "User: " + mUser + " has no reservations!", Toast.LENGTH_LONG ).show();

                            // return to main menu
//                            setResult(RESULT_OK);
//                            finish();
                       }

                        // update ui
                        mAdapter.swapCursor(mmCursor);

                    }catch (CursorIndexOutOfBoundsException e){


                        // User has no reservations.
                        Toast.makeText(getApplicationContext(), "User: " + mUser + " has no reservations!", Toast.LENGTH_LONG ).show();

//                        // return to main menu
//                        setResult(RESULT_OK);
//                        finish();

                    }
                }else{

                    Log.d(LOG_TAG, "HEEEYYYY 3");

                    // User has no reservations.
                    Toast.makeText(getApplicationContext(), "User: " + mUser + " has no reservations!", Toast.LENGTH_LONG ).show();

                    // return to main menu
//                    setResult(RESULT_OK);
//                    finish();
                }

            }

            // attach the ItemTouchHelper to the RecyclerView
        }).attachToRecyclerView(cancelReservedFlightsRecyclerView);



    } // onCreate


    private Cursor getUserReservations(String username){

        SQLiteDatabase mmDb = dbHelper.getWritableDatabase();

        String selection = ReserveContract.ReserveEntry.COLUMN_BY_USER + " = ?";


        String[] selectArg = {
                username
        };

        return mmDb.query(
                ReserveContract.ReserveEntry.TABLE_NAME,
                null,
                selection,
                selectArg,
                null,
                null,
                null
        );
    }

    private Cursor getAllReservedFlights() {

        return mDb.query(
                ReserveContract.ReserveEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                ReserveContract.ReserveEntry.COLUMN_TIMESTAMP
        );
    }

    private Cursor getRowDataFromID( int id){

        SQLiteDatabase mmDb = dbHelper.getWritableDatabase();

        String selection = ReserveContract.ReserveEntry._ID + " = ?";

        String _id = String.valueOf(id);


        String[] selectArg = {
                _id
        };

        return mmDb.query(
                ReserveContract.ReserveEntry.TABLE_NAME,
                null,
                selection,
                selectArg,
                null,
                null,
                null
        );

    }

    // returns the id of a flight from the match flightCode reservation
    private int getFlightIDfromReservedFlightCode( String flightCode){

        String selection = FlightContract.FlightEntry.COLUMN_DESIGNATOR + " = ?";


        String[] selectArg = {
                flightCode
        };

        Cursor mCursor = mDb.query(
                FlightContract.FlightEntry.TABLE_NAME,
                null,
                selection,
                selectArg,
                null,
                null,
                null
        );

        if(mCursor != null){
            mCursor.moveToFirst();
        }

        String flightID = mCursor.getString(mCursor.getColumnIndex(FlightContract.FlightEntry._ID));

        return Integer.parseInt(flightID);

    }

    private boolean removeReservedFlight(long id){

        return mDb.delete(ReserveContract.ReserveEntry.TABLE_NAME, ReserveContract.ReserveEntry._ID + "=" + id, null) > 0;
    }




}
