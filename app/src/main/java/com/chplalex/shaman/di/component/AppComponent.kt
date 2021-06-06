package com.chplalex.shaman.di.component

import com.chplalex.shaman.di.module.ActivityModule
import com.chplalex.shaman.di.module.ApiModule
import com.chplalex.shaman.di.module.AppModule
import com.chplalex.shaman.di.module.DbModule
import com.chplalex.shaman.di.scope.AppScope
import dagger.Component

@AppScope
@Component(
    modules = [
        ApiModule::class,
        AppModule::class,
        DbModule::class
    ]
)

interface AppComponent {
    fun createActivityComponent(activityModule : ActivityModule) : ActivityComponent
}