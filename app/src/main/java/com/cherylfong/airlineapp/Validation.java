package com.cherylfong.airlineapp;

import android.content.Context;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    private Context context;

    public Validation(Context context){
        this.context = context;
    }


    /**
     * to check if username or password meets the following constrains,
     * with at least:
     *
     *  1. 1 special symbol: ! @ # $ , and
     *  2. one number and,
     *  3. one uppercase alphabet and,
     *  4. one lowercase alphabet
     *
     */
    public boolean isMeetConstrains(String input){


        Matcher m = Pattern.compile(".*[!@#$].*").matcher(input);
        Matcher m1 = Pattern.compile(".*[a-z]+.*").matcher(input);
        Matcher m2 = Pattern.compile(".*[A-Z]+.").matcher(input);
        Matcher m3 = Pattern.compile(".*[0-9].*").matcher(input);

        Log.d("isMeetConstrains", "input is: " + input);

        if(m.find()){

            Log.d("isMeetConstrains", "pass special symbol");

            if(m1.find()){

                Log.d("isMeetConstrains", "pass lowercase alphabets");

                if(m2.find()){

                    Log.d("isMeetConstrains", "pass upper alphabets");

                    if(m3.find()){
                        Log.d("isMeetConstrains", "pass numbers");
                        return true;
                    }
                }
            }
        }

        Log.d("isMeetConstrains", "FAILS");
        return false;

    }
}
