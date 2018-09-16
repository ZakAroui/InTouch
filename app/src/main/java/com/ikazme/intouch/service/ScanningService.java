package com.ikazme.intouch.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.ikazme.intouch.R;
import com.ikazme.intouch.utils.Utils;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;
import static com.ikazme.intouch.utils.Utils.REQUEST_IMAGE_CAPTURE;

/**
 * Created by ikazme on 9/03/18.
 */

public class ScanningService {


    private static ScanningService scanningService;

    private String mCurrentPhotoPath;
    private String mName;
    private String mEmailAddress;
    private String mPhoneNumber;
    private String mNote;


    private ScanningService() {
    }

    public static ScanningService getInstance(){
        if (scanningService == null){
            scanningService = new ScanningService();
        }
        return scanningService;
    }


    public void dispatchTakePictureIntent(PackageManager packageManager, Context applicationContext, AppCompatActivity activity) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Utils.showShortToast("Error occurred while creating the File", applicationContext);
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity,
                        "com.ikazme.intouch.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath() + "/InTouch");
        storageDir.mkdir();

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void processImage(Bitmap bitmap, final Context applicationContext, Activity activity){

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        processResult(image, applicationContext, activity);
    }

    public void processImage(Uri imageUri, final Context applicationContext, Activity activity){

        FirebaseVisionImage image;
        try {
            image = FirebaseVisionImage.fromFilePath(applicationContext, imageUri);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        processResult(image, applicationContext, activity);
    }

    private void processResult(FirebaseVisionImage image, final Context applicationContext, final Activity activity){

        clearData(activity);

        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        textRecognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText result) {
                        processResultText(result, applicationContext);
                        updateFieldOnSuccess(activity);
                    }
                })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Utils.showShortToast("Failed to process text!", applicationContext);
                    }
                });
    }

    private void processResultText(FirebaseVisionText result, Context applicationContext){
        String resultText = result.getText();
        Utils.showLongToast(resultText, applicationContext);

        for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
            String blockText = block.getText();
            for (FirebaseVisionText.Line line: block.getLines()) {
                String lineText = line.getText();

                if(TextUtils.isEmpty(mEmailAddress) && lineText.contains("@")){
                    Pattern ePattern = Pattern.compile("\\S+@\\S+\\.\\S+");
                    Matcher eMatcher = ePattern.matcher(lineText);
                    if(eMatcher.find()){
                        int s = eMatcher.start();
                        int e = eMatcher.end();
                        mEmailAddress = lineText.substring(s,e);
                    }
                }

                if(TextUtils.isEmpty(mPhoneNumber)){
                    Pattern p = Pattern.compile("(\\d)+?");
                    Matcher m = p.matcher(lineText);
                    int count = 0;
                    while (m.find()){
                        count++;
                    }

                    if(count > 9){
                        StringBuilder sb = new StringBuilder(lineText);
                        for (int i = 0; i < sb.length(); i++){
                            if(!Character.isDigit(sb.charAt(i))){
                                sb.deleteCharAt(i);
                                i = i - 1;
                            }
                        }
                        mPhoneNumber = sb.toString();
                    }

                }

                for (FirebaseVisionText.Element element: line.getElements()) {
                    String elementText = element.getText();


                }
            }
        }

        for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
            for (FirebaseVisionText.Line line: block.getLines()) {
                String lineText = line.getText();
                for (FirebaseVisionText.Element element: line.getElements()) {
                    String elementText = element.getText();
                    if(!TextUtils.isEmpty(mEmailAddress) && TextUtils.isEmpty(mName)){
                        String es = mEmailAddress.substring(0, mEmailAddress.indexOf("@"));
                        if(es.toLowerCase().contains(elementText.toLowerCase())) mName = lineText;
                    }


                }
            }
        }

    }

    public Uri getUriOfImage(){
        try {
            File f = new File(mCurrentPhotoPath);
            return Uri.fromFile(f);
        } catch (Exception e){
            Log.d(TAG, "getUriOfImage: " + e);
            return null;
        }
    }

    public void clearImage(){
        if(mCurrentPhotoPath != null){
            File f = new File(mCurrentPhotoPath);
            f.delete();
            mCurrentPhotoPath = null;
        }

    }

    private void clearData(Activity activity){
        mEmailAddress = null;
        mPhoneNumber = null;
        mName = null;
        mNote = null;
        updateFieldOnSuccess(activity);
    }


    public void clearImage(String imagePath){
        if(imagePath != null){
            try{
                File f = new File(imagePath);
                f.delete();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

    }

    //TODO - fixed this
    private void updateFieldOnSuccess(Activity activity){
        EditText nameEditor =  activity.findViewById(R.id.name_editText);
        EditText emailEditor =  activity.findViewById(R.id.email_editText);
        EditText phoneEditor =  activity.findViewById(R.id.phone_editText);
        EditText noteEditor =  activity.findViewById(R.id.note_editText);

        nameEditor.setText(mName);
        emailEditor.setText(mEmailAddress);
        phoneEditor.setText(mPhoneNumber);
        noteEditor.setText(mNote);


    }

    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setmCurrentPhotoPath(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
    }


}

