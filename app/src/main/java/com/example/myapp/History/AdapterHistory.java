package com.example.myapp.History;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapp.DBService.RequestForAll;
import com.example.myapp.DBService.ShamanDao;
import com.example.myapp.MainApp;
import com.example.myapp.R;

import java.util.List;

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolder> {

    private final List<RequestForAll> requests;

    public AdapterHistory() {
        this.requests = MainApp.getInstance().getShamanDao().getAllRequests();
        RequestForAll r1 = requests.get(0);
        RequestForAll r2 = requests.get(0);
        RequestForAll r3 = requests.get(0);
        RequestForAll r4 = requests.get(0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        RequestForAll requestForAll = requests.get(position);
        holder.initViews(requestForAll);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder {
        public TextView txtHistoryLocation;
        public TextView txtHistoryCountry;
        public TextView txtHistoryTime;
        public TextView txtHistoryDate;
        public TextView txtHistoryTemperature;
        public ImageButton btnHistoryDelete;
        public ImageButton btnHistoryFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHistoryLocation = itemView.findViewById(R.id.txtHistoryLocation);
            txtHistoryCountry = itemView.findViewById(R.id.txtHistoryCountry);
            txtHistoryTime = itemView.findViewById(R.id.txtHistoryTime);
            txtHistoryDate = itemView.findViewById(R.id.txtHistoryDate);
            txtHistoryTemperature = itemView.findViewById(R.id.txtHistoryTemperature);
            btnHistoryDelete = itemView.findViewById(R.id.btnHistoryDelete);
            btnHistoryFavorite = itemView.findViewById(R.id.btnHistoryFavorite);
        }

        public void initViews(@NonNull RequestForAll requestForAll) {
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