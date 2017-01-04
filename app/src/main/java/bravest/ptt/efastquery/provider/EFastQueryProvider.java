package bravest.ptt.efastquery.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by root on 1/4/17.
 */

public class EFastQueryProvider extends ContentProvider {

    private static final String TAG = "EFastQueryProvider";
    private static final String PACKAGE_NAME = "bravest.ptt.efastquery";
    private static final String DATABASE_NAME = "efastquery.db";
    private static final int HISTORY = 1;
    private static final int HISTORY_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private EFastQueryDBHelper mOpenHelper;

    static {
        sUriMatcher.addURI(PACKAGE_NAME, "history", 1);
        sUriMatcher.addURI(PACKAGE_NAME, "history/#", 2);
    }

    private void getDatabaseHelper() {
        if (!getContext().getDatabasePath(DATABASE_NAME).exists()) {
            mOpenHelper = new EFastQueryDBHelper(getContext());
        }
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "provider onCreate");
        mOpenHelper = new EFastQueryDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projectionIn, String selection, String[] selectionArgs, String sortOrder) {
        getDatabaseHelper();
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Log.d(TAG, "provider query uri: " + uri + " match: " + match + " selection: " + selection + " selectionArgs: " + selectionArgs);

        switch (match) {
            case 1:
                sqliteQueryBuilder.setTables("history");
                break;
            case 2:
                sqliteQueryBuilder.setTables("history");
                sqliteQueryBuilder.appendWhere("_id=");
                sqliteQueryBuilder.appendWhere(uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        Cursor cursor = sqliteQueryBuilder.query(db, projectionIn, selection, selectionArgs, null, null, sortOrder);
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        } else {
            Log.e(TAG, "query failed");
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case 1:
                return "vnd.android.cursor.dir/history";
            case 2:
                return "vnd.android.cursor.item/history";
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        getDatabaseHelper();
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        Log.d(TAG, "insert: uri = " + uri + ", match = " + match);

        long rowId;
        switch (match) {
            case 1:
            case 2:
                rowId = db.insert("history", null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URL");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, rowId);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int match = sUriMatcher.match(uri);
        getDatabaseHelper();
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        Log.d(TAG, "provider delete uri: " + uri + " where: " + where + " whereArgs: " + whereArgs + " match: " + match);

        int affectedRows;
        switch (match) {
            case 1:
                affectedRows = db.delete("history", where, whereArgs);
                break;
            case 2:
                affectedRows = db.delete("history", "_id=" + uri.getLastPathSegment() + " AND ( " + where + " )", whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete from URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return affectedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        getDatabaseHelper();
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int affectedRows;
        switch(match) {
            case 1:
                affectedRows = db.update("history", values, where, whereArgs);
                break;
            case 2:
                affectedRows = db.update("history", values, "_id" + uri.getLastPathSegment(), null);
                break;
            default:
                throw new UnsupportedOperationException("Cannot update URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return affectedRows;
    }
}
