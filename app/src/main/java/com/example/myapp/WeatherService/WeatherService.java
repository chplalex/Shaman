package com.example.myapp.WeatherService;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.myapp.Common.Event;
import com.example.myapp.Locations.AdapterLocations;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import static android.os.Build.VERSION_CODES.N;
import static com.example.myapp.Common.Utils.*;

public abstract class WeatherService {

    protected <T> void makeWeatherRequest(String spec, Type typeOfT) {
        final Handler handler = new Handler(); // Запоминаем основной поток
        try {
            final URL url = new URL(spec);
            new Thread(() -> {
                HttpsURLConnection urlConnection = null;
                String result = "Empty result";
                try {
                    urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET"); // установка метода получения данных -GET
                    urlConnection.setReadTimeout(READ_TIMEOUT); // установка таймаута
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток
                    result = getLines(in);
                    // преобразование данных запроса в контейнер
                    Gson gson = new Gson();
                    T data = gson.fromJson(result, typeOfT);
                    if (data == null) {
                        handler.post(() -> EventBus.getDefault().post(new Event(EVENT_WEATHER_UPDATE_FAIL)));
                    } else {
                        setData(data);
                        handler.post(() -> EventBus.getDefault().post(new Event(EVENT_WEATHER_UPDATE_DONE)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(() -> EventBus.getDefault().post(new Event(EVENT_WEATHER_UPDATE_FAIL)));
                } finally {
                    if (null != urlConnection) {
                        urlConnection.disconnect();
                    }
                }
                Log.d(LOGCAT_TAG, result);
            }).start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            handler.post(() -> EventBus.getDefault().post(new Event(EVENT_WEATHER_UPDATE_FAIL)));
        }
    }

    protected Object getDataFromRequest(String spec, Type typeOfT) {
        HttpsURLConnection urlConnection = null;
        String result = "";
        try {
            final URL url = new URL(spec);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET"); // установка метода получения данных -GET
            urlConnection.setReadTimeout(READ_TIMEOUT); // установка таймаута
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток
            result = in.readLine();
            // преобразование данных запроса в контейнер
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != urlConnection) {
                urlConnection.disconnect();
            }
        }
        Log.d(LOGCAT_TAG, result);
        Gson gson = new Gson();
        Object d = gson.fromJson(result, typeOfT);
        if (d == null) {
            try {
                d = Type.class.newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return d;
    }

    // TODO: Оставил на будущее.
    public void close() {

    }

    public abstract void setData(@NotNull Object data);

    @RequiresApi(api = N)
    private String getLines(BufferedReader in) {
        // TODO: in.lines() требует API 24. Переделать на 21.
        return in.lines().collect(Collectors.joining("\n"));
    }

}
