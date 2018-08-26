package com.zikorico.intouch;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by ikazme
 */

public class ContactAdapter extends CursorAdapter***REMOVED***


    public ContactAdapter(Context context, Cursor c, int flags) ***REMOVED***
        super(context, c, flags);
***REMOVED***

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) ***REMOVED***
        View view = LayoutInflater.from(context).inflate(R.layout.contact_list_item, parent, false);
        return view;
***REMOVED***

    @Override
    public void bindView(View view, Context context, Cursor cursor) ***REMOVED***
        TextView nameTv = (TextView)view.findViewById(R.id.contact_textview);
        String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY));
        nameTv.setText(contactName);

        TextView emailTv = (TextView)view.findViewById(R.id.email_textView);
        String contactEmail = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
        emailTv.setText(contactEmail);

        Random randomGenerator = new Random();
        String mockNumber = "(";
        int cnt = 0;
        for (int idx = 1; idx <= 10; ++idx)***REMOVED***
            int randomInt = randomGenerator.nextInt(10);

            if (cnt == 3)***REMOVED***
                mockNumber += ") ";
    ***REMOVED***
            if (cnt == 6)***REMOVED***
                mockNumber += "-";
    ***REMOVED***
            mockNumber += randomInt;
            cnt++;
***REMOVED***
        TextView phoneTv = (TextView) view.findViewById(R.id.phone_textView);
        phoneTv.setText(mockNumber);
***REMOVED***
***REMOVED***
