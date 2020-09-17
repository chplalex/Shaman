package com.example.myapp.DBService;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "requests", indices = {@Index(value = {"location_id", "time"})})
public class Request {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;
    @ColumnInfo(name = "location_id")
    public long locationId;
    @ColumnInfo(name = "time", index = true)
    public long time;
    @ColumnInfo(name = "temperature")
    public float temperature;

    public Request(long locationId, float temperature) {
        this.locationId = locationId;
        this.temperature = temperature;
        this.time = System.currentTimeMillis();
    }

}
