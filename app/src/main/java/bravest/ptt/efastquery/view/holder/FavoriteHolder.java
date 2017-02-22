package bravest.ptt.efastquery.view.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import bravest.ptt.efastquery.R;


public class FavoriteHolder extends AbsHolder {
    private TextView mWordTextView;
    private TextView mUsPhonetic;
    private TextView mUkPhonetic;

    private TextView mExplainsTv;
    private ImageView mFavoriteIm;
    private View mContentView;

    public FavoriteHolder(View itemView) {
        super(itemView);

        mWordTextView = (TextView) itemView.findViewById(R.id.item_fav_word);
        mUsPhonetic = (TextView) itemView.findViewById(R.id.item_fav_speak_us);
        mUkPhonetic = (TextView) itemView.findViewById(R.id.item_fav_speak_uk);
        mExplainsTv = (TextView) itemView.findViewById(R.id.item_fav_explains);
        mFavoriteIm = (ImageView) itemView.findViewById(R.id.item_fav_favorite);
        mContentView = itemView.findViewById(R.id.item_fav_content);
    }

    public TextView getmExplainsTv() {
        return mExplainsTv;
    }

    public TextView getWordTextView() {
        return mWordTextView;
    }

    public TextView getUsPhoneticTv() {
        return mUsPhonetic;
    }

    public TextView getUkPhoneticTv() {
        return mUkPhonetic;
    }

    public ImageView getFavoriteIm() {
        return mFavoriteIm;
    }

    public View getContentView() {
        return mContentView;
    }
}
