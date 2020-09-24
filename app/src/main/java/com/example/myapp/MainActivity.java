package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import static com.example.myapp.Common.Utils.LOGCAT_TAG;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private NavController navController;
    private WiFiChangeReceiver wifiChangeReceiver;
    public SharedPreferences sharedPreferences;

    private static final int PERMISSION_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiChangeReceiver = new WiFiChangeReceiver(getApplicationContext());
        sharedPreferences = getSharedPreferences(
                getResources().getString(R.string.file_name_prefs),
                Context.MODE_PRIVATE);
        initTheme();
        setContentView(R.layout.activity_main);
        initToken();
        initNotificationChannel();
        requestPermissions();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wifiChangeReceiver.registerNetworkCallback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wifiChangeReceiver.unregisterNetworkCallback();
    }

    private void initToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d(LOGCAT_TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        String token = task.getResult().getToken();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("pref_token", token);
                        editor.apply();
                    }
                });
    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("2", "name", importance);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initViews() {
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        drawerLayout = findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);

        NavigationUI.setupWithNavController(navigationView, navController);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
    }

    private void initTheme() {
        Resources resources = getResources();
        if (sharedPreferences.getBoolean(resources.getString(R.string.pref_theme_system), true)) {
            setTheme(R.style.AppThemeSystem);
        }
        if (sharedPreferences.getBoolean(resources.getString(R.string.pref_theme_light), false)) {
            setTheme(R.style.AppThemeLight);
        }
        if (sharedPreferences.getBoolean(resources.getString(R.string.pref_theme_dark), false)) {
            setTheme(R.style.AppThemeDark);
        }
    }

    private void requestPermissions() {
        // Проверим, есть ли Permission’ы, и если их нет, запрашиваем их у пользователя
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOGCAT_TAG, "Permissions is granted");
        } else {
            // Permission’ов нет, запрашиваем их у пользователя
            requestLocationPermissions();
        }
    }

    // Запрашиваем Permission’ы для геолокации
    private void requestLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            Log.d(LOGCAT_TAG, "Can't request permissions");
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    // Результат запроса Permission’а у пользователя:
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {   // Запрошенный нами Permission
            if (grantResults.length == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                            grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                Log.d(LOGCAT_TAG, "Permissions is granted");
                recreate();
            } else {
                Log.d(LOGCAT_TAG, "Permissions is not granted");
                finish();
            }
        }
    }
}