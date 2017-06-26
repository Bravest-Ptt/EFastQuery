package bravest.ptt.efastquery.adapter.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.adapter.holder.AbsHolder;
import bravest.ptt.efastquery.adapter.holder.ArticleHolder;
import bravest.ptt.efastquery.entity.Article;

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
                break;
            case Article.TYPE_MULTI_IMAGE:
                break;
            default:
                break;
        }
        AbsHolder holder = new ArticleHolder(view, viewType);
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
    }

    @Override
    public int getItemViewType(int position) {
        if (getData() != null && getData().size() > position) {
            Article article = (Article) getData().get(position);
            return article.getType();
        }
        return Article.TYPE_NORMAL;
    }
}
