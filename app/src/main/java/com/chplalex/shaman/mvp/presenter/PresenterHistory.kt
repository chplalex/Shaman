package com.chplalex.shaman.mvp.presenter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.chplalex.shaman.mvp.model.db.RequestForAll
import com.chplalex.shaman.R
import com.chplalex.shaman.utils.LOCATION_ARG_COUNTRY
import com.chplalex.shaman.utils.LOCATION_ARG_NAME
import com.chplalex.shaman.mvp.presenter.list.IPresenterListHistory
import com.chplalex.shaman.mvp.view.IViewHistory
import com.chplalex.shaman.mvp.view.list.IItemViewHistory
import com.chplalex.shaman.ui.App.Companion.instance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.MvpPresenter

class PresenterHistory(private val fragment: Fragment) : MvpPresenter<IViewHistory>() {

    val presenterList = PresenterListHistory()

    private val requests = mutableListOf<RequestForAll>()
    private val shamanDao = instance.shamanDao
    private val disposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadData()
    }

    private fun loadData() {
        disposable.add(shamanDao.getAllRequests
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    requests.clear()
                    requests.addAll(it)
                    viewState.notifyDataSetChanged()
                }, {
                    viewState.showErrorDB(it)
                }))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    fun onActionStart() {
        NavHostFragment.findNavController(fragment).navigate(R.id.actionStart, null)
    }

    inner class PresenterListHistory() : IPresenterListHistory {

        override fun bindView(view: IItemViewHistory) {
            with(requests[view.pos]) {

                val onViewClick = fun(v: View) {
                    Bundle().also {
                        it.putCharSequence(LOCATION_ARG_NAME, name)
                        it.putCharSequence(LOCATION_ARG_COUNTRY, country)
                        Navigation.findNavController(v).navigate(R.id.actionStart, it)
                    }
                }

                val onDeleteButtonClick = fun(_: View) {
                    disposable.add(shamanDao.deleteRequestByTime(time)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                requests.remove(this)
                                viewState.notifyItemRemoved(view.pos)
                            }, {
                                viewState.showErrorDB(it)
                            }))
                }

                val onFavoriteButtonClick = fun(_: View) {
                    disposable.add(shamanDao.updateLocationFavorite(name, country, !favorite)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                val newFavoriteValue = !favorite
                                for (i in requests.indices) {
                                    val r = requests[i]
                                    if (r.name == name && r.country == country) {
                                        r.favorite = newFavoriteValue
                                        viewState.notifyItemChanged(i)
                                    }
                                }
                            }, {
                                viewState.showErrorDB(it)
                            }))
                }

                view.setListenerOnView(onViewClick)
                view.setListenerOnDeleteButton(onDeleteButtonClick)
                view.setListenerOnFavoriteButton(onFavoriteButtonClick)
                view.setName(name)
                view.setCountry(country)
                view.setTime(timeString)
                view.setDate(dateString)
                view.setTemperature(temperatureString)
                view.setFavorite(favorite)
            }
        }

        override fun getCount() = requests.size
    }
}