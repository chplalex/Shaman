package com.chplalex.shaman.Favorites

import android.app.Activity
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.chplalex.shaman.ui.App.Companion.instance
import com.chplalex.shaman.Common.Utils
import com.chplalex.shaman.DBService.Location
import com.chplalex.shaman.DBService.ShamanDao
import com.chplalex.shaman.R
import com.chplalex.shaman.WeatherData.WeatherData
import com.chplalex.shaman.WeatherService.OpenWeatherRetrofit
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdapterFavorites(
    private val activity: Activity,
    sharedPreferences: SharedPreferences
) :
    RecyclerView.Adapter<AdapterFavorites.ViewHolder>() {

    private val shamanDao: ShamanDao = instance.shamanDao
    private val locations: MutableList<Location>
    private val openWeatherRetrofit: OpenWeatherRetrofit
    private val lang: String
    private val units: String

    init {
        locations = shamanDao.favoriteLocations

        val baseURL = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            OpenWeatherRetrofit.HTTPS + OpenWeatherRetrofit.BASE_URL
        else
            OpenWeatherRetrofit.HTTP + OpenWeatherRetrofit.BASE_URL

        openWeatherRetrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherRetrofit::class.java)

        lang = sharedPreferences.getString("pref_lang", "RU") ?: "RU"
        units = sharedPreferences.getString("pref_units", "metric") ?: "metric"
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.fragment_favorites_item, viewGroup, false)
    )

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.location = locations[i]
        viewHolder.requestOpenWeatherRetrofit()
    }

    override fun getItemCount() = locations.size

    inner class ViewHolder internal constructor(private val view: View) : RecyclerView.ViewHolder(view) {

        var location: Location? = null

        private val txtFavoriteName: MaterialTextView = itemView.findViewById(R.id.txtFavoriteName)
        private val txtFavoriteCountry: MaterialTextView = itemView.findViewById(R.id.txtFavoriteCountry)
        private val txtTemperature: MaterialTextView = itemView.findViewById(R.id.txtTemperature)
        private val imgWeatherIcon: ShapeableImageView = itemView.findViewById(R.id.imgWeatherIcon)
        private val btnFavoriteDelete: ImageButton = itemView.findViewById(R.id.btnFavoriteDelete)

        init {
            view.setOnClickListener { view: View? ->
                val bundle = Bundle()
                location?.let {
                    bundle.putCharSequence(Utils.LOCATION_ARG_NAME, it.name)
                    bundle.putCharSequence(Utils.LOCATION_ARG_COUNTRY, it.country)
                }
                view?.let { Navigation.findNavController(it).navigate(R.id.actionStart, bundle) }
            }
            btnFavoriteDelete.setOnClickListener {
                location?.let {
                    it.favorite = false
                    Thread { shamanDao.updateLocation(it) }.start()
                    locations.remove(it)
                    notifyItemRemoved(adapterPosition)
                }
            }
        }

        private fun initViewsByGoodResponse(wd: WeatherData) {
            wd.setResources(view.resources)
            txtFavoriteName.text = wd.getName()
            txtFavoriteCountry.text = wd.country
            imgWeatherIcon.setImageResource(wd.imageResource)
            txtTemperature.text = wd.tempString
        }

        private fun initViewsByFailResponse() {
            txtFavoriteName.text = view.resources.getString(R.string.not_found_location_name)
            txtFavoriteCountry.text = view.resources.getString(R.string.not_found_location_country)
            imgWeatherIcon.setImageResource(R.drawable.ic_report_problem)
            txtTemperature.text = view.resources.getString(R.string.not_found_location_temp)
        }

        fun requestOpenWeatherRetrofit() = location?.let {
            Thread {
                openWeatherRetrofit.loadWeather(
                    it.name + "," + it.country,
                    OpenWeatherRetrofit.APP_ID,
                    lang,
                    units
                ).enqueue(object : Callback<WeatherData?> {

                    override fun onResponse(call: Call<WeatherData?>, response: Response<WeatherData?>) {
                        if (response.isSuccessful && response.body() != null) {
                            activity.runOnUiThread { initViewsByGoodResponse(response.body()!!) }
                        } else {
                            activity.runOnUiThread { initViewsByFailResponse() }
                        }
                    }

                    override fun onFailure(call: Call<WeatherData?>, t: Throwable) {
                        activity.runOnUiThread { initViewsByFailResponse() }
                    }

                })
            }.start()
        }
    }
}