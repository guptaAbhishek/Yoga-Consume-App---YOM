package com.example.siddarthshikhar.yogaapp;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Siddarth Shikhar on 6/26/2015.
 */
public class FetchAddressIntentService extends IntentService {
    protected ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super("blah");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
        } catch (IOException ioException) {
            errorMessage = "Service Not Available";
            Log.e("location", errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = "Invalid Latitude and Longitude";
            Log.e("location",errorMessage+". "+"Latitude = "+location.getLatitude()+", Longitude = "+location.getLongitude(), illegalArgumentException);
        }
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "No Address Found";
                Log.e("location", errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                addressFragments.add(address.getAddressLine(i));
            Log.i("location", "Address Found");
            deliverResultToReceiver(Constants.SUCCESS_RESULT,TextUtils.join(System.getProperty("line.separator"),addressFragments));
        }
    }
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}
