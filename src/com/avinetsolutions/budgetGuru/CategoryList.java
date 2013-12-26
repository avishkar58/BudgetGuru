package com.avinetsolutions.budgetGuru;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Avishkar on 2013/12/24.
 */
public class CategoryList extends ListFragment {
    Map<Category, Double> categoryAmounts = new HashMap<Category, Double>();
    private DatabaseHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            db = new DatabaseHelper(getActivity().getApplicationContext());
            setListSource();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListSource() {
        try {
            ArrayList<Cashflow> cashflows = db.getCashflows();
            for(Cashflow flow : cashflows) {
                if (categoryAmounts.containsKey(flow.getCategory())) categoryAmounts.put(flow.getCategory(), categoryAmounts.get(flow.getCategory()) + flow.getAmount());
                else categoryAmounts.put(flow.getCategory(), flow.getAmount());
            }
            setListAdapter(new CategoryAmountAdapter(categoryAmounts.entrySet().toArray(new Map.Entry[0])));
        }
        catch (Throwable t) {
            Toast.makeText(getActivity(), "Exception: " + t.toString(), Toast.LENGTH_LONG);
            t.printStackTrace();
        }
    }

    class CategoryAmountAdapter extends ArrayAdapter<Map.Entry<Category, Double>> {
        private Map.Entry[] entries;

        public CategoryAmountAdapter(Map.Entry[] entries) {
            super(getActivity(), R.layout.cashflowrow, R.id.list_description, entries);
            this.entries = entries;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.categoryrow, parent, false);
            }

            TextView categoryView = (TextView) row.findViewById(R.id.cat_category);
            TextView amountView = (TextView) row.findViewById(R.id.cat_amount);
            Map.Entry<Category, Double> categoryAmount = entries[position];

            categoryView.setText(categoryAmount.getKey().getName());
            amountView.setText(String.format("%1$.2f", categoryAmount.getValue()));

            return row;
        }
    }

}