package com.cherylfong.airlineapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BookFlightActivity extends AppCompatActivity {

    private FlightListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private Cursor cursor;
    private RecyclerView flightListRecylerView;

    private EditText searchDepart;
    private EditText searchArrive;

    private Spinner spinner;

    private final static String LOG_TAG = BookFlightActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_flight);

        flightListRecylerView = (RecyclerView) this.findViewById(R.id.book_flights_list_view);

        searchArrive = (EditText) this.findViewById(R.id.arrive_editText);
        searchDepart = (EditText) this.findViewById(R.id.depart_editText);

        flightListRecylerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        AppDBHelper dbHelper = new AppDBHelper(this);

        mDb = dbHelper.getReadableDatabase();

        // get all available flights
        cursor = getAllFlights();

        mAdapter = new FlightListAdapter(getApplicationContext(), cursor);

        flightListRecylerView.setAdapter(mAdapter);

        findViewById(R.id.book_flights_list_view).setVisibility(View.INVISIBLE);

        //
        // https://developer.android.com/guide/topics/ui/controls/spinner
        //
        spinner = findViewById(R.id.ticket_num_spinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.ticketNum_array, R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        if (isCursorEmpty(cursor)) {

            Log.d(LOG_TAG, "cursor empty");

            Toast.makeText(getApplicationContext(), "NO FLIGHTS AVAILABLE", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.get_all_flights_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                findViewById(R.id.book_flights_list_view).setVisibility(View.VISIBLE);

                mAdapter.swapCursor(getAllFlights());

                if (isCursorEmpty(cursor)) {

                    Log.d(LOG_TAG, "cursor empty BUTTON CLICK");

                    Toast.makeText(getApplicationContext(), "NO FLIGHTS AVAILABLE", Toast.LENGTH_SHORT).show();
                }

                searchArrive.clearFocus();
                searchDepart.getText().clear();
                searchArrive.getText().clear();

            }
        });

        findViewById(R.id.search_flight_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText d = findViewById(R.id.depart_editText);
                EditText a = findViewById(R.id.arrive_editText);

                String ticketNumSelected = spinner.getSelectedItem().toString();
                String depart = d.getText().toString().trim();
                String arrive = a.getText().toString().trim();

                cursor = getQueriedFlights(depart, arrive);

                if (isCursorEmpty(cursor)) {

                    Toast.makeText(getApplicationContext(), "No Flights leaving " + depart + ", & arriving " + arrive, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
//                    return;
                }

                cursor = getTicketNumBasedQuery(ticketNumSelected, depart, arrive);

                if (isCursorEmpty(cursor)) {

                    Toast.makeText(getApplicationContext(), "No Flights leaving " + depart + ", & arriving " + arrive + " with capacity at least " + ticketNumSelected, Toast.LENGTH_SHORT).show();
                    return;
                }

                mAdapter.swapCursor(getTicketNumBasedQuery(ticketNumSelected, depart, arrive));

                findViewById(R.id.book_flights_list_view).setVisibility(View.VISIBLE);

            }
        });


//         A new ItemTouchHelper with a SimpleCallback that handles both LEFT and RIGHT swipe directions
//         To handle swiping items off the list
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

                startIntentUsingRowID(id);

            }

        }).attachToRecyclerView(flightListRecylerView);

    }

    // returns all flights via cursor
    private Cursor getAllFlights(){
        return mDb.query(
               FlightContract.FlightEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                FlightContract.FlightEntry.COLUMN_DESIGNATOR
        );
    }


    // returns a cursor with the selected depart and arrive terms
    private Cursor getQueriedFlights(String depart, String arrive) {

        String[] columns = {
                FlightContract.FlightEntry._ID
        };

        String selection = FlightContract.FlightEntry.COLUMN_DEPART + " = ? AND "
                + FlightContract.FlightEntry.COLUMN_ARRIVE + " = ?";

        String[] selectArg = {
                depart,
                arrive
        };

        return mDb.query(
                FlightContract.FlightEntry.TABLE_NAME,
                columns,
                selection,
                selectArg,
                null,
                null,
                null
        );
    }

    // return the cursor queried flights with the capacity >= ticketnum
    private Cursor getTicketNumBasedQuery(String ticketNum, String depart, String arrive){


        String selection = FlightContract.FlightEntry.COLUMN_DEPART + " = ? AND "
                + FlightContract.FlightEntry.COLUMN_ARRIVE + " = ? AND "
                + FlightContract.FlightEntry.COLUMN_CAPACITY + " >= ?";

        String[] selectArg = {
                depart,
                arrive,
                ticketNum
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

    // returns true if cursor is empty
    private boolean isCursorEmpty( Cursor cursor){

        int cursorCount = cursor.getCount();

        if(cursorCount<=0){
            return true;
        }

        return false;
    }

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

    // start book confirmation intent
    // bring information from id over to that intent
    private void startIntentUsingRowID(int id){

        Cursor cursor = getRowData(id);

        // IMPORTANT !!!!
        // otherwise:
        // android.database.CursorIndexOutOfBoundsException: Index -1 requested, with a size of 1
        // TODO keep this note!
//        if (cursor != null)
//            cursor.moveToPosition(id);

        // IMPORTANT !!!!!
        // since cursor returns only one row, that row id is considered as 1
        // so movingToPosition(id) where id > 1 is invalid !!!!
        if(cursor != null){
            cursor.moveToFirst();

            String depart = cursor.getString(cursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_DEPART));
            String arrive = cursor.getString(cursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_ARRIVE));
            String designator = cursor.getString(cursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_DESIGNATOR));
            int capacity = cursor.getInt(cursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_CAPACITY));
            String takeoff = cursor.getString(cursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_TAKEOFF_TIME));
            double price = cursor.getDouble(cursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_PRICE));

            Log.d(LOG_TAG, "startIntentUsingRowID price: "+ String.valueOf(price) + " takeoff "+ takeoff );

            String ticketNumSelected = spinner.getSelectedItem().toString();

            // make sure that ticket number does not exceed flight capacity !!!
            if( Integer.parseInt(ticketNumSelected) > capacity) {

                Toast.makeText(getApplicationContext(), "Ticket number more than flight capacity !", Toast.LENGTH_LONG).show();
                // update ui with the same selection as before
                mAdapter.swapCursor(getRowData(id));
                return;
            }

            Intent intent = new Intent(BookFlightActivity.this, ConfirmFlightActivity.class);

            Bundle mBundle = new Bundle();

            mBundle.putString("depart", depart);
            mBundle.putString("arrive", arrive);
            mBundle.putString("designator", designator);
            mBundle.putString("tickets", ticketNumSelected);
            mBundle.putString("takeOff", takeoff);
            mBundle.putString("price", String.valueOf(price));
            mBundle.putString("flight_id", String.valueOf(id));

            intent.putExtras(mBundle);

            startActivityForResult(intent, 200);

        }{
            Toast.makeText(getApplicationContext(), "Something went wrong - BookFlightActivity", Toast.LENGTH_LONG);
            return;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        finish();
    }

}
