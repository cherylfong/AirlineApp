package com.cherylfong.airlineapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.AccountViewHolder> {

    // Holds on to the cursor to display the waitlist
    private Cursor mCursor;
    private Context mContext;

    /**
     * Constructor using the context and the db cursor
     * @param context the calling context/activity
     * @param cursor the db cursor with accounts data to display
     */
    public AccountListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.account_list_item, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder( AccountViewHolder holder, int position){

        // Move the mCursor to the position of the item to be displayed
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null

        // Update the view holder with the information needed to display
        String username = mCursor.getString(mCursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_USERNAME));
        String password = mCursor.getString(mCursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_PASSWORD));
        String timestamp = mCursor.getString(mCursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_TIMESTAMP));
        int _id = mCursor.getInt(mCursor.getColumnIndex(AccountContract.AccountEntry._ID));

        // Display the variables above
        holder.usernameTextView.setText(username);
        holder.passwordTextView.setText(password);
        holder.timestampTextView.setText(timestamp);
        holder.idTextView.setText(String.valueOf(_id));

        // set the tag of the itemview to match id of entry
        holder.itemView.setTag(_id);
    }

    @Override
    public int getItemCount(){
        return mCursor.getCount();
    }


    /**
     * Swaps the Cursor currently held in the adapter with a new one
     * and triggers a UI refresh
     *
     * @param newCursor the new cursor that will replace the existing one
     */
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

    /**
     * Inner class to hold the views needed to display a single item in the recycler-view
     */
    class AccountViewHolder extends RecyclerView.ViewHolder {

        // Will display the following variables
        TextView usernameTextView;
        TextView passwordTextView;
        TextView idTextView;
        TextView timestampTextView;

        /**
         * Constructor for ViewHolder. Within this constructor, get a reference to
         * TextViews
         *
         * @param itemView The View that was inflated in
         *                 {@link AccountListAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public AccountViewHolder(View itemView) {
            super(itemView);

            // within account_list_item.xml

            usernameTextView = (TextView) itemView.findViewById(R.id.username_text_view);
            passwordTextView = (TextView) itemView.findViewById(R.id.password_text_view);
            timestampTextView = (TextView) itemView.findViewById(R.id.timestamp_text_view);
            idTextView = (TextView) itemView.findViewById(R.id.account_id_text_view);
        }

    }

}
