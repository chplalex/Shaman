package com.chplalex.shaman.DBService

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Location::class, Request::class], version = 1)
abstract class ShamanDB : RoomDatabase() {

    abstract val shamanDao: ShamanDao

    companion object {
        const val DB_NAME = "shaman.db"
    }

}