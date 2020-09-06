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
import java.io.IOException;
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

public abstract class WeatherService <T> {

    protected void makeWeatherRequest(String spec, Type typeOfT) {
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
                    if (null != urlConnection) urlConnection.disconnect();
                }
                Log.d(LOGCAT_TAG, result);
            }).start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            handler.post(() -> EventBus.getDefault().post(new Event(EVENT_WEATHER_UPDATE_FAIL)));
        }
    }

    protected T getDataFromRequest(String spec, Type typeOfT) {
        HttpsURLConnection urlConnection = null;
        BufferedReader in = null;
        String result = "";
        try {
            final URL url = new URL(spec);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET"); // установка метода получения данных -GET
            urlConnection.setReadTimeout(READ_TIMEOUT); // установка таймаута
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток
            result = getLines(in);
            // преобразование данных запроса в контейнер
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
            try {
                if (in != null) in.close();
            }
                catch(IOException exception){
                    exception.printStackTrace();
                }
            }
            Log.d(LOGCAT_TAG, result);
            Gson gson = new Gson();
            return gson.fromJson(result, typeOfT);
        }

        public abstract void setData (@NotNull Object data);

        private String getLines (BufferedReader in){
            StringBuilder sb = new StringBuilder(1024);

            while (true) {
                try {
                    String s = in.readLine();
                    if (s == null) break;
                    sb.append(s).append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return sb.toString();
        }

    }
