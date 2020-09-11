package com.example.myapp.DBService;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "locations")
public class Location {
    @PrimaryKey
    @ColumnInfo(name = "id", index = true)
    public long id;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "country")
    public String country;
    @ColumnInfo(name = "coord_lon")
    public float lon;
    @ColumnInfo(name = "coord_lat")
    public float lat;
    @ColumnInfo(name = "favorite")
    boolean favorite;

    public Location(long id, String name, String country, float lon, float lat) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.lon = lon;
        this.lat = lat;
        this.favorite = false;
    }
}
