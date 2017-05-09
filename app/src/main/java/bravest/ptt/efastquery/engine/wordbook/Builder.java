package bravest.ptt.efastquery.engine.wordbook;

import java.io.File;

import bravest.ptt.efastquery.listeners.OnBuildListener;

/**
 * Created by root on 2/20/17.
 */

public abstract class Builder {
    protected OnBuildListener mOnBuildListener;
    public Builder setBuildListener(OnBuildListener listener) {
        mOnBuildListener = listener;
        return this;
    }

    protected void onResult(File file, boolean success) {
        if (mOnBuildListener != null) {
            mOnBuildListener.onBuildingResult(file, success);
        }
    }
}
