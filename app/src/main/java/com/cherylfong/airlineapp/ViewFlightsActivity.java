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

public class ViewFlightsActivity extends AppCompatActivity {

    private FlightListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private AppDBHelper dbHelper;

    private EditText mNewDepartEditText;
    private EditText mNewArriveEditText;
    private EditText mNewTakeoffEditText;
    private EditText mNewCapacityEditText;
    private EditText mNewPriceEditText;
    private EditText mNewFlightCodeEditText;

    private final static String LOG_TAG = ViewFlightsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_flights);

        RecyclerView flightListRecyclerView;

        flightListRecyclerView = (RecyclerView) this.findViewById(R.id.all_system_flights);

        mNewDepartEditText = this.findViewById(R.id.depart_system_editText);
        mNewArriveEditText = this.findViewById(R.id.arrive_system_editText);
        mNewTakeoffEditText = this.findViewById(R.id.takeoff_time_system_editText);
        mNewCapacityEditText = this.findViewById(R.id.capacity_system_editText);
        mNewPriceEditText = this.findViewById(R.id.price_system_editText);
        mNewFlightCodeEditText = this.findViewById(R.id.flightCode_system_editText);

        flightListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new AppDBHelper(this);

        mDb = dbHelper.getWritableDatabase();

        Cursor cursor = getAllSystemFlights();

        mAdapter = new FlightListAdapter(this, cursor);

        flightListRecyclerView.setAdapter(mAdapter);

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

                dbHelper.addLogEntry("delete flight", getInfoFromID(id));

                //remove from DB
                removeFlight(id);

                //update the list
                mAdapter.swapCursor(getAllSystemFlights());

                Toast.makeText(getApplicationContext(), "Flight removed.", Toast.LENGTH_SHORT).show();
            }

            // attach the ItemTouchHelper to the RecyclerView
        }).attachToRecyclerView(flightListRecyclerView);

    }

    // TODO add function to button
    public void addFlight(View view){

        if(mNewFlightCodeEditText.getText().length() == 0 ||
                mNewDepartEditText.getText().length() == 0 ||
                mNewArriveEditText.getText().length() == 0 ||
                mNewTakeoffEditText.getText().length() == 0 ||
                mNewCapacityEditText.getText().length() == 0 ||
                mNewPriceEditText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), "All Fields must be filled!", Toast.LENGTH_SHORT).show();
            return;
        }

        String newFlightCode = mNewFlightCodeEditText.getText().toString().trim();
        String newDeparture = mNewDepartEditText.getText().toString().trim();
        String newArrival = mNewArriveEditText.getText().toString().trim();
        String newTakeoff = mNewTakeoffEditText.getText().toString().trim();
        int newCapacity = 0;
        double newPrice = 0.0;

        try{
           newPrice = Double.parseDouble(mNewPriceEditText.getText().toString().trim());
        }catch (Exception e){
            Log.d(LOG_TAG, "Invalid double for price");
            Toast.makeText(getApplicationContext(), "Price must be numbers w/o decimal", Toast.LENGTH_LONG).show();
            return;
        }

        try{
            newCapacity = Integer.parseInt(mNewCapacityEditText.getText().toString().trim());
        }catch (Exception e){
            Log.d(LOG_TAG, "Invalid int for capacity");
            Toast.makeText(getApplicationContext(), "Capacity must be numbers with no decimal", Toast.LENGTH_LONG).show();
            return;
        }

        addNewFlightEntry(newFlightCode, newDeparture, newArrival, newTakeoff, newCapacity, newPrice);

        mAdapter.swapCursor(getAllSystemFlights());

        Toast.makeText(getApplicationContext(), "New flight added.", Toast.LENGTH_SHORT).show();

        dbHelper.addLogEntry("new flight", "flycode=" + newFlightCode + " depart=" + newDeparture
                + " arrive=" + newArrival + " cap=" + newCapacity + " t=" + newTakeoff + " p=" + newPrice);


        mNewFlightCodeEditText.clearFocus();
        mNewFlightCodeEditText.getText().clear();
        mNewDepartEditText.getText().clear();
        mNewArriveEditText.getText().clear();
        mNewTakeoffEditText.getText().clear();
        mNewCapacityEditText.getText().clear();
        mNewPriceEditText.getText().clear();
    }

    private Cursor getAllSystemFlights(){
        return mDb.query(
                FlightContract.FlightEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                FlightContract.FlightEntry.COLUMN_TIMESTAMP
        );
    }

    private long addNewFlightEntry(String flightCode, String depart, String arrive,
                                   String takeoff, int capacity, Double price){

        ContentValues cv = new ContentValues();
        cv.put(FlightContract.FlightEntry.COLUMN_DESIGNATOR, flightCode);
        cv.put(FlightContract.FlightEntry.COLUMN_DEPART, depart);
        cv.put(FlightContract.FlightEntry.COLUMN_ARRIVE, arrive);
        cv.put(FlightContract.FlightEntry.COLUMN_TAKEOFF_TIME, takeoff);
        cv.put(FlightContract.FlightEntry.COLUMN_CAPACITY, capacity);
        cv.put(FlightContract.FlightEntry.COLUMN_PRICE, price);


        return mDb.insert(FlightContract.FlightEntry.TABLE_NAME, null, cv);
    }

    private boolean removeFlight(int id){
        return mDb.delete(FlightContract.FlightEntry.TABLE_NAME, FlightContract.FlightEntry._ID + " = " + id, null) > 0 ;
    }

    // return cursor with matching id
    private Cursor getRowData(int id){

        String selection = FlightContract.FlightEntry._ID + " = ?";


        String[] selectArg = {
                String.valueOf(id)
        };

        return mDb.query(
                FlightContract.FlightEntry.TABLE_NAME,
                null,
                selection,
                selectArg,
                null,
                null,
                null
        );

    }

    // return a string of information from db that matches id
    private String getInfoFromID(int id){

        Cursor cursor = getRowData(id);

        if (cursor != null){

            cursor.moveToFirst();

            String flightCode = cursor.getString(cursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_DESIGNATOR));
            String depart = cursor.getString(cursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_DEPART));
            String arrive = cursor.getString(cursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_ARRIVE));
            String takeOff = cursor.getString(cursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_TAKEOFF_TIME));
            String capacity = cursor.getString(cursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_CAPACITY));
            String price = cursor.getString(cursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_PRICE));
            String t = cursor.getString(cursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_TIMESTAMP));

            return "flycode=" + flightCode + " depart=" + depart + " arrive=" + arrive + " cap=" + capacity
                    + " t="
                    + takeOff
                    + " p=$"
                    + price
                    + " createdAt="
                    + t;


        }else{

            Toast.makeText(getApplicationContext(), "Something went wrong. - ViewFlightsActivity", Toast.LENGTH_LONG);
            return "ERROR getInfoFromID - ViewFlightsActivity";

        }


    }
}
