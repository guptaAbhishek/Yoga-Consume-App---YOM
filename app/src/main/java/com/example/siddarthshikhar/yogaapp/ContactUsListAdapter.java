package com.example.siddarthshikhar.yogaapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Siddarth Shikhar on 7/1/2015.
 */
public class ContactUsListAdapter extends ArrayAdapter<String> {
    Context context;
    LayoutInflater l;
    String[] list;

    public ContactUsListAdapter(Context context, int resource, String[] objects,LayoutInflater l) {
        super(context, resource,objects);
        this.context=context;
        this.list=objects;
        this.l=l;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v=convertView;
        if(v==null)
            v=l.inflate(R.layout.contact_us_individual_item,null);
        TextView drawIndItem=(TextView)v.findViewById(R.id.drawer_item_text);
        drawIndItem.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Los Angeleno Sans.ttf"));
        drawIndItem.setText(list[position]);
        ImageView temp=(ImageView)v.findViewById(R.id.drawer_item_icon);
        if(position==0)
            temp.setImageResource(R.drawable.ic_call_black_24dp);
        if(position==1)
            temp.setImageResource(R.drawable.ic_email_black_24dp);
        if(position==2)
            temp.setImageResource(R.drawable.ic_star_border_black_24dp);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position==0){
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+918285556855"));
                    context.startActivity(intent);
                }else if(position==1){
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "admin@yom.co.in", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                    context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }else{
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=com.moveforwardtechnologies.yomconsumers"));
                    context.startActivity(intent);
                }
            }
        });
        return v;
    }
}
