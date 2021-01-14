package com.chplalex.shaman.di.module

import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named

@Module
class AppModule() {

    @Named("UI")
    @Provides
    fun uiScheduler() = AndroidSchedulers.mainThread()

    @Named("IO")
    @Provides
    fun ioScheduler() = Schedulers.io()

}