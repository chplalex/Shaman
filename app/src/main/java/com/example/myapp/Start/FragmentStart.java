package com.example.myapp.Start;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.myapp.R;
import com.example.myapp.Settings.SettingsContainer;
import com.example.myapp.WeatherData.CurrentWeatherContainer;
import com.example.myapp.WeatherData.CurrentWeatherData;
import com.example.myapp.WeatherData.Main;
import com.example.myapp.WeatherData.Sys;
import com.example.myapp.WeatherData.Weather;
import com.example.myapp.WeatherData.WeatherData;
import com.example.myapp.WeatherData.Wind;
import com.example.myapp.WeatherService.OpenWeatherService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.myapp.Common.Utils.HPAS_IN_ONE_MMHG;
import static com.example.myapp.Common.Utils.LOCATION_ARG;

public class FragmentStart extends Fragment {

    // эти поля всегда на экране
    private TextView txtPoint;
    private ImageView imgYandexWheather;
    private TextView txtTemperature;
    private RecyclerView rvForecasts;
    private ImageView imgWeather;
    private TextView txtWeatherDescription;

    // эти поля видны при выборе пользователем установок
    private TableRow rowPressure;
    private TextView txtPressure;

    private TableRow rowWind;
    private TextView txtWind;

    private TableRow rowSunMoving;
    private TextView txtSunMoving;

    private TableRow rowHumidity;
    private TextView txtHumidity;

    // внутренний класс для запроса погодных данных
    private class WeatherDataStart extends WeatherData {
        Main main;
        Weather[] weather;
        Wind wind;
        Sys sys;

        public void WeatherDataStart () {
            weather = new Weather[1];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity fa = getActivity();
        if (fa != null) {
            fa.setTitle(R.string.label_start);
        }

        findViewsById(view);
        initViews(getWeatherLocation());
    }

    private String getWeatherLocation() {
        Bundle arguments = getArguments();
        if (arguments == null || arguments.getCharSequence(LOCATION_ARG) == null) {
            return getResources().getString(R.string.DebugPoint);
        } else {
            return arguments.getCharSequence(LOCATION_ARG).toString();
        }
    }

    private void findViewsById(View view) {
        txtPoint = view.findViewById(R.id.txtPoint);
        imgYandexWheather = view.findViewById(R.id.imgYandexWeather);
        txtTemperature = view.findViewById(R.id.txtTemperature);
        rvForecasts = view.findViewById(R.id.rvForecasts);
        imgWeather = view.findViewById(R.id.imgWeather);

        txtWeatherDescription = view.findViewById(R.id.txtWeather);

        rowPressure = view.findViewById(R.id.rowPressure);
        txtPressure = view.findViewById(R.id.txtPressure);

        rowWind = view.findViewById(R.id.rowWind);
        txtWind = view.findViewById(R.id.txtWind);

        rowSunMoving = view.findViewById(R.id.rowSunMoving);
        txtSunMoving = view.findViewById(R.id.txtSunMoving);

        rowHumidity = view.findViewById(R.id.rowHumidity);
        txtHumidity = view.findViewById(R.id.txtHumidity);
    }

    private void initViews(String location) {
        new Thread(() -> {
            OpenWeatherService weatherService = new OpenWeatherService();
            CurrentWeatherData wd = (CurrentWeatherData) weatherService.getData(location, CurrentWeatherData.class);
            if (wd == null) {
                final String name = getResources().getString(R.string.not_found_location_name);
                final String temp = getResources().getString(R.string.not_found_location_temp);
                txtPoint.post(() -> txtPoint.setText(name));
                txtTemperature.post(() -> txtTemperature.setText(temp));
                txtWeatherDescription.post(() -> txtWeatherDescription.setText(""));
                txtPressure.post(() -> txtPressure.setText(""));
                txtWind.post(() -> txtWind.setText(""));
                txtSunMoving.post(() -> txtSunMoving.setText(""));
                txtHumidity.post(() -> txtHumidity.setText(""));
            } else {
                final String name = wd.name;
                final String temp = String.format(Locale.getDefault(), "%+.0f°C", wd.main.temp);
                final String desc = wd.weather[0].description;
                final String pres = String.format(Locale.getDefault(),
                        "%d %s = %.0f %s",
                        wd.main.pressure,
                        getResources().getString(R.string.PressureUnit_hPa),
                        wd.main.pressure / HPAS_IN_ONE_MMHG,
                        getResources().getString(R.string.PressureUnit_mmHg));
                final String wind = String.format(Locale.getDefault(),
                        "%s %.0f %s",
                        windDegToAzimuth(wd.wind.deg),
                        wd.wind.speed,
                        getResources().getString(R.string.WindSpeedUnit));
                final String sunm = String.format(Locale.getDefault(),
                        "%s %s, %s %s",
                        getResources().getString(R.string.Sunrise),
                        timeToString(wd.sys.sunrise, wd.timezone),
                        getResources().getString(R.string.Sunset),
                        timeToString(wd.sys.sunset, wd.timezone));
                final String humi = String.format(Locale.getDefault(),
                        "%d",
                        wd.main.humidity);
                txtPoint.post(() -> txtPoint.setText(name));
                txtTemperature.post(() -> txtTemperature.setText(temp));
                txtWeatherDescription.post(() -> txtWeatherDescription.setText(desc));
                txtPressure.post(() -> txtPressure.setText(pres));
                txtWind.post(() -> txtWind.setText(wind));
                txtSunMoving.post(() -> txtSunMoving.setText(sunm));
                txtHumidity.post(() -> txtHumidity.setText(humi));
            }
        }).start();

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
            rowSunMoving.setVisibility(View.VISIBLE);
        } else {
            rowSunMoving.setVisibility(View.GONE);
        }
        if (sc.isChkBoxHumidity) {
            rowHumidity.setVisibility(View.VISIBLE);
        } else {
            rowHumidity.setVisibility(View.GONE);
        }
    }

    private String timeToString(long unixSeconds, long unixSecondsDiff) {
        Date date = new Date(unixSeconds * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    private String windDegToAzimuth(int deg) {
        if (338 <= deg || deg < 23) {
            return getResources().getString(R.string.wind_north);
        }
        if (23 <= deg && deg < 68) {
            return getResources().getString(R.string.wind_north_east);
        }
        if (68 <= deg && deg < 113) {
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
        return String.format(Locale.getDefault(),
                "%s: %d",
                getResources().getString(R.string.incorrect_data),
                deg);
    }
}