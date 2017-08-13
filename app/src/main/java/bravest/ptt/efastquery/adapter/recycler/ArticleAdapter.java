package bravest.ptt.efastquery.adapter.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.Date;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.adapter.holder.AbsHolder;
import bravest.ptt.efastquery.adapter.holder.ArticleGroupHolder;
import bravest.ptt.efastquery.adapter.holder.ArticleHolder;
import bravest.ptt.efastquery.entity.Article;
import bravest.ptt.efastquery.utils.Utils;

/**
 * Created by pengtian on 2017/6/26.
 */

public class ArticleAdapter extends AbsAdapter {

    @Override
    public AbsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case Article.TYPE_NORMAL:
                view = getInflater().inflate(R.layout.item_article_normal, parent, false);
                break;
            case Article.TYPE_BIG_IMAGE:
                view = getInflater().inflate(R.layout.item_article_big_picture, parent, false);
                break;
            case Article.TYPE_MULTI_IMAGE:
                view = getInflater().inflate(R.layout.item_article_multi_picture, parent, false);
                break;
            case Article.TYPE_NORMAL_GROUP:
                view = getInflater().inflate(R.layout.item_article_normal_group, parent, false);
                break;
//            case Article.TYPE_BIG_IMAGE_GROUP:
//                view = getInflater().inflate(R.layout.item_article_normal_group, parent, false);
//                break;
            case Article.TYPE_MULTI_IMAGE_GROUP:
                view = getInflater().inflate(R.layout.item_article_normal_group, parent, false);
                break;
            default:
                break;
        }
        AbsHolder holder;
        if (viewType == Article.TYPE_NORMAL_GROUP
//                || viewType == Article.TYPE_BIG_IMAGE_GROUP
                || viewType == Article.TYPE_MULTI_IMAGE_GROUP) {
            holder = new ArticleGroupHolder(view, viewType);
        } else {
            holder = new ArticleHolder(view, viewType);
        }

        holder.setItemClickListener(getItemClickListener());
        return holder;
    }

    @Override
    public void onBindViewHolder(AbsHolder holder, int position) {
        ArticleHolder articleHolder = (ArticleHolder) holder;
        Article article = (Article) getData().get(position);
        //Load image
        String imageUrl = article.getImageUrl();
        Glide.with(getContext()).load(imageUrl).into(articleHolder.image);

        //load text
        articleHolder.title.setText(article.getTitle());
        articleHolder.tag.setText(article.getTag());
        articleHolder.readCount.setText("阅读量:" + article.getReadCount());

        if (articleHolder instanceof ArticleGroupHolder) {
            ((ArticleGroupHolder) articleHolder).date.setText(Utils.friendlyDate(article.getCreatedAt()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getData() != null && getData().size() > position) {
            Article compareArticle = (Article) getData().get(position);
            if (position == 0) {
                return compareArticle.getType();
            }

            Article originalArticle = (Article) getData().get(position - 1);
            Date originalDate = originalArticle.getCreatedAt();
            Date compareDate = compareArticle.getCreatedAt();
            int dateDiff = Utils.daysOfTwo(originalDate, compareDate);
            if(dateDiff > 0) {
                return item2Group(compareArticle.getType());
            } else {
                return compareArticle.getType();
            }
        }
        return Article.TYPE_NORMAL;
    }

    private int item2Group(int itemType) {
        switch (itemType) {
            case Article.TYPE_NORMAL:
                return Article.TYPE_NORMAL_GROUP;
//            case Article.TYPE_BIG_IMAGE:
//                return Article.TYPE_BIG_IMAGE_GROUP;
            case Article.TYPE_MULTI_IMAGE:
                return Article.TYPE_MULTI_IMAGE_GROUP;
            default:
                return -1;
        }
    }
}
