package org.a5calls.android.a5calls.controller;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.a5calls.android.a5calls.model.AccountManager;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    public interface TimePickerCallback {
        void onTimeSelected(int hourOfDay, int minute);
    }

    private TimePickerCallback callback;

    public void setCallback(TimePickerCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final int storedMinutes = AccountManager.Instance.getReminderMinutes(requireContext());
        final int hourOfDay = storedMinutes / 60;
        final int minute = storedMinutes % 60;

        // Create a simple custom dialog with text inputs
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireActivity());

        // Simple clean layout
        android.widget.LinearLayout layout = new android.widget.LinearLayout(requireActivity());
        layout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        layout.setPadding(40, 20, 40, 20);
        layout.setGravity(android.view.Gravity.CENTER);

        boolean is24Hour = DateFormat.is24HourFormat(requireContext());

        // Hour picker
        android.widget.NumberPicker hourPicker = new android.widget.NumberPicker(requireActivity());
        if (is24Hour) {
            hourPicker.setMinValue(0);
            hourPicker.setMaxValue(23);
            hourPicker.setValue(hourOfDay);
        } else {
            hourPicker.setMinValue(1);
            hourPicker.setMaxValue(12);
            int displayHour = hourOfDay;
            if (hourOfDay == 0) displayHour = 12;
            else if (hourOfDay > 12) displayHour = hourOfDay - 12;
            hourPicker.setValue(displayHour);
        }

        // Clean styling for hour picker
        hourPicker.setWrapSelectorWheel(true);
        android.widget.LinearLayout.LayoutParams hourParams = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        hourParams.setMargins(0, 0, 20, 0);
        hourPicker.setLayoutParams(hourParams);

        // Minute picker
        android.widget.NumberPicker minutePicker = new android.widget.NumberPicker(requireActivity());
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(minute);
        minutePicker.setFormatter(value -> String.format("%02d", value));
        minutePicker.setWrapSelectorWheel(true);

        android.widget.LinearLayout.LayoutParams minuteParams = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        minuteParams.setMargins(0, 0, 20, 0);
        minutePicker.setLayoutParams(minuteParams);

        layout.addView(hourPicker);
        layout.addView(minutePicker);

        // AM/PM picker - only for 12-hour format
        android.widget.NumberPicker amPmPicker = null;
        if (!is24Hour) {
            amPmPicker = new android.widget.NumberPicker(requireActivity());
            amPmPicker.setMinValue(0);
            amPmPicker.setMaxValue(1);
            amPmPicker.setDisplayedValues(new String[]{"AM", "PM"});
            amPmPicker.setValue(hourOfDay >= 12 ? 1 : 0);
            amPmPicker.setWrapSelectorWheel(false); // Don't wrap for AM/PM
            layout.addView(amPmPicker);
        }

        builder.setTitle("Set Time");
        builder.setView(layout);

        final android.widget.NumberPicker finalAmPmPicker = amPmPicker;

        builder.setPositiveButton("OK", (dialog, which) -> {
            int h = hourPicker.getValue();
            int m = minutePicker.getValue();

            // Convert to 24-hour format if needed
            if (!is24Hour) {
                boolean isPM = finalAmPmPicker != null && finalAmPmPicker.getValue() == 1;
                if (h == 12 && !isPM) {
                    h = 0; // 12 AM = 0 hours
                } else if (h != 12 && isPM) {
                    h += 12; // PM hours (except 12 PM)
                }
                // 12 PM stays as 12
            }

            if (callback != null) {
                callback.onTimeSelected(h, m);
            }
        });
        builder.setNegativeButton("Cancel", null);

        return builder.create();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (callback != null) {
            callback.onTimeSelected(hourOfDay, minute);
        }
    }
}
