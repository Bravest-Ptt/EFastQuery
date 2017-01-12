package bravest.ptt.efastquery.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 12/27/16.
 */

public class Result {

    private static final String TAG = "Result";
    //RESULT-NORMAL
    public static final int RESULT_SUCCESS = 0;
    //RESULT-ERROR
    public static final int RESULT_LONG = 20;
    public static final int RESULT_INVALID = 30;
    public static final int RESULT_UNSUPPORTED = 40;
    public static final int RESULT_KEY_ERROR = 50;
    public static final int RESULT_NO_RESULT = 60;

    //RESULT-LOCALE-ERROR
    public static final int LOCALE_UNSUPPORTED_ENCODER = 70;
    public static final int LOCALE_MALFORMED_URL = 80;
    public static final int LOCALE_IO_EXCEPTION = 90;
    public static final int LOCALE_JSON_EXCEPTION = 100;


    private JSONObject mResult;

    public String error_code;
    public String query;

    public JSONArray translation;

    public String phonetic;
    public String us_phonetic;
    public String uk_phonetic;
    public JSONArray explains;

    public JSONArray web;

    public Result(JSONObject result) {
        this.mResult = result;
        parseJsonResult();
    }

    private void parseJsonResult() {
        try {
            parseTranslation();
            parseBasic();
            parseWeb();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseTranslation() throws JSONException {
        translation = mResult.getJSONArray(YouDaoItem.YOUDAO_TRANSLATION);
    }

    private void parseBasic() throws JSONException {
        error_code = mResult.getString(YouDaoItem.YOUDAO_ERRORCODE);
        query = mResult.getString(YouDaoItem.YOUDAO_QUERY);

        JSONObject basic = mResult.getJSONObject(YouDaoItem.YOUDAO_BASIC);
        if (basic != null) {
            phonetic = basic.has(YouDaoItem.YOUDAO_PHONETIC)
                    ? basic.getString(YouDaoItem.YOUDAO_PHONETIC)
                    : null;
            us_phonetic = basic.has(YouDaoItem.YOUDAO_US_PHONETIC)
                    ? basic.getString(YouDaoItem.YOUDAO_US_PHONETIC)
                    : null;
            uk_phonetic = basic.has(YouDaoItem.YOUDAO_UK_PHONETIC)
                    ? basic.getString(YouDaoItem.YOUDAO_UK_PHONETIC)
                    : null;
            explains = basic.has(YouDaoItem.YOUDAO_EXPLAINS)
                    ? basic.getJSONArray(YouDaoItem.YOUDAO_EXPLAINS)
                    : null;
            Log.d(TAG, "parseBasic: explains = " + explains);
        }
    }

    private void parseWeb() throws JSONException {
        web = mResult.getJSONArray(YouDaoItem.YOUDAO_WEB);
        Log.d(TAG, "parseWeb: web = " + web);
    }

    public String getResultWithQuery() {
        String result = "query : " + query + "\n";

        try {
            if (translation != null) {
                result += "translations : ";
                int transLength = translation.length();
                for (int i = 0; i < transLength; i++) {
                    result += (i == 0) ? translation.getString(i) : "; " + translation.getString(i);
                }
                result += "\n";
            }

            if (explains != null) {
                result += "explains : ";
                int explainsLength = explains.length();
                for (int i = 0; i < explainsLength; i++) {
                    result += (i == 0) ? explains.getString(i) : "; " + explains.getString(i);
                }
                result += "\n";

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return result + "phonetic : " + phonetic + "\n"
                + "us_phonetic : " + us_phonetic + "\n"
                + "uk_phonetic : " + uk_phonetic + ";";
    }

    public String getResult() {
        String result = getResultWithQuery();
        int index = result.indexOf("translations");
        return result.substring(index);
    }

    public class YouDaoItem {
        public static final String YOUDAO_ERRORCODE = "errorCode";
        public static final String YOUDAO_QUERY = "query";
        public static final String YOUDAO_TRANSLATION = "translation";
        public static final String YOUDAO_BASIC = "basic";
        public static final String YOUDAO_WEB = "web";

        public static final String YOUDAO_PHONETIC = "phonetic";
        public static final String YOUDAO_UK_PHONETIC = "uk-phonetic";
        public static final String YOUDAO_US_PHONETIC = "us-phonetic";
        public static final String YOUDAO_EXPLAINS = "explains";
    }
}
