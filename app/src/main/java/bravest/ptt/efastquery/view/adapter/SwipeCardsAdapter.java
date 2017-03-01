package bravest.ptt.efastquery.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.data.Result;

/**
 * Created by root on 3/1/17.
 */

public class SwipeCardsAdapter extends BaseAdapter {

    private ArrayList<Result> mData;
    private Context mContext;
    private LayoutInflater mInflater;

    public SwipeCardsAdapter(ArrayList<Result> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item, parent, false);

        }

        return null;
    }

    class ViewHolder {

    }
}
