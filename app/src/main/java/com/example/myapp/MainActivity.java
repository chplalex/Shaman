package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.myapp.WeatherData.CurrentWeatherContainer;
import com.example.myapp.WeatherData.CurrentWeatherData;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import static android.os.Build.VERSION_CODES.N;
import static com.example.myapp.Utils.LOGCAT_TAG;
import static com.example.myapp.Utils.WEATHER_UPDATE_KEY;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private static final String TAG = "WEATHER";
    private static final String WEATHER_REQUEST = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&lang=RU&units=metric";
    private static final String WEATHER_API_KEY = "bb18dcd129bad0dd351cdb2816a1aa9b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
        setContentView(R.layout.activity_main);
        initWeather();
        initViews();
        setOnClickForSideMenuItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings: {
                Toast.makeText(getApplicationContext(), "Для перехода в настройки кликни город", Toast.LENGTH_LONG).show();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void initViews() {
        navigationView = findViewById(R.id.navigation_view);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initWeather() {
        try {
            String spec = String.format(WEATHER_REQUEST, "Moscow", WEATHER_API_KEY);
            final URL uri = new URL(spec);
            final Handler handler = new Handler(); // Запоминаем основной поток
            new Thread(() -> {
                HttpsURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpsURLConnection) uri.openConnection();
                    urlConnection.setRequestMethod("GET"); // установка метода получения данных -GET
                    urlConnection.setReadTimeout(10000); // установка таймаута - 10 000 миллисекунд
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток
                    String result = getLines(in);
                    Log.d(LOGCAT_TAG, result);
                    // преобразование данных запроса в модель
                    Gson gson = new Gson();
                    CurrentWeatherData wd = gson.fromJson(result, CurrentWeatherData.class);
                    if (wd != null) {
                        CurrentWeatherContainer.setData(wd);
                    }
                    // Возвращаемся к основному потоку
                    handler.post(() -> EventBus.getDefault().post(new MsgEvent(WEATHER_UPDATE_KEY)));
                } catch (Exception e) {
                    Log.e(LOGCAT_TAG, "Fail connection", e);
                    e.printStackTrace();
                } finally {
                    if (null != urlConnection) {
                        urlConnection.disconnect();
                    }
                }
            }).start();
        } catch (MalformedURLException e) {
            Log.e(TAG, "Fail URI", e);
            e.printStackTrace();
        }
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

    @RequiresApi(api = N)
    private String getLines(BufferedReader in) {
        return in.lines().collect(Collectors.joining("\n"));
    }

    private void setOnClickForSideMenuItems() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_accu_weather: {
                        Toast.makeText(getApplicationContext(), R.string.DebugMenuAccureWeather, Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    }
                    case R.id.nav_clima_cell: {
                        Toast.makeText(getApplicationContext(), R.string.DebugMenuClimaCell, Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    }
                    case R.id.nav_dark_sky_weather: {
                        Toast.makeText(getApplicationContext(), R.string.DebugMenuDarkSkyWeather, Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    }
                    case R.id.nav_weather_2020: {
                        Toast.makeText(getApplicationContext(), R.string.DebugMenuWeather2020, Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    }
                    case R.id.nav_weather_bit: {
                        Toast.makeText(getApplicationContext(), R.string.DebugMenuWeatherBit, Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    }
                    case R.id.nav_yandex_weather: {
                        Toast.makeText(getApplicationContext(), R.string.DebugMenuYandexWeather, Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    }
                    case R.id.nav_About: {
                        Toast.makeText(getApplicationContext(), R.string.DebugMenuAboutAuthor, Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    }
                }
                return true;
            }
        });
    }

}