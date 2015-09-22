package com.example.siddarthshikhar.yogaapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


public class BookingConfirmation extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

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
        title.setText("BOOKING CONFIRMED");
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Create an Intent that will start the Menu-Activity.
                Intent mainIntent = new Intent(BookingConfirmation.this, MapsActivity.class);
                if (counter != 2)
                    BookingConfirmation.this.finish();
            }
        }, 15000);
        counter=0;
        ((TextView)findViewById(R.id.booking_confirmation_label1)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.booking_confirmation_label2)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
        ((TextView)findViewById(R.id.booking_confirmation_label3)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Los Angeleno Sans.ttf"));
    }
    int counter;
    public void onClick(View v){
        int id=v.getId();
        if(id==R.id.booking_confirm_outer_layout){
            if(counter==2){
                BookingConfirmation.this.finish();
            }
            else
                counter++;
        }else if(id==R.id.booking_confirmation_label3){
            startActivity(new Intent(this,ContactUsActivity.class));
            BookingConfirmation.this.finish();
        }
    }
}
