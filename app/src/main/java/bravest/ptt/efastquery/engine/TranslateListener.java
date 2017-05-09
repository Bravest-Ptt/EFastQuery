package bravest.ptt.efastquery.engine;

import java.io.Serializable;

/**
 * Created by root on 1/4/17.
 */

public interface TranslateListener<T extends Serializable> {
    void onTranslateStart();
    void onTranslateSuccess(T result);
    void onTranslateFailed(String error);
}
