package com.example.siddarthshikhar.yogaapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class EnterPromoCodeActivity extends ActionBarActivity implements PostRequestAsyncTask.RequestDoneTaskListener, CustomErrorDialog.ErrorDialogTaskDone {

    Intent intentFromPreviousActivity;
    ProgressDialog dlg;
    int queryForPromoCode,origAmount;
    MaterialEditText promoCode;
    TextView totalCost;
    String promo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_promo_code);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if(android.os.Build.VERSION.SDK_INT>=21)
            window.setStatusBarColor(getResources().getColor(R.color.action_bar_color));

        ((MaterialEditText)findViewById(R.id.enter_promo_code)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText)findViewById(R.id.enter_promo_code)).setAccentTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.total_cost_label)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.total_cost)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((Button)findViewById(R.id.verify_promo_code)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));
        ((Button)findViewById(R.id.proceed_to_checkout)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));

        promo=null;
        promoCode=(MaterialEditText)findViewById(R.id.enter_promo_code);
        totalCost=(TextView)findViewById(R.id.total_cost);
        intentFromPreviousActivity=getIntent();
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowCustomEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater=LayoutInflater.from(this);
        View v=inflater.inflate(R.layout.actionbarview, null);
        bar.setCustomView(v);
        TextView title=(TextView)v.findViewById(R.id.actbartitle);
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        title.setText("ENTER PROMO CODE");
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));

        ((TextView)findViewById(R.id.total_cost)).setText("Rs " + intentFromPreviousActivity.getExtras().getInt("noOfClasses") * 4 * getIntent().getExtras().getInt("cityrate") + "/-");
        origAmount=0;
        try {
            String x = totalCost.getText().toString();
            origAmount = Integer.parseInt(x.substring(3, x.length() - 2));
        }catch (NumberFormatException e){
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
    public void onClick(View v){
        int id=v.getId();
        if(id==R.id.proceed_to_checkout){
            queryForPromoCode=0;
            putInDatabase();
        }else if(id==R.id.verify_promo_code){
            View vieww = this.getCurrentFocus();
            if (vieww != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(vieww.getWindowToken(), 0);
            }
            queryForPromoCode=1;
            dlg = new ProgressDialog(this);
            dlg.setTitle("Please wait.");
            dlg.setMessage("Verifying Promo Code...");
            dlg.show();
            JSONObject toBePosted=new JSONObject();
            try {
                toBePosted.put("phone", getSharedPreferences("profilephone", 0).getString("phone", null));
                toBePosted.put("authKey", getSharedPreferences("Authorization", 0).getString("Auth_key", null));
                toBePosted.put("promo_code",promoCode.getText().toString());
            } catch (JSONException e) {
            }
            PostRequestAsyncTask puttingTask=new PostRequestAsyncTask();
            puttingTask.toBePosted=toBePosted;
            puttingTask.listener=this;
            puttingTask.execute("http://"+Constants.IP_ADDRESS+"validatepromocode");
        }
    }
    public void putInDatabase(){
        dlg = new ProgressDialog(this);
        dlg.setTitle("Please wait.");
        dlg.setMessage("Booking Class...");
        dlg.show();
        JSONObject toBePosted=new JSONObject();
        try {
            toBePosted.put("phone", getSharedPreferences("profilephone", 0).getString("phone", null));
            toBePosted.put("authKey", getSharedPreferences("Authorization", 0).getString("Auth_key", null));
            toBePosted.put("venue",intentFromPreviousActivity.getExtras().getString("venue"));
            toBePosted.put("venue_lat",intentFromPreviousActivity.getExtras().getString("venue_lat"));
            toBePosted.put("venue_long",intentFromPreviousActivity.getExtras().getString("venue_long"));
            toBePosted.put("start_date",intentFromPreviousActivity.getExtras().getString("start_date"));
            toBePosted.put("end_date",intentFromPreviousActivity.getExtras().getString("end_date"));
            toBePosted.put("start_time",intentFromPreviousActivity.getExtras().getString("start_time"));
            toBePosted.put("end_time",intentFromPreviousActivity.getExtras().getString("end_time"));
            toBePosted.put("days", intentFromPreviousActivity.getExtras().getString("days"));
            String x=totalCost.getText().toString();
            toBePosted.put("rate",x.substring(3,x.length()-2));
            toBePosted.put("promo_code",promo);
        } catch (JSONException e) {
        }
        PostRequestAsyncTask puttingTask=new PostRequestAsyncTask();
        puttingTask.toBePosted=toBePosted;
        puttingTask.listener=this;
        puttingTask.execute("http://"+Constants.IP_ADDRESS+"addclass");
    }
    @Override
    public void DialogEnded(String dialogString) {
        if(dialogString.equals("We are experiencing some Technical difficulties.Please contact us.")){
            Intent mainIntent = new Intent(this, ContactUsActivity.class);
            this.startActivity(mainIntent);
            this.finish();
        }
    }

    @Override
    public void processEnquiries(Boolean ifExecuted, String output, int typeOfError) {
        dlg.dismiss();
        if(queryForPromoCode==0){
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
                    Intent mainIntent = new Intent(this, BookingConfirmation.class);
                    this.startActivity(mainIntent);
                    this.finish();
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
        }else{
            if(ifExecuted==true){
                JSONObject obj = null;
                String responseType="",responseMessage="",amountInit="",percentageInit="";
                try {
                    obj = new JSONObject(output);
                    responseType=obj.getString("Response_Type");
                    responseMessage=obj.getString("Response_Message");
                    amountInit=obj.getString("Amount:");
                    percentageInit=obj.getString("Percentage:");
                } catch (JSONException e) {
                }
                int percentage=0,amount=0,currAmount=origAmount;
                try{
                    percentage = Integer.parseInt(percentageInit);
                    amount = Integer.parseInt(amountInit);
                }catch (NumberFormatException e){
                }
                if(responseType.equals("Success")){
                    int disc1=amount;
                    float disc2=(float)percentage/100;
                    disc2*=currAmount;
                    if(disc1<disc2)
                        currAmount-=disc1;
                    else
                        currAmount-=disc2;
                    totalCost.setText("Rs "+currAmount+"/-");
                    promo=promoCode.getText().toString();
                    CustomErrorDialog dialog=new CustomErrorDialog();
                    dialog.listener=this;
                    dialog.dialogString=responseMessage;
                    dialog.show(getFragmentManager(), "");
                }else{
                    promo=null;
                    totalCost.setText("Rs "+currAmount+"/-");
                    CustomErrorDialog dialog=new CustomErrorDialog();
                    dialog.listener=this;
                    dialog.dialogString=responseMessage;
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
}
