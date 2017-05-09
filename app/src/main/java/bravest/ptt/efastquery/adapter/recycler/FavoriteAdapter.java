package bravest.ptt.efastquery.adapter.recycler;

import android.view.ViewGroup;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.adapter.holder.AbsHolder;
import bravest.ptt.efastquery.adapter.holder.FavoriteHolder;


public class FavoriteAdapter extends AbsAdapter {

    @Override
    public AbsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FavoriteHolder holder = new FavoriteHolder(getInflater().inflate(R.layout.item_favorite_pre, null));
        holder.setItemClickListener(getItemClickListener());
        return holder;
    }

    @Override
    public void onBindViewHolder(AbsHolder absHolder, int position) {

    }
}
