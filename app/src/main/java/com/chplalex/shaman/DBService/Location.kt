package com.chplalex.shaman.DBService

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
class Location(
    @field:PrimaryKey
    @field:ColumnInfo(name = "id", index = true) var id: Long,
    @field:ColumnInfo(name = "name") var name: String,
    @field:ColumnInfo(name = "country") var country: String,
    @field:ColumnInfo(name = "coord_lon") var lon: Float,
    @field:ColumnInfo(name = "coord_lat") var lat: Float
) {
    @ColumnInfo(name = "favorite")
    var favorite = false
}