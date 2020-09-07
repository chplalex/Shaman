package com.example.myapp.WeatherService;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static com.example.myapp.Common.Utils.*;

public abstract class WeatherService <T> {

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
            Gson gson = new Gson();
            return gson.fromJson(result, typeOfT);
        }

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
