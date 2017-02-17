package bravest.ptt.efastquery.files.comparator;

import java.io.File;
import java.util.Comparator;

/**
 * Created by root on 2/17/17.
 */

public class DRecentModifiedComparator implements Comparator {

    public static final int SORT_ORDER_RECENT_MODIFIED = 1;
    public static final int SORT_ORDER_NAME = 2;

    private int mMode;

    public DRecentModifiedComparator(int mode) {
        mMode = mode;
    }
    @Override
    public int compare(Object o, Object t1) {
        File f1 = (File)o;
        File f2 = (File)t1;
        switch (mMode) {
            case SORT_ORDER_RECENT_MODIFIED:
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
