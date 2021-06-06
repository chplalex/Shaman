package com.chplalex.shaman.mvp.presenter.list

import com.chplalex.shaman.mvp.view.list.IItemView

interface IPresenterList<V : IItemView> {
    fun bindView(view: V)
    fun getCount(): Int
}