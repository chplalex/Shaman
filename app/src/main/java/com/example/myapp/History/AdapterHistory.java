package com.example.myapp.History;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.myapp.DBService.RequestForAll;
import com.example.myapp.DBService.ShamanDao;
import com.example.myapp.MainApp;
import com.example.myapp.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import static com.example.myapp.Common.Utils.LOCATION_ARG_COUNTRY;
import static com.example.myapp.Common.Utils.LOCATION_ARG_NAME;

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolder> {

    private List<RequestForAll> requests;
    private ShamanDao shamanDao;

    public AdapterHistory(Activity activity) {
        shamanDao = MainApp.getInstance().getShamanDao();
        new Thread(() -> {
            requests = shamanDao.getAllRequests();
            activity.runOnUiThread(this::notifyDataSetChanged);
        }).start();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.requestForAll = requests.get(position);
        holder.initViews();
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder {
        private RequestForAll requestForAll;
        private MaterialTextView txtHistoryLocation;
        private MaterialTextView txtHistoryCountry;
        private MaterialTextView txtHistoryTime;
        private MaterialTextView txtHistoryDate;
        private MaterialTextView txtHistoryTemperature;
        private ImageButton btnHistoryDelete;
        private ImageButton btnHistoryFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHistoryLocation = itemView.findViewById(R.id.txtHistoryLocation);
            txtHistoryCountry = itemView.findViewById(R.id.txtHistoryCountry);
            txtHistoryTime = itemView.findViewById(R.id.txtHistoryTime);
            txtHistoryDate = itemView.findViewById(R.id.txtHistoryDate);
            txtHistoryTemperature = itemView.findViewById(R.id.txtHistoryTemperature);
            btnHistoryDelete = itemView.findViewById(R.id.btnHistoryDelete);
            btnHistoryFavorite = itemView.findViewById(R.id.btnHistoryFavorite);

            itemView.setOnClickListener((View view) -> {
                Bundle bundle = new Bundle();
                bundle.putCharSequence(LOCATION_ARG_NAME, requestForAll.name);
                bundle.putCharSequence(LOCATION_ARG_COUNTRY, requestForAll.country);
                Navigation.findNavController(view).navigate(R.id.actionStart, bundle);
            });

            btnHistoryDelete.setOnClickListener((View view) -> {
                new Thread(() -> {
                    shamanDao.deleteRequestByTime(requestForAll.time);
                }).start();
                requests.remove(requestForAll);
                notifyItemRemoved(getAdapterPosition());
            });

            btnHistoryFavorite.setOnClickListener((View view) -> {
                boolean newFavoriteValue = !requestForAll.favorite;
                for (int i = 0; i < requests.size(); i++) {
                    RequestForAll r = requests.get(i);
                    if (r.name.equals(requestForAll.name) && r.country.equals(requestForAll.country)) {
                        r.favorite = newFavoriteValue;
                        notifyItemChanged(i);
                    }
                }
                new Thread(() -> {
                    shamanDao.updateLocationFavoriteByNameAndCountry(
                            requestForAll.name,
                            requestForAll.country,
                            requestForAll.favorite);
                }).start();
            });
        }

        public void initViews() {
            txtHistoryLocation.setText(requestForAll.name);
            txtHistoryCountry.setText(requestForAll.country);
            txtHistoryTime.setText(requestForAll.getTimeString());
            txtHistoryDate.setText(requestForAll.getDateString());
            txtHistoryTemperature.setText(requestForAll.getTemperatureString());
            if (requestForAll.favorite) {
                btnHistoryFavorite.setImageResource(R.drawable.ic_favorite_yes);
            } else {
                btnHistoryFavorite.setImageResource(R.drawable.ic_favorite_no);
            }
        }
    }
}