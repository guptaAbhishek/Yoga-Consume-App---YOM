package com.example.siddarthshikhar.yogaapp;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends ActionBarActivity implements DayPickerDialog.dayPicked,OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMyLocationButtonClickListener, AdapterView.OnItemClickListener, TimePickerDialog.OnTimeSetListener, GoogleMap.OnCameraChangeListener, PostRequestAsyncTask.RequestDoneTaskListener, CustomErrorDialog.ErrorDialogTaskDone, AppExitDialog.AppExitTaskDone {
    private AddressResultReceiver mResultReceiver;
    ProgressDialog dlg;
    String endtime,name,address,cityName;
    GoogleMap map;
    int x,addressComingFromAutocomplete,typeOfRequest;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    Boolean mAddressRequested,checkForGPS;
    double latitude,longitude;
    MapFragment mapFragment;
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    AutoCompleteTextView mAutocompleteView;
    ArrayList mSelectedItems;
    TextView starttime,sunday,monday,tuesday,wednesday,thursday,friday,saturday;
    final Context c=this;
    final String drawerListTitles[]={"BOOK CLASS","SCHEDULE","MY PROFILE","SHARE APP","FAQ'S","SETTINGS","CONTACT US"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if(android.os.Build.VERSION.SDK_INT>=21)
            window.setStatusBarColor(getResources().getColor(R.color.action_bar_color));

        name="";
        address="";

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DrawerLayout drawer = (DrawerLayout) inflater.inflate(R.layout.decor, null); // "null" is important.

        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        View child = decor.getChildAt(0);
        decor.removeView(child);
        FrameLayout container = (FrameLayout) drawer.findViewById(R.id.container); // This is the container we defined just now.
        container.addView(child);
        drawer.findViewById(R.id.drawer_outer).setPadding(0, getStatusBarHeight(), 0, 0);
        decor.addView(drawer);

        mDrawerLayout=drawer;
        mDrawerList=(ListView)findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new DrawerListAdapter(this, 0, drawerListTitles, getLayoutInflater()));
        DrawerItemClickListener temp=new DrawerItemClickListener();
        temp.c=this;
        temp.currDrawer=mDrawerLayout;
        temp.currDrawerList=(LinearLayout)findViewById(R.id.drawer_outer);
        temp.compare=0;
        mDrawerList.setOnItemClickListener(temp);
        mDrawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.abc_action_bar_home_description,R.string.abc_action_bar_home_description);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mAutocompleteView=(AutoCompleteTextView)findViewById(R.id.enter_address);
        mAutocompleteView.setAdapter(new GooglePlacesAutocompleteAdapter(this, 0, getLayoutInflater()));
        mAutocompleteView.setOnItemClickListener(this);
        mAutocompleteView.setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Los Angeleno Sans.ttf"));
        mAutocompleteView.setHintTextColor(getResources().getColor(R.color.White));

        checkForGPS=false;
        getNameAndAddress();

        enableGPS();

        if(checkForGPS==true){
            mAddressRequested=true;
        }
        else{
            mAddressRequested=false;
        }

        mSelectedItems=new ArrayList();
        endtime="";
        x =0;
        addressComingFromAutocomplete=0;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ActionBar bar=getSupportActionBar();
        bar.setDisplayShowHomeEnabled(false);
        bar.setDisplayShowCustomEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        inflater=LayoutInflater.from(this);
        View v=inflater.inflate(R.layout.actionbarview, null);
        bar.setCustomView(v);
        TextView title=(TextView)v.findViewById(R.id.actbartitle);
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        title.setText("SELECT YOUR LOCATION");
        title.setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Comfortaa-Bold.ttf"));

        mResultReceiver = new AddressResultReceiver(new Handler());
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        buildGoogleApiClient();

        starttime=(TextView)findViewById(R.id.book_class_start_time);
        starttime.setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.drawer_text)).setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.pick_your_days_label)).setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.enter_start_time_label)).setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((Button)findViewById(R.id.address_confirmed)).setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Comfortaa-Bold.ttf"));
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    public void onClick(View v){
        int id=v.getId();
        if(id==R.id.clear_address){
            mAutocompleteView.setText("");
        }
        else if(id==R.id.address_confirmed){
            if(mSelectedItems.size()==0){
                CustomErrorDialog dialog=new CustomErrorDialog();
                dialog.listener=this;
                dialog.dialogString="Please Select atleast 1 day";
                dialog.show(getFragmentManager(), "");
                return;
            }else if(starttime.getText().equals("")){
                CustomErrorDialog dialog=new CustomErrorDialog();
                dialog.listener=this;
                dialog.dialogString="Please Enter Start Time!";
                dialog.show(getFragmentManager(), "");
                return;
            } else if(mAutocompleteView.getText().equals("")){
                CustomErrorDialog dialog=new CustomErrorDialog();
                dialog.listener=this;
                dialog.dialogString="Please Enter Your Address!";
                dialog.show(getFragmentManager(), "");
                return;
            }
            LatLng finalLocation=getLocationFromAddress(mAutocompleteView.getText().toString());
            if(finalLocation==null){
                CustomErrorDialog dialog=new CustomErrorDialog();
                dialog.listener=this;
                dialog.dialogString="Please Enter A Valid Address!";
                dialog.show(getFragmentManager(), "");
                return;
            }
            latitude=finalLocation.latitude;
            longitude=finalLocation.longitude;
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                CustomErrorDialog dialog=new CustomErrorDialog();
                dialog.listener=this;
                dialog.dialogString="No Internet Connection!";
                dialog.show(getFragmentManager(), "");
                return;
            }
            cityName="";
            if (addresses.size() > 0)
               cityName=addresses.get(0).getLocality();
            if(cityName==null || cityName.equals("")){
                CustomErrorDialog dialog=new CustomErrorDialog();
                dialog.listener=this;
                dialog.dialogString="Sorry! We do not provide services in this area!";
                dialog.show(getFragmentManager(), "");
            }else{
                checkIfCityServicesPresent();
            }
        }
        else if(id==R.id.book_class_day_picker){
            DayPickerDialog newFragment=new DayPickerDialog();
            newFragment.listener=this;
            newFragment.temp=mSelectedItems;
            newFragment.show(getFragmentManager(), "DayPicker");
        }
        else if(id==R.id.book_class_start_time){
            CustomTimePickerDialog dialog=new CustomTimePickerDialog(this,this, Calendar.HOUR_OF_DAY,Calendar.MINUTE,true);
            dialog.show();
        }
        else if(id==R.id.initially_day_picker){
            DayPickerDialog newFragment=new DayPickerDialog();
            newFragment.listener=this;
            newFragment.temp=mSelectedItems;
            newFragment.show(getFragmentManager(), "DayPicker");
        }
    }
    public void checkIfCityServicesPresent(){
        dlg = new ProgressDialog(MapsActivity.this);
        dlg.setTitle("Please wait.");
        dlg.setMessage("Processing Request");
        dlg.show();
        typeOfRequest=1;
        JSONObject toBePosted=new JSONObject();
        try {
            toBePosted.put("cityname",cityName);
        } catch (JSONException e) {
        }
        PostRequestAsyncTask puttingTask=new PostRequestAsyncTask();
        puttingTask.toBePosted=toBePosted;
        puttingTask.listener=this;
        puttingTask.execute("http://" + Constants.IP_ADDRESS + "getcityrates");
    }
    @Override
    public void populatedays(ArrayList selectedItems) {
        if(x==0){
            x++;
            View C = findViewById(R.id.replacable_parent_layout);
            ViewGroup parent = (ViewGroup) C.getParent();
            int index = parent.indexOfChild(C);
            parent.removeView(C);
            C = getLayoutInflater().inflate(R.layout.day_picker_to_be_loaded, parent, false);
            parent.addView(C, index);
            sunday=(TextView)C.findViewById(R.id.sunday);
            monday=(TextView)C.findViewById(R.id.monday);
            tuesday=(TextView)C.findViewById(R.id.tuesday);
            wednesday=(TextView)C.findViewById(R.id.wednesday);
            thursday=(TextView)C.findViewById(R.id.thursday);
            friday=(TextView)C.findViewById(R.id.friday);
            saturday=(TextView)C.findViewById(R.id.saturday);
            sunday.setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            monday.setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            tuesday.setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            wednesday.setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            thursday.setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            friday.setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            saturday.setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Los Angeleno Sans.ttf"));
        }
        mSelectedItems=new ArrayList();
        this.mSelectedItems=selectedItems;
        sunday.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectanglewhitebackgr));
        monday.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectanglewhitebackgr));
        tuesday.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectanglewhitebackgr));
        wednesday.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectanglewhitebackgr));
        thursday.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectanglewhitebackgr));
        friday.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectanglewhitebackgr));
        saturday.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectanglewhitebackgr));
        for(int i=0;i<selectedItems.size();i++){
            int x=(Integer)selectedItems.get(i);
            switch (x){
                case 0:
                    sunday.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectanglebluebackgr));
                    break;
                case 1:
                    monday.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectanglebluebackgr));
                    break;
                case 2:
                    tuesday.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectanglebluebackgr));
                    break;
                case 3:
                    wednesday.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectanglebluebackgr));
                    break;
                case 4:
                    thursday.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectanglebluebackgr));
                    break;
                case 5:
                    friday.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectanglebluebackgr));
                    break;
                case 6:
                    saturday.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectanglebluebackgr));
                    break;
            }
        }
    }
    public void getNameAndAddress(){
        dlg = new ProgressDialog(MapsActivity.this);
        dlg.setTitle("Please wait.");
        dlg.setMessage("Setting Up Map");
        dlg.show();
        typeOfRequest=0;
        JSONObject toBePosted=new JSONObject();
        try {
            toBePosted.put("phone",getSharedPreferences("profilephone", 0).getString("phone", null));
        } catch (JSONException e) {
        }
        PostRequestAsyncTask puttingTask=new PostRequestAsyncTask();
        puttingTask.toBePosted=toBePosted;
        puttingTask.listener=this;
        puttingTask.execute("http://" + Constants.IP_ADDRESS + "welcomeconsumer");
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String hours="",mins="";
        if(hourOfDay<10)
            hours+="0"+hourOfDay;
        else
            hours+=hourOfDay;
        if(minute<10)
            mins+="0"+minute;
        else
            mins+=minute;
        starttime.setText(hours + ":" + mins + " hrs");
        hours="";
        hourOfDay++;
        if(hourOfDay<10)
            hours+="0"+hourOfDay;
        else
            hours+=hourOfDay;
        endtime=hours + ":" + mins + " hrs";
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }


    public void enableGPS(){
        String provider = Settings.Secure.getString(getContentResolver(),     Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider != null ){
            if(! provider.contains("gps")){
                new AlertDialog.Builder(MapsActivity.this).setMessage("GPS is switched off. enable?").setPositiveButton("Enable GPS", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 5);
                    }
                })
                        .setNegativeButton("Don't do it", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                checkForGPS=false;
                            }
                        })
                        .show();
            }
            else
                checkForGPS=true;
        }
        else
            checkForGPS=false;
        return;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 5 && resultCode == 0){
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if(provider != null){
                switch(provider.length()){
                    case 0:
                        checkForGPS=false;
                        break;
                    default:
                        checkForGPS=true;
                        break;
                }
            }
        }
        else
            checkForGPS=false;
        if(checkForGPS==true){
            mAddressRequested=true;
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }
    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        CameraPosition camPos = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(15).build();
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        googleMap.clear();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude) , 15) );
//        googleMap.animateCamera(camUpd3);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setIndoorEnabled(false);
        UiSettings settings = googleMap.getUiSettings();
        settings.setCompassEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);
        map = googleMap;
        map.setOnCameraChangeListener(this);
    }
    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            if (!Geocoder.isPresent()) {
                Toast.makeText(this,"No Geocoder Available",Toast.LENGTH_LONG).show();
                return;
            }
            if (mAddressRequested){
                latitude=mLastLocation.getLatitude();
                longitude=mLastLocation.getLongitude();
                mapFragment.getMapAsync(this);
                startIntentService();
            }
        }
    }
    @Override
    public boolean onMyLocationButtonClick() {
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
            return false;
        }else{
            mAddressRequested = true;
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            return true;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        addressComingFromAutocomplete=1;
        String str = (String) parent.getItemAtPosition(position);
        mAutocompleteView.setText(str);
        LatLng temp=getLocationFromAddress(str);
        latitude=temp.latitude;
        longitude=temp.longitude;
        mapFragment.getMapAsync(this);
        View vieww = this.getCurrentFocus();
        if (vieww != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(vieww.getWindowToken(), 0);
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        map.clear();
        latitude=cameraPosition.target.latitude;
        longitude=cameraPosition.target.longitude;
        if(mLastLocation==null){
            mLastLocation=new Location("");
        }
        mLastLocation.setLatitude(cameraPosition.target.latitude);
        mLastLocation.setLongitude(cameraPosition.target.longitude);
        mLastLocation.setTime(new Date().getTime());
        if (addressComingFromAutocomplete==0)
            startIntentService();
        addressComingFromAutocomplete=0;
    }

    @Override
    public void processEnquiries(Boolean ifExecuted, String output, int typeOfError) {
        if(typeOfRequest==0){
            if(ifExecuted==true){
                JSONObject obj = null;
                String recievedname="",recievedaddress="";
                try {
                    obj = ((JSONArray)new JSONArray(output)).getJSONObject(0);
                    recievedname=obj.getString("NAME");
                    recievedaddress=obj.getString("ADDRESS");
                } catch (JSONException e) {
                }
                name=recievedname;
                ((TextView)findViewById(R.id.drawer_text)).setText("Welcome " + name);
                if(recievedaddress.equals("null")==false && recievedaddress.equals("")==false){
                    address=recievedaddress;
                    LatLng savedAddressLatLng=getLocationFromAddress(address);
                    if(savedAddressLatLng!=null){
                        latitude=savedAddressLatLng.latitude;
                        longitude=savedAddressLatLng.longitude;
                        mAutocompleteView.setText(address);
                        mAddressRequested=false;
                        addressComingFromAutocomplete=1;
                        mapFragment.getMapAsync(this);
                    }else{
                        latitude=28.6129;
                        longitude=77.2293;
                        mAutocompleteView.setText("India Gate,New Delhi");
                        addressComingFromAutocomplete=1;
                        mapFragment.getMapAsync(this);
                    }
                }else if(checkForGPS==false){
                    latitude=28.6129;
                    longitude=77.2293;
                    mAutocompleteView.setText("India Gate,New Delhi");
                    addressComingFromAutocomplete=1;
                    mapFragment.getMapAsync(this);
                }
            }else{
                CustomErrorDialog dialog=new CustomErrorDialog();
                dialog.listener=this;
                if(typeOfError==1){
                    dialog.dialogString="No Internet Connection!";
                    dialog.show(getFragmentManager(), "");
                }
                else{
                }
            }
        }else{
            if(ifExecuted==true){
                JSONObject obj = null;
                String cityRate="",responseType="";
                try {
                    obj = ((JSONArray)new JSONArray(output)).getJSONObject(0);
                    cityRate=obj.getString("RATE");
                } catch (JSONException e) {
                    responseType="Error";
                }
                if(responseType.equals("")){
                    Intent i=new Intent(c, BookClass.class);
                    i.putExtra("address", mAutocompleteView.getText().toString());
                    i.putExtra("starttime", starttime.getText().toString().substring(0,5));
                    i.putExtra("endtime", endtime.substring(0,5));
                    if(mSelectedItems.contains(0))
                        i.putExtra("sunday",true);
                    if(mSelectedItems.contains(1))
                        i.putExtra("monday",true);
                    if(mSelectedItems.contains(2))
                        i.putExtra("tuesday",true);
                    if(mSelectedItems.contains(3))
                        i.putExtra("wednesday",true);
                    if(mSelectedItems.contains(4))
                        i.putExtra("thursday",true);
                    if(mSelectedItems.contains(5))
                        i.putExtra("friday",true);
                    if(mSelectedItems.contains(6))
                        i.putExtra("saturday",true);
                    int rate=0,curr=0;
                    while(curr<cityRate.length()){
                        rate*=10;
                        rate+=(cityRate.charAt(curr)-'0');
                        curr++;
                    }
                    i.putExtra("cityrate",rate);
                    startActivity(i);
                }else{
                    CustomErrorDialog dialog=new CustomErrorDialog();
                    dialog.listener=this;
                    dialog.dialogString="Sorry! We do not provide services in this area!";
                    dialog.show(getFragmentManager(), "");
                }
            }else{
                CustomErrorDialog dialog=new CustomErrorDialog();
                dialog.listener=this;
                if(typeOfError==1){
                    dialog.dialogString="No Internet Connection!";
                    dialog.show(getFragmentManager(), "");
                } else{
                }
            }
        }
        dlg.dismiss();
    }

    @Override
    public void DialogEnded(String dialogString) {

    }

    @Override
    public void appExitAsked(Boolean ifExiting) {
        if(ifExiting==true)
            super.onBackPressed();
    }


    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            if (resultCode == Constants.SUCCESS_RESULT) {
                mAutocompleteView.setText(mAddressOutput);
            }
        }
    }
    public LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Address location=null;
        int noOfTries=1;
        LatLng p1 = null;
        while(noOfTries<=10){
            try {
                address = coder.getFromLocationName(strAddress, 5);
                if (address == null) {
                    return null;
                }
                location = address.get(0);
                location.getLatitude();
                location.getLongitude();
                p1 = new LatLng(location.getLatitude(), location.getLongitude() );
            } catch (Exception ex) {
                noOfTries++;
            }
            if(location!=null)
                break;
        }
        return p1;
    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
    @Override
    public void onBackPressed() {
        AppExitDialog dialog=new AppExitDialog();
        dialog.listener=this;
        dialog.show(getFragmentManager(), "");
    }
}
