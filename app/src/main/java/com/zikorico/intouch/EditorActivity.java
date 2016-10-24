package com.zikorico.intouch;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity ***REMOVED***
    public static final String EDITOR_TYPE = "Type";
    public static final String CONTENT_ITEM_TYPE = "Contact";
    private String nextAction;
    private final String NEW_CONTACT = "new";
    private final String EDIT_CONTACT = "edit";
    //define the search string for the selection of the query
    private String[] mSelectionArgs = ***REMOVED*** "" ***REMOVED***;
    private int INSERT_CONTACT_REQUEST = 201;
    //contact uri for edit
    private Uri mSelectedContactUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) ***REMOVED***
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        EditText nameEditor = (EditText) findViewById(R.id.name_editText);
        EditText emailEditor = (EditText) findViewById(R.id.email_editText);
        EditText phoneEditor = (EditText) findViewById(R.id.phone_editText);
        EditText noteEditor = (EditText) findViewById(R.id.note_editText);

        Intent intent = getIntent();
        String newActionType = intent.getStringExtra(EDITOR_TYPE);
        if (newActionType == null) ***REMOVED***
            String mlookup = intent.getStringExtra(CONTENT_ITEM_TYPE);
            nextAction = EDIT_CONTACT;
            mSelectionArgs[0] = mlookup;
            String[] contactData = getContactData(mSelectionArgs);
            setTitle(contactData[0]);
            nameEditor.setText(contactData[0]);
            emailEditor.setText(contactData[1]);
            phoneEditor.setText(contactData[2]);
            noteEditor.setText(contactData[3]);
            // TODO: 23-Oct-16 show all emails and phone numbers
            mSelectedContactUri = ContactsContract.Contacts.getLookupUri(Long.parseLong(contactData[4]), mlookup);
***REMOVED***else ***REMOVED***
            setTitle("New Contact");
            nextAction = NEW_CONTACT;
            FloatingActionButton newContactFl = (FloatingActionButton) findViewById(R.id.editorFloatingButton);
            newContactFl.setImageResource(R.drawable.ic_action_add);
***REMOVED***
***REMOVED***

    public String[] getContactData(String[] uSelectionArgs)***REMOVED***
        // get the <span class="IL_AD" id="IL_AD4">phone number</span>
        Cursor emailCur = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.LOOKUP_KEY + " = ? AND " +
                        ContactsContract.Contacts.Data.MIMETYPE + " = '"
                        + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'",
                uSelectionArgs,
                null);
        String contactName="";
        String contactEmail="";
        long contactId = 0;
        if (emailCur.getCount() > 0) ***REMOVED***
            emailCur.moveToFirst();
            contactName = emailCur.getString(emailCur.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY));
            contactEmail = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
            contactId = emailCur.getLong(emailCur.getColumnIndex(ContactsContract.Data._ID));
***REMOVED***
        emailCur.close();

        Cursor phoneCur = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.LOOKUP_KEY + " = ? AND " +
                        ContactsContract.Contacts.Data.MIMETYPE + " = '"
                        + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'",
                uSelectionArgs,
                null);
        String contactPhone = "";
        if (phoneCur.getCount() > 0) ***REMOVED***
            phoneCur.moveToFirst();
            contactPhone = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
***REMOVED***
        phoneCur.close();

        Cursor noteCur = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.LOOKUP_KEY + " = ? AND " +
                        ContactsContract.Contacts.Data.MIMETYPE + " = '"
                        + ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE + "'",
                uSelectionArgs,
                null);
        String contactNote = "";
        if (noteCur.getCount() > 0) ***REMOVED***
            noteCur.moveToFirst();
            contactNote = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
***REMOVED***
        noteCur.close();
        return new String[]***REMOVED***contactName, contactEmail, contactPhone, contactNote, Long.toString(contactId)***REMOVED***;
***REMOVED***

    @Override
    public boolean onCreateOptionsMenu(Menu menu) ***REMOVED***
        if (nextAction == EDIT_CONTACT) ***REMOVED***
            getMenuInflater().inflate(R.menu.menu_editor, menu);
***REMOVED***
        return true;
***REMOVED***

    @Override
    public boolean onOptionsItemSelected(MenuItem item) ***REMOVED***
        int id = item.getItemId();
        switch (id)***REMOVED***
            case R.id.action_delete:
                deleteContact();
                break;
***REMOVED***
        return super.onOptionsItemSelected(item);
***REMOVED***

    private void deleteContact() ***REMOVED***
        // TODO: 23-Oct-16 fix this delete method
        getContentResolver().delete(mSelectedContactUri,null,null);
        Toast.makeText(this, getString(R.string.note_deleted), Toast.LENGTH_SHORT).show();
        finish();

        Intent deleteIntent = new Intent(Intent.ACTION_DELETE);
        deleteIntent.setDataAndType(mSelectedContactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        if (deleteIntent.resolveActivity(getPackageManager()) != null) ***REMOVED***
            startActivity(deleteIntent);
***REMOVED***
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
        ***REMOVED***
                break;
            case EDIT_CONTACT:
                // TODO: 23-Oct-16 fix the contact edit text fields
                // Creates a new Intent to edit a contact
                Intent editIntent = new Intent(Intent.ACTION_EDIT);
                editIntent.setDataAndType(mSelectedContactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                editIntent.putExtra(ContactsContract.Intents.Insert.EMAIL, emailNw);
                editIntent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNw);
                editIntent.putExtra(ContactsContract.Intents.Insert.NOTES, "");
                editIntent.putExtra(ContactsContract.Intents.Insert.NOTES, noteNw);
                if (editIntent.resolveActivity(getPackageManager()) != null) ***REMOVED***
                    startActivity(editIntent);
                    finish();
        ***REMOVED***
                break;
***REMOVED***
        
***REMOVED***
***REMOVED***
