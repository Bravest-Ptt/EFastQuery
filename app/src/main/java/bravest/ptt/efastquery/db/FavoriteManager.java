package bravest.ptt.efastquery.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.engine.Result;
import bravest.ptt.efastquery.interfaces.IWord;
import bravest.ptt.efastquery.entity.word.Word;
import bravest.ptt.efastquery.entity.word.WordBook;
import bravest.ptt.androidlib.utils.RegularUtils;

/**
 * Created by root on 2/13/17.
 */

public class FavoriteManager {
    private static final String TAG = "FavoriteManager";

    public static final String SP_NAME = "efastquery.xml";
    public static final String FM_GROUP = "fm_groups";

    private Context mContext;
    private ContentResolver mResolver;

    public FavoriteManager(Context context) {
        mContext = context;
        mResolver = mContext.getContentResolver();
    }

    public ArrayList<Word> getFavoritePreByGroup(String group) {
        Log.d(TAG, "getFavoritePre: group = " + group);
        if (TextUtils.equals(group, mContext.getString(R.string.group_default))) {
            group = EFastQueryDbUtils.Favorite.GROUPS_DEFAULT_VALUE;
        }
        ArrayList<Word> FavoriteList = new ArrayList<>();

        String selectionArgs = EFastQueryDbUtils.Favorite.GROUPS + "=?";
        Cursor cursor = mResolver.query(EFastQueryDbUtils.Favorite.CONTENT_URI,
                null,
                selectionArgs,
                new String[]{group},
                EFastQueryDbUtils.Favorite.DATE + " DESC");
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

                result.setDate(cursor.getLong(cursor.getColumnIndex(EFastQueryDbUtils.Favorite.DATE)));

                Word word = result.getWord();

                FavoriteList.add(word);
            }
            cursor.close();
        }
        return FavoriteList;
    }

    public ArrayList<WordBook> getFavoriteByGroup(String group) {
        Log.d(TAG, "getGroupFavorite: group = " + group);
        if (TextUtils.equals(group, mContext.getString(R.string.group_default))) {
            group = EFastQueryDbUtils.Favorite.GROUPS_DEFAULT_VALUE;
        }
        ArrayList<WordBook> FavoriteList = new ArrayList<>();

        String selectionArgs = EFastQueryDbUtils.Favorite.GROUPS + "=?";
        Cursor cursor = mResolver.query(EFastQueryDbUtils.Favorite.CONTENT_URI,
                null,
                selectionArgs,
                new String[]{group},
                EFastQueryDbUtils.Favorite.DATE + " DESC");
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

                result.setDate(cursor.getLong(cursor.getColumnIndex(EFastQueryDbUtils.Favorite.DATE)));

                WordBook book = result.getWordBook();

                book.setTags(group);

                FavoriteList.add(book);
            }
            cursor.close();
        }
        return FavoriteList;
    }

    public static final int MODE_RESULT = 1;
    public static final int MODE_WORD = 2;
    public static final int MODE_WORDBOOK = 3;

    public ArrayList<IWord> getFavoriteByGroup(ArrayList<IWord> list, String group, int mode) {
        if (TextUtils.isEmpty(group)) {
            return null;
        }
        if (list == null) {
            list = new ArrayList<>();
        }

        Log.d(TAG, "getGroupFavorite: group = " + group);
        if (TextUtils.equals(group, mContext.getString(R.string.group_default))) {
            group = EFastQueryDbUtils.Favorite.GROUPS_DEFAULT_VALUE;
        }


        String selectionArgs = EFastQueryDbUtils.Favorite.GROUPS + "=?";
        Cursor cursor = mResolver.query(EFastQueryDbUtils.Favorite.CONTENT_URI,
                null,
                selectionArgs,
                new String[]{group},
                EFastQueryDbUtils.Favorite.DATE + " DESC");

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

                result.setDate(cursor.getLong(cursor.getColumnIndex(EFastQueryDbUtils.Favorite.DATE)));

                switch (mode) {
                    case MODE_RESULT:
                        list.add(result);
                        break;
                    case MODE_WORD:
                        Word word = result.getWord();
                        word.setTags(group);
                        list.add(word);
                        break;
                    case MODE_WORDBOOK:
                        WordBook book = result.getWordBook();
                        book.setTags(group);
                        list.add(book);
                        break;
                    default:
                        throw new RuntimeException("Favorite Manager mode error");
                }

            }
            cursor.close();
        }
        return list;
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
        if (TextUtils.isEmpty(request)) {
            return false;
        }
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

    public static boolean createGroup(Context context, String groupName) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Set<String> set = new HashSet<String>();
        set.add(context.getString(R.string.group_default));
        Set<String> groupSet = sp.getStringSet(FM_GROUP, set);

        if (!TextUtils.isEmpty(groupName) && RegularUtils.isUsername(groupName)) {
            if (!groupName.equals(EFastQueryDbUtils.Favorite.GROUPS_DEFAULT_VALUE)) {
                groupSet.add(groupName);
                Toast.makeText(context, R.string.file_manager_create_group_success, Toast.LENGTH_SHORT).show();
            }
            editor.putStringSet(FM_GROUP, groupSet);
            editor.commit();
            return true;
        } else {
            Toast.makeText(context, R.string.file_manager_create_group_failed, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static String[] getGroup(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        Set<String> groupSet = sp.getStringSet(FM_GROUP, new HashSet<String>());
        if (groupSet.size() == 0) {
            createGroup(context, EFastQueryDbUtils.Favorite.GROUPS_DEFAULT_VALUE);
            groupSet = sp.getStringSet(FM_GROUP, new HashSet<String>());
        }
        String[] groups = groupSet.toArray(new String[groupSet.size()]);
        return groups;
    }
}
