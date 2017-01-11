package bravest.ptt.efastquery.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by root on 1/11/17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder>{

    private Context mContext;

    public HistoryAdapter(Context context) {
        mContext = context;
    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HistoryHolder holder = new HistoryHolder(null);
        return null;
    }

    @Override
    public void onBindViewHolder(HistoryHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class HistoryHolder extends RecyclerView.ViewHolder{

        public HistoryHolder(View itemView) {
            super(itemView);
        }
    }
}
