package bravest.ptt.efastquery.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

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

    public ArrayList<HistoryModule> getAllHistory() {
        ArrayList<HistoryModule> historyList = new ArrayList<>();

        Cursor cursor = mResolver.query(History.CONTENT_URI, null, null, null, History.DATE + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                HistoryModule model = new HistoryModule();
                model.request = cursor.getString(cursor.getColumnIndex(History.REQUEST));
                Log.d(TAG, "getAllHistory: model.request = " + model.request);
                model.result = cursor.getString(cursor.getColumnIndex(History.RESULT));
                Log.d(TAG, "getAllHistory: model.result = " + model.result);
                model.date = cursor.getString(cursor.getColumnIndex(History.DATE));
                Log.d(TAG, "getAllHistory: model.date = " + model.date);
                historyList.add(model);
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
        values.put(History.RESULT, result.getResult());
        mResolver.insert(History.CONTENT_URI, values);
        return time;
    }

    public void updateHistory(String request) {
        ContentValues values = new ContentValues();
        values.put(History.DATE, System.currentTimeMillis());
        mResolver.update(History.CONTENT_URI, values, History.REQUEST+"=?",new String[]{request});
    }

    public boolean isRequestExist(String request) {
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
