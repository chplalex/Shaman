package com.chplalex.shaman.Common;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class Utils {
    public final static String LOGCAT_TAG = "mtvd";
    public final static float HPAS_IN_ONE_MMHG = 133.3224f / 100;
    // 10 секунд = время ожидания отклика сервера в миллисекундах
    public final static int READ_TIMEOUT = 10 * 1000;
    public final static String LOCATION_ARG_NAME = "location_name";
    public final static String LOCATION_ARG_COUNTRY = "location_country";

    public static void showToast(Context context, String msg) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        } else {
            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());
        }
    }

}
