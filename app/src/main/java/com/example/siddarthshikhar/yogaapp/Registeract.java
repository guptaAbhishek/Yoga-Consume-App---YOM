package com.example.siddarthshikhar.yogaapp;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by INSPIRON 3521 on 6/24/2015.
 */
public class Registeract extends ActionBarActivity implements PostRequestAsyncTask.RequestDoneTaskListener, CustomErrorDialog.ErrorDialogTaskDone {

    MaterialEditText registerFullName;
    MaterialEditText registerEmail;
    MaterialEditText registerPhone;
    MaterialEditText registerPassword;
    MaterialEditText registerPasswordAgain;
    ProgressDialog dlg;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeract);

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
        title.setText("CREATE A NEW ACCOUNT");
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));

        registerFullName=(MaterialEditText)findViewById(R.id.register_fullname);
        registerEmail=(MaterialEditText)findViewById(R.id.register_emailid);
        registerPassword=(MaterialEditText)findViewById(R.id.register_password);
        registerPasswordAgain=(MaterialEditText)findViewById(R.id.register_passwordagain);
        registerPhone=(MaterialEditText)findViewById(R.id.register_phoneno);

        ((MaterialEditText)findViewById(R.id.register_fullname)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText)findViewById(R.id.register_fullname)).setAccentTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText)findViewById(R.id.register_emailid)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText)findViewById(R.id.register_emailid)).setAccentTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText)findViewById(R.id.register_phoneno)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText)findViewById(R.id.register_phoneno)).setAccentTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText)findViewById(R.id.register_password)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText)findViewById(R.id.register_password)).setAccentTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText)findViewById(R.id.register_passwordagain)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText)findViewById(R.id.register_passwordagain)).setAccentTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((Button)findViewById(R.id.register_done)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));
    }
    public void onClick(View v){
        int id=v.getId();
        if(id==R.id.register_done){
            Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
            Matcher m = p.matcher(registerEmail.getText().toString());
            CustomErrorDialog dialog=new CustomErrorDialog();
            dialog.listener=this;
            if(registerPhone.getText().toString().length()!=10){
                dialog.dialogString="Please Enter a 10-digit Mobile No.";
                dialog.show(getFragmentManager(),"");
                return;
            }else if(m.matches()==false){
                dialog.dialogString="Please Enter Valid Email-ID";
                dialog.show(getFragmentManager(),"");
                return;
            }else if(registerFullName.getText().toString().length()==0){
                dialog.dialogString="Please Enter Your Name";
                dialog.show(getFragmentManager(),"");
                return;
            }else if(registerPassword.getText().toString().length()<5){
                dialog.dialogString="Password should be of Minimum 5 characters";
                dialog.show(getFragmentManager(),"");
                return;
            }else if(registerPassword.getText().toString().length()>15){
                dialog.dialogString="Password should be of Maximum 15 characters";
                dialog.show(getFragmentManager(),"");
                return;
            }else if(registerPassword.getText().toString().equals(registerPasswordAgain.getText().toString())==false){
                dialog.dialogString="Passwords do not match";
                dialog.show(getFragmentManager(),"");
                return;
            }
            dlg = new ProgressDialog(Registeract.this);
            dlg.setTitle("Please wait.");
            dlg.setMessage("Signing up.  Please wait.");
            dlg.show();
            JSONObject toBePosted=new JSONObject();
            try {
                toBePosted.put("name",registerFullName.getText().toString());
                toBePosted.put("phone",registerPhone.getText().toString());
                toBePosted.put("email",registerEmail.getText().toString());
                toBePosted.put("password",registerPasswordAgain.getText().toString());
            } catch (JSONException e) {
            }
            PostRequestAsyncTask puttingTask=new PostRequestAsyncTask();
            puttingTask.toBePosted=toBePosted;
            puttingTask.listener=this;
            puttingTask.execute("http://"+Constants.IP_ADDRESS+"registerconsumer");
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
                CustomErrorDialog dialog=new CustomErrorDialog();
                dialog.listener=this;
                dialog.dialogString="User Registered Successfully";
                dialog.show(getFragmentManager(), "");
            }else{
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

    @Override
    public void DialogEnded(String dialogString) {
        if(dialogString.equals("User Registered Successfully")){
            Intent mainIntent = new Intent(Registeract.this, Loginactivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Registeract.this.startActivity(mainIntent);
            Registeract.this.finish();
        }
    }
}
