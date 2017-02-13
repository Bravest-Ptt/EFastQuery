package bravest.ptt.efastquery.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.data.Result;

/**
 * Created by root on 1/11/17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder> {

    private static final String TAG = "ptt";

    private Context mContext;
    private ArrayList<Result> mData;

    private ESearchMainPanel.ItemClickListener mItemClickListener;

    public HistoryAdapter(Context context, ArrayList<Result> data) {
        mContext = context;
        this.mData = data;
    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (inflater == null) {
            return null;
        }
        return new HistoryHolder(inflater.inflate(R.layout.item_holder, null), mItemClickListener);
    }

    @Override
    public void onBindViewHolder(HistoryHolder historyHolder, int position) {
        Result result = mData.get(position);

        historyHolder.tv_content_request.setText(result.query);
        historyHolder.tv_content_explains.setText(mData.get(position).getExplainsString());
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setOnItemClickListener(ESearchMainPanel.ItemClickListener listener) {
        this.mItemClickListener = listener;
    }
}
