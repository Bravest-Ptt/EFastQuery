package bravest.ptt.efastquery.engine;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import bravest.ptt.efastquery.interfaces.IWord;
import bravest.ptt.efastquery.entity.word.Word;
import bravest.ptt.efastquery.entity.word.WordBook;

/**
 * Created by root on 12/27/16.
 */

public class Result implements Serializable, IWord {

    private static final String TAG = "Result";
    //RESULT-NORMAL
    static final int RESULT_SUCCESS = 0;
    //RESULT-ERROR
    static final int RESULT_LONG = 20;
    static final int RESULT_INVALID = 30;
    static final int RESULT_UNSUPPORTED = 40;
    static final int RESULT_KEY_ERROR = 50;
    static final int RESULT_NO_RESULT = 60;

    //RESULT-LOCALE-ERROR
    static final int LOCALE_UNSUPPORTED_ENCODER = 70;
    static final int LOCALE_MALFORMED_URL = 80;
    static final int LOCALE_IO_EXCEPTION = 90;
    static final int LOCALE_JSON_EXCEPTION = 100;


    private JSONObject mResult;


    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(long time) {
        this.date = new Date(time);
    }

    //Parsed
    String error_code;
    public String query;

    JSONArray translation;

    public String phonetic;
    public String us_phonetic;
    public String uk_phonetic;
    public JSONArray explains;

    JSONArray web;

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
        WordBook wordBook = new WordBook(query, explains_str, "[" + phonetic + "]", null);
        wordBook.setDate(getDate());
        return wordBook;
    }

    public Word getWord() {
        Word word = new Word(query, explains_str, getDate());
        return word;
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

    public static final String JSON_SPLIT = "; ";

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


    /**
     * n =名词
     v=动词,包括vt=及物动词,vi=不及物动词
     adj=形容词
     adv=副词
     pron=代词
     mum=数词
     art=冠词
     prep=介词
     interj=叹词
     conj=连词
     aux=助词
     */
    private static final String[] ENGLIST_TAGS = {
            "n.",
            "v.",
            "vi.",
            "vt.",
            "adj.",
            "adv.",
            "pron.",
            "num.",
            "art.",
            "prep.",
            "interj.",
            "conj.",
            "aux."
    };

    private boolean containTags(String string) {
        for (int j = 0; j < ENGLIST_TAGS.length; j++) {
            if (string.contains(ENGLIST_TAGS[j])) {
                return true;
            }
        }
        return false;
    }

    public HashMap<String,String> getResultMap() {
        HashMap<String,String> map = new HashMap<>();
        map.put(YouDaoItem.YOUDAO_QUERY, query);

        if (TextUtils.isEmpty(uk_phonetic)) {
            map.put(YouDaoItem.YOUDAO_UK_PHONETIC, "");
        } else {
            map.put(YouDaoItem.YOUDAO_UK_PHONETIC, "英 [" + uk_phonetic + "]");
        }

        if (TextUtils.isEmpty(us_phonetic)) {
            map.put(YouDaoItem.YOUDAO_US_PHONETIC, "");
        } else {
            map.put(YouDaoItem.YOUDAO_US_PHONETIC, "美 [" + us_phonetic + "]");
        }

        try {
            if (explains != null) {
                String result = "";
                Log.d(TAG, "getResultMap: explains = " + explains);
                int length = explains.length();
                for (int i = 0; i < length; i++) {
                    result += explains.getString(i) + ";";
                    if (i != length - 1 && containTags(explains.getString(i + 1))) {
                        result += "\n";
                    }
                }
                map.put(YouDaoItem.YOUDAO_EXPLAINS, result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public String getResult() {
        return null;
    }

    public static class YouDaoItem {
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
            Log.d(TAG, "jsonArray2String: length = " + length);
            for (int i = 0; i < length; i++) {
                if (i != length - 1 || length == 1)
                    result += jsonArray.getString(i) + split;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    private JSONArray string2JsonArray(String original, String split) {
        if (TextUtils.isEmpty(original)) return null;
        JSONArray jsonArray = new JSONArray();
        String[] array = original.split(split);
        for (String str : array) {
            jsonArray.put(str);
        }
        return jsonArray;
    }
}
