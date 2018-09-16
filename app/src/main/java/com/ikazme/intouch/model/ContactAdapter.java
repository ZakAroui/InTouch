package com.ikazme.intouch.model;

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

import com.ikazme.intouch.R;
import com.ikazme.intouch.service.ContactsService;

/**
 * Created by ikazme
 */

public class ContactAdapter extends CursorAdapter {

    public ContactAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_list_item, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTv = view.findViewById(R.id.contact_textview);
        String contactName = cursor.getString(cursor.getColumnIndex(Data.DISPLAY_NAME_PRIMARY));
        nameTv.setText(contactName);

        TextView emailTv = view.findViewById(R.id.email_textView);
        String contactEmail = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Email.ADDRESS));
        emailTv.setText(contactEmail);

        TextView phoneTv =  view.findViewById(R.id.phone_textView);
        int hasPnumber = cursor.getInt(cursor.getColumnIndex(CommonDataKinds.Phone.HAS_PHONE_NUMBER));
        if (hasPnumber == 1){
            ContactsService cs = ContactsService.getInstance();
            String[] mSelectionArgs = { cursor.getString(cursor.getColumnIndex(Data.LOOKUP_KEY)) };
            String phoneNumber = cs.getPhoneNumber(mSelectionArgs, context);
            phoneTv.setText(phoneNumber);
        } else {
            phoneTv.setText("");
        }
    }


}
