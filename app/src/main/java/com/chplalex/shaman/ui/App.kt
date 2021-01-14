package com.chplalex.shaman.ui

import android.app.Application
import com.chplalex.shaman.di.component.ActivityComponent
import com.chplalex.shaman.di.component.AppComponent
import com.chplalex.shaman.di.component.DaggerAppComponent
import com.chplalex.shaman.di.module.ActivityModule
import com.chplalex.shaman.ui.activity.MainActivity

class App : Application() {

    lateinit var appComponent: AppComponent
        private set

    var activityComponent: ActivityComponent? = null
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        appComponent = DaggerAppComponent.create()
    }

    fun createActivityComponent(activity: MainActivity) {
        activityComponent = appComponent.createActivityComponent(ActivityModule(activity))
    }

    fun destroyActivityComponent() {
        activityComponent = null
    }

    companion object {

        lateinit var instance: App
            private set
    }
}