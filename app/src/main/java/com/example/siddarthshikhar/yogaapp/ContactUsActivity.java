package com.example.siddarthshikhar.yogaapp;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class ContactUsActivity extends ActionBarActivity {
    ListView list;
    final String[] listViewTitles={"Call Us!","Drop in an Email!","Give Us Some Love!"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if(android.os.Build.VERSION.SDK_INT>=21)
            window.setStatusBarColor(getResources().getColor(R.color.action_bar_color));

        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowCustomEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater=LayoutInflater.from(this);
        View v=inflater.inflate(R.layout.actionbarview, null);
        bar.setCustomView(v);
        TextView title=(TextView)v.findViewById(R.id.actbartitle);
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        title.setText("CONTACT US");
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf"));

        list=(ListView)findViewById(R.id.contact_us_list);
        list.setAdapter(new ContactUsListAdapter(this, 0, listViewTitles, getLayoutInflater()));
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


}
