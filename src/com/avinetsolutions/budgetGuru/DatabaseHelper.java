package com.avinetsolutions.budgetGuru;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Avishkar on 2013/12/18.
 */

public class DatabaseHelper extends SQLiteAssetHelper {
    public static final String DATABASE_NAME = "budgetguru";
    public static final int DATABASE_VERSION = 1;

    private ArrayList<Category> categoryCache;
    private ArrayList<CategoryType> categoryTypeCache;

    private static final String cashflowQuery = "SELECT Cashflow._id, Cashflow.Date, Cashflow.Amount, CashFlow.Description, CategoryId FROM CashFLow WHERE DATETIME(Cashflow.Date) >= DATETIME(?) AND DATETIME(Cashflow.Date) <= DATETIME(?) ORDER BY Date";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public ArrayList<Cashflow> getCashflows() throws Exception {
        return getCashflows(new Date(), new Date());
    }

    public ArrayList<Cashflow> getCashflows(Date start, Date end) throws Exception {
        ArrayList<Cashflow> results = new ArrayList<Cashflow>();
        Cursor cursor = getReadableDatabase().rawQuery(cashflowQuery,new String[] {dateFormat.format(start), dateFormat.format(end)});
        cursor.moveToFirst();
        while (! cursor.isAfterLast()) {
            Category category = cursor.getString(4) == null ? null : getCategory(cursor.getInt(4));
            Cashflow cashflow = new Cashflow(dateFormat.parse(cursor.getString(1)), cursor.getDouble(2), cursor.getString(3), category);
            cashflow.setId(cursor.getInt(0));
            results.add(cashflow);
            cursor.moveToNext();
        }
        cursor.close();
        return results;
    }

    public void insertOrUpdate(Cashflow cashflow) {
        ContentValues vals = new ContentValues();
        vals.put("Date", dateFormat.format(cashflow.getDate()));
        vals.put("Amount", cashflow.getAmount());
        vals.put("Description", cashflow.getDescription());
        if (cashflow.getCategory() != null) vals.put("CategoryId", cashflow.getCategory().getId());

        SQLiteDatabase db = getWritableDatabase();
        if (cashflow.getId() >= 0)
            db.update("Cashflow", vals, "_id = ?", new String[] {"" + cashflow.getId()});
        else
            cashflow.setId((int)db.insert("Cashflow", "Date", vals));

        db.close();
    }

    public void insertOrUpdate(Category category) {
        ContentValues vals = new ContentValues();
        vals.put("Name", category.getName());
        vals.put("CategoryTypeId", category.getCategoryType().getId());
        if (category.getId() >= 0)
            getWritableDatabase().update("Category", vals, "_id = ?", new String[] {"" + category.getId()});
        else {
            category.setId((int)getWritableDatabase().insert("Category", "Name", vals));
            categoryCache.add(category);
        }
    }

    public void insertOrUpdate(CategoryRule rule) {
        ContentValues vals = new ContentValues();
        vals.put("CategoryId", rule.getCategory().getId());
        vals.put("ContainsText", rule.getContainsText());
        vals.put("Ordering", rule.getOrder());

        if (rule.getId() >= 0)
            getWritableDatabase().update("CategoryRule", vals, "_id = ?", new String[] {"" + rule.getId()});
        else
            rule.setId((int)getWritableDatabase().insert("CategoryRule", "ContainsText", vals));
    }

    public CategoryType getCategoryType(int id) {
        ArrayList<CategoryType> categoryTypes = getCategoryTypes();
        for(int i =0; i < categoryTypes.size(); i++) {
            if (categoryTypes.get(i).getId() == id) return categoryTypes.get(i);
        }
        return null;
    }

