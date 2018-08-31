package com.zikorico.intouch;

import android.Manifest;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.provider.ContactsContract.CommonDataKinds.Email;

/**
 * Created by ikazme
 */

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener
***REMOVED***
    private static final int EDITOR_REQUEST_CODE = 1001;
    private static final int NEW_REQUEST_CODE = 1002;

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

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

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

        // TODO: 23-Oct-16 add a search bar at the top of the listview
        // TODO: 01-Nov-16 add business card text recognition - ML Kit or OCR api
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
***REMOVED***
***REMOVED***
***REMOVED***
