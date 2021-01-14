package com.chplalex.shaman.di.module

import android.content.Context
import androidx.room.Room
import com.chplalex.shaman.service.db.ShamanDB
import com.chplalex.shaman.service.db.ShamanDao
import com.chplalex.shaman.ui.App.Companion.instance
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbModule {

    @Singleton
    @Provides
    fun dao(appContext : Context) : ShamanDao = Room.databaseBuilder(appContext, ShamanDB::class.java, ShamanDB.DB_NAME)
        .build()
        .shamanDao

    @Provides
    fun appContext() : Context = instance.applicationContext

}