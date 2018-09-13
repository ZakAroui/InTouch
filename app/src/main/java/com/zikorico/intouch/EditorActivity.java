package com.zikorico.intouch;

import android.annotation.TargetApi;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
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
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.zikorico.intouch.model.CopyImageTask;
import com.zikorico.intouch.service.ContactsService;
import com.zikorico.intouch.service.PermissionsService;
import com.zikorico.intouch.service.ScanningService;
import com.zikorico.intouch.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.zikorico.intouch.utils.Utils.PERMISSIONS_REQUEST_DELETE_CONTACTS;
import static com.zikorico.intouch.utils.Utils.PERMISSIONS_REQUEST_WRITE_CONTACTS;
import static com.zikorico.intouch.utils.Utils.PERMISSIONS_REQUEST_WRITE_EXT_STORAGE;
import static com.zikorico.intouch.utils.Utils.REQUEST_IMAGE_CAPTURE;
import static com.zikorico.intouch.utils.Utils.REQUEST_PICK_FROM_FILE;

/**
 * Created by ikazme
 */

public class EditorActivity extends AppCompatActivity {
    public static final String EDITOR_TYPE = "Type";
    public static final String CONTACT_LOOKUP = "ContactLookup";
    public static final String CONTACT_NAME = "ContactName";
    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private final String NEW_CONTACT = "new";
    private final String EDIT_CONTACT = "edit";
    String[] contactData;
    private String nextAction;
    private String[] mSelectionArgs = { "" };
    private Uri mSelectedContactUri;
    private String mName;
    private String mContactShare;
    private String mLookup;
    private ImageView mImageView;
    private FloatingActionButton mRotateImage;

    private Bitmap mSelectedBitmap;

    private Integer mImageMaxWidth;
    private Integer mImageMaxHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mImageView = findViewById(R.id.bcImageView);
        mRotateImage = findViewById(R.id.rotateFloatingButton);

        //TODO - USE CONSTRAINT LAYOUT
        //TODO - ADD BC IMAGE/PICTURE TO EXISTING CONTACT
        EditText nameEditor =  findViewById(R.id.name_editText);
        EditText emailEditor =  findViewById(R.id.email_editText);
        EditText phoneEditor =  findViewById(R.id.phone_editText);
        EditText noteEditor =  findViewById(R.id.note_editText);


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

            if(!TextUtils.isEmpty(contactData[5])){
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
            FloatingActionButton newContactFl = findViewById(R.id.editorFloatingButton);
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
        if (EDIT_CONTACT.equals(nextAction)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
            MenuItem menuItem = menu.findItem(R.id.action_share);
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
            if (mShareActionProvider != null ) {
                mShareActionProvider.setShareIntent(createShareContactIntent());
            } else {
                Log.w(LOG_TAG, "onCreateOptionsMenu(): Share Action Provider is null!");
            }
        } else if(NEW_CONTACT.equals(nextAction)){
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
                break;
            case R.id.action_remove_bc:
                removeCurrentBc();
                break;
            case R.id.action_open_asset:
                mSelectedBitmap = getBitmapFromAsset("work_street_sign.png");
                showImage(mSelectedBitmap);
                ScanningService.getInstance().processImage(mSelectedBitmap, getApplicationContext(), this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_WRITE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.showShortToast("Permission granted!", this);
                addOrOpenContactsEditor(null);
            } else {
                Utils.showShortToast("Grant the permission to delete the contact.", this);
            }
        } else if (requestCode == PERMISSIONS_REQUEST_DELETE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.showShortToast("Permission granted!", this);
                deleteContact();
            } else {
                Utils.showShortToast("Grant the permission to delete the contact.", this);
            }
        } else if(requestCode == PERMISSIONS_REQUEST_WRITE_EXT_STORAGE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.showShortToast("Permission granted!", this);
                ScanningService.getInstance().clearImage();
                ScanningService.getInstance().dispatchTakePictureIntent(getPackageManager(), getApplicationContext(), this);
            } else {
                Utils.showShortToast("Grant the permission to use the camera.", this);
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

        Utils.showShortToast(mName+" Deleted.", this);
        finish();
        setResult(RESULT_OK);
    }

    protected void removeCurrentBc(){
        if(NEW_CONTACT.equals(nextAction)){
            ScanningService.getInstance().clearImage();
            mImageView.invalidate();
            mImageView.setVisibility(View.INVISIBLE);
            mRotateImage.setVisibility(View.INVISIBLE);
        }
    }

