package com.cherylfong.airlineapp;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ManageSystemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_system);

        findViewById(R.id.accounts_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ManageSystemActivity.this, ViewAccountsActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.logs_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick (View v){

                Intent intent = new Intent( ManageSystemActivity.this, ViewSystemLogsActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.flights_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick (View v){

                Intent intent = new Intent( ManageSystemActivity.this, ViewFlightsActivity.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.all_reservations_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick (View v){

                Intent intent = new Intent( ManageSystemActivity.this, ViewReservedFlightsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        setResult(RESULT_OK);
        finish();
    }
}
