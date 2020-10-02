package com.example.myapp.DBService;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Location.class, Request.class}, version = 1)
public abstract class ShamanDB extends RoomDatabase {
    public abstract ShamanDao getShamanDao();
}
