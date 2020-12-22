package com.chplalex.shaman.Start

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chplalex.shaman.MainActivity
import com.chplalex.shaman.R
import com.chplalex.shaman.WeatherData.WeatherData

class FragmentStart : Fragment(), SearchView.OnQueryTextListener, MenuItem.OnMenuItemClickListener {

    // эти поля всегда на экране
    private var txtLocationName: TextView? = null
    private var txtLocationCountry: TextView? = null
    private var tempView: TempView? = null
    private var txtWeatherDescription: TextView? = null

    // эти поля видны при выборе пользователем установок
    private var rowPressure: TableRow? = null
    private var txtPressure: TextView? = null
    private var rowWind: TableRow? = null
    private var txtWind: TextView? = null
    private var rowSunMoving: TableRow? = null
    private var txtSunMoving: TextView? = null
    private var rowHumidity: TableRow? = null
    private var txtHumidity: TextView? = null
    private var searchItem: MenuItem? = null
    private var favoriteItem: MenuItem? = null
    private var vmStart: ViewModelStart? = null
    private var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = activity as MainActivity?
        sharedPreferences = activity!!.sharedPreferences

        // создаем модель для фрагмента
        vmStart = ViewModelProvider(this).get(ViewModelStart::class.java)
        // устанавливаем местоположение по цепочке:
        // 1. getArguments() (переход из Favorites и History)
        // 2. (если нет, то) SharedPreferences
        // 3. (если нет, то) Current Location
        // 4. (если нет, то) null
        // Если местоположение != null, то -> аснихронная загрузка данных loadData()
        vmStart!!.initLocationData(arguments)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.setTitle(R.string.label_start)
        findViewsById(view)
        initRowsFromSharedPreferences()
        val owner = viewLifecycleOwner

        // Подписываем модель саму на себя на изменения данных о местоположении
        vmStart!!.liveLocationData.observe(owner, vmStart!!)
        // Подписываемся на изменения LiveData с погодными данными
        vmStart!!.liveWeatherData.observe(owner, { wd: WeatherData? -> initViewsByWeatherData(wd) })
        // Подписываемся на изменения LiveData с данными Избранное/Неизбранное
        vmStart!!.liveFavoriteData.observe(owner, { isFavorite: Boolean? -> initViewsByFavoriteData(isFavorite) })
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
        val searchView = searchItem?.actionView
        (searchView as SearchView).setOnQueryTextListener(this)

        menu.findItem(R.id.action_favorite)?.setOnMenuItemClickListener(this)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        searchItem?.collapseActionView()
        if (query.trim { it <= ' ' }.isEmpty()) return false
        vmStart?.initLocationDataByQuery(query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        return false
    }

    private fun findViewsById(view: View) {
        txtLocationName = view.findViewById(R.id.txtStartName)
        txtLocationCountry = view.findViewById(R.id.txtStartCountry)
        tempView = view.findViewById(R.id.tempView)
        txtWeatherDescription = view.findViewById(R.id.txtWeather)
        rowPressure = view.findViewById(R.id.rowPressure)
        txtPressure = view.findViewById(R.id.txtPressure)
        rowWind = view.findViewById(R.id.rowWind)
        txtWind = view.findViewById(R.id.txtWind)
        rowSunMoving = view.findViewById(R.id.rowSunMoving)
        txtSunMoving = view.findViewById(R.id.txtSunMoving)
        rowHumidity = view.findViewById(R.id.rowHumidity)
        txtHumidity = view.findViewById(R.id.txtHumidity)
    }

    private fun initRowsFromSharedPreferences() {
        if (sharedPreferences!!.getBoolean(getString(R.string.pref_pressure), true)) {
            rowPressure!!.visibility = View.VISIBLE
        } else {
            rowPressure!!.visibility = View.GONE
        }
        if (sharedPreferences!!.getBoolean(getString(R.string.pref_wind), true)) {
            rowWind!!.visibility = View.VISIBLE
        } else {
            rowWind!!.visibility = View.GONE
        }
        if (sharedPreferences!!.getBoolean(getString(R.string.pref_sun_moving), true)) {
            rowSunMoving!!.visibility = View.VISIBLE
        } else {
            rowSunMoving!!.visibility = View.GONE
        }
        if (sharedPreferences!!.getBoolean(getString(R.string.pref_humidity), true)) {
            rowHumidity!!.visibility = View.VISIBLE
        } else {
            rowHumidity!!.visibility = View.GONE
        }
    }

    private fun initViewsByWeatherData(wd: WeatherData?) {
        if (wd == null) {
            initViewsByFailResponse()
        } else {
            wd.setResources(resources)
            txtLocationName!!.text = wd.getName()
            txtLocationCountry!!.text = wd.country
            tempView!!.temp = wd.temp
            txtWeatherDescription!!.text = wd.description
            txtPressure!!.text = wd.pressure
            txtWind!!.text = wd.getWind()
            txtSunMoving!!.text = wd.sunMoving
            txtHumidity!!.text = wd.humidity
            val editor = sharedPreferences!!.edit()
            editor.putString("pref_loc_name", wd.getName())
            editor.putString("pref_loc_country", wd.country)
            editor.apply()
        }
    }

    private fun initViewsByFailResponse() {
        txtLocationName!!.text = getString(R.string.not_found_location_name)
        txtLocationCountry!!.text = "--"
        tempView!!.setUncertain()
        txtWeatherDescription!!.text = "--"
        txtPressure!!.text = "--"
        txtWind!!.text = "--"
        txtSunMoving!!.text = "--"
        txtHumidity!!.text = "--"
        val editor = sharedPreferences!!.edit()
        editor.remove("pref_loc_name")
        editor.remove("pref_loc_country")
        editor.apply()
    }

    private fun initViewsByFavoriteData(isFavorite: Boolean?) {
        if (favoriteItem == null) return
        if (isFavorite == null) {
            favoriteItem!!.isVisible = false
        } else {
            favoriteItem!!.isVisible = true
            favoriteItem!!.setIcon(if (isFavorite) R.drawable.ic_favorite_yes else R.drawable.ic_favorite_no)
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

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_my_location -> {
                vmStart!!.initLocationDataByCurrentLocation()
                true
            }
            R.id.action_favorite -> {
                vmStart!!.reverseFavorite()
                true
            }
            else -> false
        }
    }
}