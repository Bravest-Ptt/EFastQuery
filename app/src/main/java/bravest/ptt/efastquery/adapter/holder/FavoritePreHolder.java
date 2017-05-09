package bravest.ptt.efastquery.adapter.holder;

import android.view.View;
import android.widget.TextView;

import bravest.ptt.efastquery.R;

/**
 * Created by root on 3/1/17.
 */

public class FavoritePreHolder extends AbsHolder {
    public TextView getWordView() {
        return word;
    }

    public TextView getExplainsView() {
        return explains;
    }

    private TextView word;
    private TextView explains;

    public TextView getTimeView() {
        return timeView;
    }

    private TextView timeView;

    public FavoritePreHolder(View itemView) {
        super(itemView);
        word = (TextView) itemView.findViewById(R.id.item_fav_word);
        explains = (TextView) itemView.findViewById(R.id.item_fav_explains);
        timeView = (TextView) itemView.findViewById(R.id.item_fav_pre_date);
        View view = itemView.findViewById(R.id.item_fav_pre);
        view.setOnClickListener(this);
    }

    public void setVisible(int visible) {
        timeView.setVisibility(visible);
    }
}
