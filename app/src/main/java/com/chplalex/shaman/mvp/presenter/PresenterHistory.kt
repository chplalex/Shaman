package com.chplalex.shaman.mvp.presenter

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
import com.chplalex.shaman.ui.App.Companion.instance
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import moxy.MvpPresenter
import javax.inject.Inject
import javax.inject.Named

class PresenterHistory() : MvpPresenter<IViewHistory>() {

    val presenterList = PresenterListHistory()

    @Inject lateinit var dao : ShamanDao
    @Inject @Named("UI") lateinit var uiScheduler : Scheduler
    @Inject @Named("IO") lateinit var ioScheduler : Scheduler
    @Inject lateinit var disposable : CompositeDisposable
    @Inject lateinit var navController : NavController

    init {
        instance.activityComponent?.inject(this)
    }

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
                }))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    fun onActionStart() {
        navController.navigate(R.id.actionStart, null)
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
                    disposable.add(dao.deleteRequestByTime(time)
                            .subscribeOn(ioScheduler)
                            .observeOn(uiScheduler)
                            .subscribe({
                                requests.remove(this)
                                viewState.notifyItemRemoved(view.pos)
                            }, {
                                viewState.showErrorDB(it)
                            }))
                }

                val onFavoriteButtonClick = fun(_: View) {
                    disposable.add(dao.updateLocationFavorite(name, country, !favorite)
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