package com.avinetsolutions.budgetGuru;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;

/**
 * Created by Avishkar on 2013/12/19.
 */
public class MessageProcessor {

    //^Absa: (?<account>[\w]*), (?<type>[\w]*), (?<date>\d\d/\d\d/\d\d) [\w]*, (?<description>[\w ]*), R(?<amount>[\d.]*), .*

    private Context context;
    private String message;
    private boolean isBankMessage, isProcessed;
    private String account, description, bankName;
    private Date date;
    private int type;
    private double amount;

    public MessageProcessor(Context context, String message) {
        this.context = context;
        this.message = message;
    }

    public boolean isBankMessage() throws Exception {
        if (! isProcessed) processMessage();
        return isBankMessage;
    }

    public String getAccount() throws Exception {
        if (! isProcessed) processMessage();
        if (! isBankMessage) throw new Exception("Not a bank message");
        return account;
    }

    public int getType() throws Exception {
        if (! isProcessed) processMessage();
        if (! isBankMessage) throw new Exception("Not a bank message");
        return type;
    }

    public String getDescription() throws Exception {
        if (! isProcessed) processMessage();
        if (! isBankMessage) throw new Exception("Not a bank message");
        return description;
    }

    public Date getDate() throws Exception {
        if (! isProcessed) processMessage();
        if (! isBankMessage) throw new Exception("Not a bank message");
        return date;
    }

    public double getAmount() throws Exception {
        if (! isProcessed) processMessage();
        if (! isBankMessage) throw new Exception("Not a bank message");
        return amount;
    }

    public String getBankName() throws Exception {
        if (! isProcessed) processMessage();
        if (! isBankMessage) throw new Exception("Not a bank message");
        return bankName;
    }

    private void processMessage() throws Exception {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Map<String, Map<String, Integer>> bankCashflowTypes = getBankCashflowTypes(db);
        Cursor banksCursor = db.rawQuery("SELECT BankName, MessageRegex, DateFormat FROM BankMessage", null);
        banksCursor.moveToFirst();

        while(! banksCursor.isAfterLast()) {
            String bankName = banksCursor.getString(0);
            String regex = banksCursor.getString(1);
            String dateFormat = banksCursor.getString(2);

            Matcher matcher = Pattern.compile(regex).matcher(message);
            if (matcher.matches()) {
                isBankMessage = true;
                this.bankName = bankName;
                Map<String, String> groups = matcher.namedGroups();
                if (groups.containsKey("account")) account = groups.get("account");
                //if (groups.containsKey("type")) type = bankCashflowTypes.get(bankName).get(groups.get("type"));
                if (groups.containsKey("date")) date = new SimpleDateFormat(dateFormat).parse(groups.get("date"));
                if (groups.containsKey("description")) description = groups.get("description");
                if (groups.containsKey("amount")) amount = Double.parseDouble(groups.get("amount"));

                break;
            }

            banksCursor.moveToNext();
        }
    }

    private Map<String,Map<String,Integer>> getBankCashflowTypes(SQLiteDatabase db) throws Exception{
        Cursor typesCursor = db.rawQuery("SELECT BankName, MessageText, CashflowTypeId FROM BankMessage INNER JOIN BankCashflowType", null);
        typesCursor.moveToFirst();
        Map<String, Map<String, Integer>> map = new HashMap<String, Map<String, Integer>>();
        while (!typesCursor.isAfterLast()) {
            String bankName = typesCursor.getString(0);
            if (! map.containsKey(bankName)) map.put(bankName, new HashMap<String, Integer>());
            Map<String, Integer> bankMap = map.get(bankName);
            bankMap.put(typesCursor.getString(1), typesCursor.getInt(2));
            typesCursor.moveToNext();
        }
        typesCursor.close();
        return map;
    }
}
