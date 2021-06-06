package com.chplalex.shaman.di.module

import android.content.Context
import androidx.room.Room
import com.chplalex.shaman.di.scope.AppScope
import com.chplalex.shaman.service.db.ShamanDB
import com.chplalex.shaman.service.db.ShamanDao
import com.chplalex.shaman.ui.App.Companion.instance
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class DbModule {

    @AppScope
    @Provides
    fun dao(@Named("appContext") appContext : Context) : ShamanDao = Room.databaseBuilder(appContext, ShamanDB::class.java, ShamanDB.DB_NAME)
        .build()
        .shamanDao

    @Named("appContext")
    @Provides
    fun appContext() : Context = instance.applicationContext

}