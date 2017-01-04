package bravest.ptt.efastquery.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by root on 1/4/17.
 */

public class EFastQueryDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "EFastQueryDBHelper";

    private static final String DATABASE_NAME = "efastquery.db";
    private static final int DATABASE_VERSION = 1;

    public EFastQueryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS history (" +
                "_id INTEGER PRIMARY KEY," +
                "time TimeStamp NOT NULL DEFAULT (datetime(CURRENT_TIMESTAMP,'localtime'))," +
                "request TEXT," +
                "result TEXT);");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
