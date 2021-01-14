package com.chplalex.shaman.ui

import android.app.Application
import com.chplalex.shaman.di.component.AppComponent
import com.chplalex.shaman.di.component.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        appComponent = DaggerAppComponent.create()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}