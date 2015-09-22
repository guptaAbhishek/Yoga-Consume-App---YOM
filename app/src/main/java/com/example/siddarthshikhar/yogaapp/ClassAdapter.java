package com.example.siddarthshikhar.yogaapp;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Siddarth Shikhar on 6/29/2015.
 */
public class ClassAdapter extends ArrayAdapter<YogaClass> {
    Context context ;
    LayoutInflater l ;
    List<YogaClass> list ;
    FragmentManager fman;
    classUpdateTask listener;
    MyScheduleActivity callingClass;
    public interface classUpdateTask{
        public void classUpdateTaskToBeDone(String status,String scheduleID,YogaClass cur);
    }
    public ClassAdapter(Context context , int resource , List<YogaClass> objects , LayoutInflater l,FragmentManager fman,classUpdateTask listener) {
        super(context, resource, objects);
        this.context = context ;
        this.list = objects ;
        this.l = l;
        this.fman=fman;
        this.listener=listener;
    }
    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        String temp=list.get(position).date+" 23:59";
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dateFromTemp=null;
        try {
            dateFromTemp=format.parse(temp);
        } catch (ParseException e) {
        }
        final YogaClass cur = list.get(position);
        if(System.currentTimeMillis()<dateFromTemp.getTime()){
            View v = convertView ;
            if(v==null)
                v = l.inflate(R.layout.specific_yoga_class,null);
            TextView venue = (TextView) v.findViewById(R.id.venue);
            TextView start = (TextView) v.findViewById(R.id.stime);
            TextView end = (TextView) v.findViewById(R.id.etime);
            TextView status=(TextView)v.findViewById(R.id.status);
            ((TextView)v.findViewById(R.id.venue)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            ((TextView)v.findViewById(R.id.stime)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            ((TextView)v.findViewById(R.id.etime)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            ((TextView)v.findViewById(R.id.status)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            venue.setText("Venue: " + cur.venue);
            start.setText("Starts at " + cur.startTime);
            end.setText("Ends at " + cur.endTime);
            status.setText("UPCOMING CLASS");
            ((LinearLayout)v.findViewById(R.id.layout_to_be_changed_acc_to_attended)).setBackgroundColor(context.getResources().getColor(R.color.action_bar_color));
            return v;
        }
        else if(cur.currStatus.equals("1")){
            View v =l.inflate(R.layout.specific_yoga_class,null);
            TextView venue = (TextView) v.findViewById(R.id.venue);
            TextView start = (TextView) v.findViewById(R.id.stime);
            TextView end = (TextView) v.findViewById(R.id.etime);
            TextView status=(TextView)v.findViewById(R.id.status);
            ((TextView)v.findViewById(R.id.venue)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            ((TextView)v.findViewById(R.id.stime)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            ((TextView)v.findViewById(R.id.etime)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            ((TextView)v.findViewById(R.id.status)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            LinearLayout toBeChanged=(LinearLayout)v.findViewById(R.id.layout_to_be_changed_acc_to_attended);
            venue.setText("Venue: "+cur.venue);
            start.setText("Started at "+cur.startTime);
            end.setText("Ended at "+cur.endTime);
            ImageView cancel=(ImageView)v.findViewById(R.id.class_status);
                status.setText("CLASS ATTENDED");
                toBeChanged.setBackgroundColor(context.getResources().getColor(R.color.Green));
                cancel.setBackground(context.getResources().getDrawable(R.drawable.ic_done_white_24dp));
            return v;
        }else if(list.size()==1 && cur.startTime.equals("00:00") && cur.endTime.equals("00:00")){
            View v= l.inflate(R.layout.class_shifted_to_unscheduled_date,null);
            Button yes,no;
            ((TextView)v.findViewById(R.id.did_you_attend_label)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            ((Button)v.findViewById(R.id.yes_unscheduled_class_attended)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            ((Button)v.findViewById(R.id.no_unscheduled_class_not_attended)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            yes=(Button)v.findViewById(R.id.yes_unscheduled_class_attended);
            no=(Button)v.findViewById(R.id.no_unscheduled_class_not_attended);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.classUpdateTaskToBeDone("1",null,cur);
                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context,MyScheduleActivity.class));
                    callingClass.finish();
                }
            });
            return v;
        } else{
            View v =l.inflate(R.layout.specific_unresponded_yoga_class,null);
            TextView venue = (TextView) v.findViewById(R.id.venue);
            TextView start = (TextView) v.findViewById(R.id.stime);
            TextView end = (TextView) v.findViewById(R.id.etime);
            ((TextView)v.findViewById(R.id.status)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            ((TextView)v.findViewById(R.id.stime)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            ((TextView)v.findViewById(R.id.etime)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            ((TextView)v.findViewById(R.id.venue)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            ((TextView)v.findViewById(R.id.did_you_attend_label)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            ((Button)v.findViewById(R.id.yes_unscheduled_class_attended)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            ((Button)v.findViewById(R.id.no_unscheduled_class_not_attended)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
            venue.setText("Venue: " + cur.venue);
            start.setText("Started at " + cur.startTime);
            end.setText("Ended at " + cur.endTime);
            Button yes,no;
            yes=(Button)v.findViewById(R.id.yes_unscheduled_class_attended);
            no=(Button)v.findViewById(R.id.no_unscheduled_class_not_attended);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.classUpdateTaskToBeDone("1", cur.scheduleID, cur);
                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.classUpdateTaskToBeDone("0",cur.scheduleID,cur);
                }
            });
            return v;
        }
    }
}