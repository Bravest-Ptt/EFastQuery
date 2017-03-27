package bravest.ptt.efastquery.view.adapter.recycler;

import android.view.ViewGroup;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.data.Result;
import bravest.ptt.efastquery.view.holder.AbsHolder;
import bravest.ptt.efastquery.view.holder.HistoryHolder;


public class HistoryAdapter extends AbsAdapter {

    @Override
    public AbsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HistoryHolder holder = new HistoryHolder(getInflater().inflate(R.layout.item_history, parent, false));
        holder.setItemClickListener(getItemClickListener());
        return holder;
    }

    @Override
    public void onBindViewHolder(AbsHolder holder, int position) {
        Result result = (Result) getData().get(position);
        HistoryHolder historyHolder = (HistoryHolder) holder;
        historyHolder.getRequestTextView().setText(result.query);
        historyHolder.getExplainsTextView().setText(result.getExplainsString());
    }
}
