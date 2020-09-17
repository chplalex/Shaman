package com.example.myapp.WeatherData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sys {
    @SerializedName("country")
    @Expose
    public String country;
    @SerializedName("sunrise")
    @Expose
    public long sunrise;
    @SerializedName("sunset")
    @Expose
    public long sunset;
}
