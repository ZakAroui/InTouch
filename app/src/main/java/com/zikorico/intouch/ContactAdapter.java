package com.zikorico.intouch;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
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
    private static final String[] PHONE_PROJECTION  = new String[] ***REMOVED***
            ContactsContract.Data._ID,
            ContactsContract.Data.LOOKUP_KEY,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            CommonDataKinds.Phone.NUMBER
***REMOVED***;

    private static final String SELECTION = ContactsContract.Data.MIMETYPE + " = '" + CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'";


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
        emailTv.setText(contactEmail);

        TextView phoneTv = (TextView) view.findViewById(R.id.phone_textView);
        int hasPnumber = cursor.getInt(cursor.getColumnIndex(CommonDataKinds.Phone.HAS_PHONE_NUMBER));
        if (hasPnumber == 1)***REMOVED***
            String phoneNumber = getPhoneNumber(cursor.getString(cursor.getColumnIndex(Data.LOOKUP_KEY)));
            phoneTv.setText(phoneNumber);
***REMOVED***
***REMOVED***

    private String getPhoneNumber(String lookupId)***REMOVED***
        //TODO REUSABLE CODE - CHECK EDITORACTIVITY
        String[] mSelectionArgs = ***REMOVED*** lookupId ***REMOVED***;
        Cursor pNumbers = context.getContentResolver().query(CommonDataKinds.Phone.CONTENT_URI,
                null,
                Data.LOOKUP_KEY + " = ?",
                mSelectionArgs,
                null);

        String contactPhone = "";
        if (pNumbers.getCount() > 0)
        ***REMOVED***
            pNumbers.moveToFirst();
            contactPhone = pNumbers.getString(pNumbers.getColumnIndex(CommonDataKinds.Phone.NUMBER));

***REMOVED***
        pNumbers.close();

        return contactPhone;
***REMOVED***

***REMOVED***
