package com.example.fyp2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    String activityCode, SelectedTime;
    EditText timeEdit;
    InterfaceCommunicator interfaceCommunicator;

    public interface InterfaceCommunicator {
        void sendRequestCode(int Code);
    }

    @SuppressLint("ValidFragment")
    public TimePickerFragment(String activityCode, EditText timeEdit, String SelectedTime) {
        super();
        this.activityCode = activityCode;
        this.timeEdit = timeEdit;
        this.SelectedTime = SelectedTime;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int hour,minute;
        if(SelectedTime.isEmpty()){
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            // Calender.HOUR is 12-hour clock, HOUR_OF_DAY is 24
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
            // Create a new instance of TimePickerDialog and return it
        }else{
            String[] TimePart = SelectedTime.split(":");
            hour = Integer.parseInt(TimePart[0]);
            minute = Integer.parseInt(TimePart[1]);
        }

        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        timeEdit.setText(String.valueOf(hour + ":" + minute));
        interfaceCommunicator.sendRequestCode(2);
    }

    @Override
    public void onAttach(Activity activity) {
        interfaceCommunicator = (TimePickerFragment.InterfaceCommunicator) activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        interfaceCommunicator = null;
        super.onDetach();
    }


}
