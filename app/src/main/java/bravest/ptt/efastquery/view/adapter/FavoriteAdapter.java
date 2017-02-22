package bravest.ptt.efastquery.view.adapter;

import android.view.ViewGroup;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.view.holder.AbsHolder;
import bravest.ptt.efastquery.view.holder.FavoriteHolder;


public class FavoriteAdapter extends AbsAdapter {

    @Override
    public AbsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FavoriteHolder holder = new FavoriteHolder(getInflater().inflate(R.layout.item_favorite, null));
        holder.setItemClickListener(getItemClickListener());
        return holder;
    }

    @Override
    public void onBindViewHolder(AbsHolder absHolder, int position) {
        
    }
}
