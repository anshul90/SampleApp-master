package com.sampleapp.com.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sampleapp.R;
import com.sampleapp.model.WorldPojo;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.DataObjectHolder> {
    static Context con;
    private ArrayList<WorldPojo> mDataset;

    public ListAdapter(Context con, ArrayList<WorldPojo> myDataset) {
        mDataset = myDataset;
        this.con = con;
    }
    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.rank.setText(position + 1 + " ");
        holder.country.setText(mDataset.get(position).getCountry());
        holder.population.setText(mDataset.get(position).getPopulation());
        if (!mDataset.get(position).getFlag_image().isEmpty()) {
            Glide.with(con)
                    .load(mDataset.get(position).getFlag_image()).placeholder(con.getResources().getDrawable(android.R.drawable.ic_menu_gallery))
                    .fitCenter()
                    .into(holder.flag);
        }
    }

    public void addItem(WorldPojo dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }


    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView rank;
        TextView country;
        TextView population;
        ImageView flag;

        public DataObjectHolder(View itemView) {
            super(itemView);
            rank = (TextView) itemView.findViewById(R.id.rank);
            country = (TextView) itemView.findViewById(R.id.country);
            population = (TextView) itemView.findViewById(R.id.population);
            flag = (ImageView) itemView.findViewById(R.id.flag);
        }

        @Override
        public void onClick(View v) {
            ((MyClickListener) con).onItemClick(getPosition(), v);
        }
    }
}
