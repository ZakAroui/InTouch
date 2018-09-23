package com.ikazme.intouch;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.ikazme.intouch.ui.ContactAdapter;
import com.ikazme.intouch.service.PermissionsService;
import com.ikazme.intouch.utils.Utils;

import static com.ikazme.intouch.utils.Utils.EDITOR_REQUEST_CODE;
import static com.ikazme.intouch.utils.Utils.EMAIL_QUERY_ID;
import static com.ikazme.intouch.utils.Utils.NEW_REQUEST_CODE;
import static com.ikazme.intouch.utils.Utils.PERMISSIONS_REQUEST_READ_CONTACTS;

/**
 * Created by ikazme
 */

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener
{
    private static final String[] EMAIL_PROJECTION  = new String[] {
            ContactsContract.Data._ID,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.PHOTO_THUMBNAIL_URI,
            ContactsContract.Data.LOOKUP_KEY,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER
    };
    private static final String SELECTION = ContactsContract.Data.MIMETYPE + " = '" + Email.CONTENT_ITEM_TYPE + "'";
    private static final String SORT_ORDER = ContactsContract.Data.DISPLAY_NAME_PRIMARY + " ASC ";
    private ContactAdapter mCursorAdapterEmail;

    private final String SEARCH_CONTACT = "search";
    private String currentAction;
    private String mSearchQuery;
    private FloatingActionButton mAddContact;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_main);

        mAddContact = findViewById(R.id.mainFloatingButton);

        if(PermissionsService.getInstance().hasOrRequestContactsReadPerm(this)){
            populateContacts();
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                if (isFirstStart) {
                    final Intent i = new Intent(MainActivity.this, OnboardingActivity.class);
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            startActivity(i);
                        }
                    });

                    SharedPreferences.Editor e = getPrefs.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                }
            }
        });
        t.start();


        //TODO - add personal bc screen 
        //TODO - ANDROID STAGING AND PROD MODES

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            currentAction = SEARCH_CONTACT;
            mAddContact.setVisibility(View.INVISIBLE);
            mSearchQuery = intent.getStringExtra(SearchManager.QUERY);
            getSupportActionBar().setSubtitle("Searched: \""+ mSearchQuery +"\"");

            searchInContacts(mSearchQuery);
        }
    }

    private void searchInContacts(String query) {
        Utils.showLongToast(query, this);
        restartLoader();
    }

    private void populateContacts(){
        invalidateOptionsMenu();
        mCursorAdapterEmail = new ContactAdapter(getApplicationContext(),null,0);

        ListView list = findViewById(R.id.contactsListview);
        list.setAdapter(mCursorAdapterEmail);
        list.setOnItemClickListener(this);
        getLoaderManager().initLoader(EMAIL_QUERY_ID, null, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.showShortToast("Permission granted!", this);
                populateContacts();
            } else {
                Utils.showShortToast("Display contacts by granting contacts permission.", this);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(SEARCH_CONTACT.equals(currentAction)){
            return false;
        }

        getMenuInflater().inflate(R.menu.menu_main, menu);

        if(!PermissionsService.getInstance().hasContactsReadPerm(this)){
            MenuItem searchItem = menu.findItem(R.id.app_bar_search);
            searchItem.setVisible(false);
        }

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setQueryRefinementEnabled(true);

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
            case R.id.app_bar_search:
                onSearchRequested();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void restartLoader() {
        getLoaderManager().restartLoader(EMAIL_QUERY_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(SEARCH_CONTACT.equals(currentAction)){
            String[] searchQueryArgs = mSearchQuery.split("\\s+");
            String selPrefixPart = "";
            for (int i = 0; i<searchQueryArgs.length;i++) {
                if(i == 0) selPrefixPart += "( ";
                selPrefixPart += ContactsContract.Data.DISPLAY_NAME_PRIMARY + " LIKE ? ";
                if(i == (searchQueryArgs.length-1)){
                    selPrefixPart += ") AND ";
                } else{
                    selPrefixPart += "AND ";
                }
            }

            String searchSelection = selPrefixPart +
                    ContactsContract.Data.MIMETYPE + " = '"
                    + Email.CONTENT_ITEM_TYPE + "'";

            String[] mSelectionArgs = new String[searchQueryArgs.length];
            for (int i=0;i<searchQueryArgs.length;i++) {
                mSelectionArgs[i] = "%"+searchQueryArgs[i]+"%";
            }

            return new CursorLoader(this,
                    ContactsContract.Data.CONTENT_URI,
                    EMAIL_PROJECTION,
                    searchSelection,
                    mSelectionArgs,
                    SORT_ORDER);

        }

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

    protected void openContactsInsert(View view) {
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(EditorActivity.EDITOR_TYPE, "insert");
        startActivityForResult(intent, NEW_REQUEST_CODE);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = mCursorAdapterEmail.getCursor();
        cursor.moveToPosition(position);
        String contactKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.LOOKUP_KEY));
        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));

        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        intent.putExtra(EditorActivity.CONTACT_LOOKUP, contactKey);
        intent.putExtra(EditorActivity.CONTACT_ID, contactId);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK){
            if(getLoaderManager().getLoader(EMAIL_QUERY_ID) != null) {
                restartLoader();
            } else {
                if(PermissionsService.getInstance().hasOrRequestContactsReadPerm(this)){
                    populateContacts();
                }
            }
        } else if (requestCode == NEW_REQUEST_CODE && resultCode == RESULT_OK){
            if(getLoaderManager().getLoader(EMAIL_QUERY_ID) != null){
                restartLoader();
            } else {
                if(PermissionsService.getInstance().hasOrRequestContactsReadPerm(this)){
                    populateContacts();
                }
            }
        } else if(requestCode == NEW_REQUEST_CODE){
            if(getLoaderManager().getLoader(EMAIL_QUERY_ID) == null && PermissionsService.getInstance().hasOrRequestContactsReadPerm(this)){
                populateContacts();
            }
        }
    }

}
