package com.chplalex.shaman.mvp.view

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface IViewFavorites : MvpView {
    fun showErrorDB(error: Throwable)
    fun showErrorRetrofit(error: Throwable)
    fun notifyDataSetChanged()
    fun notifyItemRemoved(pos: Int)
}
