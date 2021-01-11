package com.chplalex.shaman.mvp.view.list

import android.view.View

interface IItemView {
    var pos: Int
    fun setListenerOnView(listener: (View) -> Unit)
}