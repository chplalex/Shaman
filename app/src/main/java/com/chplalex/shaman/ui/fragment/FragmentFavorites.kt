package com.chplalex.shaman.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chplalex.shaman.R
import com.chplalex.shaman.mvp.presenter.PresenterAbout
import com.chplalex.shaman.utils.showToast
import com.chplalex.shaman.mvp.presenter.PresenterFavorites
import com.chplalex.shaman.mvp.view.IViewFavorites
import com.chplalex.shaman.ui.App
import com.chplalex.shaman.ui.App.Companion.instance
import com.chplalex.shaman.ui.adapter.AdapterFavorites
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class FragmentFavorites : MvpAppCompatFragment(R.layout.fragment_favorites), IViewFavorites {

    @Inject
    lateinit var injectPresenter: Provider<PresenterFavorites>

    private val presenter by moxyPresenter {
        injectPresenter.get()
    }

    private val adapter by lazy {
        AdapterFavorites(presenter.presenterList, resources)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        instance.activityComponent?.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_no_start, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_start) {
            presenter.onActionStart()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setTitle(R.string.label_favorites)
        setHasOptionsMenu(true)
        view.findViewById<RecyclerView>(R.id.rvLocations).adapter = adapter
    }

    override fun showErrorDB(error: Throwable) {
        context?.showToast("Ошибка БД: $error")
    }

    override fun showErrorRetrofit(error: Throwable) {
        context?.showToast("Ошибка API OpenWeather: $error")
    }

    override fun notifyDataSetChanged() = adapter.notifyDataSetChanged()
    override fun notifyItemRemoved(pos: Int) = adapter.notifyItemRemoved(pos)
}