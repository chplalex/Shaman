package com.chplalex.shaman.mvp.view.list

import android.view.View

interface IItemViewHistory: IItemView {
    fun setListenerOnDeleteButton(listener: (View) -> Unit)
    fun setListenerOnFavoriteButton(listener: (View) -> Unit)
    fun setName(name: String)
    fun setCountry(country: String)
    fun setTime(timeString: String)
    fun setDate(dateString: String)
    fun setTemperature(temperatureString: String)
    fun setFavorite(favorite: Boolean)
}