package com.chplalex.shaman.ui.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.chplalex.shaman.R
import com.chplalex.shaman.mvp.presenter.list.IPresenterListFavorites
import com.chplalex.shaman.mvp.view.list.IItemViewFavorite
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class AdapterFavorites(
        private val presenter: IPresenterListFavorites,
        private val resources: Resources
) :
        RecyclerView.Adapter<AdapterFavorites.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) = ViewHolder(
            LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.fragment_favorites_item, viewGroup, false)
    )

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.pos = position
        presenter.bindView(viewHolder)
    }

    override fun getItemCount() = presenter.getCount()

    inner class ViewHolder(private val container: View) : RecyclerView.ViewHolder(container), IItemViewFavorite {

        private val txtName: MaterialTextView = itemView.findViewById(R.id.txtFavoriteName)
        private val txtCountry: MaterialTextView = itemView.findViewById(R.id.txtFavoriteCountry)
        private val txtTemperature: MaterialTextView = itemView.findViewById(R.id.txtTemperature)
        private val imgIcon: ShapeableImageView = itemView.findViewById(R.id.imgWeatherIcon)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnFavoriteDelete)

        override var pos = -1

        override fun setListenerOnView(listener: (View) -> Unit) {
            container.setOnClickListener(listener)
        }

        override fun setName(name: String) {
            txtName.text = name
        }

        override fun setCountry(country: String) {
            txtCountry.text = country
        }

        override fun setTemp(temp: String) {
            txtTemperature.text = temp
        }

        override fun setNoTemp() {
            txtTemperature.text = resources.getText(R.string.not_found_location_temp)
        }

        override fun setListenerOnDeleteButton(listener: (View) -> Unit) {
            btnDelete.setOnClickListener(listener)
        }

        override fun setIcon(imageResource: Int) {
            imgIcon.setImageResource(imageResource)
        }
    }
}