package com.chplalex.shaman.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chplalex.shaman.ui.adapter.AdapterHistory
import com.chplalex.shaman.R
import com.chplalex.shaman.mvp.presenter.PresenterAbout
import com.chplalex.shaman.utils.showToast
import com.chplalex.shaman.mvp.presenter.PresenterHistory
import com.chplalex.shaman.mvp.view.IViewHistory
import com.chplalex.shaman.ui.App
import com.chplalex.shaman.ui.App.Companion.instance
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class FragmentHistory : MvpAppCompatFragment(R.layout.fragment_history), IViewHistory {

    @Inject
    lateinit var injectPresenter: Provider<PresenterHistory>

    private val presenter by moxyPresenter {
        injectPresenter.get()
    }

    private val adapter by lazy {
        AdapterHistory(presenter.presenterList)
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
        activity?.setTitle(R.string.label_history)
        setHasOptionsMenu(true)
        view.findViewById<RecyclerView>(R.id.rvHistory).adapter = adapter
    }

    override fun showErrorDB(error: Throwable) {
        context?.showToast("Ошибка БД: $error")
    }

    override fun notifyItemRemoved(pos: Int) = adapter.notifyItemRemoved(pos)
    override fun notifyItemChanged(pos: Int) = adapter.notifyItemChanged(pos)
    override fun notifyDataSetChanged() = adapter.notifyDataSetChanged()
}