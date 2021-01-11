package com.chplalex.shaman.mvp.model.db

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RequestForAll {

    var name: String = ""
    var country: String = ""
    var time: Long = 0
    var temperature = 0f
    var favorite = false
    val timeString: String
        get() {
            val date = Date(time)
            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return simpleDateFormat.format(date)
        }
    val dateString: String
        get() {
            val date = Date(time)
            val simpleDateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            return simpleDateFormat.format(date)
        }
    val temperatureString: String
        get() = String.format(Locale.getDefault(), "%+.0f°C", temperature)
}