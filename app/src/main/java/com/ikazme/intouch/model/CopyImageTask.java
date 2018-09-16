package com.ikazme.intouch.model;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.ikazme.intouch.service.ScanningService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ikazme on 9/9/18.
 */

public class CopyImageTask extends AsyncTask<Uri, Void, String> {

    private Context context;
    private Activity activity;

    public CopyImageTask(Context context, Activity activity){

        this.context = context;
        this.activity = activity;
    }

    protected String doInBackground(Uri... uris) {
        String path = null;
        path = copyImageToAppdir(context.getContentResolver(), uris[0]);

        if (isCancelled()) path = null;

        return path;
    }

    protected void onPostExecute(String result) {
        ScanningService.getInstance().setmCurrentPhotoPath(result);
        Toast.makeText(context, "Image copied.", Toast.LENGTH_SHORT).show();

        ScanningService.getInstance().processImage(ScanningService.getInstance().getUriOfImage(), context, activity);

    }

    private  String copyImageToAppdir(ContentResolver contentResolver, Uri pickedBcUri){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, pickedBcUri);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String imageFileName = "PNG_Copied_" + timeStamp + "_";
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getPath() + "/InTouch");
            storageDir.mkdir();

            File image = File.createTempFile(
                    imageFileName,
                    ".jpeg",
                    storageDir
            );

            String cPath = image.getAbsolutePath();

            FileOutputStream fOut = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 97, fOut);

            fOut.flush();
            fOut.close();

            return cPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}



