package com.cherylfong.airlineapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ManageSystemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_system);

// TODO (5) intent code change

        findViewById(R.id.accounts_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ManageSystemActivity.this, ViewAccountsActivity.class);
                startActivityForResult(intent, 100);

            }
        });
    }
}