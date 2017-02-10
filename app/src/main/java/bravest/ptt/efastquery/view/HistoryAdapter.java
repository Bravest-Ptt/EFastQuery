package bravest.ptt.efastquery.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.data.Result;
import bravest.ptt.efastquery.model.HistoryModule;
import bravest.ptt.efastquery.model.Module;

/**
 * Created by root on 1/11/17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder> implements View.OnClickListener {

    private static final String TAG = "ptt";

    private Context mContext;
    private ArrayList<Result> mData;

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
        return new HistoryHolder(inflater.inflate(R.layout.item_holder, null));
    }

    @Override
    public void onBindViewHolder(HistoryHolder historyHolder, int position) {
        Result result = mData.get(position);

        historyHolder.tv_content_request.setText(result.query);
        historyHolder.tv_content_explains.setText(mData.get(position).getExplainsString());
        historyHolder.iv_voice.setOnClickListener(this);
        historyHolder.iv_favourite.setOnClickListener(this);
        historyHolder.iv_delete.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
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
            case R.id.item_content_explains:
            case R.id.item_content_request:

                break;
            default:
                break;
        }
    }
}
