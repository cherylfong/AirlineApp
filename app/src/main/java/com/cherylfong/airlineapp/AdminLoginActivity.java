package com.cherylfong.airlineapp;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AdminLoginActivity extends AppCompatActivity {

    private final AppCompatActivity activity = AdminLoginActivity.this;

    // fixed admin username and password
    private final String adminUnP = "!admiM2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);


        findViewById(R.id.admin_login_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String username = ((EditText) findViewById(R.id.admin_username_editText)).getText().toString().trim();
                String password = ((EditText) findViewById(R.id.admin_password_editText)).getText().toString().trim();


                Log.d("AdminLogin button", "value of u: " + username + "value of p: " + password);

                if( username.matches("") && password.matches("")){
                    Toast.makeText(getApplicationContext(), "Edit fields cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (username.matches("")){
                    Toast.makeText(getApplicationContext(), "Input username.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (password.matches("")){
                    Toast.makeText(getApplicationContext(), "Input password", Toast.LENGTH_SHORT).show();
                    return;
                }


                // check if input matches constants
                if( username.equals(adminUnP) && password.equals(adminUnP) ){

                    Intent manageSystemIntent = new Intent(activity, ManageSystemActivity.class);
                    startActivity(manageSystemIntent);
                }


                Snackbar.make(findViewById(R.id.admin_login_title), "Invalid login credentials.", Snackbar.LENGTH_SHORT).show();

            }
        });
    }


}
