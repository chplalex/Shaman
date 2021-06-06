package com.chplalex.shaman.mvp.presenter

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.chplalex.shaman.R
import com.chplalex.shaman.mvp.view.IViewAbout
import moxy.MvpPresenter
import javax.inject.Inject
import javax.inject.Named

class PresenterAbout @Inject constructor(
    private val navController : NavController,
    @Named("actContext")
    private val context : Context
) :
    MvpPresenter<IViewAbout>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        initListeners()
    }

    private fun initListeners() {
        viewState.setListenerFacebook(this::onFacebook)
        viewState.setListenerWhatsapp(this::onWhatsapp)
        viewState.setListenerEmail(this::onEmail)
        viewState.setListenerTelegram(this::onTelegram)
        viewState.setListenerInstagram(this::onInstagram)
        viewState.setListenerLinkedin(this::onLinkedin)
    }

    private fun onTelegram(view: View) {
        val uri = "http://telegram.me/chepel_alexander"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(context, intent, null)
    }

    private fun onEmail(view: View) {
        val uri = "mailto:chepel.alexander@gmail.com"
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(uri))
        startActivity(context, intent, null)
    }

    private fun onFacebook(view: View) {
        val uri = "https://web.facebook.com/alexander.chepel"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(context, intent, null)
    }

    private fun onInstagram(view: View) {
        val uri = "https://www.instagram.com/chepel.alexander/"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(context, intent, null)
    }

    private fun onLinkedin(view: View) {
        val uri = "https://www.linkedin.com/in/александр-чепель-08005a97"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(context, intent, null)
    }

    private fun onWhatsapp(view: View) {
        val uri = "https://api.whatsapp.com/send?phone=79037259610"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(context, intent, null)
    }

    fun isAppAvailable(appName: String): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun onActionStart() {
        navController.navigate(R.id.actionStart, null)
    }
}