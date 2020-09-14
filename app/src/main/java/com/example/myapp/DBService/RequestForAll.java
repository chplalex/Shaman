package com.example.myapp.DBService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RequestForAll {
    public String name;
    public String country;
    public long time;
    public float temperature;
    public boolean favorite;

    public String getTimeString() {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public String getDateString() {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.YY", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public String getTemperatureString() {
        return String.format(Locale.getDefault(), "%+.0f°C", temperature);
    }

}
