package com.example.siddarthshikhar.yogaapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Siddarth Shikhar on 6/25/2015.
 */
public class DayPickerDialog extends DialogFragment {
    String[] daysname={"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    dayPicked listener;
    ArrayList temp;
    public interface dayPicked
    {
        public void populatedays(ArrayList selectedItems);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ArrayList mSelectedItems = temp;
        boolean[] checked;
        if(temp.size()!=0){
            checked=new boolean[7];
            for(int i=0;i<temp.size();i++)
                checked[(Integer)temp.get(i)]=true;
        }
        else{
            checked=null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Pick the Days").setMultiChoiceItems(daysname, checked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked)
                    mSelectedItems.add(which);
                else if (mSelectedItems.contains(which))
                    mSelectedItems.remove(Integer.valueOf(which));
            }
        })
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.populatedays(mSelectedItems);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });

        return builder.create();
    }
}
