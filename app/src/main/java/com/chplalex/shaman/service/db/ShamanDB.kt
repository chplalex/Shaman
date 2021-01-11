package com.chplalex.shaman.service.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chplalex.shaman.mvp.model.db.Location
import com.chplalex.shaman.mvp.model.db.Request

@Database(entities = [Location::class, Request::class], version = 1)
abstract class ShamanDB : RoomDatabase() {

    abstract val shamanDao: ShamanDao

    companion object {
        const val DB_NAME = "shaman.db"
    }

}