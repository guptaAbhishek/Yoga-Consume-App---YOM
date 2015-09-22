package com.example.siddarthshikhar.yogaapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Siddarth Shikhar on 7/26/2015.
 */
public class AppExitDialog extends DialogFragment{
    public interface AppExitTaskDone {
        public void appExitAsked(Boolean ifExiting);
    }
    AppExitTaskDone listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to exit the app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.appExitAsked(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        listener.appExitAsked(false);
                    }
                });
        return builder.create();
    }
}
