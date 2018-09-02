package com.zikorico.intouch;

import android.Manifest;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.provider.ContactsContract.CommonDataKinds.Email;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ikazme
 */

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener
***REMOVED***
    private static final int EDITOR_REQUEST_CODE = 1001;
    private static final int NEW_REQUEST_CODE = 1002;
    private static final int REQUEST_IMAGE_CAPTURE = 1003;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSIONS_REQUEST_WRITE_EXT_STORAGE = 101;
    private static final int PERMISSIONS_REQUEST_READ_EXT_STORAGE = 102;


    private static final int EMAIL_QUERY_ID = 0;
    private ContactAdapter mCursorAdapterEmail;

    private static final String[] EMAIL_PROJECTION  = new String[] ***REMOVED***
            ContactsContract.Data._ID,
            ContactsContract.Data.LOOKUP_KEY,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER
***REMOVED***;

    private static final String SELECTION = ContactsContract.Data.MIMETYPE + " = '" + Email.CONTENT_ITEM_TYPE + "'";
    private static final String SORT_ORDER = ContactsContract.Data.DISPLAY_NAME_PRIMARY + " ASC ";
    private long mContactId;
    private String mContactKey;
    private Uri mContactUri;

    private ImageView mImageView;
    private LinearLayout mLinearLayout;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) ***REMOVED***
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) ***REMOVED***
            requestPermissions(new String[]***REMOVED***Manifest.permission.READ_CONTACTS***REMOVED***, PERMISSIONS_REQUEST_READ_CONTACTS);
***REMOVED*** else ***REMOVED***
            populateContacts();
***REMOVED***

        mImageView = findViewById(R.id.imageView);
        mLinearLayout = findViewById(R.id.imagePreviewLayout);
        // TODO: 23-Oct-16 add a search bar at the top of the listview
        // TODO - IMPLEMENT LANDING PAGE
        // TODO - IMPLEMENT BOTTOM NAVIGATION MENU
***REMOVED***

    private void populateContacts()***REMOVED***
        mCursorAdapterEmail = new ContactAdapter(this,null,0);

        ListView list = (ListView) findViewById(R.id.contactsListview);
        list.setAdapter(mCursorAdapterEmail);
        list.setOnItemClickListener(this);
        getLoaderManager().initLoader(EMAIL_QUERY_ID, null, this);
***REMOVED***

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) ***REMOVED***
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) ***REMOVED***
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) ***REMOVED***
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                populateContacts();
    ***REMOVED*** else ***REMOVED***
                Toast.makeText(this, "Grant the permission to display contacts.", Toast.LENGTH_SHORT).show();
    ***REMOVED***
***REMOVED*** else if(requestCode == PERMISSIONS_REQUEST_WRITE_EXT_STORAGE)***REMOVED***
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) ***REMOVED***
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                dispatchTakePictureIntent();
    ***REMOVED*** else ***REMOVED***
                Toast.makeText(this, "Grant the permission to use the camera.", Toast.LENGTH_SHORT).show();
    ***REMOVED***
***REMOVED***
***REMOVED***

    @Override
    public boolean onCreateOptionsMenu(Menu menu) ***REMOVED***
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
***REMOVED***

    @Override
    public boolean onOptionsItemSelected(MenuItem item) ***REMOVED***
        int id = item.getItemId();

        switch (id)***REMOVED***
            case R.id.action_create_contact:
                Intent intent = new Intent(this, EditorActivity.class);
                intent.putExtra(EditorActivity.EDITOR_TYPE, "insert");
                startActivityForResult(intent, NEW_REQUEST_CODE);
                break;
            case R.id.action_scan_card:
                scanBusinessCard(null);
                break;
***REMOVED***
        return super.onOptionsItemSelected(item);
***REMOVED***


    private void restartLoader() ***REMOVED***
        getLoaderManager().restartLoader(EMAIL_QUERY_ID, null, this);
***REMOVED***

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) ***REMOVED***

        return new CursorLoader(this,
                ContactsContract.Data.CONTENT_URI,
                EMAIL_PROJECTION,
                SELECTION,
                null,
                SORT_ORDER);
***REMOVED***

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) ***REMOVED***
        mCursorAdapterEmail.swapCursor(data);
***REMOVED***

    @Override
    public void onLoaderReset(Loader<Cursor> loader) ***REMOVED***
        mCursorAdapterEmail.swapCursor(null);
***REMOVED***

    public void openContactsInsert(View view) ***REMOVED***
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(EditorActivity.EDITOR_TYPE, "insert");
        startActivityForResult(intent, NEW_REQUEST_CODE);
