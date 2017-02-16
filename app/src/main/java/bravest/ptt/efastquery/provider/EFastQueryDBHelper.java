package bravest.ptt.efastquery.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class EFastQueryDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "EFastQueryDBHelper";

    private static final String DATABASE_NAME = "efastquery.db";
    private static final int DATABASE_VERSION = 1;

    public EFastQueryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private void createTable(SQLiteDatabase db) {
        //Create history
        db.execSQL("CREATE TABLE IF NOT EXISTS history (" +
                "_id INTEGER PRIMARY KEY," +
                "date TimeStamp NOT NULL DEFAULT (datetime(CURRENT_TIMESTAMP,'localtime'))," +
                "request TEXT NOT NULL," +
                "translations TEXT," +
                "explains TEXT," +
                "webs TEXT," +
                "phonetic TEXT," +
                "us_phonetic TEXT," +
                "uk_phonetic TEXT," +
                "favorite INTEGER DEFAULT 0);");

        //Create Favorite
        db.execSQL("CREATE TABLE IF NOT EXISTS favorite (" +
                "_id INTEGER PRIMARY KEY," +
                "date TimeStamp NOT NULL DEFAULT (datetime(CURRENT_TIMESTAMP,'localtime'))," +
                "request TEXT NOT NULL," +
                "translations TEXT," +
                "explains TEXT," +
                "webs TEXT," +
                "phonetic TEXT," +
                "us_phonetic TEXT," +
                "uk_phonetic TEXT," +
                "groups TEXT DEFAULT 'null');");

        db.execSQL("CREATE TRIGGER favorite_insert_listener" +
                " AFTER INSERT" +
                " ON favorite" +
                " BEGIN" +
                " UPDATE history SET favorite=1 WHERE (request=NEW.request);" +
                " END;");

        db.execSQL("CREATE TRIGGER favorite_delete_listener" +
                " BEFORE DELETE" +
                " ON favorite" +
                " BEGIN" +
                " UPDATE history SET favorite=0 WHERE (request=OLD.request);" +
                " END;");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
