package com.chplalex.shaman.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import com.chplalex.shaman.R
import com.chplalex.shaman.mvp.presenter.PresenterAbout
import com.chplalex.shaman.mvp.view.IViewAbout
import com.chplalex.shaman.ui.App.Companion.instance
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class FragmentAbout : MvpAppCompatFragment(R.layout.fragment_about), IViewAbout {

    @Inject
    lateinit var injectPresenter: Provider<PresenterAbout>

    private val presenter by moxyPresenter {
        injectPresenter.get()
    }

    private lateinit var imageFacebook: ImageView
    private lateinit var imageWhatsapp: ImageView
    private lateinit var imageEmail: ImageView
    private lateinit var imageTelegram: ImageView
    private lateinit var imageInstagram: ImageView
    private lateinit var imageLinkedin: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        instance.activityComponent?.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setTitle(R.string.label_about)
        setHasOptionsMenu(true)
        findViewsById(view)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_no_start, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_start) {
            presenter.onActionStart()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun findViewsById(view: View) {
        imageFacebook = view.findViewById(R.id.image_facebook)
        imageWhatsapp = view.findViewById(R.id.image_whatsapp)
        imageEmail = view.findViewById(R.id.image_email)
        imageTelegram = view.findViewById(R.id.image_telegram)
        imageInstagram = view.findViewById(R.id.image_instagram)
        imageLinkedin = view.findViewById(R.id.image_linkedin)
    }

    override fun setListenerFacebook(listener: (View) -> Unit) {
        imageFacebook.setOnClickListener(listener)
    }

    override fun setListenerWhatsapp(listener: (View) -> Unit) {
        imageWhatsapp.setOnClickListener(listener)
    }

    override fun setListenerTelegram(listener: (View) -> Unit) {
        imageTelegram.setOnClickListener(listener)
    }

    override fun setListenerEmail(listener: (View) -> Unit) {
        imageEmail.setOnClickListener(listener)
    }

    override fun setListenerLinkedin(listener: (View) -> Unit) {
        imageLinkedin.setOnClickListener(listener)
    }

    override fun setListenerInstagram(listener: (View) -> Unit) {
        imageInstagram.setOnClickListener(listener)
    }
}