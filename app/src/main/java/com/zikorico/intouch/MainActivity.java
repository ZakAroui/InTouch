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
import com.zikorico.intouch.service.PermissionsService;
import com.zikorico.intouch.service.ScanningService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.zikorico.intouch.utils.Utils.*;

/**
 * Created by ikazme
 */

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener
{
     private ContactAdapter mCursorAdapterEmail;

    private static final String[] EMAIL_PROJECTION  = new String[] {
            ContactsContract.Data._ID,
            ContactsContract.Data.LOOKUP_KEY,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER
    };

    private static final String SELECTION = ContactsContract.Data.MIMETYPE + " = '" + Email.CONTENT_ITEM_TYPE + "'";
    private static final String SORT_ORDER = ContactsContract.Data.DISPLAY_NAME_PRIMARY + " ASC ";
    private long mContactId;
    private String mContactKey;
    private Uri mContactUri;

    private ImageView mImageView;
    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_main);

        if(PermissionsService.getInstance().hasContactsReadPerm(this)){
            populateContacts();
        }

        mImageView = findViewById(R.id.imageView);
        mLinearLayout = findViewById(R.id.imagePreviewLayout);
        // TODO - add a search bar at the top of the listview
        // TODO - IMPLEMENT LANDING PAGE
        // TODO - IMPLEMENT BOTTOM NAVIGATION MENU
    }

    private void populateContacts(){
        mCursorAdapterEmail = new ContactAdapter(this,null,0);

        ListView list = (ListView) findViewById(R.id.contactsListview);
        list.setAdapter(mCursorAdapterEmail);
        list.setOnItemClickListener(this);
        getLoaderManager().initLoader(EMAIL_QUERY_ID, null, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                populateContacts();
            } else {
                Toast.makeText(this, "Grant the permission to display contacts.", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == PERMISSIONS_REQUEST_WRITE_EXT_STORAGE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                ScanningService.getInstance().dispatchTakePictureIntent(getPackageManager(), getApplicationContext(), this);
            } else {
                Toast.makeText(this, "Grant the permission to use the camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_create_contact:
                Intent intent = new Intent(this, EditorActivity.class);
                intent.putExtra(EditorActivity.EDITOR_TYPE, "insert");
                startActivityForResult(intent, NEW_REQUEST_CODE);
                break;
            case R.id.action_scan_card:
                scanBusinessCard(null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void restartLoader() {
        getLoaderManager().restartLoader(EMAIL_QUERY_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                ContactsContract.Data.CONTENT_URI,
                EMAIL_PROJECTION,
                SELECTION,
                null,
                SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapterEmail.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapterEmail.swapCursor(null);
    }

    public void openContactsInsert(View view) {
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(EditorActivity.EDITOR_TYPE, "insert");
        startActivityForResult(intent, NEW_REQUEST_CODE);
    }

    public void scanBusinessCard(View view){
        //TODO - ALLOW USER TO CHOOSE BETWEEN FILE AND PHOTO
        //TODO - ALLOW USER TO ROTATE PICTURE
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            if(PermissionsService.getInstance().hasWriteExternalStoragePerm(this)){
                ScanningService.getInstance().dispatchTakePictureIntent(getPackageManager(), getApplicationContext(), this);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = mCursorAdapterEmail.getCursor();
        cursor.moveToPosition(position);
        mContactKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.LOOKUP_KEY));
        String mContactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY));

        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        intent.putExtra(EditorActivity.CONTACT_LOOKUP, mContactKey);
        intent.putExtra(EditorActivity.CONTACT_NAME, mContactName);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK){
            restartLoader();
        } else if (requestCode == NEW_REQUEST_CODE && resultCode == RESULT_OK){
            restartLoader();
        } else if (requestCode == REQUEST_IMAGE_CAPTURE){

            if(resultCode == RESULT_OK){
                Uri imageUri = ScanningService.getInstance().getUriOfImage();
                mLinearLayout.setVisibility(View.VISIBLE);
                mImageView.setImageURI(imageUri);
                mImageView.setClickable(true);

                ScanningService.getInstance().processImage(imageUri, getApplicationContext());

            } else if(resultCode == RESULT_CANCELED){
                ScanningService.getInstance().clearImage();
            }
        }
    }

    protected void hideImageView(View view){
        mLinearLayout.setVisibility(View.INVISIBLE);
    }
}
