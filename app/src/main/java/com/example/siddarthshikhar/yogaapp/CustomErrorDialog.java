package com.example.siddarthshikhar.yogaapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

/**
 * Created by Siddarth Shikhar on 6/30/2015.
 */
public class CustomErrorDialog extends DialogFragment {
    String dialogString;
    public interface ErrorDialogTaskDone {
        public void DialogEnded(String dialogString);
    }
    ErrorDialogTaskDone listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(dialogString)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.DialogEnded(dialogString);
                    }
                });
        return builder.create();
    }
}
