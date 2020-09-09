package com.example.myapp.Start;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapp.R;
import com.example.myapp.Settings.SettingsContainer;
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
import static com.example.myapp.Common.Utils.LOGCAT_TAG;

public class FragmentStart extends Fragment implements SearchView.OnQueryTextListener {

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

    private MenuItem searchItem;
    private SearchView searchView;
    private SearchRecentSuggestions suggestions;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        suggestions.saveRecentQuery(query, null);
        initViews(query);
        searchItem.collapseActionView();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    // внутренний класс для запроса погодных данных
    private class WeatherDataStart extends WeatherData {
        Main main;
        Weather[] weather;
        Wind wind;
        Sys sys;

        public void WeatherDataStart() {
            weather = new Weather[1];
        }

        public String getName() {
            return name;
        }

        public String getTemperature() {
            return String.format(Locale.getDefault(), "%+.0f°C", main.temp);
        }

        public String getPressure() {
            return String.format(Locale.getDefault(),
                    "%d %s = %.0f %s",
                    main.pressure,
                    getResources().getString(R.string.PressureUnit_hPa),
                    main.pressure / HPAS_IN_ONE_MMHG,
                    getResources().getString(R.string.PressureUnit_mmHg));
        }

        public String getHumidity() {
            return String.format(Locale.getDefault(),
                    "%d",
                    main.humidity);
        }

        public String getSunMoving() {
            return String.format(Locale.getDefault(),
                    "%s %s, %s %s",
                    getResources().getString(R.string.Sunrise),
                    timeToString(sys.sunrise, timezone),
                    getResources().getString(R.string.Sunset),
                    timeToString(sys.sunset, timezone));
        }

        public String getWind() {
            return String.format(Locale.getDefault(),
                    "%s %.0f %s",
                    windDegToAzimuth(wind.deg),
                    wind.speed,
                    getResources().getString(R.string.WindSpeedUnit));
        }

