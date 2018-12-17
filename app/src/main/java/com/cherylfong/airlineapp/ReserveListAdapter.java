package com.cherylfong.airlineapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReserveListAdapter extends RecyclerView.Adapter<ReserveListAdapter.ReserveViewHolder>{

    private Cursor mCursor;
    private Context mContext;

    public ReserveListAdapter(Context context, Cursor cursor){
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public ReserveViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.reserve_list_item, parent, false);
        return new ReserveViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ReserveViewHolder holder, int position){

        if( !mCursor.moveToPosition(position))
            return;

        String timestamp = mCursor.getString(mCursor.getColumnIndex(ReserveContract.ReserveEntry.COLUMN_TIMESTAMP));
        String depart = mCursor.getString(mCursor.getColumnIndex(ReserveContract.ReserveEntry.COLUMN_DEPART));
        String arrive = mCursor.getString(mCursor.getColumnIndex(ReserveContract.ReserveEntry.COLUMN_ARRIVE));
        String designator = mCursor.getString(mCursor.getColumnIndex(ReserveContract.ReserveEntry.COLUMN_DESIGNATOR));
        int ticketNum = mCursor.getInt(mCursor.getColumnIndex(ReserveContract.ReserveEntry.COLUMN_TICKETS));
        String takeOff = mCursor.getString(mCursor.getColumnIndex(ReserveContract.ReserveEntry.COLUMN_TAKEOFF_TIME));
        double price = mCursor.getDouble(mCursor.getColumnIndex(ReserveContract.ReserveEntry.COLUMN_PRICE));

        int _id = mCursor.getInt(mCursor.getColumnIndex(ReserveContract.ReserveEntry._ID));

        holder.timestampTextView.setText(timestamp);
        holder.departTextView.setText(depart);
        holder.arriveTextView.setText(arrive);
        holder.designatorTextView.setText(designator);
        holder.ticketNumTextView.setText(String.valueOf(ticketNum));
        holder.takeoffTextView.setText(takeOff);
        holder.priceTextView.setText("$" + String.valueOf(price) +
                " * " + String.valueOf(ticketNum) +
                " = " + String.valueOf((double)ticketNum*price));

        holder.itemView.setTag(_id);
    }

    @Override
    public int getItemCount() {return mCursor.getCount();}

    public void swapCursor(Cursor newCursor){

        if(mCursor != null) mCursor.close();

        mCursor = newCursor;

        if(newCursor != null){
            this.notifyDataSetChanged();
        }

    }

    class ReserveViewHolder extends RecyclerView.ViewHolder {

        TextView timestampTextView;

        TextView departTextView;
        TextView arriveTextView;
        TextView designatorTextView;
        TextView ticketNumTextView;
        TextView takeoffTextView;
        TextView priceTextView;

        // TODO add username to tile of reservations activity

        public ReserveViewHolder(View itemView){

            super(itemView);

            timestampTextView = itemView.findViewById(R.id.reserved_timeAt_textView);

            departTextView = itemView.findViewById(R.id.reserved_depart_textView);
            arriveTextView  = itemView.findViewById(R.id.reserved_arrive_textView);
            designatorTextView = itemView.findViewById(R.id.reserved_flightCode_textView);
            ticketNumTextView = itemView.findViewById(R.id.reserved_ticketNum_textView);
            takeoffTextView  = itemView.findViewById(R.id.reserved_takeoff_textView);
            priceTextView = itemView.findViewById(R.id.reserved_total_price_textView);

        }
    }

}
