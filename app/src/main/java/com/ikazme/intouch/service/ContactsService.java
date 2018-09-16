package com.ikazme.intouch.service;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * Created by ikazme on 8/30/18.
 */

public class ContactsService {

    public static final String BC_IMAGE_PATH = "data2";
    public static final String BC_CONTENT_TYPE = "com.ikazme.intouch/bc_path";

    private static ContactsService contactsService;

    private ContactsService() {
    }

    public static ContactsService getInstance(){
        if (contactsService == null){
            contactsService = new ContactsService();
        }
        return contactsService;
    }

    public String getPhoneNumber(String[] uSelectionArgs, Context context){
        Cursor pNumbersCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.Data.LOOKUP_KEY + " = ?",
                uSelectionArgs,
                null);

        String contactPhone = "";
        if(pNumbersCur != null){
            if (pNumbersCur.getCount() > 0)
            {
                pNumbersCur.moveToFirst();
                contactPhone = pNumbersCur.getString(pNumbersCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            }
            pNumbersCur.close();
        }

        return contactPhone;
    }

    public String[] getNameEmailContactId(String[] uSelectionArgs, Context context){
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
        if(emailCur != null){
            if (emailCur.getCount() > 0) {
                emailCur.moveToFirst();
                contactName = emailCur.getString(emailCur.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY));
                contactEmail = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                contactId = emailCur.getLong(emailCur.getColumnIndex(ContactsContract.Data._ID));
                rawContactId = emailCur.getLong(emailCur.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
            }
            emailCur.close();
        }

        return new String[]{contactName, contactEmail, Long.toString(contactId), Long.toString(rawContactId)};
    }

    public String getNote(String[] uSelectionArgs, Context context){
        Cursor noteCur = context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.LOOKUP_KEY + " = ? AND " +
                        ContactsContract.Contacts.Data.MIMETYPE + " = '"
                        + ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE + "'",
                uSelectionArgs,
                null);
        String contactNote = "";
        if(noteCur != null){
            if (noteCur.getCount() > 0) {
                noteCur.moveToFirst();
                contactNote = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
            }
            noteCur.close();
        }

        return contactNote;
    }

    public String getBcImagePath(String[] uSelectionArgs, Context context){
        Cursor pathCur = context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.LOOKUP_KEY + " = ? AND " +
                        ContactsContract.Data.MIMETYPE + " = '"
                        + BC_CONTENT_TYPE + "'",
                uSelectionArgs,
                null);
        String contactImagePath = "";
        if(pathCur != null){
            if (pathCur.getCount() > 0) {
                pathCur.moveToFirst();
                contactImagePath = pathCur.getString(pathCur.getColumnIndex(BC_IMAGE_PATH));
            }
            pathCur.close();
        }

        return contactImagePath;
    }

}