***REMOVED***

    public void scanBusinessCard(View view)***REMOVED***

        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))***REMOVED***
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ***REMOVED***
                requestPermissions(new String[]***REMOVED***Manifest.permission.WRITE_EXTERNAL_STORAGE***REMOVED***, PERMISSIONS_REQUEST_WRITE_EXT_STORAGE);
    ***REMOVED*** else ***REMOVED***
                dispatchTakePictureIntent();
    ***REMOVED***
***REMOVED***
***REMOVED***

    private void processImage(Uri imageUri)***REMOVED***
        //TODO - GET IMAGE FROM Media Type

        FirebaseVisionImage image;
        try ***REMOVED***
            image = FirebaseVisionImage.fromFilePath(getApplicationContext(), imageUri);
***REMOVED*** catch (IOException e) ***REMOVED***
            e.printStackTrace();
            return;
***REMOVED***

        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        textRecognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() ***REMOVED***
                    @Override
                    public void onSuccess(FirebaseVisionText result) ***REMOVED***
                        processResultText(result);
            ***REMOVED***
        ***REMOVED***)
                .addOnFailureListener( new OnFailureListener() ***REMOVED***
                            @Override
                            public void onFailure(@NonNull Exception e) ***REMOVED***
                                Toast.makeText(getApplicationContext(), "failed to process text!", Toast.LENGTH_SHORT).show();
                    ***REMOVED***
                ***REMOVED***);
***REMOVED***

    private void processResultText(FirebaseVisionText result)***REMOVED***
        //TODO - PROCESS TEXT RESULT
        String resultText = result.getText();
        Toast.makeText(getApplicationContext(), resultText, Toast.LENGTH_LONG).show();

        for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) ***REMOVED***
            String blockText = block.getText();
            Float blockConfidence = block.getConfidence();
            List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for (FirebaseVisionText.Line line: block.getLines()) ***REMOVED***
                String lineText = line.getText();
                Float lineConfidence = line.getConfidence();
                List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                for (FirebaseVisionText.Element element: line.getElements()) ***REMOVED***
                    String elementText = element.getText();
                    Float elementConfidence = element.getConfidence();
                    List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
        ***REMOVED***
    ***REMOVED***
***REMOVED***

***REMOVED***

    private void dispatchTakePictureIntent() ***REMOVED***

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) ***REMOVED***
            File photoFile = null;
            try ***REMOVED***
                photoFile = createImageFile();
    ***REMOVED*** catch (IOException ex) ***REMOVED***
                Toast.makeText(getApplicationContext(), "Error occurred while creating the File", Toast.LENGTH_SHORT);
                return;
    ***REMOVED***
            if (photoFile != null) ***REMOVED***
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.zikorico.intouch.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    ***REMOVED***
***REMOVED***
***REMOVED***

    private File createImageFile() throws IOException ***REMOVED***

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath() + "/InTouch");
        storageDir.mkdir();

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        //TODO - PERSIST THE PATH FOR EACH CONTACT'S BC
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
***REMOVED***

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) ***REMOVED***
        Cursor cursor = mCursorAdapterEmail.getCursor();
        cursor.moveToPosition(position);
        mContactKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.LOOKUP_KEY));
        String mContactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY));

        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        intent.putExtra(EditorActivity.CONTACT_LOOKUP, mContactKey);
        intent.putExtra(EditorActivity.CONTACT_NAME, mContactName);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
***REMOVED***

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) ***REMOVED***
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK)***REMOVED***
            restartLoader();
***REMOVED*** else if (requestCode == NEW_REQUEST_CODE && resultCode == RESULT_OK)***REMOVED***
            restartLoader();
***REMOVED*** else if (requestCode == REQUEST_IMAGE_CAPTURE)***REMOVED***
            if(resultCode == RESULT_OK)***REMOVED***
                mLinearLayout.setVisibility(View.VISIBLE);
                File f = new File(mCurrentPhotoPath);
                Uri imageUri = Uri.fromFile(f);
                mImageView.setImageURI(imageUri);
                mImageView.setClickable(true);

                processImage(imageUri);

    ***REMOVED*** else if(resultCode == RESULT_CANCELED)***REMOVED***
                File f = new File(mCurrentPhotoPath);
                f.delete();
                mCurrentPhotoPath = null;

    ***REMOVED***
***REMOVED***
***REMOVED***

    protected void hidImageView(View view)***REMOVED***
        mLinearLayout.setVisibility(View.INVISIBLE);
***REMOVED***
***REMOVED***
