package bravest.ptt.efastquery.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by root on 1/4/17.
 */

public class EFastQueryDbUtils {

    private static final String AUTHORITY = "bravest.ptt.efastquery";

    private static final String TAG = "EFastQueryDbUtils";

    public static final String DATABASE_NAME = "efastquery.db";

    public interface History extends BaseColumns {
        String TABLE_NAME = "history";
        Uri CONTENT_URI = Uri.parse("content://bravest.ptt.efastquery/history");
        String DATE = "date";
        String REQUEST = "request";
        String TRANSLATIONS = "translations";
        String EXPLAINS = "explains";
        String WEBS = "webs";
        String PHONETIC = "phonetic";
        String US_PHONETIC = "us_phonetic";
        String UK_PHONETIC = "uk_phonetic";
        String FAVORITE = "favorite";
    }

    public interface Favorite extends BaseColumns {
        String TABLE_NAME = "favorite";
        Uri CONTENT_URI = Uri.parse("content://bravest.ptt.efastquery/favorite");
        String DATE = "date";
        String REQUEST = "request";
        String TRANSLATIONS = "translations";
        String EXPLAINS = "explains";
        String WEBS = "webs";
        String PHONETIC = "phonetic";
        String US_PHONETIC = "us_phonetic";
        String UK_PHONETIC = "uk_phonetic";
        String GROUPS = "groups";
        String GROUPS_DEFAULT_VALUE = "p1n2t3u4tl_l";
    }
}
