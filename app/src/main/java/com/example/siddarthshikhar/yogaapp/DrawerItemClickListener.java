package com.example.siddarthshikhar.yogaapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by INSPIRON 3521 on 6/23/2015.
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {
    Context c;
    DrawerLayout currDrawer;
    LinearLayout currDrawerList;
    int compare;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (position){
            case 0:
                if(compare==0){
                    currDrawer.closeDrawer(currDrawerList);
                    break;
                }
                intent = new Intent(c, MapsActivity.class);
                c.startActivity(intent);
                break;
            case 1:
                intent = new Intent(c, MyScheduleActivity.class);
                c.startActivity(intent);
                break;
            case 2:
                intent = new Intent(c, MyProfile.class);
                c.startActivity(intent);
                break;
            case 3:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey!Check this amazing app out! You can Book Yoga Trainers at your convenience" +
                        " and stay fit always! " +
                        "http://bit.ly/1JQ8HXn");
                sendIntent.setType("text/plain");
                c.startActivity(sendIntent);
                break;
            case 4:
                intent = new Intent(c, FAQActivity.class);
                c.startActivity(intent);
                break;
            case 5:
                intent = new Intent(c, SettingsActivity.class);
                c.startActivity(intent);
                break;
            case 6:
                intent = new Intent(c, ContactUsActivity.class);
                c.startActivity(intent);
                break;
        }
    }
}
