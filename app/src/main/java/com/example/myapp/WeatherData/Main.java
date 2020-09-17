package com.example.myapp.WeatherData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {
    @SerializedName("temp")
    @Expose
    public float temp;
    @SerializedName("pressure")
    @Expose
    public int pressure;
    @SerializedName("humidity")
    @Expose
    public int humidity;
}
