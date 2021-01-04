package com.chplalex.shaman.mvp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Coord(
    @SerializedName("lat") @Expose
    var lat: Float = 0f,
    @SerializedName("lon") @Expose
    var lon: Float = 0f
)