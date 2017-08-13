package bravest.ptt.efastquery.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;

import bravest.ptt.efastquery.listeners.OnItemClickListener;
import bravest.ptt.efastquery.listeners.OnItemLongClickListener;

public abstract class AbsHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnLongClickListener {
    private static final String TAG = "AbsHolder";
    public void setItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    AbsHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            Log.d(TAG, "onClick: ");
            mOnItemClickListener.onItemClicked(view, getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }
}
