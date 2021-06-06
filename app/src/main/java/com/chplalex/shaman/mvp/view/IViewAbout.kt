package com.chplalex.shaman.mvp.view

import android.view.View
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface IViewAbout : MvpView{
    fun setListenerFacebook(listener: (View) -> Unit)
    fun setListenerWhatsapp(listener: (View) -> Unit)
    fun setListenerTelegram(listener: (View) -> Unit)
    fun setListenerEmail(listener: (View) -> Unit)
    fun setListenerLinkedin(listener: (View) -> Unit)
    fun setListenerInstagram(listener: (View) -> Unit)
}