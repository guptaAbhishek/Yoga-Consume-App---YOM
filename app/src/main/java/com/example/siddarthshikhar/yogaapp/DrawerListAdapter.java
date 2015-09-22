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
 * Created by INSPIRON 3521 on 6/23/2015.
 */
public class DrawerListAdapter extends ArrayAdapter<String> {
    Context context;
    LayoutInflater l;
    String[] list;

    public DrawerListAdapter(Context context, int resource, String[] objects,LayoutInflater l) {
        super(context, resource,objects);
        this.context=context;
        this.list=objects;
        this.l=l;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=convertView;
        if(v==null)
            v=l.inflate(R.layout.drawer_individual_item,null);
        TextView drawIndItem=(TextView)v.findViewById(R.id.drawer_item_text);
        drawIndItem.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Comfortaa-Bold.ttf"));
        drawIndItem.setText(list[position]);
        ImageView temp=(ImageView)v.findViewById(R.id.drawer_item_icon);
        if(position==0)
            temp.setImageResource(R.drawable.ic_book_black_24dp);
        else if(position==1)
            temp.setImageResource(R.drawable.ic_event_available_black_24dp);
        else if(position==2)
            temp.setImageResource(R.drawable.ic_account_circle_black_24dp);
        else if(position==3)
            temp.setImageResource(R.drawable.ic_share_black_24dp);
        else if(position==4)
            temp.setImageResource(R.drawable.ic_help_outline_black_24dp);
        else if(position==5)
            temp.setImageResource(R.drawable.ic_settings_black_24dp);
        else if(position==6)
            temp.setImageResource(R.drawable.ic_contacts_black_24dp);
        return v;
    }
}
