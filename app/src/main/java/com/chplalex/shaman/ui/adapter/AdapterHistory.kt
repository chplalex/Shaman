package com.chplalex.shaman.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.chplalex.shaman.R
import com.chplalex.shaman.mvp.presenter.list.IPresenterListHistory
import com.chplalex.shaman.mvp.view.list.IItemViewHistory
import com.google.android.material.textview.MaterialTextView

class AdapterHistory(
        private val presenter: IPresenterListHistory
) :
        RecyclerView.Adapter<AdapterHistory.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_history_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.pos = position
        presenter.bindView(holder)
    }

    override fun getItemCount() = presenter.getCount()

    inner class ViewHolder(private val container: View) : RecyclerView.ViewHolder(container), IItemViewHistory {

        private val txtName: MaterialTextView = itemView.findViewById(R.id.txtHistoryLocation)
        private val txtCountry: MaterialTextView = itemView.findViewById(R.id.txtHistoryCountry)
        private val txtTime: MaterialTextView = itemView.findViewById(R.id.txtHistoryTime)
        private val txtDate: MaterialTextView = itemView.findViewById(R.id.txtHistoryDate)
        private val txtTemperature: MaterialTextView = itemView.findViewById(R.id.txtHistoryTemperature)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnHistoryDelete)
        private val btnFavorite: ImageButton = itemView.findViewById(R.id.btnHistoryFavorite)

        override var pos = -1

        override fun setListenerOnDeleteButton(listener: (View) -> Unit) {
            btnDelete.setOnClickListener(listener)
        }

        override fun setListenerOnFavoriteButton(listener: (View) -> Unit) {
            btnFavorite.setOnClickListener(listener)
        }

        override fun setName(name: String) {
            txtName.text = name
        }

        override fun setCountry(country: String) {
            txtCountry.text = country
        }

        override fun setTime(timeString: String) {
            txtTime.text = timeString
        }

        override fun setDate(dateString: String) {
            txtDate.text = dateString
        }

        override fun setTemperature(temperatureString: String) {
            txtTemperature.text = temperatureString
        }

        override fun setFavorite(favorite: Boolean) {
            btnFavorite.setImageResource(if (favorite) R.drawable.ic_favorite_yes else R.drawable.ic_favorite_no)
        }

        override fun setListenerOnView(onClick: (View) -> Unit) {
            container.setOnClickListener(onClick)
        }

    }
}