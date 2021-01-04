package com.chplalex.shaman.mvp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Sys(
    @SerializedName("country") @Expose
    var country: String = "",
    @SerializedName("sunrise") @Expose
    var sunrise: Long = 0,
    @SerializedName("sunset") @Expose
    var sunset: Long = 0
)