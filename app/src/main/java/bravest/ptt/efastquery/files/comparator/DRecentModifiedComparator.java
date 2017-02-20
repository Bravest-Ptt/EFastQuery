package bravest.ptt.efastquery.files.comparator;

import android.content.Context;

import java.io.File;
import java.util.Comparator;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.files.FileUtils;
import bravest.ptt.efastquery.fragment.FileManagerFragment;

/**
 * Created by root on 2/17/17.
 */

public class DRecentModifiedComparator implements Comparator {

    public static final int SORT_ORDER_RECENT_MODIFIED = 1;
    public static final int SORT_ORDER_NAME = 2;

    private int mMode;
    private static final String APP_NAME = "快查英语";

    public DRecentModifiedComparator(int mode) {
        mMode = mode;
    }
    @Override
    public int compare(Object o, Object t1) {
        File f1 = (File)o;
        File f2 = (File)t1;
        switch (mMode) {
            case SORT_ORDER_RECENT_MODIFIED:
                if (f2.isDirectory() && f2.getAbsolutePath().equals(FileUtils.EXTERNAL + "/" + APP_NAME)) {
                    return 1;
                } else if (f1.isDirectory() && f1.getAbsolutePath().equals(FileUtils.EXTERNAL + "/" + APP_NAME)) {
                    return -1;
                }
                if (f1.lastModified() < f2.lastModified()) {
                    return 1;
                } else {
                    return -1;
                }
            case SORT_ORDER_NAME:
                return f1.getName().compareTo(f2.getName());
            default:
                break;
        }
        return 0;
    }
}
