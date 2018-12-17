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
import android.widget.Toast;

public class ViewReservedFlightsActivity extends AppCompatActivity {

    private static final String LOG_TAG = ViewReservedFlightsActivity.class.getSimpleName();

    private ReserveListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private AppDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reserved_flights);

        RecyclerView reservedFlightsRecyclerView;

        reservedFlightsRecyclerView = this.findViewById(R.id.all_reserved_flights_view);
        reservedFlightsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new AppDBHelper(this);

        mDb = dbHelper.getWritableDatabase();

        Cursor cursor = getAllReservedFlights();

        mAdapter = new ReserveListAdapter(this, cursor);

        reservedFlightsRecyclerView.setAdapter(mAdapter);

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

                Log.d("ItemTouchHelper SYSTEM", "value of id: " + id);


                // get tickets from id
                Cursor mCursor = getRowDataFromReservationID(id);


                if(mCursor != null) {
                    mCursor.moveToFirst();

                    try {

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

                        // UPDATE CAPACITY
                        dbHelper.updateFlightCapacity(flightID, false, imTicket);

                    } catch (CursorIndexOutOfBoundsException e) {

                        Log.d(LOG_TAG, "get ticket content failed!");
                    } catch (Exception e) {
                        Log.d(LOG_TAG, "ERROR:" + e.getMessage());

                    }
                }

                //remove from DB
                removeReservedFlight(id);

                //update the list
                mAdapter.swapCursor(getAllReservedFlights());

                Toast.makeText(getApplicationContext(), "Reserved flight removed.", Toast.LENGTH_SHORT).show();
            }

            // attach the ItemTouchHelper to the RecyclerView
        }).attachToRecyclerView(reservedFlightsRecyclerView);

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

    private boolean removeReservedFlight(long id){

        return mDb.delete(ReserveContract.ReserveEntry.TABLE_NAME, ReserveContract.ReserveEntry._ID + "=" + id, null) > 0;
    }


    private Cursor getRowDataFromReservationID( int id){

        String selection = ReserveContract.ReserveEntry._ID + " = ?";

        String _id = String.valueOf(id);


        String[] selectArg = {
                _id
        };

        return mDb.query(
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
}
