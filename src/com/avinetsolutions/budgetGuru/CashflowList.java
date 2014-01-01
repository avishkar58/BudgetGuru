package com.avinetsolutions.budgetGuru;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Avishkar on 2013/12/24.
 */
public class CashflowList extends BaseCashflowList {

    public CashflowList() {
        super();
    }

    @Override
    public void onListItemClick (ListView l, View v, int position, long id) {
        Cashflow item = (Cashflow) getListView().getItemAtPosition(position);
        Intent intent = new Intent(getActivity().getApplicationContext(), CashflowCategory.class);
        intent.putExtra("Cashflow", item);
        startActivity(intent);
        try {
            //cashflows.clear();
            //cashflows.addAll(db.getCashflows());
            ((CashflowAdapter)getListAdapter()).notifyDataSetChanged();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setListSource() {
        try {
            cashflows.clear();
            cashflows.addAll(db.getCashflows(start, end));
            setListAdapter(new CashflowAdapter());
        }
        catch (Throwable t) {
            Toast.makeText(getActivity(), "Exception: " + t.toString(), Toast.LENGTH_LONG);
            t.printStackTrace();
        }
    }

    class CashflowAdapter extends ArrayAdapter<Cashflow> {
        public CashflowAdapter() {
            super(getActivity(), R.layout.cashflowrow, R.id.list_description, cashflows.toArray(new Cashflow[0]));
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.cashflowrow, parent, false);
            }

            TextView dateView = (TextView) row.findViewById(R.id.list_date);
            TextView amountView = (TextView) row.findViewById(R.id.list_amount);
            TextView descriptionView = (TextView) row.findViewById(R.id.list_description);
            TextView categoryView = (TextView) row.findViewById(R.id.list_categoryName);
            Cashflow cashflow = cashflows.get(position);

            dateView.setText(new SimpleDateFormat("dd MMM yyyy").format(cashflow.getDate()));
            amountView.setText(String.format("%1$.2f", cashflow.getAmount()));
            descriptionView.setText(cashflow.getDescription());

            categoryView.setText(cashflow.getCategory() == null ? "" : cashflow.getCategory().getName());

            return row;
        }
    }
}