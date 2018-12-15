package com.cherylfong.airlineapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SystemLogsAdapter extends RecyclerView.Adapter<SystemLogsAdapter.SystemLogsViewHolder>{


    private Cursor mCursor;
    private Context mContext;

    public SystemLogsAdapter(Context context, Cursor cursor){
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public SystemLogsViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.system_logs_item, parent, false);
        return new SystemLogsAdapter.SystemLogsViewHolder(view);
    }

    @Override
    public void onBindViewHolder( SystemLogsViewHolder holder, int position ){

        if(!mCursor.moveToPosition(position)){
            return;
        }

        String type = mCursor.getString(mCursor.getColumnIndex(SystemLogsContract.LogEntry.COLUMN_TYPE));
        String details = mCursor.getString(mCursor.getColumnIndex(SystemLogsContract.LogEntry.COLUMN_DETAILS));
        String timestamp = mCursor.getString(mCursor.getColumnIndex(SystemLogsContract.LogEntry.COLUMN_TIMESTAMP));
        int _id = mCursor.getInt(mCursor.getColumnIndex(SystemLogsContract.LogEntry._ID));

        holder.typeTextView.setText(type);
        holder.timestampTextView.setText(timestamp);
        holder.detailsTextView.setText(details);
        holder.idTextView.setText(String.valueOf(_id));

        holder.itemView.setTag(_id);
    }

    @Override
    public int getItemCount(){ return mCursor.getCount(); }

    public void swapCursor(Cursor newCursor){

        if(mCursor != null) mCursor.close();

        mCursor = newCursor;

        if(newCursor != null) {
            this.notifyDataSetChanged();
        }
    }

    class SystemLogsViewHolder extends RecyclerView.ViewHolder {

        // Will display the following variables
        TextView typeTextView;
        TextView detailsTextView;
        TextView idTextView;
        TextView timestampTextView;

        public SystemLogsViewHolder(View itemView)
        {
            super(itemView);

            typeTextView = (TextView) itemView.findViewById(R.id.system_logs_type_textView);
            detailsTextView = (TextView) itemView.findViewById(R.id.system_logs_details_textView);
            idTextView = (TextView) itemView.findViewById(R.id.system_logs_id_textView);
            timestampTextView = (TextView) itemView.findViewById(R.id.system_logs_timestamp_textView);


        }
    }
}
