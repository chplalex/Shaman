package com.chplalex.shaman.di.module

import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Module
class AppModule() {

    @Provides
    fun uiScheduler() = AndroidSchedulers.mainThread()

    @Provides
    fun ioScheduler() = Schedulers.io()

}