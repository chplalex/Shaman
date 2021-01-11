package com.chplalex.shaman.mvp.view.list

import android.view.View

interface IItemViewHistory: IItemView {
    fun setOnDeleteButtonClick(onClick: (View) -> Unit)
    fun setOnFavoriteButtonClick(onClick: (View) -> Unit)
    fun setName(name: String)
    fun setCountry(country: String)
    fun setTime(timeString: String)
    fun setDate(dateString: String)
    fun setTemperature(temperatureString: String)
    fun setFavorite(favorite: Boolean)
}