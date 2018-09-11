package com.zikorico.intouch;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.zikorico.intouch.model.CopyImageTask;
import com.zikorico.intouch.service.ContactsService;
import com.zikorico.intouch.service.PermissionsService;
import com.zikorico.intouch.service.ScanningService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.zikorico.intouch.utils.Utils.*;

/**
 * Created by ikazme
 */

public class EditorActivity extends AppCompatActivity {
    public static final String EDITOR_TYPE = "Type";
    public static final String CONTACT_LOOKUP = "ContactLookup";
    public static final String CONTACT_NAME = "ContactName";
    private static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private String nextAction;
    private final String NEW_CONTACT = "new";
    private final String EDIT_CONTACT = "edit";

    private String[] mSelectionArgs = { "" };

    private Uri mSelectedContactUri;
    private String mName;
    private String mContactShare;

    private String mLookup;

    String[] contactData;

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mImageView = findViewById(R.id.bcImageView);

        //TODO - USE CONSTRAINT LAYOUT
        EditText nameEditor = (EditText) findViewById(R.id.name_editText);
        EditText emailEditor = (EditText) findViewById(R.id.email_editText);
        EditText phoneEditor = (EditText) findViewById(R.id.phone_editText);
        EditText noteEditor = (EditText) findViewById(R.id.note_editText);


