package bravest.ptt.efastquery.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import bravest.ptt.efastquery.data.Result;

/**
 * Created by root on 2/13/17.
 */

public class FavoriteManager {
    private static final String TAG = "FavoriteManager";

    private Context mContext;
    private ContentResolver mResolver;

    public FavoriteManager(Context context) {
        mContext = context;
        mResolver = mContext.getContentResolver();
    }

    public ArrayList<Result> getAllFavorite() {
        ArrayList<Result> FavoriteList = new ArrayList<>();

        Cursor cursor = mResolver.query(EFastQueryDbUtils.Favorite.CONTENT_URI, null, null, null, EFastQueryDbUtils.Favorite.DATE + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Result result = new Result();

                result.query = cursor.getString(cursor.getColumnIndex(EFastQueryDbUtils.Favorite.REQUEST));

                result.setTranslateString(cursor.getString(cursor.getColumnIndex(EFastQueryDbUtils.Favorite.TRANSLATIONS)));

                result.setExplainString(cursor.getString(cursor.getColumnIndex(EFastQueryDbUtils.Favorite.EXPLAINS)));

                result.setWebString(cursor.getString(cursor.getColumnIndex(EFastQueryDbUtils.Favorite.WEBS)));

                result.phonetic = cursor.getString(cursor.getColumnIndex(EFastQueryDbUtils.Favorite.PHONETIC));

                result.uk_phonetic = cursor.getString(cursor.getColumnIndex(EFastQueryDbUtils.Favorite.UK_PHONETIC));

                result.us_phonetic = cursor.getString(cursor.getColumnIndex(EFastQueryDbUtils.Favorite.US_PHONETIC));

                FavoriteList.add(result);
            }
            cursor.close();
        }
        return FavoriteList;
    }

    public long insertFavorite(Result result, String group) {
        if (result == null) {
            return 0;
        }
        ContentValues values = new ContentValues();
        long time = System.currentTimeMillis();

        values.put(EFastQueryDbUtils.Favorite.DATE, time);

        values.put(EFastQueryDbUtils.Favorite.REQUEST, result.query);

        values.put(EFastQueryDbUtils.Favorite.TRANSLATIONS, result.getTranslateString());

        values.put(EFastQueryDbUtils.Favorite.EXPLAINS, result.getExplainsString());

        values.put(EFastQueryDbUtils.Favorite.WEBS, result.getWebString());

        values.put(EFastQueryDbUtils.Favorite.PHONETIC, result.phonetic);

        values.put(EFastQueryDbUtils.Favorite.UK_PHONETIC, result.uk_phonetic);

        values.put(EFastQueryDbUtils.Favorite.US_PHONETIC, result.us_phonetic);

        if (group != null) {
            values.put(EFastQueryDbUtils.Favorite.GROUPS, group);
        }
        mResolver.insert(EFastQueryDbUtils.Favorite.CONTENT_URI, values);

        return time;
    }

    public void updateFavoriteTime(String request) {
        ContentValues values = new ContentValues();
        values.put(EFastQueryDbUtils.Favorite.DATE, System.currentTimeMillis());
        mResolver.update(EFastQueryDbUtils.Favorite.CONTENT_URI, values, EFastQueryDbUtils.Favorite.REQUEST + "=?", new String[]{request});
    }

    public boolean isFavoriteExist(String request) {
        boolean exist = false;
        Cursor cursor = mResolver.query(EFastQueryDbUtils.Favorite.CONTENT_URI, new String[]{EFastQueryDbUtils.Favorite.REQUEST}, EFastQueryDbUtils.Favorite.REQUEST + "=?", new String[]{request}, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                exist = true;
            }
            cursor.close();
        }
        return exist;
    }

    public void deleteFavorite(String request) {
        mResolver.delete(EFastQueryDbUtils.Favorite.CONTENT_URI, EFastQueryDbUtils.Favorite.REQUEST + "=?", new String[]{request});
    }

    public void deleteAllFavorite() {
        mResolver.delete(EFastQueryDbUtils.Favorite.CONTENT_URI, "_id!=-1", null);
    }
}
