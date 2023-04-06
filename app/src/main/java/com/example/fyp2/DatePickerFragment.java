package com.example.fyp2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    String activityCode, selectedDate;
    EditText dateEdit;
    InterfaceCommunicator interfaceCommunicator;

    public interface InterfaceCommunicator {
        void sendRequestCode(int Code);
    }

    @SuppressLint("ValidFragment")
    public DatePickerFragment(String activityCode, EditText dateEdit, String selectedDate) {
        super();
        this.activityCode = activityCode;
        this.dateEdit = dateEdit;
        this.selectedDate = selectedDate;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year, month, day;
        if (selectedDate.isEmpty()) {
            // Use the current date as the default date in the picker
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DATE);
        } else {
            // Current selected date as the default date in the picker
            String[] DatePart = selectedDate.split("/");
            year = Integer.parseInt(DatePart[2]);
            month = (Integer.parseInt(DatePart[1]) - 1);
            day = Integer.parseInt(DatePart[0]);

        }

        DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), DatePickerFragment.this, year, month, day);
        pickerDialog.getDatePicker().setMinDate(new Date().getTime());
        return pickerDialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        dateEdit.setText(String.valueOf(day + "/" + (month + 1) + "/" + year));
        interfaceCommunicator.sendRequestCode(1);
    }

    @Override
    public void onAttach(Activity activity) {
        interfaceCommunicator = (InterfaceCommunicator) activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        interfaceCommunicator = null;
        super.onDetach();
    }
}
