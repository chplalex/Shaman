package com.example.myapp.History;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapp.R;

import java.util.List;

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolder> {

    private final List<ItemHistory> listHistory;

    public AdapterHistory(List<ItemHistory> searchHistory) {
        this.listHistory = searchHistory;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.searchItem = listHistory.get(position);
        holder.textView.setText(listHistory.get(position).location);
    }

    @Override
    public int getItemCount() {
        return listHistory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View containerView;
        public final TextView textView;
        public ItemHistory searchItem;

        public ViewHolder(View view) {
            super(view);
            containerView = view;
            textView = view.findViewById(R.id.txtHistoryLocation);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + searchItem + "'";
        }
    }
}