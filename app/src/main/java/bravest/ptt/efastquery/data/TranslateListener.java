package bravest.ptt.efastquery.data;

/**
 * Created by root on 1/4/17.
 */

public interface TranslateListener {
    void onTranslateStart();
    void onTranslateSuccess(Result result);
    void onTranslateFailed(String error);
}
