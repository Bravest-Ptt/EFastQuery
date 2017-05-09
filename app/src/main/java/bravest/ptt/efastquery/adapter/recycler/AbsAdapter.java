package bravest.ptt.efastquery.adapter.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import bravest.ptt.efastquery.listeners.OnItemClickListener;
import bravest.ptt.efastquery.adapter.holder.AbsHolder;

public abstract class AbsAdapter<T> extends RecyclerView.Adapter<AbsHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<T> mData;
    private OnItemClickListener mOnItemClickListener;

    public AbsAdapter setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
        return this;
    }

    OnItemClickListener getItemClickListener() {
        return mOnItemClickListener;
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
