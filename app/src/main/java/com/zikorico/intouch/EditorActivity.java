package com.zikorico.intouch;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zikorico.intouch.service.ContactsService;
import com.zikorico.intouch.service.PermissionsService;
import com.zikorico.intouch.service.ScanningService;

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

    private ImageView mImageView;
    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mImageView = findViewById(R.id.imageView);
        mLinearLayout = findViewById(R.id.imagePreviewLayout);


        //TODO - SHOW PHOTO OF CONTACT
        EditText nameEditor = (EditText) findViewById(R.id.name_editText);
        EditText emailEditor = (EditText) findViewById(R.id.email_editText);
        EditText phoneEditor = (EditText) findViewById(R.id.phone_editText);
        EditText noteEditor = (EditText) findViewById(R.id.note_editText);

        Intent intent = getIntent();
        String newActionType = intent.getStringExtra(EDITOR_TYPE);
        if (newActionType == null) {
            String mLookup = intent.getStringExtra(CONTACT_LOOKUP);
            nextAction = EDIT_CONTACT;
            mSelectionArgs[0] = mLookup;
            mName = intent.getStringExtra(CONTACT_NAME);
            String[] contactData = getContactData(mSelectionArgs);
            setTitle(contactData[0]);
            mContactShare = contactData[0]+"\n"+contactData[1]+"\n"+contactData[2];

            nameEditor.setText(contactData[0]);
            emailEditor.setText(contactData[1]);
            phoneEditor.setText(contactData[2]);
            noteEditor.setText(contactData[3]);

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

        //TODO - MAKE THIS CLEANER
        if(PermissionsService.getInstance().hasContactsWritePerm(this, PERMISSIONS_REQUEST_WRITE_CONTACTS)){
            cs.setImagePath(getApplicationContext(), Integer.valueOf(nameEmailContactid[3]));
            mSelectionArgs[0] = nameEmailContactid[3];
            cs.getBcImagePath(mSelectionArgs , getApplicationContext());
        }

        return new String[]{nameEmailContactid[0], nameEmailContactid[1], contactPhone, contactNote, nameEmailContactid[2]};
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_WRITE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
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
                ScanningService.getInstance().dispatchTakePictureIntent(getPackageManager(), getApplicationContext(), this);
            } else {
                Toast.makeText(this, "Grant the permission to use the camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteContact() {
        getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI,
                ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY+" = ?", new String[]{mName});
        Toast.makeText(this, mName+" Deleted.", Toast.LENGTH_SHORT).show();
        finish();
        setResult(RESULT_OK);
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
                Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.NAME, nameNw);
                intent.putExtra(ContactsContract.Intents.Insert.EMAIL, emailNw);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNw);
                intent.putExtra(ContactsContract.Intents.Insert.NOTES, noteNw);

                ArrayList<ContentValues> data = new ArrayList<ContentValues>();

                ContentValues cvs = new ContentValues();
                cvs.put(ContactsContract.Data.MIMETYPE, ContactsService.BC_CONTENT_TYPE);
                cvs.put(ContactsService.BC_IMAGE_PATH, "asdf://adfasa/iamhereyoyo");
                data.add(cvs);

                intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);

                startActivity(intent);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_ADD_CONTACT);
//                    finish();
//                    setResult(RESULT_OK);
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

    public void scanBusinessCard(View view){
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            if(PermissionsService.getInstance().hasWriteExternalStoragePerm(this)){
                ScanningService.getInstance().dispatchTakePictureIntent(getPackageManager(), getApplicationContext(), this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (requestCode == REQUEST_IMAGE_CAPTURE){

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
