package com.zikorico.intouch.service;

import android.content.ContentResolver;
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
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.zikorico.intouch.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.zikorico.intouch.utils.Utils.REQUEST_IMAGE_CAPTURE;

/**
 * Created by ikazme on 9/03/18.
 */

public class ScanningService {


    private static ScanningService scanningService;

    private String mCurrentPhotoPath;

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
                        "com.zikorico.intouch.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    public File createImageFile() throws IOException {

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

    public void processImage(Bitmap bitmap, final Context applicationContext){

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        processResult(image, applicationContext);
    }

    public void processImage(Uri imageUri, final Context applicationContext){

        FirebaseVisionImage image;
        try {
            image = FirebaseVisionImage.fromFilePath(applicationContext, imageUri);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        processResult(image, applicationContext);
    }

    private void processResult(FirebaseVisionImage image, final Context applicationContext){

        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        textRecognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText result) {
                        processResultText(result, applicationContext);
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
        //TODO - PROCESS TEXT RESULT
        String resultText = result.getText();
        Utils.showLongToast(resultText, applicationContext);

        for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
            String blockText = block.getText();
            Float blockConfidence = block.getConfidence();
            List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for (FirebaseVisionText.Line line: block.getLines()) {
                String lineText = line.getText();
                Float lineConfidence = line.getConfidence();
                List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                for (FirebaseVisionText.Element element: line.getElements()) {
                    String elementText = element.getText();
                    Float elementConfidence = element.getConfidence();
                    List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
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

    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setmCurrentPhotoPath(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
    }


}

