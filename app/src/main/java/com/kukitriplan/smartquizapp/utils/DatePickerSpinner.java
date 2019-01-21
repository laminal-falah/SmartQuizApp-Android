package com.kukitriplan.smartquizapp.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;

import com.kukitriplan.smartquizapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public final class DatePickerSpinner extends DialogFragment {

    private static final String TAG = DatePickerSpinner.class.getSimpleName();

    static OnPositiveClickListener onPositiveClickListener;

    public DatePicker mDatePicker;
    Calendar mCalendar;
    Calendar newDate;

    public static DatePickerSpinner newInstance(String title, String tgl, OnPositiveClickListener onPositiveClickListener1) {
        onPositiveClickListener = onPositiveClickListener1;
        DatePickerSpinner frag = new DatePickerSpinner();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("tanggal", tgl);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    public Dialog onCreateDialog(final Bundle bundle) {
        mCalendar = Calendar.getInstance();
        newDate = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        View v = getActivity().getLayoutInflater().inflate(R.layout.utils_date_picker, null);
        mDatePicker = v.findViewById(R.id.dialog_date_datePicker);
        mDatePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newDate.set(year, monthOfYear, dayOfMonth);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String date = newDate.getTime() == null ? dateFormat.format(mCalendar.getTime()) :
                                dateFormat.format(newDate.getTime());
                        onPositiveClickListener.onClick(dateFormat.format(newDate.getTime()));
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
    }

    public interface OnPositiveClickListener {
        void onClick(String date);
    }
}
