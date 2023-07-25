package com.example.compraeintercambia.otrhers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.compraeintercambia.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckInternetConection {

    private Context context;

    public CheckInternetConection(Context context) {
        this.context = context;
    }

    public boolean isNetworkAvailable(){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());

    }

   /* public boolean hasInternetConnection() {


        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL("https://www.google.com").openConnection();
            urlConnection.setRequestProperty("User-agent", "Android");
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setConnectTimeout(1500);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                return true;
            }
        } catch (Exception e) {
            Log.i("COMPRA E INTERCAMBIA", "No network available", e);
        }

        return false;

    }*/



}
