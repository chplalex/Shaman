package com.chplalex.shaman.di.component

import com.chplalex.shaman.di.module.ActivityModule
import com.chplalex.shaman.di.scope.ActivityScope
import com.chplalex.shaman.mvp.presenter.PresenterAbout
import com.chplalex.shaman.mvp.presenter.PresenterFavorites
import com.chplalex.shaman.mvp.presenter.PresenterHistory
import com.chplalex.shaman.mvp.presenter.PresenterMap
import com.chplalex.shaman.mvp.presenter.PresenterSettings
import com.chplalex.shaman.mvp.presenter.PresenterStart
import com.chplalex.shaman.service.location.LocationService
import com.chplalex.shaman.ui.activity.MainActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent(
    modules = [
        ActivityModule::class
    ]
)

interface ActivityComponent {
    fun inject(activity : MainActivity)
    fun inject(presenter : PresenterAbout)
    fun inject(presenter : PresenterFavorites)
    fun inject(presenter : PresenterHistory)
    fun inject(presenter : PresenterMap)
    fun inject(presenter : PresenterSettings)
    fun inject(presenter : PresenterStart)
    fun inject(locationService : LocationService)
}