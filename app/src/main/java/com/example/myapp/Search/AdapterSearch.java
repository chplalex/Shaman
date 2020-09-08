package com.example.myapp.Search;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapp.R;

import java.util.List;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.ViewHolder> {

    private final List<String> searchHistory;

    public AdapterSearch(List<String> searchHistory) {
        this.searchHistory = searchHistory;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.searchItem = searchHistory.get(position);
        holder.textView.setText(searchHistory.get(position));
    }

    @Override
    public int getItemCount() {
        return searchHistory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View containerView;
        public final TextView textView;
        public String searchItem;

        public ViewHolder(View view) {
            super(view);
            containerView = view;
            textView = view.findViewById(R.id.item_number);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + searchItem + "'";
        }
    }
}