package bravest.ptt.efastquery.listeners;

import java.io.Serializable;

/**
 * Created by root on 1/4/17.
 */

public interface OnTranslateListener<T extends Serializable> {
    void onTranslateStart();
    void onTranslateSuccess(T result);
    void onTranslateFailed(String error);
}
