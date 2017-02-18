package bravest.ptt.efastquery.files;


import android.os.Environment;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;

import bravest.ptt.efastquery.files.comparator.DRecentModifiedComparator;
import bravest.ptt.efastquery.utils.PLog;

/**
 * Created by root on 2/17/17.
 */

public class FileUtils {
    private static final String TAG = "FileUtils";

    public static final int MODE_JUST_DIR = 1;
    public static final int MODE_NORMAL = 2;
    public static final int MODE_INCLUDE_HIDDEN = 3;

    public static ArrayList<File> getPathContent(String path, int mode, int order) {
        if (mode != MODE_JUST_DIR && mode != MODE_NORMAL && mode != MODE_INCLUDE_HIDDEN) {
            PLog.d(TAG, "mode invalid");
            return null;
        }
        File curDir = new File(path);
        if (!curDir.exists()) {
            PLog.d(TAG, "file path error");
            return null;
        }
        if (curDir.exists() && !curDir.isDirectory()) {
            PLog.d(TAG, "file is not directory");
            return null;
        }

        ArrayList<File> filesArray = new ArrayList<>();
        ArrayList<File> dirsArray = new ArrayList<>();
        DFFilter filter = new DFFilter();
        filter.setMode(mode);
        File[] files = curDir.listFiles(filter);
        if (files == null) {
            return null;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                dirsArray.add(f);
            } else if (f.isFile()) {
                filesArray.add(f);
            }
            PLog.d(TAG, f.getAbsolutePath());
        }

        DRecentModifiedComparator orderComparator = new DRecentModifiedComparator(order);
        Collections.sort(filesArray, orderComparator);
        Collections.sort(dirsArray, orderComparator);

        ArrayList<File> total = new ArrayList<>();
        total.addAll(dirsArray);
        total.addAll(filesArray);

        return total;
    }

    public static ArrayList<File> getPathContent(String path, int mode) {
        return getPathContent(path, mode, DRecentModifiedComparator.SORT_ORDER_RECENT_MODIFIED);
    }

    public static String getPathWithoutExternal(String path) throws PathFormatErrorException{
        String external = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (!path.contains(external)) {
            throw new PathFormatErrorException();
        }
        PLog.log("before : " + path);
        path = path.substring(external.length());
        PLog.log("after : " + path);
        return path;
    }

    public static String getPathWithoutExternal(File file) throws PathFormatErrorException{
        return getPathWithoutExternal(file.getAbsolutePath());
    }

    public static class PathFormatErrorException extends Exception{
    }
}
