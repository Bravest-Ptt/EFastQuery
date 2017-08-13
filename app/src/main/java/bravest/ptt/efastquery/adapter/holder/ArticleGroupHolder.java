package bravest.ptt.efastquery.adapter.holder;

import android.view.View;
import android.widget.TextView;

import bravest.ptt.efastquery.R;

/**
 * Created by pengtian on 2017/8/13.
 */

public class ArticleGroupHolder extends ArticleHolder {
    public TextView date;

    public ArticleGroupHolder(View itemView, int itemType) {
        super(itemView, itemType);
        date = (TextView) itemView.findViewById(R.id.item_article_group_date);
    }
}
