package com.chplalex.shaman.mvp.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "requests", indices = [Index(value = ["location_id", "time"])])
class Request(
    @field:ColumnInfo(name = "location_id")
    var locationId: Long,
    @field:ColumnInfo(name = "temperature")
    var temperature: Float
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
    @ColumnInfo(name = "time", index = true)
    var time: Long = System.currentTimeMillis()
}
