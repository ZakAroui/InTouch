package com.ikazme.intouch.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static com.ikazme.intouch.utils.Utils.*;

/**
 * Created by ikazme on 9/3/18.
 */

public class PermissionsService {


    private static PermissionsService permissionsService;

    private PermissionsService() {
    }

    public static PermissionsService getInstance(){
        if (permissionsService == null){
            permissionsService = new PermissionsService();
        }
        return permissionsService;
    }




    public boolean hasWriteExternalStoragePerm(AppCompatActivity activity, int reqCode){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, reqCode);
            return false;
        } else {
            return true;
        }
    }

    public boolean hasContactsReadPerm(AppCompatActivity activity){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            return false;
        } else {
            return true;
        }
    }

    public boolean hasContactsWritePerm(AppCompatActivity activity, int reqCode){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && activity.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, reqCode);
            return false;
        } else {
            return true;
        }
    }


}
