package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.myapp.Settings.SettingsContainer;
import com.example.myapp.Start.FragmentStartSuggestionProvider;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
        setContentView(R.layout.activity_main);

        //TODO: временный код для первоначальной инициализации SharedPreferences
        // убрать, когда будет закончен модуль Избранное
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("favorites", new HashSet(Arrays.asList(getResources().getStringArray(R.array.locations))));
        editor.putStringSet("search_history", new HashSet(Arrays.asList(getResources().getStringArray(R.array.debug_search_history))));
        editor.apply();

        String location = getResources().getString(R.string.DebugPoint);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Toast.makeText(getApplicationContext(), "Intent.ACTION_SEARCH", Toast.LENGTH_SHORT).show();
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
                    this,
                    FragmentStartSuggestionProvider.AUTHORITY,
                    FragmentStartSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            location = query;
        }

        initViews(location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        return true;
    }

    private void initViews(String location) {
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        drawerLayout = findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);

        //TODO: передать location аргументом в FragmentStart
        NavigationUI.setupWithNavController(navigationView, navController);
        // TODO: Почему то не работает. Отложил на будущее - разберусь.
        // NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
    }

    private void initTheme() {
        SettingsContainer sc = SettingsContainer.getInstance();
        if (sc.isThemeSystem) {
            setTheme(R.style.AppThemeSystem);
        }
        if (sc.isThemeLight) {
            setTheme(R.style.AppThemeLight);
        }
        if (sc.isThemeDark) {
            setTheme(R.style.AppThemeDark);
        }
    }
}