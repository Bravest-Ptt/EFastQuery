package bravest.ptt.efastquery.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import bravest.ptt.efastquery.callback.ItemClickListener;
import bravest.ptt.efastquery.view.holder.AbsHolder;

public abstract class AbsAdapter<T> extends RecyclerView.Adapter<AbsHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<T> mData;
    private ItemClickListener mItemClickListener;

    public AbsAdapter setOnItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
        return this;
    }

    ItemClickListener getItemClickListener() {
        return mItemClickListener;
    }

    ArrayList<T> getData() {
        return mData;
    }

    public AbsAdapter setData(ArrayList<T> mData) {
        this.mData = mData;
        return this;
    }


    public LayoutInflater getInflater() {
        return mInflater;
    }

    public AbsAdapter setContext(Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        return this;
    }

    public Context getContext() {
        return mContext;
    }


    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    @Override
    public AbsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(AbsHolder holder, int position) {
    }
}
