package com.zikorico.intouch.service;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * Created by ikazme on 8/30/18.
 */

public class ContactsService ***REMOVED***

    private static ContactsService contactsService;

    public static ContactsService getInstance()***REMOVED***
        if (contactsService == null)***REMOVED***
            return new ContactsService();
***REMOVED***
        return contactsService;
***REMOVED***

    public String getPhoneNumber(String[] uSelectionArgs, Context context)***REMOVED***
        Cursor pNumbersCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.Data.LOOKUP_KEY + " = ?",
                uSelectionArgs,
                null);

        String contactPhone = "";
        if (pNumbersCur.getCount() > 0)
        ***REMOVED***
            pNumbersCur.moveToFirst();
            contactPhone = pNumbersCur.getString(pNumbersCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

***REMOVED***
        pNumbersCur.close();

        return contactPhone;
***REMOVED***

    public String[] getNameEmailContactId(String[] uSelectionArgs, Context context)***REMOVED***
        Cursor emailCur = context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.LOOKUP_KEY + " = ? AND " +
                        ContactsContract.Contacts.Data.MIMETYPE + " = '"
                        + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'",
                uSelectionArgs,
                null);
        String contactName="";
        String contactEmail="";
        long contactId = 0;
        if (emailCur.getCount() > 0) ***REMOVED***
            emailCur.moveToFirst();
            contactName = emailCur.getString(emailCur.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY));
            contactEmail = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
            contactId = emailCur.getLong(emailCur.getColumnIndex(ContactsContract.Data._ID));
***REMOVED***
        emailCur.close();

        return new String[]***REMOVED***contactName, contactEmail, Long.toString(contactId)***REMOVED***;
***REMOVED***


    public String getNote(String[] uSelectionArgs, Context context)***REMOVED***
        Cursor noteCur = context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.LOOKUP_KEY + " = ? AND " +
                        ContactsContract.Contacts.Data.MIMETYPE + " = '"
                        + ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE + "'",
                uSelectionArgs,
                null);
        String contactNote = "";
        if (noteCur.getCount() > 0) ***REMOVED***
            noteCur.moveToFirst();
            contactNote = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
***REMOVED***
        noteCur.close();

        return contactNote;
***REMOVED***

***REMOVED***

