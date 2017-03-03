package bravest.ptt.efastquery.view.adapter.recycler;

import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.data.Result;
import bravest.ptt.efastquery.data.wordbook.W;
import bravest.ptt.efastquery.data.wordbook.Word;
import bravest.ptt.efastquery.view.holder.AbsHolder;
import bravest.ptt.efastquery.view.holder.FavoritePreHolder;

/**
 * Created by root on 3/1/17.
 */

public class FavoritePreAdapter extends AbsAdapter {

    private static final int NORMAL = 1;
    private static final int DATE = 2;

    @Override
    public AbsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FavoritePreHolder holder = new FavoritePreHolder(getInflater().inflate(R.layout.item_favorite_pre, parent, false));
        holder.setItemClickListener(getItemClickListener());
        if (viewType == NORMAL) {
            holder.setVisible(View.GONE);
        } else if (viewType == DATE) {
            holder.setVisible(View.VISIBLE);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(AbsHolder absHolder, int position) {
        Result word = (Result) getData().get(position);
        if (word == null) {
            return;
        }
        boolean visible = ((FavoritePreHolder)absHolder).getTimeView().getVisibility() == View.VISIBLE;
        if (visible) {
            bindDateItem(word, (FavoritePreHolder)absHolder);
        } else {
            bindNormalItem(word, (FavoritePreHolder)absHolder);
        }
    }

    private void bindDateItem(W result, FavoritePreHolder holder) {
        Word word = ((Result) result).getWord();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String time = df.format(word.getDate());
        holder.getTimeView().setText(time);
        bindNormalItem(result, holder);
    }


    private void bindNormalItem(W result, FavoritePreHolder holder) {
        Word word = ((Result) result).getWord();
        holder.getWordView().setText(word.getWord());
        holder.getExplainsView().setText(word.getTrans());
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return DATE;
        }
        Date currentDate = ((Result) getData().get(position)).getDate();
        if (currentDate == null) {
            return NORMAL;
        }
        Date preDate = ((Result) getData().get(position - 1)).getDate();
        if (sameDate(currentDate, preDate)) {
            return NORMAL;
        } else {
            return DATE;
        }
    }

    private boolean sameDate(Date d1, Date d2) {
        return (d1.getYear() == d2.getYear() &&
                d1.getMonth() == d2.getMonth() &&
                d1.getDay() == d2.getDay());
    }
}
