package com.chplalex.shaman.mvp.model.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all") @Expose
    var all: Int = 0
)