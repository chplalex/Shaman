package com.chplalex.shaman.mvp.model.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Wind(
    @SerializedName("speed") @Expose
    var speed: Float = 0f,
    @SerializedName("deg") @Expose
    var deg: Int = 0
)