package com.avinetsolutions.budgetGuru;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener{
    ArrayList<Cashflow> cashflows = new ArrayList<Cashflow>();
    private TextView selection;
    private ListView list;
    private DatabaseHelper db;

    public MainActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getApplicationContext());
        setContentView(R.layout.main);
        list = (ListView) findViewById(R.id.list);
        setListSource();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cashflow item = (Cashflow) parent.getItemAtPosition(position);
        Intent intent = new Intent(getApplicationContext(), CashflowCategory.class);
        intent.putExtra("Cashflow", item);
        startActivity(intent);
        try {
            cashflows.clear();
            cashflows.addAll(db.getCashflows());
            ((CashflowAdapter)list.getAdapter()).notifyDataSetChanged();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListSource() {
        try {
            //SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.row, result,new String[] {"Date", "Amount", "Description", "CategoryName"}, new int[] {R.id.list_date, R.id.list_amount, R.id.list_description, R.id.list_categoryName}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            cashflows = db.getCashflows();
            list.setAdapter(new CashflowAdapter());
            list.setOnItemClickListener(this);
            selection = (TextView) findViewById(R.id.selection);
        }
        catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG);
            t.printStackTrace();
        }
    }

    class CashflowAdapter extends ArrayAdapter<Cashflow> {
        public CashflowAdapter() {
            super(MainActivity.this, R.layout.row, R.id.list_description, cashflows.toArray(new Cashflow[0]));
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.row, parent, false);
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

    /*private void populateItems() {
        try {
            InputStream in = getResources().openRawResource(R.raw.words);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in, null);
            NodeList words = doc.getElementsByTagName("word");
            for(int i =0; i < words.getLength(); i++) {
                items.add(((Element)words.item(i)).getAttribute("value"));
            }
            in.close();
        }
        catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }*/

    /*
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selection.setText(items.get(position));
        if (items.get(position).equals("Invoke Alert")) {
            new AlertDialog.Builder(this)
                    .setTitle("ALERT")
                    .setMessage("Click on the button to raise a toast")
                    .setNeutralButton("Click me", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "Hello world", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
        }
    }*/

    /*class IconAdapter extends ArrayAdapter<String> {
        IconAdapter() {
            super(MainActivity.this, R.layout.row, R.id.label, items);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.row, parent, false);
            }

            TextView label = (TextView) row.findViewById(R.id.label);
            label.setText(items.get(position));
            ImageView icon = (ImageView) row.findViewById(R.id.icon);

            if (items.get(position).length() > 4)
                icon.setImageResource(R.drawable.ic_action_star);
            else
                icon.setImageResource(R.drawable.ic_action_user);

            return row;
        }
    }*/
}
