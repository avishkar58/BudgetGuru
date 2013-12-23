package com.avinetsolutions.budgetGuru;

import android.content.*;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by Avishkar on 2013/12/17.
 */
public class SMSReceiver extends BroadcastReceiver {
    public static final String SMS_EXTRA_NAME ="pdus";
    private DatabaseHelper db;

    // Retrieve SMS
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        if ( extras != null )
        {
            db = new DatabaseHelper(context);
            Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );

            for ( int i = 0; i < smsExtra.length; ++i )
            {
                SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);

                String body = sms.getMessageBody().toString();

                try {
                    MessageProcessor processor = new MessageProcessor(context, body);
                    if (processor.isBankMessage()) {
                        Category cat = getCategory(processor.getDescription());
                        Cashflow newCashflow = new Cashflow(processor.getDate(), processor.getAmount(), processor.getDescription(), cat);
                        db.insertOrUpdate(newCashflow);

                        String message = String.format("Bank message received: %s at %s on %s for %s", processor.getAmount(), processor.getDescription(), processor.getDate().toString(), cat == null ? "UNKNOWN" : cat.getName());
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(context, "Non bank message: " + body, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Throwable t) {
                    Toast.makeText(context, "Error while processing SMS: " + t.toString(), Toast.LENGTH_LONG).show();
                }
            }
            db.close();
        }
    }

    private Category getCategory(String description) {
        ArrayList<CategoryRule> rules = db.getCategoryRules();
        for(CategoryRule rule : rules) {
            if (description.contains(rule.getContainsText())) return rule.getCategory();
        }
        return null;
    }
}
