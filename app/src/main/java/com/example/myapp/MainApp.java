package com.example.myapp;

import android.app.Application;

import androidx.room.Room;

import com.example.myapp.DBService.ShamanDB;
import com.example.myapp.DBService.ShamanDao;

public class MainApp extends Application {
    private static MainApp instance;
    private ShamanDB db;

    public static MainApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        db = Room.databaseBuilder(
                getApplicationContext(),
                ShamanDB.class,
                "shaman_database")
                .allowMainThreadQueries() //TODO: временно. в порядке тестирования.
                .build();
    }

    public ShamanDao getShamanDao() {
        return db.getShamanDao();
    }
}