    public ArrayList<CategoryType> getCategoryTypes() {
        if (categoryTypeCache == null) {
            categoryTypeCache = new ArrayList<CategoryType>();
            Cursor cursor = getReadableDatabase().rawQuery("SELECT _id, Name FROM CategoryType",null);
            cursor.moveToFirst();
            while (! cursor.isAfterLast()) {
                CategoryType type = new CategoryType(cursor.getInt(0), cursor.getString(1));
                categoryTypeCache.add(type);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return categoryTypeCache;
    }
    public Category getCategory(int id) {
        ArrayList<Category> categories = getCategories();
        for(int i =0; i < categories.size(); i++) {
            if (categories.get(i).getId() == id) return categories.get(i);
        }
        return null;
    }

    public ArrayList<Category> getCategories() {
        if (categoryCache == null) {
            categoryCache = new ArrayList<Category>();
            Cursor cursor = getReadableDatabase().rawQuery("SELECT _id, Name, CategoryTypeId FROM Category",null);
            cursor.moveToFirst();
            while (! cursor.isAfterLast()) {
                Category cat = new Category(cursor.getString(1), getCategoryType(cursor.getInt(2)));
                cat.setId(cursor.getInt(0));
                categoryCache.add(cat);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return categoryCache;
    }

    public ArrayList<CategoryRule> getCategoryRules() {
        ArrayList<CategoryRule> rules = new ArrayList<CategoryRule>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT _id, CategoryId, ContainsText, Ordering FROM CategoryRule ORDER BY Ordering",null);
        cursor.moveToFirst();
        while (! cursor.isAfterLast()) {
            CategoryRule rule = new CategoryRule(cursor.getInt(3), getCategory(cursor.getInt(1)), cursor.getString(2));
            rule.setId(cursor.getInt(0));
            rules.add(rule);
            cursor.moveToNext();
        }
        cursor.close();

        return rules;
    }
}

/*public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "budgetguru";
    private static final String[] createTableSql = {
            "CREATE TABLE Account(_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT, Type TEXT);",
            "CREATE TABLE CategoryType(_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT);",
            "CREATE TABLE Category(_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT, CategoryTypeId INTEGER, FOREIGN KEY(CategoryTypeId) REFERENCES CategoryType(_id));",
            "CREATE TABLE CashflowType(_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT);",
            "CREATE TABLE Cashflow(_id INTEGER PRIMARY KEY AUTOINCREMENT, Date TEXT, Amount REAL, Account TEXT, Description TEXT, CashflowTypeId INTEGER, CategoryId INTEGER, FOREIGN KEY(CashflowTypeId) REFERENCES CashflowType(_id), FOREIGN KEY(CategoryId) REFERENCES Category(_id));",
            "CREATE TABLE BankMessage(_id INTEGER PRIMARY KEY AUTOINCREMENT, BankName TEXT, MessageRegex TEXT, DateFormat TEXT);",
            "CREATE TABLE BankCashflowType(_id INTEGER PRIMARY KEY AUTOINCREMENT, BankMessageId INTEGER, MessageText TEXT, CashflowTypeId INTEGER, FOREIGN KEY(BankMessageId) REFERENCES BankMessage(_id), FOREIGN KEY CashflowTypeId REFERENCES CashflowType(_id));",
    };

    private static final String[] dropTableSql = {
            "DROP TABLE IF EXISTS BankCashflowType",
            "DROP TABLE IF EXISTS BankMessage",
            "DROP TABLE IF EXISTS Cashflow",
            "DROP TABLE IF EXISTS CashflowType",
            "DROP TABLE IF EXISTS Category",
            "DROP TABLE IF EXISTS CategoryType",
            "DROP TABLE IF EXISTS Account"
    };

    private static final Map<String, Pair<String, String>> bankRegexes = new HashMap<String, Pair<String, String>>();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        bankRegexes.put("Absa", Pair.create("^Absa: (?<account>[\\w]*), (?<type>[\\w]*), (?<date>\\d\\d/\\d\\d/\\d\\d) [\\w]*, (?<description>[\\w ]*), R(?<amount>[\\d.]*), .*", "dd/MM/yy"));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        executeAll(db, createTableSql);

        ContentValues val = new ContentValues();
        val.put("Name", "Deposit");
        db.insert("CashflowType", "Name", val);

        val = new ContentValues();
        val.put("Name", "Purchase");
        db.insert("CashflowType", "Name", val);

        val = new ContentValues();
        val.put("Name", "Cash Withdrawal");
        db.insert("CashflowType", "Name", val);

        for(Map.Entry<String, Pair<String, String>> entry : bankRegexes.entrySet()) {
            val = new ContentValues();
            val.put("BankName", entry.getKey());
            val.put("MessageRegex", entry.getValue().first);
            val.put("DateFormat", entry.getValue().second);
            db.insert("BankMessage", "BankName", val);
        }
    }

    private void executeAll(SQLiteDatabase db, String ... sql) {
        for(int i = 0; i < sql.length; i++) {
            db.execSQL(sql[i]);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        android.util.Log.w("BudgetGuruDB", "Upgrading database. This will destroy all captured data");
        executeAll(db, dropTableSql);
        onCreate(db);
    }
}*/


