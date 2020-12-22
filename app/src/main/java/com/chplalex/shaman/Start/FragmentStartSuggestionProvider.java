package com.chplalex.shaman.Start;

import android.content.SearchRecentSuggestionsProvider;

public class FragmentStartSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.example.myapp.Start.FragmentStartSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public FragmentStartSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
