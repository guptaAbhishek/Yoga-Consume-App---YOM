package com.example.siddarthshikhar.yogaapp;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Siddarth Shikhar on 7/7/2015.
 */
public class SpinnerAdapter extends ArrayAdapter<String>{
    Context context;
    LayoutInflater l;
    String[] list;

    public SpinnerAdapter(Context context, int resource, String[] objects,LayoutInflater l) {
        super(context, resource,objects);
        this.context=context;
        this.list=objects;
        this.l=l;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v=convertView;
        if(v==null)
            v=l.inflate(R.layout.spinner_individual_item,null);
        TextView drawIndItem=(TextView)v.findViewById(R.id.spinner_item);
        drawIndItem.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
        drawIndItem.setText(list[position]);
        return v;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=convertView;
        if(v==null)
            v=l.inflate(R.layout.spinner_individual_item,null);
        TextView drawIndItem=(TextView)v.findViewById(R.id.spinner_item);
        drawIndItem.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
        drawIndItem.setText(list[position]);
        return v;
    }
}
