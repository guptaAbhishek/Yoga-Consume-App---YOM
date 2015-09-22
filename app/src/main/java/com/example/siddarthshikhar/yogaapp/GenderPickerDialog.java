package com.example.siddarthshikhar.yogaapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Siddarth Shikhar on 7/20/2015.
 */
public class GenderPickerDialog extends DialogFragment {
    public interface GenderDialogTaskDone {
        public void genderDialogEnded(String genderPicked);
    }
    GenderDialogTaskDone listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Enter Your Gender")
                .setPositiveButton("Male", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.genderDialogEnded("Male");
                    }
                })
                .setNegativeButton("Female", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        listener.genderDialogEnded("Female");
                    }
                })
                .setNeutralButton("Cancel",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        return builder.create();
    }
}