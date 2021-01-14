package com.chplalex.shaman.di.module

import com.chplalex.shaman.BuildConfig
import com.chplalex.shaman.service.api.BASE_URL
import com.chplalex.shaman.service.api.OpenWeatherRetrofit
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApiModule {

    @Singleton
    @Provides
    fun retrofit(okClient: OkHttpClient): OpenWeatherRetrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(OpenWeatherRetrofit::class.java)
    }

    @Provides
    fun okHttpClient(loggingInterceptor: HttpLoggingInterceptor) = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    @Provides
    fun httpLoggingInterceptor() = HttpLoggingInterceptor().also {
        it.level = if (BuildConfig.DEBUG) {
            BODY
        } else {
            NONE
        }
    }

}