package bravest.ptt.efastquery.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.entity.Article;

/**
 * Created by pengtian on 2017/6/26.
 */

public class ArticleHolder extends AbsHolder {

    public ImageView image;
    public ImageView imageMiddle;
    public ImageView imageEnd;
    public TextView title;
    public TextView tag;
    public TextView readCount;

    public ArticleHolder(View itemView, int itemType) {
        super(itemView);

        if (itemType == Article.TYPE_MULTI_IMAGE) {
//            imageMiddle = itemView.findViewById(R.id.)
//            imageEnd = itemView.findViewById(R.id.)
        }
        image = (ImageView) itemView.findViewById(R.id.image);
        title = (TextView) itemView.findViewById(R.id.text_title);
        tag = (TextView) itemView.findViewById(R.id.text_summary_tag);
        readCount = (TextView) itemView.findViewById(R.id.text_summary_read);
    }
}
