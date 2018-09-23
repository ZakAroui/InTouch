package com.ikazme.intouch.database;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by ikazme
 */

public class SearchSuggestionsProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "com.ikazme.intouch.database.SearchSuggestionsProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

}
