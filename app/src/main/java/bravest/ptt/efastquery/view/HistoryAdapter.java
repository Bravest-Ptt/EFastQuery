package bravest.ptt.efastquery.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.model.HistoryModule;

/**
 * Created by root on 1/11/17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener {

    private static final String TAG = "ptt";

    private Context mContext;
    private ArrayList<HistoryModule> mData;

    public HistoryAdapter(Context context, ArrayList<HistoryModule> data) {
        mContext = context;
        this.mData = data;
        if (mData != null) {
            Log.d(TAG, "HistoryAdapter: mData.size = " + mData.size());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (inflater == null) {
            return null;
        }
        return new ViewHolder(inflater.inflate(R.layout.item_holder, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.tv_content.setText(mData.get(position).result);
        viewHolder.iv_voice.setOnClickListener(this);
        viewHolder.iv_favourite.setOnClickListener(this);
        viewHolder.iv_delete.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            Log.d(TAG, "getItemCount: = " + mData.size());
            return mData.size();
        }
        return 0;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_voice:
                break;
            case R.id.item_favourite:
                break;
            case R.id.item_delete:
                break;
            default:
                break;
        }
    }
}
