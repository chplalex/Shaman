package com.chplalex.shaman.mvp.presenter

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.chplalex.shaman.mvp.model.db.RequestForAll
import com.chplalex.shaman.R
import com.chplalex.shaman.utils.LOCATION_ARG_COUNTRY
import com.chplalex.shaman.utils.LOCATION_ARG_NAME
import com.chplalex.shaman.mvp.presenter.list.IPresenterListHistory
import com.chplalex.shaman.mvp.view.IViewHistory
import com.chplalex.shaman.mvp.view.list.IItemViewHistory
import com.chplalex.shaman.service.db.ShamanDao
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import moxy.MvpPresenter
import javax.inject.Inject
import javax.inject.Named

class PresenterHistory @Inject constructor(
    private val sp: SharedPreferences,
    private val dao: ShamanDao,
    private val disposable: CompositeDisposable,
    private val navController: NavController,
    @Named("UI")
    private val uiScheduler: Scheduler,
    @Named("IO")
    private val ioScheduler: Scheduler
) :
    MvpPresenter<IViewHistory>() {

    val presenterList = PresenterListHistory()

    private val requests = mutableListOf<RequestForAll>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadData()
    }

    private fun loadData() {
        disposable.add(
            dao.getAllRequests
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    requests.clear()
                    requests.addAll(it)
                    viewState.notifyDataSetChanged()
                }, {
                    viewState.showErrorDB(it)
                })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    fun onActionStart() {
        navController.navigate(R.id.actionStart, null)
    }

    inner class PresenterListHistory() : IPresenterListHistory {

        override fun bindView(view: IItemViewHistory) {
            with(requests[view.pos]) {

                val onViewClick = fun(v: View) {
                    with(sp.edit()) {
                        putString(LOCATION_ARG_NAME, name)
                        putString(LOCATION_ARG_COUNTRY, country)
                        apply()
                    }
                    navController.navigate(R.id.actionStart, null)
                }

                val onDeleteButtonClick = fun(_: View) {
                    disposable.add(
                        dao.deleteRequestByTime(time)
                            .subscribeOn(ioScheduler)
                            .observeOn(uiScheduler)
                            .subscribe({
                                requests.remove(this)
                                viewState.notifyItemRemoved(view.pos)
                            }, {
                                viewState.showErrorDB(it)
                            })
                    )
                }

                val onFavoriteButtonClick = fun(_: View) {
                    disposable.add(
                        dao.updateLocationFavorite(name, country, !favorite)
                            .subscribeOn(ioScheduler)
                            .observeOn(uiScheduler)
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
                            })
                    )
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