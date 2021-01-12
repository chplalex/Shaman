package com.chplalex.shaman.di.module

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP_MR1
import com.chplalex.shaman.service.api.BASE_URL
import com.chplalex.shaman.service.api.HTTP
import com.chplalex.shaman.service.api.HTTPS
import com.chplalex.shaman.service.api.OpenWeatherRetrofit
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApiModule {

    private val baseURL = if (SDK_INT > LOLLIPOP_MR1) { HTTPS + BASE_URL } else { HTTP + BASE_URL }

    @Singleton
    @Provides
    fun retrofit() = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(OpenWeatherRetrofit::class.java)

}