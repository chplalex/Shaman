package com.chplalex.shaman.di.component

import com.chplalex.shaman.di.module.ApiModule
import com.chplalex.shaman.di.module.AppModule
import com.chplalex.shaman.di.module.DbModule
import com.chplalex.shaman.mvp.presenter.PresenterFavorites
import com.chplalex.shaman.mvp.presenter.PresenterHistory
import com.chplalex.shaman.mvp.presenter.PresenterStart
import com.chplalex.shaman.service.api.OpenWeatherRetrofit
import com.chplalex.shaman.service.db.ShamanDB
import com.chplalex.shaman.service.db.ShamanDao
import dagger.Component
import io.reactivex.Scheduler
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApiModule::class,
        AppModule::class,
        DbModule::class
    ]
)

interface AppComponent {
//    @Named("UI")
//    fun getUiScheduler() : Scheduler
//    @Named("IO")
//    fun getIoScheduler() : Scheduler
//    fun getRetrofit() : OpenWeatherRetrofit
//    fun getDao() : ShamanDao
    fun inject(presenter: PresenterStart)
    fun inject(presenter: PresenterFavorites)
    fun inject(presenter: PresenterHistory)
}