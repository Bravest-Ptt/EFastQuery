package bravest.ptt.efastquery.data.wordbook;

import java.io.File;

import bravest.ptt.efastquery.callback.BuildListener;

/**
 * Created by root on 2/20/17.
 */

public abstract class Builder {
    protected BuildListener mBuildListener;
    public Builder setBuildListener(BuildListener listener) {
        mBuildListener = listener;
        return this;
    }

    protected void onResult(File file, boolean success) {
        if (mBuildListener != null) {
            mBuildListener.onBuildingResult(file, success);
        }
    }
}
