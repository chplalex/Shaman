package com.example.myapp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.myapp.WeatherService.OpenWeatherService;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private NavController navController;
    private OpenWeatherService weatherService;

    private static final String TAG = "WEATHER";
    private static final String WEATHER_REQUEST = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&lang=RU&units=metric";
    private static final String WEATHER_API_KEY = "bb18dcd129bad0dd351cdb2816a1aa9b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
        setContentView(R.layout.activity_main);
        initWeatherService();
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doneWeatherService();
    }

    private void initViews() {
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        drawerLayout = findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);
        NavigationUI.setupWithNavController(navigationView, navController);
        // Почему то не работает. Отложил на будущее - разберусь.
        // NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
    }

    private void initWeatherService() {
        weatherService = new OpenWeatherService();
        weatherService.requestCurrent();
    }

    private void doneWeatherService() {
        if (weatherService != null) weatherService.close();
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