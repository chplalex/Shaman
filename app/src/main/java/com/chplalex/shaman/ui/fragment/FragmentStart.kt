package com.chplalex.shaman.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import com.chplalex.shaman.R
import com.chplalex.shaman.utils.showToast
import com.chplalex.shaman.mvp.presenter.PresenterStart
import com.chplalex.shaman.mvp.view.IViewStart
import com.chplalex.shaman.ui.view.TempView
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FragmentStart : MvpAppCompatFragment(),
    IViewStart,
    SearchView.OnQueryTextListener,
    MenuItem.OnMenuItemClickListener {

    private val presenter by moxyPresenter {
        PresenterStart(this, arguments)
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setTitle(R.string.label_start)
        findViewsById(view)
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
        presenter.actionLocationByQuerySelected(query)
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

    override fun onMenuItemClick(item: MenuItem) = when (item.itemId) {
        R.id.action_my_location -> {
            presenter.actionMyLocationSelected()
            true
        }
        R.id.action_favorite -> {
            presenter.actionFavoriteSelected()
            true
        }
        else -> false
    }

    override fun setLocationName(value: String) {
        txtLocationName.text = value
    }

    override fun setLocationCountry(value: String) {
        txtLocationCountry.text = value
    }

    override fun setTemp(value: Float) {
        tempView.temp = value
    }

    override fun setUncertainTemp() {
        tempView.setUncertain()
    }

    override fun setWeatherDescription(value: String) {
        txtWeatherDescription.text = value
    }

    override fun setPressure(value: String) {
        txtPressure.text = value
    }

    override fun setWind(value: String) {
        txtWind.text = value
    }

    override fun setSunMoving(value: String) {
        txtSunMoving.text = value
    }

    override fun setHumidity(value: String) {
        txtHumidity.text = value
    }

    override fun setPressureVisibility(value: Int) {
        txtPressure.visibility = value
    }

    override fun setWindVisibility(value: Int) {
        txtWind.visibility = value
    }

    override fun setSunMovingVisibility(value: Int) {
        txtSunMoving.visibility = value
    }

    override fun setHumidityVisibility(value: Int) {
        txtHumidity.visibility = value
    }

    override fun showErrorLocation(error: Throwable) {
        context?.showToast("Ошибка определения текущего положения: $error")
    }

    override fun showErrorRetrofit(error: Throwable) {
        context?.showToast("Ошибка API OpenWeather: $error")
    }

    override fun showErrorDB(error: Throwable) {
        context?.showToast("Ошибка операции БД: $error")
    }

    override fun setFavoriteState(state: Boolean) {
        if (this::favoriteItem.isInitialized) {
            favoriteItem.setIcon(if (state) R.drawable.ic_favorite_yes else R.drawable.ic_favorite_no)
        }
    }
}