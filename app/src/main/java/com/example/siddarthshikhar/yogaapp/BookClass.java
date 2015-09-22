package com.example.siddarthshikhar.yogaapp;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class BookClass extends ActionBarActivity implements CustomErrorDialog.ErrorDialogTaskDone, PostRequestAsyncTask.RequestDoneTaskListener {
    EditText editableAdd;
    TextView finalAdd;
    String startTime,endTime;
    ArrayList mSelectedItems;
    ProgressDialog dlg;
    TextView totalCost,noOfClasses,daysSelected;
    Intent intentToNextActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_class);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if(android.os.Build.VERSION.SDK_INT>=21)
            window.setStatusBarColor(getResources().getColor(R.color.action_bar_color));

        intentToNextActivity=new Intent(this,EnterPromoCodeActivity.class);

        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowCustomEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater=LayoutInflater.from(this);
        View v=inflater.inflate(R.layout.actionbarview, null);
        bar.setCustomView(v);
        TextView title=(TextView)v.findViewById(R.id.actbartitle);
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        title.setText("CLASS DETAILS");
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));

        editableAdd=(EditText)findViewById(R.id.editable_address);
        editableAdd.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        finalAdd=(TextView)findViewById(R.id.final_address);
        finalAdd.setText(getIntent().getExtras().getString("address"));
        finalAdd.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));

        String toBeDisplayed="";
        mSelectedItems=new ArrayList();
        if(getIntent().getExtras().getBoolean("sunday")==true){
            mSelectedItems.add(0);
            toBeDisplayed+="Sun";
        }
        if(getIntent().getExtras().getBoolean("monday")==true){
            mSelectedItems.add(1);
            if(toBeDisplayed.equals(""))
                toBeDisplayed+="Mon";
            else
                toBeDisplayed+=",Mon";
        }
        if(getIntent().getExtras().getBoolean("tuesday")==true){
            mSelectedItems.add(2);
            if(toBeDisplayed.equals(""))
                toBeDisplayed+="Tue";
            else
                toBeDisplayed+=",Tue";
        }
        if(getIntent().getExtras().getBoolean("wednesday")==true){
            mSelectedItems.add(3);
            if(toBeDisplayed.equals(""))
                toBeDisplayed+="Wed";
            else
                toBeDisplayed+=",Wed";
        }
        if(getIntent().getExtras().getBoolean("thursday")==true){
            mSelectedItems.add(4);
            if(toBeDisplayed.equals(""))
                toBeDisplayed+="Thu";
            else
                toBeDisplayed+=",Thu";
        }
        if(getIntent().getExtras().getBoolean("friday")==true){
            mSelectedItems.add(5);
            if(toBeDisplayed.equals(""))
                toBeDisplayed+="Fri";
            else
                toBeDisplayed+=",Fri";
        }
        if(getIntent().getExtras().getBoolean("saturday")==true){
            mSelectedItems.add(6);
            if(toBeDisplayed.equals(""))
                toBeDisplayed+="Sat";
            else
                toBeDisplayed+=",Sat";
        }
        ((TextView)findViewById(R.id.cost_per_class_value)).setText("Rs "+getIntent().getExtras().getInt("cityrate")+"/-");
        intentToNextActivity.putExtra("cityrate",getIntent().getExtras().getInt("cityrate"));

        totalCost=(TextView)findViewById(R.id.book_class_total_cost);
        totalCost.setText("Rs "+mSelectedItems.size()*4*getIntent().getExtras().getInt("cityrate")+"/-");
        totalCost.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));

        noOfClasses=(TextView)findViewById(R.id.book_class_no_of_classes);
        noOfClasses.setText("" + mSelectedItems.size() * 4);
        noOfClasses.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));

        startTime=getIntent().getExtras().getString("starttime");
        endTime=getIntent().getExtras().getString("endtime");

        daysSelected=(TextView)findViewById(R.id.book_class_days_selected);
        daysSelected.setText(toBeDisplayed+" at "+startTime+" hrs");
        daysSelected.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));

        ((TextView)findViewById(R.id.address_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.timings_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.no_of_classes_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.cost_per_class_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.total_cost_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.cost_per_class_value)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((Button)findViewById(R.id.book_class_confirm)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));

    }
    public void onClick(View v){
        int id=v.getId();
        if(id==R.id.book_class_confirm){
            putInDatabase();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void putInDatabase(){
        LatLng locationOfAddress=getLocationFromAddress(finalAdd.getText().toString());
        CustomErrorDialog dialog=new CustomErrorDialog();
        dialog.listener=this;
        if(locationOfAddress==null) {
            dialog.dialogString = "Please Enter A Valid Address seperated by proper punctuation";
            dialog.show(getFragmentManager(), "");
            return;
        }
        Date currDate=new Date(System.currentTimeMillis());
        long timeAfter28Days=System.currentTimeMillis()/1000;
        timeAfter28Days+=28*24*60*60;
        Date dateAfter28Days=new Date(timeAfter28Days*1000);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd",Locale.US);
        String daysSelected="";
        for(int i=1;i<=6;i++){
            if(mSelectedItems.contains(i)){
                daysSelected+="Y";
            }else
                daysSelected+="N";
            if(i==0)
                break;
            else if(i==6)
                i=-1;
        }
        intentToNextActivity.putExtra("venue",editableAdd.getText().toString()+", "+finalAdd.getText().toString());
        intentToNextActivity.putExtra("venue_lat",""+locationOfAddress.latitude);
        intentToNextActivity.putExtra("venue_long",""+locationOfAddress.longitude);
        intentToNextActivity.putExtra("start_date",format.format(currDate));
        intentToNextActivity.putExtra("end_date",format.format(dateAfter28Days));
        intentToNextActivity.putExtra("start_time",startTime+":00");
        intentToNextActivity.putExtra("end_time",endTime+":00");
        intentToNextActivity.putExtra("days",daysSelected);
        intentToNextActivity.putExtra("noOfClasses",mSelectedItems.size());
        startActivity(intentToNextActivity);
        finish();
    }
    public LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );
        } catch (Exception ex) {
        }
        return p1;
    }
    @Override
    public void DialogEnded(String dialogString) {
        if(dialogString.equals("We are experiencing some Technical difficulties.Please contact us.")){
            Intent mainIntent = new Intent(BookClass.this, ContactUsActivity.class);
            BookClass.this.startActivity(mainIntent);
            BookClass.this.finish();
        }
    }

    @Override
    public void processEnquiries(Boolean ifExecuted, String output, int typeOfError) {
        dlg.dismiss();
        if(ifExecuted==true){
            JSONObject obj = null;
            String responseType="",responseMessage="";
            try {
                obj = new JSONObject(output);
                responseType=obj.getString("Response_Type");
                responseMessage=obj.getString("Response_Message");
            } catch (JSONException e) {
            }
            if(responseType.equals("Success")){
                Intent mainIntent = new Intent(BookClass.this, BookingConfirmation.class);
                BookClass.this.startActivity(mainIntent);
                BookClass.this.finish();
            }else{
                CustomErrorDialog dialog=new CustomErrorDialog();
                dialog.listener=this;
                dialog.dialogString="We are experiencing some Technical difficulties.Please contact us";
                dialog.show(getFragmentManager(), "");
            }
        }else{
            CustomErrorDialog dialog=new CustomErrorDialog();
            dialog.listener=this;
            if(typeOfError==1){
                dialog.dialogString="No Internet Connection!";
                dialog.show(getFragmentManager(), "");
            }
            else{
                dialog.dialogString="Oops! Server Error! Please try after some time";
                dialog.show(getFragmentManager(),"");
            }
        }
    }
}
