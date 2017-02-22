package bravest.ptt.efastquery.view.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnLongClickListener;

import bravest.ptt.efastquery.callback.ItemClickListener;
import bravest.ptt.efastquery.callback.ItemLongClickListener;

public abstract class AbsHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnLongClickListener {
    public void setItemClickListener(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void setItemLongClickListener(ItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }

    private ItemClickListener mItemClickListener;
    private ItemLongClickListener mItemLongClickListener;

    AbsHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onClick(View view) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClicked(view, getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }
}
