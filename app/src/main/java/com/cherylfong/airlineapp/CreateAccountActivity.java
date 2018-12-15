package com.cherylfong.airlineapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateAccountActivity extends AppCompatActivity {

    private AppDBHelper dbHelper;
    private Validation validate = new Validation(this);

    private int errorCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        errorCount = 0;

        findViewById(R.id.create_account_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = ((EditText) findViewById(R.id.create_username_editText)).getText().toString().trim();
                String password = ((EditText) findViewById(R.id.create_password_editText)).getText().toString().trim();

                Log.d("Create Account button", "value of u: " + username + "value of p: " + password);

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


                // check the database if the input is the same with existing usernames
                dbHelper = new AppDBHelper(getApplicationContext());


                if( dbHelper.isUser(username) ){

                    Toast.makeText(getApplicationContext(), "Username already exists!", Toast.LENGTH_LONG).show();
                    errorCount += 1;
                } else {

                    // check if username and password meets constraints
                    if( validate.isMeetConstrains(username) ){
                        Log.d("Validation", "username passes");

                        if(validate.isMeetConstrains(password)){
                            Log.d("Validation", "password passes");

                            // use username and password to create account entry
                            Account c = new Account(username,password);
                            dbHelper.addUser(c);

                            Toast.makeText(getApplicationContext(), "Account Created !", Toast.LENGTH_LONG).show();

                            dbHelper.addLogEntry("new account", "usernm=" + username + "pword=" + password);

                            finish();

                        }else{
                            Toast.makeText(getApplicationContext(), "Password does not meet requirements!", Toast.LENGTH_LONG).show();
                            errorCount += 1;
                        }

                    }else {
                        Toast.makeText(getApplicationContext(), "Username does not meet requirements!", Toast.LENGTH_LONG).show();
                        errorCount += 1;
                    }

                }

                if( errorCount > 1){
                    finish();
                }


            }
        });
    }
}
