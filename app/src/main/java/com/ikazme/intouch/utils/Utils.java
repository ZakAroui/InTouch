package com.zikorico.intouch.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ikazme on 9/3/18.
 */

public class Utils {


    public static final int EDITOR_REQUEST_CODE = 1001;
    public static final int NEW_REQUEST_CODE = 1002;
    public static final int REQUEST_IMAGE_CAPTURE = 1003;

    public static final int REQUEST_PICK_FROM_FILE = 1004;

    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public static final int PERMISSIONS_REQUEST_WRITE_CONTACTS = 101;
    public static final int PERMISSIONS_REQUEST_DELETE_CONTACTS = 102;
    public static final int PERMISSIONS_REQUEST_WRITE_EXT_STORAGE = 103;

    public int INSERT_CONTACT_REQUEST = 201;

    public static final int EMAIL_QUERY_ID = 0;

    public static void showShortToast(String message, Context appContext){
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(String message, Context appContext){
        Toast.makeText(appContext, message, Toast.LENGTH_LONG).show();
    }

}
