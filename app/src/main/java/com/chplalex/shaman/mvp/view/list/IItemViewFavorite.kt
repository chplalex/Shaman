package com.chplalex.shaman.mvp.view.list

import android.view.View

interface IItemViewFavorite: IItemView {
    fun setName(name: String)
    fun setCountry(country: String)
    fun setTemp(temp: String)
    fun setNoTemp()
    fun setListenerOnDeleteButton(listener: (View) -> Unit)
    fun setIcon(imageResource: Int)
}