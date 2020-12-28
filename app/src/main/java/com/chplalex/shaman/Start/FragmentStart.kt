package com.chplalex.shaman.Start

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chplalex.shaman.Common.Utils.SP_NAME
import com.chplalex.shaman.R
import com.chplalex.shaman.WeatherData.WeatherData

class FragmentStart : Fragment(), SearchView.OnQueryTextListener, MenuItem.OnMenuItemClickListener {

    // эти поля всегда на экране
    private lateinit var txtLocationName: TextView
    private lateinit var txtLocationCountry: TextView
    private lateinit var tempView: TempView
    private lateinit var txtWeatherDescription: TextView

    // эти поля видны при выборе пользователем установок
    private lateinit var rowPressure: TableRow
    private lateinit var txtPressure: TextView
    private lateinit var rowWind: TableRow
    private lateinit var txtWind: TextView
    private lateinit var rowSunMoving: TableRow
    private lateinit var txtSunMoving: TextView
    private lateinit var rowHumidity: TableRow
    private lateinit var txtHumidity: TextView
    private lateinit var searchItem: MenuItem
    private lateinit var favoriteItem: MenuItem
    private lateinit var vmStart: ViewModelStart
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences(SP_NAME, MODE_PRIVATE)

        // создаем модель для фрагмента
        vmStart = ViewModelProvider(this).get(ViewModelStart::class.java)
        // устанавливаем местоположение по цепочке:
        // 1. getArguments() (переход из Favorites и History)
        // 2. (если нет, то) SharedPreferences
        // 3. (если нет, то) Current Location
        // 4. (если нет, то) null
        // Если местоположение != null, то -> аснихронная загрузка данных loadData()
        vmStart.initLocationData(arguments)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setTitle(R.string.label_start)
        findViewsById(view)
        initRowsFromSharedPreferences()
        val owner = viewLifecycleOwner

        // Подписываем модель саму на себя на изменения данных о местоположении
        vmStart.liveLocationData.observe(owner, vmStart)
        // Подписываемся на изменения LiveData с погодными данными
        vmStart.liveWeatherData.observe(owner, { wd: WeatherData? -> initViewsByWeatherData(wd) })
        // Подписываемся на изменения LiveData с данными Избранное/Неизбранное
        vmStart.liveFavoriteData.observe(owner, { isFavorite: Boolean? -> initViewsByFavoriteData(isFavorite) })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_start, menu)
        menu.findItem(R.id.action_my_location).setOnMenuItemClickListener(this)

        searchItem = menu.findItem(R.id.action_search)
        (searchItem.actionView as SearchView).setOnQueryTextListener(this)

        favoriteItem = menu.findItem(R.id.action_favorite).also { it.setOnMenuItemClickListener(this) }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        searchItem.collapseActionView()
        if (query.trim { it <= ' ' }.isEmpty()) return false
        vmStart.initLocationDataByQuery(query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        return false
    }

    private fun findViewsById(view: View) = with(view) {
        txtLocationName = findViewById(R.id.txtStartName)
        txtLocationCountry = findViewById(R.id.txtStartCountry)
        tempView = findViewById(R.id.tempView)
        txtWeatherDescription = findViewById(R.id.txtWeather)
        rowPressure = findViewById(R.id.rowPressure)
        txtPressure = findViewById(R.id.txtPressure)
        rowWind = findViewById(R.id.rowWind)
        txtWind = findViewById(R.id.txtWind)
        rowSunMoving = findViewById(R.id.rowSunMoving)
        txtSunMoving = findViewById(R.id.txtSunMoving)
        rowHumidity = findViewById(R.id.rowHumidity)
        txtHumidity = findViewById(R.id.txtHumidity)
    }

    private fun initRowsFromSharedPreferences() {
        rowPressure.visibility = if (sharedPreferences.getBoolean("pref_pressure", true)) VISIBLE else GONE
        rowWind.visibility = if (sharedPreferences.getBoolean("pref_wind", true)) VISIBLE else GONE
        rowSunMoving.visibility = if (sharedPreferences.getBoolean("pref_sun_moving", true)) VISIBLE else GONE
        rowHumidity.visibility = if (sharedPreferences.getBoolean("pref_humidity", true)) VISIBLE else GONE
    }

    private fun initViewsByWeatherData(wd: WeatherData?) {
        if (wd == null) {
            initViewsByFailResponse()
        } else {
            wd.setResources(resources)
            txtLocationName.text = wd.getName()
            txtLocationCountry.text = wd.country
            tempView.temp = wd.temp
            txtWeatherDescription.text = wd.description
            txtPressure.text = wd.pressure
            txtWind.text = wd.getWind()
            txtSunMoving.text = wd.sunMoving
            txtHumidity.text = wd.humidity
            sharedPreferences.edit().apply {
                putString("pref_loc_name", wd.getName())
                putString("pref_loc_country", wd.country)
                apply()
            }
        }
    }

    private fun initViewsByFailResponse() {
        txtLocationName.text = getString(R.string.not_found_location_name)
        txtLocationCountry.text = "--"
        tempView.setUncertain()
        txtWeatherDescription.text = "--"
        txtPressure.text = "--"
        txtWind.text = "--"
        txtSunMoving.text = "--"
        txtHumidity.text = "--"
        sharedPreferences.edit().apply {
            remove("pref_loc_name")
            remove("pref_loc_country")
            apply()
        }
    }

    private fun initViewsByFavoriteData(isFavorite: Boolean?) {
        if (!this::favoriteItem.isInitialized) return
        if (isFavorite == null) {
            favoriteItem.isVisible = false
        } else {
            favoriteItem.isVisible = true
            favoriteItem.setIcon(if (isFavorite) R.drawable.ic_favorite_yes else R.drawable.ic_favorite_no)
        }
    }

    private fun showAlert(vararg msg: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Проблема")
            .setItems(msg, null)
            .setIcon(R.drawable.ic_error)
            .setCancelable(true)
            .setPositiveButton("Понятно", null)
            .create()
            .show()
    }

    override fun onMenuItemClick(item: MenuItem) = when (item.itemId) {
        R.id.action_my_location -> {
            vmStart.initLocationDataByCurrentLocation()
            true
        }
        R.id.action_favorite -> {
            vmStart.reverseFavorite()
            true
        }
        else -> false
    }
}