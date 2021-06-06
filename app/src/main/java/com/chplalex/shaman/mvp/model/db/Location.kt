package com.chplalex.shaman.mvp.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chplalex.shaman.mvp.model.api.WeatherData

@Entity(tableName = "locations")
data class Location(
        @field:PrimaryKey
        @field:ColumnInfo(name = "id", index = true) var id: Long,
        @field:ColumnInfo(name = "name") var name: String,
        @field:ColumnInfo(name = "country") var country: String,
        @field:ColumnInfo(name = "coord_lon") var lon: Float,
        @field:ColumnInfo(name = "coord_lat") var lat: Float,
        @field:ColumnInfo(name = "favorite") var favorite: Boolean
) {
    constructor(wd: WeatherData, favorite: Boolean) : this(
            wd.id.toLong(),
            wd.name,
            wd.sys.country,
            wd.coord.lon,
            wd.coord.lat,
            favorite)
}