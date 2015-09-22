package com.example.siddarthshikhar.yogaapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EditProfileActivity extends ActionBarActivity implements setDateFragment.datePicked, GenderPickerDialog.GenderDialogTaskDone, AdapterView.OnItemClickListener, PostRequestAsyncTask.RequestDoneTaskListener, CustomErrorDialog.ErrorDialogTaskDone {

    SharedPreferences sp;
    int year,month,day;
    SharedPreferences.Editor editor;
    MaterialAutoCompleteTextView address;
    MaterialEditText age,gender;
    MaterialEditText name,height,weight,phone,email;
    ProgressDialog dlg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if(android.os.Build.VERSION.SDK_INT>=21)
            window.setStatusBarColor(getResources().getColor(R.color.action_bar_color));

        ActionBar bar=getSupportActionBar();
        bar.setDisplayShowCustomEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater=LayoutInflater.from(this);
        View v=inflater.inflate(R.layout.actionbarview, null);
        bar.setCustomView(v);
        TextView title=(TextView)v.findViewById(R.id.actbartitle);
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        title.setText("EDIT PROFILE");
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));

        name=(MaterialEditText)findViewById(R.id.edit_profile_name);
        name.setText(getSharedPreferences("profilename", 0).getString("name", null));
        age=(MaterialEditText)findViewById(R.id.edit_profile_age);
        age.setText(getSharedPreferences("profileage", 0).getString("age", null));
        age.setKeyListener(null);
        height=(MaterialEditText)findViewById(R.id.edit_profile_height);
        height.setText(getSharedPreferences("profileheight", 0).getString("height", null));
        weight=(MaterialEditText)findViewById(R.id.edit_profile_weight);
        weight.setText(getSharedPreferences("profileweight",0).getString("weight",null));
        phone=(MaterialEditText)findViewById(R.id.edit_profile_phone);
        phone.setText(getSharedPreferences("profilephone", 0).getString("phone", null));
        phone.setKeyListener(null);
        email=(MaterialEditText)findViewById(R.id.edit_profile_email);
        email.setText(getSharedPreferences("profileemail", 0).getString("email", null));
        address=(MaterialAutoCompleteTextView) findViewById(R.id.edit_profile_address);
        address.setText(getSharedPreferences("profileaddress", 0).getString("address", null));
        address.setAdapter(new GooglePlacesAutocompleteAdapter(this, 0, getLayoutInflater()));
        address.setOnItemClickListener(this);

        gender=(MaterialEditText)findViewById(R.id.edit_profile_gender);
        gender.setKeyListener(null);

        year = 1980;
        month = 0;
        day = 1;

        age.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    age.clearFocus();
                    DialogFragment newFragment = new setDateFragment();
                    setDateFragment temp = (setDateFragment) newFragment;
                    temp.listener = EditProfileActivity.this;
                    temp.presetDay = day;
                    temp.presetMonth = month;
                    temp.presetYear = year;
                    newFragment.show(getFragmentManager(), "DatePicker");
                }
                return false;
            }
        });
        gender.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    gender.clearFocus();
                    GenderPickerDialog dialog = new GenderPickerDialog();
                    dialog.listener = EditProfileActivity.this;
                    dialog.show(getFragmentManager(), "");
                }
                return false;
            }
        });

        if(getSharedPreferences("profilegender", 0).getString("gender", null).equals("Male"))
            gender.setText("Male");
        else if(getSharedPreferences("profilegender", 0).getString("gender", null).equals("Female"))
            gender.setText("Female");



        ((MaterialEditText) findViewById(R.id.edit_profile_name)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText) findViewById(R.id.edit_profile_name)).setAccentTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText) findViewById(R.id.edit_profile_age)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText) findViewById(R.id.edit_profile_age)).setAccentTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText) findViewById(R.id.edit_profile_gender)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText) findViewById(R.id.edit_profile_gender)).setAccentTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText) findViewById(R.id.edit_profile_height)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText) findViewById(R.id.edit_profile_height)).setAccentTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText) findViewById(R.id.edit_profile_weight)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText) findViewById(R.id.edit_profile_weight)).setAccentTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText) findViewById(R.id.edit_profile_phone)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText) findViewById(R.id.edit_profile_phone)).setAccentTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText) findViewById(R.id.edit_profile_email)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText) findViewById(R.id.edit_profile_email)).setAccentTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialAutoCompleteTextView) findViewById(R.id.edit_profile_address)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialAutoCompleteTextView) findViewById(R.id.edit_profile_address)).setAccentTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.personal_info_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.contact_info_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.cm_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.kgs_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((Button)findViewById(R.id.edit_profile_save)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));
        ((Button)findViewById(R.id.edit_profile_discard)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));
    }
    public void populateSetDate(int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++;
        this.year=year;
        month=monthOfYear;
        day=dayOfMonth;
        age.setText(year + "/" + monthOfYear + "/" + dayOfMonth);
    }
    public void onClick(View v){
        int id=v.getId();
        if(id==R.id.edit_profile_save){
            Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
            Matcher m = p.matcher(email.getText().toString());
            CustomErrorDialog dialog=new CustomErrorDialog();
            int finheight=0,finweight=0;
            if(height.getText().toString().equals("")==false)
                finheight=Integer.parseInt(height.getText().toString());
            if(weight.getText().toString().equals("")==false)
                finweight=Integer.parseInt(weight.getText().toString());
            dialog.listener=this;
            if(m.matches()==false){
                dialog.dialogString="Please Enter Valid Email-ID";
                dialog.show(getFragmentManager(),"");
                return;
            }else if(name.getText().toString().length()==0){
                dialog.dialogString="Please Enter Your Name";
                dialog.show(getFragmentManager(),"");
                return;
            }else if(finheight>280){
                dialog.dialogString="The World's tallest man is shorter than you! Enter a Valid height";
                dialog.show(getFragmentManager(),"");
                return;
            }else if(finweight > 600){
                dialog.dialogString="The World's heaviest man is lighter than you! Enter a Valid weight";
                dialog.show(getFragmentManager(),"");
                return;
            }else if(address.getText().toString().equals("")==false && getLocationFromAddress(address.getText().toString())==null){
                dialog.dialogString="Enter Valid Address";
                dialog.show(getFragmentManager(),"");
                return;
            }
            dlg = new ProgressDialog(EditProfileActivity.this);
            dlg.setTitle("Please wait.");
            dlg.setMessage("Editing Profile...");
            dlg.show();
            JSONObject toBePosted=new JSONObject();
            try {
                toBePosted.put("name", name.getText().toString());
                toBePosted.put("phone",phone.getText().toString());
                toBePosted.put("email",email.getText().toString());
                if(age.getText().toString().equals("")==false)
                    toBePosted.put("bdate",age.getText().toString());
                else
                    toBePosted.put("bdate",null);
                toBePosted.put("height",height.getText().toString());
                toBePosted.put("weight",weight.getText().toString());
                toBePosted.put("address",address.getText().toString());
                toBePosted.put("gender",gender.getText().toString());
                toBePosted.put("authKey",getSharedPreferences("Authorization",0).getString("Auth_key", null));
            } catch (JSONException e) {
            }
            PostRequestAsyncTask puttingTask=new PostRequestAsyncTask();
            puttingTask.toBePosted=toBePosted;
            puttingTask.listener=this;
            puttingTask.execute("http://"+Constants.IP_ADDRESS+"updateconsumer");
        }
        else if(id==R.id.edit_profile_discard){
            finish();
        }
    }

    @Override
    public void genderDialogEnded(String genderPicked) {
        gender.setText(genderPicked);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String str = (String) parent.getItemAtPosition(position);
        address.setText(str);
        View vieww = this.getCurrentFocus();
        if (vieww != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(vieww.getWindowToken(), 0);
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
                finish();
            }else{
                CustomErrorDialog dialog=new CustomErrorDialog();
                dialog.listener=this;
                dialog.dialogString="We Are Unable To Process Your Request. Please contact us through the Contact Us Tab";
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
    @Override
    public void DialogEnded(String dialogString) {
        if(dialogString.equals("We Are Unable To Process Your Request. Please contact us through the Contact Us Tab")){
            startActivity(new Intent(this,ContactUsActivity.class));
            finish();
        }
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
}
