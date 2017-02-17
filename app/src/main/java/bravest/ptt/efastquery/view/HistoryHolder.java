package bravest.ptt.efastquery.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.callback.ItemClickListener;
import butterknife.OnClick;

/**
 * Created by pengtian on 2017/1/19.
 */

public class HistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView tv_content_request;
    TextView tv_content_explains;
    ImageView iv_voice;
    ImageView iv_delete;
    View ln_view;

    private ItemClickListener mItemClickListener;

    public HistoryHolder(View itemView, ItemClickListener listener) {
        super(itemView);

        mItemClickListener = listener;

        tv_content_request = (TextView) itemView.findViewById(R.id.item_content_request);
        tv_content_explains = (TextView) itemView.findViewById(R.id.item_content_explains);
        iv_voice = (ImageView) itemView.findViewById(R.id.item_voice);
        iv_delete = (ImageView) itemView.findViewById(R.id.item_delete);

        ln_view = itemView.findViewById(R.id.item_content);

        iv_voice.setOnClickListener(this);
        iv_delete.setOnClickListener(this);
        ln_view.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClicked(view, getAdapterPosition());
        }
    }
}
