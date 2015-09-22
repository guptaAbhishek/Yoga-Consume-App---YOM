package com.example.siddarthshikhar.yogaapp;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Siddarth Shikhar on 7/17/2015.
 */
public class PostRequestAsyncTask extends AsyncTask<String,Void,Boolean> {
    public interface RequestDoneTaskListener {
        public void processEnquiries(Boolean ifExecuted,String output,int typeOfError);
    }
    JSONObject toBePosted;
    RequestDoneTaskListener listener;
    int typeOfError;
    String output,urlToBePosted;
    @Override
    protected void onPostExecute(Boolean ifExecuted) {
        listener.processEnquiries(ifExecuted,output,typeOfError);
    }
    @Override
    protected Boolean doInBackground(String... params) {

        typeOfError=0;
        output="";
        urlToBePosted = params[0];

        URL url = null;
        try {
            url = new URL(urlToBePosted);
        } catch (MalformedURLException e) {
            typeOfError=2;
            return false;
        }

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            typeOfError=1;
            return false;
        }

        if (urlConnection == null) {
            typeOfError=1;
            return false;
        }

        try {
            urlConnection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            typeOfError=1;
            return false;
        }

        urlConnection.setRequestProperty("Content-Type", "application/json");

        try {
            urlConnection.connect();
        } catch (IOException e) {
            typeOfError=1;
            return false;
        }


        InputStream inputStream = null;
        try {
            urlConnection.getOutputStream().write(toBePosted.toString().getBytes());
            inputStream = urlConnection.getInputStream();
        } catch (IOException e) {
            typeOfError=2;
            return false;
        }

        if (inputStream == null) {
            typeOfError=2;
            return false;
        }

        StringBuffer buffer = new StringBuffer();
        String temp =   "";
        Scanner s = new Scanner(inputStream);
        while (s.hasNext()) {
            buffer.append(s.nextLine());
        }
        output=buffer.toString();
        return true;
    }
}
