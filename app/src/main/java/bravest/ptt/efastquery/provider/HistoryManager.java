package bravest.ptt.efastquery.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;

import bravest.ptt.efastquery.data.Result;
import bravest.ptt.efastquery.model.HistoryModule;
import bravest.ptt.efastquery.provider.EFastQueryDbUtils.*;

/**
 * Created by root on 1/12/17.
 */

public class HistoryManager {
    private static final String TAG = "HistoryManager";

    private Context mContext;
    private ContentResolver mResolver;

    public HistoryManager(Context context) {
        mContext = context;
        mResolver = mContext.getContentResolver();
    }

    public ArrayList<Result> getAllHistory() {
        ArrayList<Result> historyList = new ArrayList<>();

        Cursor cursor = mResolver.query(History.CONTENT_URI, null, null, null, History.DATE + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Result result = new Result();

                result.query = cursor.getString(cursor.getColumnIndex(History.REQUEST));

                result.setTranslateString(cursor.getString(cursor.getColumnIndex(History.TRANSLATIONS)));

                result.setExplainString(cursor.getString(cursor.getColumnIndex(History.EXPLAINS)));

                result.setWebString(cursor.getString(cursor.getColumnIndex(History.WEBS)));

                result.phonetic = cursor.getString(cursor.getColumnIndex(History.PHONETIC));

                result.uk_phonetic = cursor.getString(cursor.getColumnIndex(History.UK_PHONETIC));

                result.us_phonetic = cursor.getString(cursor.getColumnIndex(History.US_PHONETIC));

                historyList.add(result);
            }
            cursor.close();
        }
        return historyList;
    }

    public long insertHistory(Result result) {
        if (result == null) {
            return 0;
        }
        ContentValues values = new ContentValues();
        long time = System.currentTimeMillis();

        values.put(History.DATE, time);

        values.put(History.REQUEST, result.query);

        values.put(History.TRANSLATIONS, result.getTranslateString());

        values.put(History.EXPLAINS, result.getExplainsString());

        values.put(History.WEBS, result.getWebString());

        values.put(History.PHONETIC, result.phonetic);

        values.put(History.UK_PHONETIC, result.uk_phonetic);

        values.put(History.US_PHONETIC, result.us_phonetic);

        mResolver.insert(History.CONTENT_URI, values);

        return time;
    }

    public void updateHistoryTime(String request) {
        if (TextUtils.isEmpty(request)) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(History.DATE, System.currentTimeMillis());
        mResolver.update(History.CONTENT_URI, values, History.REQUEST + "=?", new String[]{request});
    }

    public boolean isRequestExist(String request) {
        if (TextUtils.isEmpty(request)) {
            return false;
        }
        boolean exist = false;
        Cursor cursor = mResolver.query(History.CONTENT_URI, new String[]{History.REQUEST}, History.REQUEST + "=?", new String[]{request}, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                exist = true;
            }
            cursor.close();
        }
        return exist;
    }

    public void deleteHistory(String request) {
        mResolver.delete(History.CONTENT_URI, History.REQUEST + "=?", new String[]{request});
    }

    public void deleteAllHistory() {
        mResolver.delete(History.CONTENT_URI, "_id!=-1", null);
    }
}