        public String getDescription() {
            return weather[0].description;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity fragmentActivity = getActivity();
        fragmentActivity.setTitle(R.string.label_start);

        suggestions = new SearchRecentSuggestions(
                fragmentActivity,
                FragmentStartSuggestionProvider.AUTHORITY,
                FragmentStartSuggestionProvider.MODE);

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

    // вариант 1. Рабочий. но некрасивый.
    private void initViews(String location) {
        new Thread(() -> {
            final OpenWeatherService weatherService = new OpenWeatherService();
            final WeatherDataStart wd = (WeatherDataStart) weatherService.getData(location, WeatherDataStart.class);
            if (wd == null) {
                final String name = getResources().getString(R.string.not_found_location_name);
                final String temperature = getResources().getString(R.string.not_found_location_temp);

                txtPoint.post(() -> txtPoint.setText(name));
                txtTemperature.post(() -> txtTemperature.setText(temperature));
                txtWeatherDescription.post(() -> txtWeatherDescription.setText(""));
                txtPressure.post(() -> txtPressure.setText(""));
                txtWind.post(() -> txtWind.setText(""));
                txtSunMoving.post(() -> txtSunMoving.setText(""));
                txtHumidity.post(() -> txtHumidity.setText(""));
            } else {
                final String name = wd.getName();
                final String temperature = wd.getTemperature();
                final String description = wd.getDescription();
                final String pressure = String.format(Locale.getDefault(),
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
                final String sunMoving = String.format(Locale.getDefault(),
                        "%s %s, %s %s",
                        getResources().getString(R.string.Sunrise),
                        timeToString(wd.sys.sunrise, wd.timezone),
                        getResources().getString(R.string.Sunset),
                        timeToString(wd.sys.sunset, wd.timezone));
                final String humidity = wd.getHumidity();

                txtPoint.post(() -> txtPoint.setText(name));
                txtTemperature.post(() -> txtTemperature.setText(temperature));
                txtWeatherDescription.post(() -> txtWeatherDescription.setText(description));
                txtPressure.post(() -> txtPressure.setText(pressure));
                txtWind.post(() -> txtWind.setText(wind));
                txtSunMoving.post(() -> txtSunMoving.setText(sunMoving));
                txtHumidity.post(() -> txtHumidity.setText(humidity));
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

    // Вариант 2. Красивее, но не работает
//    private void initViews(String location) {
//        new Thread(() -> {
//            final OpenWeatherService weatherService = new OpenWeatherService();
//            final WeatherDataStart wd = (WeatherDataStart) weatherService.getData(location, WeatherDataStart.class);
//            if (wd == null) {
//                final String name = getResources().getString(R.string.not_found_location_name);
//                final String temperature = getResources().getString(R.string.not_found_location_temp);
//
//                txtPoint.post(() -> txtPoint.setText(name));
//                txtTemperature.post(() -> txtTemperature.setText(temperature));
//                txtWeatherDescription.post(() -> txtWeatherDescription.setText(""));
//                txtPressure.post(() -> txtPressure.setText(""));
//                txtWind.post(() -> txtWind.setText(""));
//                txtSunMoving.post(() -> txtSunMoving.setText(""));
//                txtHumidity.post(() -> txtHumidity.setText(""));
//            } else {
//                final String name = wd.getName();
//                final String temperature = wd.getTemperature();
//                final String description = wd.getDescription();
//                final String pressure = wd.getPressure();
//                final String wind = wd.getWind();
//                final String sunMoving = wd.getSunMoving();
//                final String humidity = wd.getHumidity();
//
//                txtPoint.post(() -> txtPoint.setText(name));
//                txtTemperature.post(() -> txtTemperature.setText(temperature));
//                txtWeatherDescription.post(() -> txtWeatherDescription.setText(description));
//                txtPressure.post(() -> txtPressure.setText(pressure));
//                txtWind.post(() -> txtWind.setText(wind));
//                txtSunMoving.post(() -> txtSunMoving.setText(sunMoving));
//                txtHumidity.post(() -> txtHumidity.setText(humidity));
//            }
//        }).start();
//
//        SettingsContainer sc = SettingsContainer.getInstance();
//        if (sc.isChkBoxPressure) {
//            rowPressure.setVisibility(View.VISIBLE);
//        } else {
//            rowPressure.setVisibility(View.GONE);
//        }
//        if (sc.isChkBoxWind) {
//            rowWind.setVisibility(View.VISIBLE);
//        } else {
//            rowWind.setVisibility(View.GONE);
//        }
//        if (sc.isChkBoxSunMoving) {
//            rowSunMoving.setVisibility(View.VISIBLE);
//        } else {
//            rowSunMoving.setVisibility(View.GONE);
//        }
//        if (sc.isChkBoxHumidity) {
//            rowHumidity.setVisibility(View.VISIBLE);
//        } else {
//            rowHumidity.setVisibility(View.GONE);
//        }
//    }

    // Вариант 3. Еще красивее, но не работает
//    private void initViews(String location) {
//        new Thread(() -> {
//            final OpenWeatherService weatherService = new OpenWeatherService();
//            final WeatherDataStart wd = (WeatherDataStart) weatherService.getData(location, WeatherDataStart.class);
//            getActivity().runOnUiThread(() -> {
//                String name = "";
//                String temperature = "";
//                String description = "";
//                String pressure = "";
//                String wind = "";
//                String sunMoving = "";
//                String humidity = "";
//                if (wd == null) {
//                    name = getResources().getString(R.string.not_found_location_name);
//                    temperature = getResources().getString(R.string.not_found_location_temp);
//                } else {
//                    name = wd.getName();
//                    temperature = wd.getTemperature();
//                    description = wd.getDescription();
//                    pressure = wd.getPressure();
//                    wind = wd.getWind();
//                    sunMoving = wd.getSunMoving();
//                    humidity = wd.getHumidity();
//                }
//                txtPoint.setText(name);
//                txtTemperature.setText(temperature);
//                txtWeatherDescription.setText(description);
//                txtPressure.setText(pressure);
//                txtWind.setText(wind);
//                txtSunMoving.setText(sunMoving);
//                txtHumidity.setText(humidity);
//            });
//        }).start();
//
//        SettingsContainer sc = SettingsContainer.getInstance();
//        if (sc.isChkBoxPressure) {
//            rowPressure.setVisibility(View.VISIBLE);
//        } else {
//            rowPressure.setVisibility(View.GONE);
//        }
//        if (sc.isChkBoxWind) {
//            rowWind.setVisibility(View.VISIBLE);
//        } else {
//            rowWind.setVisibility(View.GONE);
//        }
//        if (sc.isChkBoxSunMoving) {
//            rowSunMoving.setVisibility(View.VISIBLE);
//        } else {
//            rowSunMoving.setVisibility(View.GONE);
//        }
//        if (sc.isChkBoxHumidity) {
//            rowHumidity.setVisibility(View.VISIBLE);
//        } else {
//            rowHumidity.setVisibility(View.GONE);
//        }
//    }

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