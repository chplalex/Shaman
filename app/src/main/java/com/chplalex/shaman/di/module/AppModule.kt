package com.chplalex.shaman.di.module

import com.chplalex.shaman.di.scope.AppScope
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Named

@Module
class AppModule() {

    @AppScope
    @Named("UI")
    @Provides
    fun uiScheduler() : Scheduler = AndroidSchedulers.mainThread()

    @Named("IO")
    @Provides
    fun ioScheduler() : Scheduler = Schedulers.io()

    @AppScope
    @Provides
    fun disposable() : CompositeDisposable = CompositeDisposable()

}