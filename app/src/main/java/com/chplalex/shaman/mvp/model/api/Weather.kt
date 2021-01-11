package com.chplalex.shaman.mvp.model.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("id") @Expose
    var id: Int = 0,
    @SerializedName("main") @Expose
    var main: String = "",
    @SerializedName("description") @Expose
    var description: String = "",
    @SerializedName("icon") @Expose
    var icon: String = ""
)