package com.example.siddarthshikhar.yogaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MyProfile extends ActionBarActivity implements PostRequestAsyncTask.RequestDoneTaskListener, CustomErrorDialog.ErrorDialogTaskDone {
    ProgressDialog dlg;
    String name,dob,gender,height,weight,phoneno,address,email;
    int goToNext;
    Boolean noNet;

    @Override
    protected void onRestart() {
        fetchProfileDetails();
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if(android.os.Build.VERSION.SDK_INT>=21)
            window.setStatusBarColor(getResources().getColor(R.color.action_bar_color));

        goToNext=0;
        noNet=false;

        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowCustomEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater=LayoutInflater.from(this);
        View v=inflater.inflate(R.layout.actionbarview, null);
        bar.setCustomView(v);
        TextView title=(TextView)v.findViewById(R.id.actbartitle);
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        title.setText("MY PROFILE");
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));
        fetchProfileDetails();

        ((TextView)findViewById(R.id.personal_info_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.name_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.my_profile_name)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.age_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.my_profile_age)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.gender_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.my_profile_gender)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.height_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.my_profile_height)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.weight_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.my_profile_weight)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.contact_info_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.mobile_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.my_profile_mobile)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.email_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.my_profile_email)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.address_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.my_profile_address)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((Button)findViewById(R.id.my_profile_edit)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));

    }
    void fetchProfileDetails(){
        dlg = new ProgressDialog(MyProfile.this);
        dlg.setTitle("Please wait.");
        dlg.setMessage("Fetching Details...");
        dlg.show();
        JSONObject toBePosted=new JSONObject();
        try {
            toBePosted.put("phone",getSharedPreferences("profilephone",0).getString("phone",null));
        } catch (JSONException e) {
        }
        PostRequestAsyncTask puttingTask=new PostRequestAsyncTask();
        puttingTask.toBePosted=toBePosted;
        puttingTask.listener=this;
        puttingTask.execute("http://"+Constants.IP_ADDRESS+"getuserdetails");
    }
    public void onClick(View v){
        int id=v.getId();
        if(id==R.id.my_profile_edit){
            if(noNet==true){
                goToNext=1;
                fetchProfileDetails();
            }
            else
                startActivity(new Intent(this, EditProfileActivity.class));
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

    @Override
    public void processEnquiries(Boolean ifExecuted, String output, int typeOfError) {
        dlg.dismiss();
        if(ifExecuted==true){
            JSONObject obj = null;
            String responseType="";
            try {
                obj= ((JSONArray)new JSONArray(output)).getJSONObject(0);
                name=obj.getString("NAME");
                dob=obj.getString("BIRTH_DATE");
                address=obj.getString("ADDRESS");
                phoneno=obj.getString("PHONE_NUM");
                email=obj.getString("EMAIL_ADDRESS");
                height=obj.getString("HEIGHT");
                weight=obj.getString("WEIGHT");
                gender=obj.getString("GENDER");
            } catch (JSONException e) {
                responseType="Error";
            }
            if(responseType.equals("")){
                ((TextView)findViewById(R.id.my_profile_name)).setText(name);
                ((TextView)findViewById(R.id.my_profile_mobile)).setText(phoneno);
                ((TextView)findViewById(R.id.my_profile_email)).setText(email);
                if(address.equals("null")){
                    ((TextView)findViewById(R.id.my_profile_address)).setText("");
                    address="";
                }else{
                    String str = (String)address;
                    for(int i=0;i<str.length();i++){
                        if(str.charAt(i)==',')     //Only retain first phrase of Google Place
                            str=str.substring(0,i);
                    }
                    ((TextView)findViewById(R.id.my_profile_address)).setText(str);
                }
                if(height.equals("null")){
                    ((TextView)findViewById(R.id.my_profile_height)).setText("");
                    height="";
                }else{
                    ((TextView)findViewById(R.id.my_profile_height)).setText(height);
                    if(((TextView)findViewById(R.id.my_profile_height)).getText().toString().equals("")==false){
                        ((TextView)findViewById(R.id.my_profile_height)).setText(height+" cms");
                    }                }
                if(weight.equals("null")){
                    ((TextView)findViewById(R.id.my_profile_weight)).setText("");
                    weight="";
                }else{
                    ((TextView)findViewById(R.id.my_profile_weight)).setText(weight);
                    if(((TextView)findViewById(R.id.my_profile_weight)).getText().toString().equals("")==false){
                        ((TextView)findViewById(R.id.my_profile_weight)).setText(weight+" kgs");
                    }
                }
                if(dob.equals("null")){
                    ((TextView)findViewById(R.id.my_profile_age)).setText("");
                    dob="";
                }else{
                    ((TextView)findViewById(R.id.my_profile_age)).setText(dob);
                }
                if(gender.equals("null")){
                    ((TextView)findViewById(R.id.my_profile_gender)).setText("");
                    gender="";
                }else {
                    ((TextView)findViewById(R.id.my_profile_gender)).setText(gender);
                }
                SharedPreferences sp=getSharedPreferences("profilename", 0);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("name", name);
                editor.commit();
                sp=getSharedPreferences("profileage", 0);
                editor=sp.edit();
                editor.putString("age", dob);
                editor.commit();
                sp=getSharedPreferences("profilegender", 0);
                editor=sp.edit();
                editor.putString("gender", gender);
                editor.commit();
                sp=getSharedPreferences("profileheight", 0);
                editor=sp.edit();
                editor.putString("height", height);
                editor.commit();
                sp=getSharedPreferences("profileweight", 0);
                editor=sp.edit();
                editor.putString("weight", weight);
                editor.commit();
                sp=getSharedPreferences("profilephone", 0);
                editor=sp.edit();
                editor.putString("phone", phoneno);
                editor.commit();
                sp=getSharedPreferences("profileemail", 0);
                editor=sp.edit();
                editor.putString("email", email);
                editor.commit();
                sp=getSharedPreferences("profileaddress", 0);
                editor=sp.edit();
                editor.putString("address", address);
                editor.commit();
                if(goToNext==1)
                    startActivity(new Intent(this, EditProfileActivity.class));
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
                noNet=true;
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
}
