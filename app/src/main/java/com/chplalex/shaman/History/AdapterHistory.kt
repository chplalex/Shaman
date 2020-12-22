package com.chplalex.shaman.History

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.chplalex.shaman.App.Companion.instance
import com.chplalex.shaman.Common.Utils
import com.chplalex.shaman.DBService.RequestForAll
import com.chplalex.shaman.DBService.ShamanDao
import com.chplalex.shaman.R
import com.google.android.material.textview.MaterialTextView

class AdapterHistory(activity: Activity) : RecyclerView.Adapter<AdapterHistory.ViewHolder>() {

    private lateinit var requests: MutableList<RequestForAll>
    private val shamanDao: ShamanDao = instance.shamanDao

    init {
        Thread {
            requests = shamanDao.allRequests
            activity.runOnUiThread { notifyDataSetChanged() }
        }.start()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_history_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.requestForAll = requests[position]
        holder.initViews()
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        lateinit var requestForAll: RequestForAll

        private val txtHistoryLocation: MaterialTextView = itemView.findViewById(R.id.txtHistoryLocation)
        private val txtHistoryCountry: MaterialTextView = itemView.findViewById(R.id.txtHistoryCountry)
        private val txtHistoryTime: MaterialTextView = itemView.findViewById(R.id.txtHistoryTime)
        private val txtHistoryDate: MaterialTextView = itemView.findViewById(R.id.txtHistoryDate)
        private val txtHistoryTemperature: MaterialTextView = itemView.findViewById(R.id.txtHistoryTemperature)
        private val btnHistoryDelete: ImageButton = itemView.findViewById(R.id.btnHistoryDelete)
        private val btnHistoryFavorite: ImageButton = itemView.findViewById(R.id.btnHistoryFavorite)

        init {

            itemView.setOnClickListener { view: View? ->
                val bundle = Bundle()
                bundle.putCharSequence(Utils.LOCATION_ARG_NAME, requestForAll.name)
                bundle.putCharSequence(Utils.LOCATION_ARG_COUNTRY, requestForAll.country)
                Navigation.findNavController(view!!).navigate(R.id.actionStart, bundle)
            }

            btnHistoryDelete.setOnClickListener {
                Thread {
                    shamanDao.deleteRequestByTime(
                        requestForAll.time
                    )
                }.start()
                requests.remove(requestForAll)
                notifyItemRemoved(adapterPosition)
            }

            btnHistoryFavorite.setOnClickListener {
                val newFavoriteValue = !requestForAll.favorite
                for (i in requests.indices) {
                    val r = requests[i]
                    if (r.name == requestForAll.name && r.country == requestForAll.country) {
                        r.favorite = newFavoriteValue
                        notifyItemChanged(i)
                    }
                }
                Thread {
                    shamanDao.updateLocationFavoriteByNameAndCountry(
                        requestForAll.name,
                        requestForAll.country,
                        requestForAll.favorite
                    )
                }.start()
            }
        }

        fun initViews() {
            txtHistoryLocation.text = requestForAll.name
            txtHistoryCountry.text = requestForAll.country
            txtHistoryTime.text = requestForAll.timeString
            txtHistoryDate.text = requestForAll.dateString
            txtHistoryTemperature.text = requestForAll.temperatureString
            if (requestForAll.favorite) {
                btnHistoryFavorite.setImageResource(R.drawable.ic_favorite_yes)
            } else {
                btnHistoryFavorite.setImageResource(R.drawable.ic_favorite_no)
            }
        }
    }
}