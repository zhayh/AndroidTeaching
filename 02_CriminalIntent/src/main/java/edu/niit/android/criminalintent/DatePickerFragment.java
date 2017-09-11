package edu.niit.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by zhayh on 2017-9-6.
 */

public class DatePickerFragment extends DialogFragment {
    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "edu.niit.android.criminalintent.date";
    private DatePicker mDatePicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dateView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        mDatePicker = (DatePicker) dateView.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), null);


        return new AlertDialog.Builder(getActivity())
                .setView(dateView)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year, month, day).getTime();
                        sendResult(Activity.RESULT_OK, date);
                    }
                })
                .create();
    }

    public static DatePickerFragment newInstance(Date date) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    private void sendResult(int resultCode, Date date) {
        if(getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
