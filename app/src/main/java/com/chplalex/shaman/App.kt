package com.chplalex.shaman

import android.app.Application
import androidx.room.Room
import com.chplalex.shaman.DBService.ShamanDB
import com.chplalex.shaman.DBService.ShamanDao

class App : Application() {

    private lateinit var db: ShamanDB

    override fun onCreate() {
        super.onCreate()
        instance = this
        db = Room.databaseBuilder(applicationContext, ShamanDB::class.java, "shaman.db")
            .allowMainThreadQueries() //TODO: временно. в порядке тестирования.
            .build()
    }

    val shamanDao: ShamanDao
        get() = db.shamanDao

    companion object {

        @JvmStatic lateinit var instance: App
            private set
    }
}