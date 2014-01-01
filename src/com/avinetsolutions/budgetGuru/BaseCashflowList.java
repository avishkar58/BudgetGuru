package com.avinetsolutions.budgetGuru;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by avishkar on 2013/12/28.
 */
public abstract class BaseCashflowList extends ListFragment {

    protected ArrayList<Cashflow> cashflows = new ArrayList<Cashflow>();
    protected DatabaseHelper db;
    protected EditText startDate;
    protected EditText endDate;
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    protected Date start, end;

    public BaseCashflowList() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH,Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
        start = cal.getTime();
        end = new Date();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        View view = inflater.inflate(R.layout.cashflow, null);
        view.findViewById(R.id.cashflow_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSetListSource();
            }
        });

        startDate = (EditText) view.findViewById(R.id.cashflow_startDate);
        startDate.setOnClickListener(new DateClickListener(startDate));

        endDate = (EditText) view.findViewById(R.id.cashflow_endDate);
        endDate.setOnClickListener(new DateClickListener(endDate));

        startDate.setText(dateFormat.format(start));
        endDate.setText(dateFormat.format(end));

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getActivity().getApplicationContext());
        callSetListSource();
    }

    private void callSetListSource() {
        try {
            if (startDate != null) {
                start = dateFormat.parse(startDate.getText().toString());
                end = dateFormat.parse(endDate.getText().toString());
            }
            setListSource();
        }
        catch (Throwable t) {
            Toast.makeText(getActivity(), "Exception: " + t.toString(), Toast.LENGTH_LONG);
            t.printStackTrace();
        }
    }

    protected abstract void setListSource();

    class DateClickListener implements View.OnClickListener {
        private EditText target;
        public DateClickListener(EditText target) {

            this.target = target;
        }


        @Override
        public void onClick(View v) {
            Calendar currentDate = Calendar.getInstance();
            int year = currentDate.get(Calendar.YEAR);
            int month = currentDate.get(Calendar.MONTH);
            int day = currentDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                    target.setText(selectedYear + "-" + (selectedMonth+1) + "-" + selectedDay);
                }
            },year, month, day);

            datePicker.setTitle("Select date");
            datePicker.show();
        }
    }

}
