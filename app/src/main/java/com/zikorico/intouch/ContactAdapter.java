package com.zikorico.intouch;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.Data;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.provider.ContactsContract.CommonDataKinds;

import java.util.Random;

/**
 * Created by ikazme
 */

public class ContactAdapter extends CursorAdapter ***REMOVED***

    private Context context;

    public ContactAdapter(Context context, Cursor c, int flags) ***REMOVED***
        super(context, c, flags);
        this.context = context;
***REMOVED***

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) ***REMOVED***
        View view = LayoutInflater.from(context).inflate(R.layout.contact_list_item, parent, false);
        return view;
***REMOVED***

    @Override
    public void bindView(View view, Context context, Cursor cursor) ***REMOVED***
        TextView nameTv = (TextView)view.findViewById(R.id.contact_textview);
        String contactName = cursor.getString(cursor.getColumnIndex(Data.DISPLAY_NAME_PRIMARY));
        nameTv.setText(contactName);

        TextView emailTv = (TextView)view.findViewById(R.id.email_textView);
        String contactEmail = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Email.ADDRESS));
//        String contactEmail = getEmail(cursor.getInt(cursor.getColumnIndex(Data._ID)));
        emailTv.setText(contactEmail);

        TextView phoneTv = (TextView) view.findViewById(R.id.phone_textView);
//        String phoneNumber = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER));
        String phoneNumber = "9999";
        phoneTv.setText(phoneNumber);
***REMOVED***

    private String getEmail(int contactId)***REMOVED***
        Cursor emails = context.getContentResolver().query(CommonDataKinds.Email.CONTENT_URI,
                null,
                CommonDataKinds.Email.CONTACT_ID + " = " + contactId,
                null,
                null);

        while (emails.moveToNext())
        ***REMOVED***
            return emails.getString(emails.getColumnIndex(CommonDataKinds.Email.DATA));
***REMOVED***
        emails.close();
        return null;
***REMOVED***

***REMOVED***
