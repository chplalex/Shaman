package com.chplalex.shaman.di.component

import com.chplalex.shaman.di.module.ActivityModule
import com.chplalex.shaman.di.scope.ActivityScope
import com.chplalex.shaman.service.location.LocationService
import com.chplalex.shaman.ui.activity.MainActivity
import com.chplalex.shaman.ui.fragment.FragmentAbout
import com.chplalex.shaman.ui.fragment.FragmentFavorites
import com.chplalex.shaman.ui.fragment.FragmentHistory
import com.chplalex.shaman.ui.fragment.FragmentMap
import com.chplalex.shaman.ui.fragment.FragmentSettings
import com.chplalex.shaman.ui.fragment.FragmentStart
import dagger.Subcomponent

@ActivityScope
@Subcomponent(
    modules = [
        ActivityModule::class
    ]
)

interface ActivityComponent {
    fun inject(activity : MainActivity)
    fun inject(fragment : FragmentAbout)
    fun inject(fragment : FragmentFavorites)
    fun inject(fragment : FragmentHistory)
    fun inject(fragment : FragmentMap)
    fun inject(fragment : FragmentSettings)
    fun inject(fragment : FragmentStart)
    fun inject(locationService : LocationService)
}