package com.chplalex.shaman.mvp.view

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface IViewHistory: MvpView {
    fun showErrorDB(error: Throwable)
    fun notifyItemRemoved(pos: Int)
    fun notifyItemChanged(pos: Int)
    fun notifyDataSetChanged()
}