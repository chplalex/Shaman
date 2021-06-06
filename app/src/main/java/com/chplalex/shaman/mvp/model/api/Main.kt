package com.chplalex.shaman.mvp.model.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("temp") @Expose
    var temp: Float = 0f,
    @SerializedName("pressure") @Expose
    var pressure: Int = 0,
    @SerializedName("humidity") @Expose
    var humidity: Int = 0
)