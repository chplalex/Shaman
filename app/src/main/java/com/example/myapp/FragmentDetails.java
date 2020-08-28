package com.example.myapp;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.myapp.WeatherData.CurrentWeatherContainer;
import com.example.myapp.WeatherData.CurrentWeatherData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static com.example.myapp.Utils.HPAS_IN_ONE_MMHG;
import static com.example.myapp.Utils.LOGCAT_TAG;
import static com.example.myapp.Utils.SETTINGS_KEY;
import static com.example.myapp.Utils.WEATHER_UPDATE_KEY;

public class FragmentDetails extends Fragment {

    // это поле всегда на экране
    private ImageView imgWeather;
    private TextView txtWeatherDescription;

    // эти поля видны при выборе пользователем установок
    private TableRow rowPressure;
    private TextView txtPressure;

    private TableRow rowWind;
    private TextView txtWind;

    private TableRow rowSun;
    private TextView txtSun;

    private TableRow rowHumidity;
    private TextView txtHumidity;

    // внутренний класс для сохранения данных активити
    private static class DataContainer {

        private static DataContainer instance;

        public CharSequence csWeatherDescription;
        public CharSequence csPressure;
        public CharSequence csWind;
        public CharSequence csSunMoving;
        public CharSequence csHumidity;

        public static DataContainer getInstance() {
            if (instance == null) {
                instance = new DataContainer();
            }
            return instance;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initContainers();
        findViewsById(view);
        initViews();
    }

    private void initContainers() {
        CurrentWeatherData wd = CurrentWeatherContainer.getData();
        DataContainer dc = DataContainer.getInstance();
        dc.csWeatherDescription = wd.weather[0].description;
        dc.csWind = String.format(Locale.getDefault(),
                "%s %.0f %s",
                windDegToAzimuth(wd.wind.deg),
                wd.wind.speed,
                getResources().getString(R.string.WindSpeedUnit));
        dc.csPressure = String.format(Locale.getDefault(),
                "%d %s = %.0f %s",
                wd.main.pressure,
                getResources().getString(R.string.PressureUnit_hPa),
                wd.main.pressure / HPAS_IN_ONE_MMHG,
                getResources().getString(R.string.PressureUnit_mmHg));
        dc.csHumidity = String.format(Locale.getDefault(),
                "%d",
                wd.main.humidity);
        dc.csSunMoving = String.format(Locale.getDefault(),
                "%s %s, %s %s",
                getResources().getString(R.string.Sunrise),
                timeToString(wd.sys.sunrise, wd.timezone),
                getResources().getString(R.string.Sunset),
                timeToString(wd.sys.sunset, wd.timezone));
    }

    private String timeToString(long unixSeconds, long unixSecondsDiff) {
        Date date = new Date(unixSeconds * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(date);
    }

    private String windDegToAzimuth(int deg) {
        if (338 <= deg || deg < 23) {
            return getResources().getString(R.string.wind_north);
        }
        if (23 <= deg && deg < 68) {
            return getResources().getString(R.string.wind_north_east);
        }
        if (68 <= deg && deg < 1138) {
            return getResources().getString(R.string.wind_east);
        }
        if (113 <= deg && deg < 158) {
            return getResources().getString(R.string.wind_south_east);
        }
        if (158 <= deg && deg < 203) {
            return getResources().getString(R.string.wind_south);
        }
        if (203 <= deg && deg < 248) {
            return getResources().getString(R.string.wind_south_east);
        }
        if (248 <= deg && deg < 293) {
            return getResources().getString(R.string.wind_west);
        }
        if (293 <= deg && deg < 338) {
            return getResources().getString(R.string.wind_north_west);
        }
        return String.format("%s: %i",
                getResources().getString(R.string.incorrect_data),
                deg);
    }

    @SuppressLint("ResourceType")
    private void findViewsById(View view) {
        imgWeather = view.findViewById(R.id.imgWeather);
        txtWeatherDescription = view.findViewById(R.id.txtWeather);

        rowPressure = view.findViewById(R.id.rowPressure);
        txtPressure = view.findViewById(R.id.txtPressure);

        rowWind = view.findViewById(R.id.rowWind);
        txtWind = view.findViewById(R.id.txtWind);

        rowSun = view.findViewById(R.id.rowSun);
        txtSun = view.findViewById(R.id.txtSun);

        rowHumidity = view.findViewById(R.id.rowHumidity);
        txtHumidity = view.findViewById(R.id.txtHumidity);
    }

    void initViews() {
        DataContainer dc = DataContainer.getInstance();
        txtWeatherDescription.setText(dc.csWeatherDescription);
        txtPressure.setText(dc.csPressure);
        txtWind.setText(dc.csWind);
        txtSun.setText(dc.csSunMoving);
        txtHumidity.setText(dc.csHumidity);

        SettingsContainer sc = SettingsContainer.getInstance();
        if (sc.isChkBoxPressure) {
            rowPressure.setVisibility(View.VISIBLE);
        } else {
            rowPressure.setVisibility(View.GONE);
        }
        if (sc.isChkBoxWind) {
            rowWind.setVisibility(View.VISIBLE);
        } else {
            rowWind.setVisibility(View.GONE);
        }
        if (sc.isChkBoxSunMoving) {
            rowSun.setVisibility(View.VISIBLE);
        } else {
            rowSun.setVisibility(View.GONE);
        }
        if (sc.isChkBoxHumidity) {
            rowHumidity.setVisibility(View.VISIBLE);
        } else {
            rowHumidity.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgEvent(MsgEvent msgEvent) {
        if (msgEvent.msg.equals(SETTINGS_KEY)) {
            initViews();
        }
        if (msgEvent.msg.equals(WEATHER_UPDATE_KEY)) {
            initContainers();
            initViews();
        }

    }
}
