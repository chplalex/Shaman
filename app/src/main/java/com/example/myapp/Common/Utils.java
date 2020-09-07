package com.example.myapp.Common;

public class Utils {
    public final static String LOGCAT_TAG = "mtvd";
    public final static String SETTINGS_UPDATE_KEY = "settings_key";
    public final static float HPAS_IN_ONE_MMHG = 133.3224f / 100;
    // 5 минут = интервал между запросами на сервер в миллисекундах
    public final static int REQUEST_INTERVAL = 5 * 60 * 1000;
    // 10 секунд = время ожидания отклика сервера в миллисекундах
    public final static int READ_TIMEOUT = 10 * 1000;

    public final static int EVENT_WEATHER_UPDATE_DONE = 1;
    public final static int EVENT_WEATHER_UPDATE_FAIL = 2;
    public final static String LOCATION_ARG = "location";
}
