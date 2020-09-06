package com.example.myapp.Start;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.myapp.Common.Event;
import com.example.myapp.R;
import com.example.myapp.Settings.SettingsContainer;
import com.example.myapp.WeatherData.CurrentWeatherContainer;
import com.example.myapp.WeatherData.CurrentWeatherData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.myapp.Common.Utils.HPAS_IN_ONE_MMHG;
import static com.example.myapp.Common.Utils.EVENT_WEATHER_UPDATE_DONE;

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

    // внутренний класс для сохранения данных активити
    private static class DataContainer {

        private static DataContainer instance;

        public CharSequence csPoint;
        public CharSequence csTemperature;
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
        if (savedInstanceState == null) {
            // Фрагмент создается впервые.
            initDataContainerFromWeatherContainer();
        }
        findViewsById(view);
        initViews();
    }

    private void initDataContainerFromWeatherContainer() {
        DataContainer dc = DataContainer.getInstance();
        // TODO:
        String[] arrPoints = getResources().getStringArray(R.array.locations);
        int index = getResources().getInteger(R.integer.DebugPointIndex);
        dc.csPoint = arrPoints[index];
        CurrentWeatherData wd = CurrentWeatherContainer.getData();
        dc.csTemperature = String.format(Locale.getDefault(), "%+.0f°C", wd.main.temp);
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

    private void initViews() {
        DataContainer dc = DataContainer.getInstance();

        txtPoint.setText(dc.csPoint);
        txtTemperature.setText(dc.csTemperature);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvForecasts.setLayoutManager(layoutManager);
        rvForecasts.setHasFixedSize(true);
        ForecastsAdapter adapter = new ForecastsAdapter(getResources().getStringArray(R.array.debug_forecasts_array));
        rvForecasts.setAdapter(adapter);

        txtWeatherDescription.setText(dc.csWeatherDescription);
        txtPressure.setText(dc.csPressure);
        txtWind.setText(dc.csWind);
        txtSunMoving.setText(dc.csSunMoving);
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
    public void onMsgEvent(Event msgEvent) {
        if (msgEvent.key == EVENT_WEATHER_UPDATE_DONE) {
            initDataContainerFromWeatherContainer();
            initViews();
        }
    }
}