package com.example.myapp.WeatherData;

import android.content.res.Resources;

import com.example.myapp.R;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.myapp.Common.Utils.HPAS_IN_ONE_MMHG;

public class WeatherData {
    @SerializedName("timezone")
    @Expose
    public int timezone;
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("cod")
    @Expose
    public int cod;
    @SerializedName("coord")
    @Expose
    public Coord coord;
    @SerializedName("weather")
    @Expose
    public Weather[] weather;
    @SerializedName("main")
    @Expose
    public Main main;
    @SerializedName("wind")
    @Expose
    public Wind wind;
    @SerializedName("clouds")
    @Expose
    public Clouds clouds;
    @SerializedName("sys")
    @Expose
    public Sys sys;

    Resources resources;

    public WeatherData() {
        weather = new Weather[1];
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public String getName() {
        return name;
    }

    public String getCountry() { return sys.country; }

    public String getTemperature() {
        return String.format(Locale.getDefault(), "%+.0f°C", main.temp);
    }

    public String getPressure() {
        return String.format(Locale.getDefault(),
                "%d %s = %.0f %s",
                main.pressure,
                resources.getString(R.string.PressureUnit_hPa),
                main.pressure / HPAS_IN_ONE_MMHG,
                resources.getString(R.string.PressureUnit_mmHg));
    }

    public String getHumidity() {
        return String.format(Locale.getDefault(),
                "%d",
                main.humidity);
    }

    public String getSunMoving() {
        return String.format(Locale.getDefault(),
                "%s %s, %s %s",
                resources.getString(R.string.Sunrise),
                timeToString(sys.sunrise, timezone),
                resources.getString(R.string.Sunset),
                timeToString(sys.sunset, timezone));
    }

    public String getWind() {
        return String.format(Locale.getDefault(),
                "%s %.0f %s",
                windDegToAzimuth(wind.deg),
                wind.speed,
                resources.getString(R.string.WindSpeedUnit));
    }

    public String getDescription() {
        return weather[0].description;
    }

    private String windDegToAzimuth(int deg) {
        if (338 <= deg || deg < 23) {
            return resources.getString(R.string.wind_north);
        }
        if (23 <= deg && deg < 68) {
            return resources.getString(R.string.wind_north_east);
        }
        if (68 <= deg && deg < 113) {
            return resources.getString(R.string.wind_east);
        }
        if (113 <= deg && deg < 158) {
            return resources.getString(R.string.wind_south_east);
        }
        if (158 <= deg && deg < 203) {
            return resources.getString(R.string.wind_south);
        }
        if (203 <= deg && deg < 248) {
            return resources.getString(R.string.wind_south_east);
        }
        if (248 <= deg && deg < 293) {
            return resources.getString(R.string.wind_west);
        }
        if (293 <= deg && deg < 338) {
            return resources.getString(R.string.wind_north_west);
        }
        return String.format(Locale.getDefault(),
                "%s: %d",
                resources.getString(R.string.incorrect_data),
                deg);
    }

    private String timeToString(long unixSeconds, long unixSecondsDiff) {
        Date date = new Date(unixSeconds * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public int getImageResource() {
        if (weather[0].icon.equals("01d")) return R.drawable.ic_01d;
        if (weather[0].icon.equals("02d")) return R.drawable.ic_02d;
        if (weather[0].icon.equals("03d")) return R.drawable.ic_03d;
        if (weather[0].icon.equals("04d")) return R.drawable.ic_04d;
        if (weather[0].icon.equals("09d")) return R.drawable.ic_09d;
        if (weather[0].icon.equals("10d")) return R.drawable.ic_10d;
        if (weather[0].icon.equals("11d")) return R.drawable.ic_11d;
        if (weather[0].icon.equals("13d")) return R.drawable.ic_13d;
        if (weather[0].icon.equals("50d")) return R.drawable.ic_50d;
        if (weather[0].icon.equals("01n")) return R.drawable.ic_01n;
        if (weather[0].icon.equals("02n")) return R.drawable.ic_02n;
        if (weather[0].icon.equals("03n")) return R.drawable.ic_03n;
        if (weather[0].icon.equals("04n")) return R.drawable.ic_04n;
        if (weather[0].icon.equals("09n")) return R.drawable.ic_09n;
        if (weather[0].icon.equals("10n")) return R.drawable.ic_10n;
        if (weather[0].icon.equals("11n")) return R.drawable.ic_11n;
        if (weather[0].icon.equals("13n")) return R.drawable.ic_13n;
        if (weather[0].icon.equals("50n")) return R.drawable.ic_50n;
        return R.drawable.ic_report_problem;
    }

}
