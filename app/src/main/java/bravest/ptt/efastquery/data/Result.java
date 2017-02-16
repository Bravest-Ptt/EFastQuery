package bravest.ptt.efastquery.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import bravest.ptt.efastquery.data.wordbook.WordBook;

/**
 * Created by root on 12/27/16.
 */

public class Result implements Serializable {

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


    //Parsed
    public String error_code;
    public String query;

    public JSONArray translation;

    public String phonetic;
    public String us_phonetic;
    public String uk_phonetic;
    public JSONArray explains;

    public JSONArray web;

    private String translation_str;
    private String explains_str;
    private String web_str;

    public Result(JSONObject result) {
        this.mResult = result;
        parseJsonResult();
    }

    public Result() {
    }

    public WordBook getWordBook() {
        return new WordBook(query, translation_str, phonetic, null);
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

    private static final String JSON_SPLIT = "; ";

    private void parseTranslation() throws JSONException {
        translation = mResult.getJSONArray(YouDaoItem.YOUDAO_TRANSLATION);
        Log.d(TAG, "parseTranslation: translation = " + translation.getString(0));
        translation_str = jsonArray2String(translation, JSON_SPLIT);
        Log.d(TAG, "parseTranslation: parse trans = " + translation_str);
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
            explains_str = jsonArray2String(explains, JSON_SPLIT);
            Log.d(TAG, "parseBasic: explains = " + explains);
        }
    }

    private void parseWeb() throws JSONException {
        web = mResult.getJSONArray(YouDaoItem.YOUDAO_WEB);
        Log.d(TAG, "parseWeb: web = " + web);
        web_str = jsonArray2String(web, JSON_SPLIT);
    }

    public String getResultWithQuery() {
        String result = "query : " + query + "\n";

        try {
            if (translation != null) {
                result += "translations : ";
                int transLength = translation.length();
                for (int i = 0; i < transLength; i++) {
                    result += translation.getString(i) + ";";
                }
                result += "\n";
            }

            if (explains != null) {
                result += "explains : ";
                int explainsLength = explains.length();
                for (int i = 0; i < explainsLength; i++) {
                    result += explains.getString(i) + ";";
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
        return index == -1 ? result : result.substring(index);
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

    public String getTranslateString() {
        Log.d(TAG, "getTranslateString: get = " + translation_str);
        return translation_str;
    }

    public void setTranslateString(String trans) {
        Log.d(TAG, "setTranslateString: trans = " + trans);
        translation_str = trans;
        if (translation == null) {
            translation = string2JsonArray(trans, JSON_SPLIT);
        }
    }

    public String getExplainsString() {
        return explains_str;
    }

    public void setExplainString(String explain) {
        explains_str = explain;
        if (explains == null) {
            explains = string2JsonArray(explain, JSON_SPLIT);
        }
    }

    public String getWebString() {
        return web_str;
    }

    public void setWebString(String webstirng) {
        web_str = webstirng;
        if (web == null) {
            web = string2JsonArray(webstirng, JSON_SPLIT);
        }
    }

    private String jsonArray2String(JSONArray jsonArray, String split) {
        String result = "";
        if (jsonArray == null) {
            return result;
        }
        try {
            int length = jsonArray.length();
            Log.d(TAG, "jsonArray2String: length = "  + length);
            for (int i = 0; i < length; i++) {
                if (i != length - 1 || length == 1)
                    result += jsonArray.getString(i) + split;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            return result;
        }
    }

    private JSONArray string2JsonArray(String original, String split) {
        if (original == "") return null;
        JSONArray jsonArray = new JSONArray();
        String[] array = original.split(split);
        for (String str:array) {
            jsonArray.put(str);
        }
        return jsonArray;
    }

//    public Result copy(Result src) {
//        Result copy = new Result();
//        copy.query = src.query;
//        copy.translation = src.translation;
//
//    }
}
