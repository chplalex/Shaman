package com.example.myapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

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

    private static final String TAG = "WEATHER";
    private static final String WEATHER_REQUEST = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&lang=RU";
    private static final String WEATHER_API_KEY = "bb18dcd129bad0dd351cdb2816a1aa9b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
        setContentView(R.layout.activity_main);
        initWeather();
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

}