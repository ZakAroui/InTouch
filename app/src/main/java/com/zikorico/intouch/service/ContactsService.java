package com.zikorico.intouch.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by ikazme on 8/30/18.
 */

public class ContactsService ***REMOVED***

    public static final String BC_IMAGE_PATH = "data2";
    public static final String BC_CONTENT_TYPE = "com.zikorico.intouch/bc_path";

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
        long rawContactId = 0;
        if (emailCur.getCount() > 0) ***REMOVED***
            emailCur.moveToFirst();
            contactName = emailCur.getString(emailCur.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY));
            contactEmail = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
            contactId = emailCur.getLong(emailCur.getColumnIndex(ContactsContract.Data._ID));
            rawContactId = emailCur.getLong(emailCur.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
***REMOVED***
        emailCur.close();

        return new String[]***REMOVED***contactName, contactEmail, Long.toString(contactId), Long.toString(rawContactId)***REMOVED***;
***REMOVED***

    public void setImagePath(Context context, int rawContactId)***REMOVED***
        ContentValues cvs = new ContentValues();
        cvs.put(ContactsContract.Data.RAW_CONTACT_ID, 232);
        cvs.put(ContactsContract.Data.MIMETYPE, BC_CONTENT_TYPE);
        cvs.put(BC_IMAGE_PATH, "asdf://adfasa/ddd");

        Uri uri = context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, cvs);
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

    public String getBcImagePath(String[] uSelectionArgs, Context context)***REMOVED***
        Cursor pathCur = context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " +
                        ContactsContract.Contacts.Data.MIMETYPE + " = '"
                        + BC_CONTENT_TYPE + "'",
                uSelectionArgs,
                null);
        String contactImagePath = "";
        if (pathCur.getCount() > 0) ***REMOVED***
            pathCur.moveToFirst();
            contactImagePath = pathCur.getString(pathCur.getColumnIndex(BC_IMAGE_PATH));
***REMOVED***
        pathCur.close();

        return contactImagePath;
***REMOVED***

***REMOVED***

