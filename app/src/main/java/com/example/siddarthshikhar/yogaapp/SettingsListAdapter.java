package com.example.siddarthshikhar.yogaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Siddarth Shikhar on 7/8/2015.
 */
public class SettingsListAdapter extends ArrayAdapter<String> {
    Context context;
    LayoutInflater l;
    String[] list;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public SettingsListAdapter(Context context, int resource, String[] objects, LayoutInflater l) {
        super(context, resource,objects);
        this.context=context;
        this.list=objects;
        this.l=l;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v=convertView;
        if(v==null)
            v=l.inflate(R.layout.settings_individual_item,null);
        TextView drawIndItem=(TextView)v.findViewById(R.id.drawer_item_text);
        drawIndItem.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
        drawIndItem.setText(list[position]);
        ImageView temp=(ImageView)v.findViewById(R.id.drawer_item_icon);
        if(position==0)
            temp.setImageResource(R.drawable.ic_edit_black_24dp);
        if(position==1)
            temp.setImageResource(R.drawable.ic_error_outline_black_24dp);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position==0){
                    Intent chgpass = new Intent(context.getApplicationContext(), ChangePassword.class);
                    context.startActivity(chgpass);
                }else if(position==1){
                    sp=context.getSharedPreferences("Authorization", 0);
                    editor=sp.edit();
                    editor.remove("Auth_key");
                    editor.commit();
                    Intent login = new Intent(context.getApplicationContext(), Loginactivity.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(login);
                }
            }
        });
        return v;
    }
}
