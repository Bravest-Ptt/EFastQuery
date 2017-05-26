package bravest.ptt.efastquery.utils;

import android.content.res.AssetManager;

import bravest.ptt.efastquery.entity.word.Key;

/**
 * Created by root on 5/26/17.
 */

public class JNIUtils {
    static{
        System.loadLibrary("EFastQuery");
    }

    public native static Key getKey(AssetManager assetManager);
}
