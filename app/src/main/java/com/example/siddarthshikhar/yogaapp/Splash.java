package com.example.siddarthshikhar.yogaapp;

/**
 * Created by INSPIRON 3521 on 6/18/2015.
 */
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class Splash extends Activity {


    private static int splashInterval = 3000;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splashz);
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                if(getSharedPreferences("Authorization",0).getString("Auth_key",null)!=null){
                    Intent i = new Intent(Splash.this,MapsActivity.class);
                    startActivity(i);
                    this.finish();
                }else{
                    Intent i = new Intent(Splash.this,Loginactivity.class);
                    startActivity(i);
                    this.finish();
                }
            }

            private void finish() {

            }
        }, splashInterval);

    };

}