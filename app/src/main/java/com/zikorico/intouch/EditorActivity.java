package com.zikorico.intouch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.Toast;

import com.zikorico.intouch.service.ContactsService;

/**
 * Created by ikazme
 */

public class EditorActivity extends AppCompatActivity ***REMOVED***
    public static final String EDITOR_TYPE = "Type";
    public static final String CONTACT_LOOKUP = "ContactLookup";
    public static final String CONTACT_NAME = "ContactName";
    private static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private String nextAction;
    private final String NEW_CONTACT = "new";
    private final String EDIT_CONTACT = "edit";

    private String[] mSelectionArgs = ***REMOVED*** "" ***REMOVED***;
    private int INSERT_CONTACT_REQUEST = 201;

    private Uri mSelectedContactUri;
    private String mName;
    private String mContactShare;

    private static final int PERMISSIONS_REQUEST_WRITE_CONTACTS = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) ***REMOVED***
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //TODO - SHOW PHOTO OF CONTACT
        EditText nameEditor = (EditText) findViewById(R.id.name_editText);
        EditText emailEditor = (EditText) findViewById(R.id.email_editText);
        EditText phoneEditor = (EditText) findViewById(R.id.phone_editText);
        EditText noteEditor = (EditText) findViewById(R.id.note_editText);

        Intent intent = getIntent();
        String newActionType = intent.getStringExtra(EDITOR_TYPE);
        if (newActionType == null) ***REMOVED***
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
***REMOVED***else ***REMOVED***
            setTitle("New Contact");
            nextAction = NEW_CONTACT;
            FloatingActionButton newContactFl = (FloatingActionButton) findViewById(R.id.editorFloatingButton);
            newContactFl.setImageResource(R.drawable.ic_action_add);
***REMOVED***
***REMOVED***

    public void disableTexteditor(EditText editText)***REMOVED***
        editText.setClickable(false);
        editText.setCursorVisible(false);
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
***REMOVED***

    public String[] getContactData(String[] uSelectionArgs)***REMOVED***
        ContactsService cs = ContactsService.getInstance();

        String[] nameEmailContactid = cs.getNameEmailContactId(uSelectionArgs, getApplicationContext());
        String contactPhone = cs.getPhoneNumber(uSelectionArgs, getApplicationContext());
        String contactNote = cs.getNote(uSelectionArgs, getApplicationContext());

        //TODO - MAKE THIS CLEANER
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) ***REMOVED***
            requestPermissions(new String[]***REMOVED***Manifest.permission.WRITE_CONTACTS***REMOVED***, MainActivity.PERMISSIONS_REQUEST_WRITE_CONTACTS);
***REMOVED*** else ***REMOVED***
            cs.setImagePath(getApplicationContext(), Integer.valueOf(nameEmailContactid[3]));
            mSelectionArgs[0] = nameEmailContactid[3];
            cs.getBcImagePath(mSelectionArgs , getApplicationContext());
***REMOVED***

        return new String[]***REMOVED***nameEmailContactid[0], nameEmailContactid[1], contactPhone, contactNote, nameEmailContactid[2]***REMOVED***;
***REMOVED***

    @Override
    public boolean onCreateOptionsMenu(Menu menu) ***REMOVED***
        if (nextAction == EDIT_CONTACT) ***REMOVED***
            getMenuInflater().inflate(R.menu.menu_editor, menu);
            MenuItem menuItem = menu.findItem(R.id.action_share);
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
            if (mShareActionProvider != null ) ***REMOVED***
                mShareActionProvider.setShareIntent(createShareContactIntent());
    ***REMOVED*** else ***REMOVED***
                Log.w(LOG_TAG, "onCreateOptionsMenu(): Share Action Provider is null!");
    ***REMOVED***
***REMOVED***
        return true;
***REMOVED***

    private Intent createShareContactIntent() ***REMOVED***
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mContactShare);
        return shareIntent;
***REMOVED***

    @Override
    public boolean onOptionsItemSelected(MenuItem item) ***REMOVED***
        int id = item.getItemId();
        switch (id)***REMOVED***
            case R.id.action_delete:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) ***REMOVED***
                    requestPermissions(new String[]***REMOVED***Manifest.permission.WRITE_CONTACTS***REMOVED***, PERMISSIONS_REQUEST_WRITE_CONTACTS);
        ***REMOVED*** else ***REMOVED***
                    deleteContact();
        ***REMOVED***
                break;
***REMOVED***
        return super.onOptionsItemSelected(item);
***REMOVED***

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) ***REMOVED***
        if (requestCode == PERMISSIONS_REQUEST_WRITE_CONTACTS) ***REMOVED***
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) ***REMOVED***
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                deleteContact();
    ***REMOVED*** else ***REMOVED***
                Toast.makeText(this, "Grant the permission to delete the contact.", Toast.LENGTH_SHORT).show();
    ***REMOVED***
***REMOVED***
***REMOVED***

    private void deleteContact() ***REMOVED***
        getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI,
                ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY+" = ?", new String[]***REMOVED***mName***REMOVED***);
        Toast.makeText(this, mName+" Deleted.", Toast.LENGTH_SHORT).show();
        finish();
        setResult(RESULT_OK);
***REMOVED***

    public void openContactsEditor(View view) ***REMOVED***
        EditText nameEditor = (EditText) findViewById(R.id.name_editText);
        EditText emailEditor = (EditText) findViewById(R.id.email_editText);
        EditText phoneEditor = (EditText) findViewById(R.id.phone_editText);
        EditText noteEditor = (EditText) findViewById(R.id.note_editText);

        String nameNw = String.valueOf(nameEditor.getText());
        String emailNw = String.valueOf(emailEditor.getText());
        String phoneNw = String.valueOf(phoneEditor.getText());
        String noteNw = String.valueOf(noteEditor.getText());

        switch (nextAction)***REMOVED***
            case NEW_CONTACT:
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.NAME, nameNw);
                intent.putExtra(ContactsContract.Intents.Insert.EMAIL, emailNw);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNw);
                intent.putExtra(ContactsContract.Intents.Insert.NOTES, noteNw);
                if (intent.resolveActivity(getPackageManager()) != null) ***REMOVED***
                    startActivity(intent);
                    finish();
                    setResult(RESULT_OK);
        ***REMOVED***
                break;
            case EDIT_CONTACT:
                Intent editIntent = new Intent(Intent.ACTION_EDIT);
                editIntent.setDataAndType(mSelectedContactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                if (editIntent.resolveActivity(getPackageManager()) != null) ***REMOVED***
                    startActivity(editIntent);
                    finish();
                    setResult(RESULT_OK);
        ***REMOVED***
                break;
***REMOVED***
***REMOVED***
***REMOVED***
