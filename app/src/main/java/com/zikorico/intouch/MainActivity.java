package com.zikorico.intouch;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener
***REMOVED***
    private static final int EDITOR_REQUEST_CODE = 1001;
    private static final int NEW_REQUEST_CODE = 1002;
    //id of the loader
    private static final int EMAIL_QUERY_ID = 0;
    private CursorAdapter cursorAdapterEmail;
    //define the projection of the query
    private static final String[] EMAIL_PROJECTION  = new String[] ***REMOVED***
            ContactsContract.Data._ID,
            ContactsContract.Data.LOOKUP_KEY,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
***REMOVED***;
    //define the selection of the query, set the search for the email MIMETYPE
    private static final String EMAIL_SELECTION  = ContactsContract.Data.MIMETYPE + " = '"
            + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'";
    //define the search string for the selection of the query
    private String mSearchString = "";
    private String[] mSelectionArgs = ***REMOVED*** mSearchString ***REMOVED***;
    //define the sorting of the query data
    private static final String SORT_ORDER = ContactsContract.Data.DISPLAY_NAME_PRIMARY + " ASC ";
    private long mContactId;
    private String mContactKey;
    private Uri mContactUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) ***REMOVED***
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: 23-Oct-16 add a search bar at the top of the listview 
        //columns to get the data from
        String[] emailFrom = ***REMOVED***ContactsContract.Data.DISPLAY_NAME_PRIMARY,
                              ContactsContract.CommonDataKinds.Email.ADDRESS,
                 ***REMOVED***;
        //the elements to show the data retrieved
        int[] emailTo = ***REMOVED***R.id.contact_textview,
                         R.id.email_textView
            ***REMOVED***;
        // TODO: 23-Oct-16 create a custom cursorAdapter
        //create cursor adapter for listview
        cursorAdapterEmail = new SimpleCursorAdapter(this,
                R.layout.contact_list_item,
                null,
                emailFrom,
                emailTo,
                0);
        
        //initialize the listview
        ListView list = (ListView) findViewById(R.id.contactsListview);
        //set the adapter to our listeview, to populate the data
        list.setAdapter(cursorAdapterEmail);
        //set the onclicklistener for listview
        list.setOnItemClickListener(this);
        //initialize the loader
        getLoaderManager().initLoader(EMAIL_QUERY_ID, null, this);
***REMOVED***

    @Override
    public boolean onCreateOptionsMenu(Menu menu) ***REMOVED***
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
***REMOVED***

    @Override
    public boolean onOptionsItemSelected(MenuItem item) ***REMOVED***
        int id = item.getItemId();

        switch (id)***REMOVED***
            case R.id.action_create_contact:
                //// TODO: 20-Oct-16 createContact()
                break;
            case R.id.action_settings:
                // TODO: 20-Oct-16 settings shared prefs
                break;
***REMOVED***
        return super.onOptionsItemSelected(item);
***REMOVED***


    private void restartLoader() ***REMOVED***
        getLoaderManager().restartLoader(EMAIL_QUERY_ID, null, this);
***REMOVED***

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) ***REMOVED***
        // OPTIONAL: Makes search string into pattern, and put it into selection criteria
        mSelectionArgs[0] = "%" + mSearchString + "%";
        //return the cursor of query
        return new CursorLoader(this,
                ContactsContract.Data.CONTENT_URI,
                EMAIL_PROJECTION,
                EMAIL_SELECTION,
                null,
                SORT_ORDER);
***REMOVED***

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) ***REMOVED***
        cursorAdapterEmail.swapCursor(data);
***REMOVED***

    @Override
    public void onLoaderReset(Loader<Cursor> loader) ***REMOVED***
        cursorAdapterEmail.swapCursor(null);
***REMOVED***

    public void openContactsInsert(View view) ***REMOVED***
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(EditorActivity.EDITOR_TYPE, "insert");
        startActivityForResult(intent, NEW_REQUEST_CODE);
***REMOVED***

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) ***REMOVED***
        // Get the Cursor
        Cursor cursor = cursorAdapterEmail.getCursor();
        // Move to the selected contact
        cursor.moveToPosition(position);
        // Get the selected LOOKUP KEY
        mContactKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.LOOKUP_KEY));
        //You can use mContactKey as the content LookupKey to retrieve the details for a contact.
        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        intent.putExtra(EditorActivity.CONTENT_ITEM_TYPE, mContactKey);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
***REMOVED***
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) ***REMOVED***
        // TODO: 23-Oct-16 check this method 
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK)***REMOVED***
            restartLoader();
***REMOVED*** else if (requestCode == NEW_REQUEST_CODE && resultCode == RESULT_OK)***REMOVED***
            restartLoader();
***REMOVED***
***REMOVED***
***REMOVED***