    public void addOrOpenContactsEditor(View view){
        if(PermissionsService.getInstance().hasContactsWritePerm(this, PERMISSIONS_REQUEST_WRITE_CONTACTS)){
            openContactsEditor(view);
        }
    }

    public void openContactsEditor(View view) {
        EditText nameEditor = findViewById(R.id.name_editText);
        EditText emailEditor = findViewById(R.id.email_editText);
        EditText phoneEditor = findViewById(R.id.phone_editText);
        EditText noteEditor = findViewById(R.id.note_editText);

        String nameNw = String.valueOf(nameEditor.getText());
        String emailNw = String.valueOf(emailEditor.getText());
        String phoneNw = String.valueOf(phoneEditor.getText());
        String noteNw = String.valueOf(noteEditor.getText());

        switch (nextAction){
            case NEW_CONTACT:

                if(TextUtils.isEmpty(nameNw) || (TextUtils.isEmpty(emailNw) && TextUtils.isEmpty(phoneNw))){
                    Utils.showShortToast("Put in some Data first!", this);
                    break;
                }

                ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nameNw)
                        .build());

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
                    Utils.showShortToast("Contact Created!", this);
                    ScanningService.getInstance().setmCurrentPhotoPath(null);
                    finish();
                    setResult(RESULT_OK);
                } catch (Exception e) {
                    Utils.showShortToast("Can't create contact!", this);
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

                 ScanningService.getInstance().processImage(imageUri, getApplicationContext(), this);
             } else if(resultCode == RESULT_CANCELED){
                 ScanningService.getInstance().clearImage();
             }
         } else if (requestCode == REQUEST_PICK_FROM_FILE) {
             if(data != null){
                 Uri pickedBcUri = data.getData();

                 new CopyImageTask(getApplicationContext(), this).execute(pickedBcUri);

                 ScanningService.getInstance().clearImage();
                 showBcImageFromBitmap(pickedBcUri);
             }
         }
    }

    private void showBcImageView(){
        mImageView.setVisibility(View.VISIBLE);
        mImageView.setClickable(true);
        if(mImageView.getDrawable() != null){
            mRotateImage.setVisibility(View.VISIBLE);
        }
    }

    private void showBcImage(Uri imageUri){
        mImageView.setImageURI(imageUri);
        showBcImageView();
    }

    private void showImage(Bitmap bitmap){
        mImageView.setImageBitmap(scaleImageBitmap(bitmap));
        showBcImageView();
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
            showImage(bitmap);
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

            Utils.showShortToast("Exif orientation: "+orientation + "\nRotate value: "+rotate, this);
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

        ScanningService.getInstance().processImage(rotated, getApplicationContext(), this);

    }

    public Bitmap getBitmapFromAsset(String filePath) {
        AssetManager assetManager = this.getAssets();

        InputStream is;
        Bitmap bitmap = null;
        try {
            is = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private Bitmap scaleImageBitmap(Bitmap bitmap){
        Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();
        int targetWidth = targetedSize.first;
        int maxHeight = targetedSize.second;

        float scaleFactor =
                Math.max(
                        (float) bitmap.getWidth() / (float) targetWidth,
                        (float) bitmap.getHeight() / (float) maxHeight);

        if(scaleFactor < 0.0) return bitmap;

        return Bitmap.createScaledBitmap(
                        bitmap,
                        (int) (bitmap.getWidth() / scaleFactor),
                        (int) (bitmap.getHeight() / scaleFactor),
                        true);

    }

    private Integer getImageMaxWidth() {
        if (mImageMaxWidth == null) {
            mImageMaxWidth = mImageView.getWidth();
        }
        return mImageMaxWidth;
    }

    private Integer getImageMaxHeight() {
        if (mImageMaxHeight == null) {
            mImageMaxHeight = mImageView.getHeight();
        }
        return mImageMaxHeight;
    }

    private Pair<Integer, Integer> getTargetedWidthHeight() {
        int targetWidth;
        int targetHeight;
        int maxWidthForPortraitMode = getImageMaxWidth();
        int maxHeightForPortraitMode = getImageMaxHeight();
        targetWidth = maxWidthForPortraitMode;
        targetHeight = maxHeightForPortraitMode;
        return new Pair<>(targetWidth, targetHeight);
    }

}