        Intent intent = getIntent();
        String newActionType = intent.getStringExtra(EDITOR_TYPE);
        if (newActionType == null) {
            mLookup = intent.getStringExtra(CONTACT_LOOKUP);
            nextAction = EDIT_CONTACT;
            mSelectionArgs[0] = mLookup;
            mName = intent.getStringExtra(CONTACT_NAME);
            contactData = getContactData(mSelectionArgs);
            setTitle(contactData[0]);
            mContactShare = contactData[0]+"\n"+contactData[1]+"\n"+contactData[2];

            nameEditor.setText(contactData[0]);
            emailEditor.setText(contactData[1]);
            phoneEditor.setText(contactData[2]);
            noteEditor.setText(contactData[3]);

            if(contactData[5] != null){
                Uri bcUri = Uri.parse(contactData[5]);
                showBcImage(bcUri);
            }

            disableTexteditor(nameEditor);
            disableTexteditor(emailEditor);
            disableTexteditor(phoneEditor);
            disableTexteditor(noteEditor);

            mSelectedContactUri = ContactsContract.Contacts.getLookupUri(Long.parseLong(contactData[4]), mLookup);
        }else {
            setTitle("New Contact");
            nextAction = NEW_CONTACT;
            FloatingActionButton newContactFl = (FloatingActionButton) findViewById(R.id.editorFloatingButton);
            newContactFl.setImageResource(R.drawable.ic_action_add);
        }
    }

    public void disableTexteditor(EditText editText){
        editText.setClickable(false);
        editText.setCursorVisible(false);
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
    }

    public String[] getContactData(String[] uSelectionArgs){
        ContactsService cs = ContactsService.getInstance();

        String[] nameEmailContactid = cs.getNameEmailContactId(uSelectionArgs, getApplicationContext());
        String contactPhone = cs.getPhoneNumber(uSelectionArgs, getApplicationContext());
        String contactNote = cs.getNote(uSelectionArgs, getApplicationContext());
        String bcImagePath = cs.getBcImagePath(uSelectionArgs , getApplicationContext());

        return new String[]{
                nameEmailContactid[0],
                nameEmailContactid[1],
                contactPhone,
                contactNote,
                nameEmailContactid[2],
                bcImagePath};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (nextAction == EDIT_CONTACT) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
            MenuItem menuItem = menu.findItem(R.id.action_share);
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
            if (mShareActionProvider != null ) {
                mShareActionProvider.setShareIntent(createShareContactIntent());
            } else {
                Log.w(LOG_TAG, "onCreateOptionsMenu(): Share Action Provider is null!");
            }
        } else if(nextAction == NEW_CONTACT){
            getMenuInflater().inflate(R.menu.menu_editor_new, menu);
        }
        return true;
    }

    private Intent createShareContactIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mContactShare);
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_delete:
                if(PermissionsService.getInstance().hasContactsWritePerm(this, PERMISSIONS_REQUEST_DELETE_CONTACTS)){
                    deleteContact();
                }
                break;
            case R.id.action_scan_bc_card:
                scanBusinessCard(null);
                break;
            case R.id.action_bc_picker:
                pickBcFromFile();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_WRITE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                addOrOpenContactsEditor(null);
            } else {
                Toast.makeText(this, "Grant the permission to delete the contact.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSIONS_REQUEST_DELETE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                deleteContact();
            } else {
                Toast.makeText(this, "Grant the permission to delete the contact.", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == PERMISSIONS_REQUEST_WRITE_EXT_STORAGE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                ScanningService.getInstance().clearImage();
                ScanningService.getInstance().dispatchTakePictureIntent(getPackageManager(), getApplicationContext(), this);
            } else {
                Toast.makeText(this, "Grant the permission to use the camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteContact() {
        if(contactData[5] != null){
            ScanningService.getInstance().clearImage(Uri.parse(contactData[5]).getPath());
        }
        getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI,
                ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY+" = ?",
                new String[]{mName});

        Toast.makeText(this, mName+" Deleted.", Toast.LENGTH_SHORT).show();
        finish();
        setResult(RESULT_OK);
    }

    public void addOrOpenContactsEditor(View view){
        if(PermissionsService.getInstance().hasContactsWritePerm(this, PERMISSIONS_REQUEST_WRITE_CONTACTS)){
            openContactsEditor(view);
        }
    }

    public void openContactsEditor(View view) {
        EditText nameEditor = (EditText) findViewById(R.id.name_editText);
        EditText emailEditor = (EditText) findViewById(R.id.email_editText);
        EditText phoneEditor = (EditText) findViewById(R.id.phone_editText);
        EditText noteEditor = (EditText) findViewById(R.id.note_editText);

        String nameNw = String.valueOf(nameEditor.getText());
        String emailNw = String.valueOf(emailEditor.getText());
        String phoneNw = String.valueOf(phoneEditor.getText());
        String noteNw = String.valueOf(noteEditor.getText());

        switch (nextAction){
            case NEW_CONTACT:

                if(TextUtils.isEmpty(nameNw) || (TextUtils.isEmpty(emailNw) && TextUtils.isEmpty(phoneNw))){
                    Toast.makeText(this, " Put in some Data first!", Toast.LENGTH_SHORT).show();
                    break;
                }

                ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());

                if(nameNw != null){
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nameNw)
                            .build());
                }

                if(emailNw != null){
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailNw)
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                            .build());
                }

                if (phoneNw != null) {
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNw)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                            .build());
                }

                if (noteNw != null) {
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Note.NOTE, noteNw)
                            .build());
                }

                Uri bcPictureUri = ScanningService.getInstance().getUriOfImage();
                if(bcPictureUri != null){
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsService.BC_CONTENT_TYPE)
                            .withValue(ContactsService.BC_IMAGE_PATH, bcPictureUri.toString())
                            .build());
                }

                try {
                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    Toast.makeText(this, "Contact Created!", Toast.LENGTH_SHORT).show();
                    ScanningService.getInstance().setmCurrentPhotoPath(null);
                    finish();
                    setResult(RESULT_OK);
                } catch (Exception e) {
                    Toast.makeText(this, "Can't create contact!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                break;
            case EDIT_CONTACT:
                Intent editIntent = new Intent(Intent.ACTION_EDIT);
                editIntent.setDataAndType(mSelectedContactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                if (editIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(editIntent);
                    finish();
                    setResult(RESULT_OK);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(nextAction == NEW_CONTACT){
            ScanningService.getInstance().clearImage();
        }
        super.onBackPressed();
    }

    public void pickBcFromFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), REQUEST_PICK_FROM_FILE);
    }

    public void scanBusinessCard(View view){
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            if(PermissionsService.getInstance().hasWriteExternalStoragePerm(this)){
                ScanningService.getInstance().clearImage();
                ScanningService.getInstance().dispatchTakePictureIntent(getPackageManager(), getApplicationContext(), this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (requestCode == REQUEST_IMAGE_CAPTURE){
             if(resultCode == RESULT_OK){
                 Uri imageUri = ScanningService.getInstance().getUriOfImage();
                 showBcImage(imageUri);

                 ScanningService.getInstance().processImage(imageUri, getApplicationContext());
             } else if(resultCode == RESULT_CANCELED){
                 ScanningService.getInstance().clearImage();
             }
         } else if (requestCode == REQUEST_PICK_FROM_FILE) {
             if(data != null){
                 Uri pickedBcUri = data.getData();

                 new CopyImageTask(this).execute(pickedBcUri);

                 ScanningService.getInstance().clearImage();
                 showBcImageFromBitmap(pickedBcUri);
             }
         }
    }

    private void showBcImage(Uri imageUri){
        mImageView.setVisibility(View.VISIBLE);
        mImageView.setImageURI(imageUri);
        mImageView.setClickable(true);
    }

    private void showBcImageFromBitmap(Uri pickedBcUri){
        int imageRotate = getCameraPhotoOrientation(pickedBcUri);

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pickedBcUri);
        } catch (IOException e) {
            Log.e("showBcImageFromBitmap", e.toString());
        }
        if(bitmap != null){
            mImageView.setVisibility(View.VISIBLE);
            mImageView.setImageBitmap(bitmap);
            mImageView.setClickable(true);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public int getCameraPhotoOrientation(Uri imageUri){
        int rotate = 0;
        InputStream in = null;
        try {
            in = getContentResolver().openInputStream(imageUri);

            ExifInterface exif = new ExifInterface(in);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Toast.makeText(this, "Exif orientation: "+orientation + "\nRotate value: "+rotate, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("getCameraPhotoOrient", e.toString());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {}
            }
        }
        return rotate;
    }

    public void rotateCurrentImage(View view){
        ImageView imageView = findViewById(R.id.bcImageView);
        imageView.invalidate();
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
        imageView.setImageBitmap(rotated);

        ScanningService.getInstance().processImage(rotated, this);

    }

}
