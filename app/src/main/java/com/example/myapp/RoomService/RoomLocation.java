package com.example.myapp.RoomService;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity
public class RoomLocation {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", index = true)
    public long id;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "country")
    public String country;
    @ColumnInfo(name = "id_system")
    public int idSystem;
    @ColumnInfo(name = "coord_lon")
    public float lon;
    @ColumnInfo(name = "coord_lat")
    public float lat;
    @ColumnInfo(name = "favorite")
    boolean favorite;
}
