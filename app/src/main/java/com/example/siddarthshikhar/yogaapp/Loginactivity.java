package com.example.siddarthshikhar.yogaapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;


public class Loginactivity extends Activity implements PostRequestAsyncTask.RequestDoneTaskListener, CustomErrorDialog.ErrorDialogTaskDone, AppExitDialog.AppExitTaskDone {
    Button btnLogin;
    Button Btnregister;
    MaterialEditText inputEmail;
    EditText inputPassword;
    TextView passreset;
    ProgressDialog dlg;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if(android.os.Build.VERSION.SDK_INT>=21)
            window.setStatusBarColor(getResources().getColor(R.color.action_bar_color));


        inputEmail = (MaterialEditText) findViewById(R.id.login_phoneno);
        inputPassword = (EditText) findViewById(R.id.pword);
        Btnregister = (Button) findViewById(R.id.registerbtn);
        btnLogin = (Button) findViewById(R.id.login);
        passreset = (TextView) findViewById(R.id.passres);

//        ((TextView)findViewById(R.id.textView)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));
        ((TextView)findViewById(R.id.textView2)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText)findViewById(R.id.login_phoneno)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((MaterialEditText)findViewById(R.id.login_phoneno)).setAccentTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.textView4)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((EditText)findViewById(R.id.pword)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((Button)findViewById(R.id.registerbtn)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));
        ((Button)findViewById(R.id.login)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));
        ((TextView)findViewById(R.id.passres)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
    }
    public void onClick(View v){
        int id=v.getId();
        if(id==R.id.login){
            CustomErrorDialog dialog=new CustomErrorDialog();
            dialog.listener=this;
            if(inputEmail.getText().toString().length()!=10){
                dialog.dialogString="Please Enter a 10-digit Mobile No.";
                dialog.show(getFragmentManager(),"");
                return;
            }else if(inputPassword.getText().toString().length()<5){
                dialog.dialogString="Password should be of Minimum 5 characters";
                dialog.show(getFragmentManager(),"");
                return;
            }else if(inputPassword.getText().toString().length()>15) {
                dialog.dialogString = "Password should be of Maximum 15 characters";
                dialog.show(getFragmentManager(), "");
                return;
            }
            dlg = new ProgressDialog(Loginactivity.this);
            dlg.setTitle("Please wait.");
            dlg.setMessage("Logging in.  Please wait.");
            dlg.show();
            JSONObject toBePosted=new JSONObject();
            try {
                toBePosted.put("username",inputEmail.getText().toString());
                toBePosted.put("password",inputPassword.getText().toString());
                toBePosted.put("pushDeviceId", Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
                toBePosted.put("deviceOS","Android");
                toBePosted.put("osVersion", Build.VERSION.RELEASE);
            } catch (JSONException e) {
            }
            PostRequestAsyncTask puttingTask=new PostRequestAsyncTask();
            puttingTask.toBePosted=toBePosted;
            puttingTask.listener=this;
            puttingTask.execute("http://"+Constants.IP_ADDRESS+"login");
        }else if(id==R.id.registerbtn){
            Intent mainIntent = new Intent(this, Registeract.class);
            startActivity(mainIntent);
        }else if(id==R.id.passres){
            Intent mainIntent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(mainIntent);
        }
    }
    @Override
    public void processEnquiries(Boolean ifExecuted, String output, int typeOfError) {
        dlg.dismiss();
        if(ifExecuted==true){
            JSONObject obj = null;
            String responseType="",responseMessage="",authKey="";
            try {
                obj = new JSONObject(output);
                responseType=obj.getString("Response_Type");
                responseMessage=obj.getString("Response_Message");
                authKey=obj.getString("Auth_Key");
            } catch (JSONException e) {
            }
            if(responseType.equals("Success")){
                sp=getSharedPreferences("Authorization", 0);
                editor=sp.edit();
                editor.putString("Auth_key", authKey);
                editor.commit();
                sp=getSharedPreferences("profilephone", 0);
                editor=sp.edit();
                editor.putString("phone",inputEmail.getText().toString());
                editor.commit();
                Toast.makeText(this, "User Successfully Logged In", Toast.LENGTH_SHORT);
                Intent mainIntent = new Intent(Loginactivity.this, MapsActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Loginactivity.this.startActivity(mainIntent);
                Loginactivity.this.finish();
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
    public void onBackPressed() {
        AppExitDialog dialog=new AppExitDialog();
        dialog.listener=this;
        dialog.show(getFragmentManager(), "");
    }
    @Override
    public void appExitAsked(Boolean ifExiting) {
        if(ifExiting==true)
            super.onBackPressed();
    }

    @Override
    public void DialogEnded(String dialogString) {
    }
}
