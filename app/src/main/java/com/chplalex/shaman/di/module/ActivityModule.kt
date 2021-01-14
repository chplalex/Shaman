package com.chplalex.shaman.di.module

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.res.Resources
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.chplalex.shaman.R
import com.chplalex.shaman.di.scope.ActivityScope
import com.chplalex.shaman.ui.activity.MainActivity
import com.chplalex.shaman.utils.SP_NAME
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ActivityModule(private val activity: MainActivity) {

    @ActivityScope
    @Provides
    fun sharedPreferences() = activity.getSharedPreferences(SP_NAME, MODE_PRIVATE)

    @ActivityScope
    @Named("actContext")
    @Provides
    fun context() : Context = activity

    @ActivityScope
    @Provides
    fun resourses() : Resources = activity.resources

    @ActivityScope
    @Provides
    fun navController() : NavController = Navigation.findNavController(activity, R.id.nav_host_fragment)

}