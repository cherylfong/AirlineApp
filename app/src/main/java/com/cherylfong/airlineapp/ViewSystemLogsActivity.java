package com.cherylfong.airlineapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

public class ViewSystemLogsActivity extends AppCompatActivity {

    private SystemLogsAdapter mAdapter;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_system_logs);

        RecyclerView systemLogsRecyclerView;

        systemLogsRecyclerView = (RecyclerView) this.findViewById(R.id.all_system_logs_view);

        systemLogsRecyclerView.setLayoutManager((new LinearLayoutManager(this)));

        AppDBHelper dbHelper = new AppDBHelper(this);

        mDb = dbHelper.getWritableDatabase();

        Cursor cursor = getAllLogs();

        mAdapter = new SystemLogsAdapter(this, cursor);

        systemLogsRecyclerView.setAdapter(mAdapter);

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

                Log.d("ItemTouchHelper SYSTEM", "value of id: " + id);

                //remove from DB
                removeLog(id);


                //update the list
                mAdapter.swapCursor(getAllLogs());
            }

            // attach the ItemTouchHelper to the RecyclerView
        }).attachToRecyclerView(systemLogsRecyclerView);

    }

    private Cursor getAllLogs() {
        return mDb.query(
                SystemLogsContract.LogEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                SystemLogsContract.LogEntry.COLUMN_TIMESTAMP
        );
    }

    private boolean removeLog(long id) {

        return mDb.delete(SystemLogsContract.LogEntry.TABLE_NAME, SystemLogsContract.LogEntry._ID + "=" + id, null) > 0;
    }

}
