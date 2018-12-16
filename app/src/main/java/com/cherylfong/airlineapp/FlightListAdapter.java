package com.cherylfong.airlineapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class FlightListAdapter extends RecyclerView.Adapter<FlightListAdapter.FlightViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public FlightListAdapter(Context context, Cursor cursor){
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public FlightViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.flight_list_item, parent, false);
        return new FlightViewHolder(view);
    }

    @Override
    public void onBindViewHolder( FlightViewHolder holder, int position ){

        if (!mCursor.moveToPosition(position))
            return; // bail if returned null

        String depart = mCursor.getString(mCursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_DEPART));
        String arrive = mCursor.getString(mCursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_ARRIVE));
        String designator = mCursor.getString(mCursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_DESIGNATOR));
        int capacity = mCursor.getInt(mCursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_CAPACITY));
        String takeoff = mCursor.getString(mCursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_TAKEOFF_TIME));
        double price = mCursor.getDouble(mCursor.getColumnIndex(FlightContract.FlightEntry.COLUMN_PRICE));

        int _id = mCursor.getInt(mCursor.getColumnIndex(FlightContract.FlightEntry._ID));

        holder.departTextView.setText(depart);
        holder.arriveTextView.setText(arrive);
        holder.designatorTextView.setText(designator);
        holder.capacityTextView.setText(String.valueOf(capacity));
        holder.takeoffTextView.setText(takeoff);
        holder.priceTextView.setText("$" + String.valueOf(price));

        holder.itemView.setTag(_id);

    }

    @Override
    public int getItemCount(){
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {

        // check if the current cursor is not null, and close if it is
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();

        // Update the local mCursor to be equal to newCursor
        mCursor = newCursor;

        // Check if the newCursor is not null, otherwise call this.notifyDataSetChanged()
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    interface RecyclerViewClickListener{
        void onClick(View view, int position);
    }

    // id and timestamp is not shown on flight selection list.
    class FlightViewHolder extends RecyclerView.ViewHolder{

        TextView idTextView;
        TextView timestampTextView;

        TextView departTextView;
        TextView arriveTextView;
        TextView designatorTextView;
        TextView capacityTextView;
        TextView takeoffTextView;
        TextView priceTextView;


        public FlightViewHolder(View itemView){
            super(itemView);

            departTextView = (TextView) itemView.findViewById(R.id.depart_textView);
            arriveTextView = (TextView) itemView.findViewById(R.id.arrive_textView);
            designatorTextView = (TextView) itemView.findViewById(R.id.flightCode_textView);
            capacityTextView = (TextView) itemView.findViewById(R.id.capacity_textView);
            takeoffTextView = (TextView) itemView.findViewById(R.id.takeoff_textView);
            priceTextView = (TextView) itemView.findViewById(R.id.price_textView);

        }


    }

}
