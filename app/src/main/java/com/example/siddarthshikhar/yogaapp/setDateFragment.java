package com.example.siddarthshikhar.yogaapp;



import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;


public class setDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public interface datePicked{
        public void populateSetDate(int year, int monthOfYear, int dayOfMonth);
    }
    datePicked listener;
    DatePicker s;
    int presetDay,presetMonth,presetYear;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        DatePickerDialog dialog=new DatePickerDialog(getActivity(), this, presetYear, presetMonth, presetDay);
        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        listener.populateSetDate(year,month,day);
    }
}