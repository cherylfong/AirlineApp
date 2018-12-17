package com.cherylfong.airlineapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmFlightActivity extends AppCompatActivity {

    private AppDBHelper dbHelper;
    private Validation validate = new Validation(this);

    private final static String LOG_TAG = ConfirmFlightActivity.class.getSimpleName();

    private String depart;
    private String arrive;
    private String designator;
    private String tickets;
    private String takeOff;
    private String price;
    private String username;
    private String flight_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_flight);

        findViewById(R.id.login_confirm_reservation_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = ((EditText) findViewById(R.id.login_username_edit_text)).getText().toString().trim();
                String password = ((EditText) findViewById(R.id.login_password_edit_text)).getText().toString().trim();

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
                    Toast.makeText(getApplicationContext(), "Input password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // check the database if the input is the same with existing usernames
                dbHelper = new AppDBHelper(getApplicationContext());

                if( !dbHelper.isUser(username) ){

                    Toast.makeText(getApplicationContext(), "Invalid username!", Toast.LENGTH_LONG).show();
                    return;

                } else {

                    if( !dbHelper.isCorrectUserPass(username,password)){

                        Toast.makeText(getApplicationContext(), "Invalid password!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();

                    depart = getIntent().getExtras().getString("depart");
                    arrive = getIntent().getExtras().getString("arrive");
                    designator = getIntent().getExtras().getString("designator");
                    tickets = getIntent().getExtras().getString("tickets");
                    takeOff = getIntent().getExtras().getString("takeOff");
                    price = getIntent().getExtras().getString("price");
                    flight_id = getIntent().getExtras().getString("flight_id");

//                    Log.d(LOG_TAG, "depart=" + depart + " arrive=" + arrive);

                    int t = Integer.parseInt(tickets);
                    double p = Double.parseDouble(price);

                    TextView user = findViewById(R.id.info_username_textView);
                    user.setText("Logged in as: " + username);
                    user.setVisibility(View.VISIBLE);

                    TextView dep = findViewById(R.id.info_depart_textView);
                    dep.setText("Depart from: " + depart);
                    dep.setVisibility(View.VISIBLE);

                    TextView arr = findViewById(R.id.info_arrive_textView);
                    arr.setText("Arrive at: " + arrive);
                    arr.setVisibility(View.VISIBLE);

                    TextView desig = findViewById(R.id.info_flightCode_textView);
                    desig.setText("Flight number: " + designator);
                    desig.setVisibility(View.VISIBLE);

                    TextView tiks = findViewById(R.id.info_ticketNum_textView);
                    tiks.setText("Number of tickets: " + tickets);
                    tiks.setVisibility(View.VISIBLE);

                    TextView takeOTime = findViewById(R.id.info_takeoff_textView);
                    takeOTime.setText("Boarding Time: "+ takeOff);
                    takeOTime.setVisibility(View.VISIBLE);

                    TextView totalPrice = findViewById(R.id.info_totalPrice_textView);
                    totalPrice.setText("Total Price: " + tickets + "*" + price + "= $" + String.valueOf(p*t));
                    totalPrice.setVisibility(View.VISIBLE);

                    findViewById(R.id.to_confirm_reservation_button).setVisibility(View.VISIBLE);
                }

            }
        });

        findViewById(R.id.to_confirm_reservation_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check the database if the input is the same with existing usernames
               AppDBHelper mdbHelper = new AppDBHelper(getApplicationContext());

                // add to reservations tables
                mdbHelper.addReservation(username, depart, arrive, designator, tickets, takeOff, price);

               Toast.makeText(getApplicationContext(), "Flight reservation saved!", Toast.LENGTH_LONG);

               // add entry to log
               mdbHelper.addLogEntry("new reservation", "usrnm=" + username + " depart="
               + depart + " arrive= " + arrive + " flightCode= " + designator + " ticketNum=" + tickets
               + " takeoffAt=" + takeOff + " totalPrice" + String.valueOf(Integer.parseInt(tickets)
               *Double.parseDouble(price)));

//               Log.d(LOG_TAG + "test", "arrive " + arrive);

                // update flight capacity
                mdbHelper.updateFlightCapacity(Integer.parseInt(flight_id), true, Integer.parseInt(tickets) );


               // go back to main menu
               // however, stops the database thread immediately !!!
//               Intent intent = new Intent(ConfirmFlightActivity.this, MainActivity.class);
//               startActivity(intent);

                // IMPORTANT, otherwise onActivityResult will not be called
                setResult(RESULT_OK);
                finish();


            }
        });
    }

}
