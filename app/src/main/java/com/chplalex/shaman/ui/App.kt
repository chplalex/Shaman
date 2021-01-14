package com.chplalex.shaman.ui

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP_MR1
import android.util.Log
import androidx.room.Room
import com.chplalex.shaman.BuildConfig
import com.chplalex.shaman.service.api.BASE_URL
import com.chplalex.shaman.service.api.HTTP
import com.chplalex.shaman.service.api.HTTPS
import com.chplalex.shaman.service.api.OpenWeatherRetrofit
import com.chplalex.shaman.service.db.ShamanDB
import com.chplalex.shaman.service.db.ShamanDao
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class App : Application() {

//    lateinit var appComponent: AppComponent
//        private set

    private lateinit var db: ShamanDB
    private lateinit var openWeatherRetrofit: OpenWeatherRetrofit

    override fun onCreate() {
        super.onCreate()
        instance = this

//        appComponent = DaggerAppComponent.builder()
//            .appModule(AppModule())
//            .build()

//        appComponent = DaggerAppComponent.create()

        db = Room.databaseBuilder(applicationContext, ShamanDB::class.java, ShamanDB.DB_NAME)
                .build()

        val baseURL = if (SDK_INT > LOLLIPOP_MR1) {
            HTTPS + BASE_URL
        } else {
            HTTP + BASE_URL
        }

        val loggingInterceptor = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        val okClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

        openWeatherRetrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(OpenWeatherRetrofit::class.java)
    }

    val shamanDao: ShamanDao
        get() = db.shamanDao

    val retrofit: OpenWeatherRetrofit
        get() = openWeatherRetrofit

    companion object {
        lateinit var instance: App
            private set
    }
}