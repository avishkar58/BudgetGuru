package com.avinetsolutions.budgetGuru;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import com.avinetsolutions.budgetGuru.Category;
import com.avinetsolutions.budgetGuru.DatabaseHelper;
import com.avinetsolutions.budgetGuru.R;

import java.util.ArrayList;

/**
 * Created by Avishkar on 2013/12/20.
 */
public class CashflowCategory extends Activity {
    private Cashflow cashflow;
    private DatabaseHelper db;
    private Spinner categorySpinner, typeSpinner;
    private EditText containsText;
    private boolean newCategory;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cashflowcategory);

        cashflow = (Cashflow) getIntent().getSerializableExtra("Cashflow");
        db = new DatabaseHelper(getApplicationContext());

        categorySpinner = (Spinner) findViewById(R.id.cashcat_category);
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<Category>(getApplicationContext(), android.R.layout.simple_spinner_item, db.getCategories());
        categorySpinner.setAdapter(categoryAdapter);

        typeSpinner = (Spinner)findViewById(R.id.cashcat_categoryType);
        ArrayAdapter<CategoryType> typeAdapter = new ArrayAdapter<CategoryType>(getApplicationContext(), android.R.layout.simple_spinner_item, db.getCategoryTypes());
        typeSpinner.setAdapter(typeAdapter);

        containsText = (EditText) findViewById(R.id.cashcat_containsText);
        containsText.setText(cashflow.getDescription());
    }

    public void saveClicked(View view) {
        try {
            Category selectedCategory;
            if (newCategory) {
                String name = ((EditText)findViewById(R.id.cashcat_newCatName)).getText().toString();
                CategoryType type = (CategoryType) typeSpinner.getSelectedItem();
                selectedCategory = new Category(name, type);
                db.insertOrUpdate(selectedCategory);
            }
            else
                selectedCategory = (Category) categorySpinner.getSelectedItem();

            cashflow.setCategory(selectedCategory);
            db.insertOrUpdate(cashflow);

            boolean createRule = ((CheckBox)findViewById(R.id.cashcat_createRule)).isChecked();
            if (createRule) {
                String ruleText = ((EditText)findViewById(R.id.cashcat_containsText)).getText().toString();
                CategoryRule rule = new CategoryRule(1, selectedCategory, ruleText);
                db.insertOrUpdate(rule);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        finish();
    }

    public void newClicked(View view) {
        findViewById(R.id.cashcat_rowSelectCategory).setVisibility(View.GONE);
        findViewById(R.id.cashcat_rowNewCategory).setVisibility(View.VISIBLE);
        newCategory = true;
    }
}