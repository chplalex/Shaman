package com.chplalex.shaman.di.component

import com.chplalex.shaman.di.module.ApiModule
import com.chplalex.shaman.di.module.AppModule
import com.chplalex.shaman.mvp.presenter.PresenterFavorites
import com.chplalex.shaman.mvp.presenter.PresenterStart
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApiModule::class,
        AppModule::class
    ]
)

interface AppComponent {
    fun inject(presenterStart: PresenterStart)
    fun inject(presenterFavorites: PresenterFavorites)
}