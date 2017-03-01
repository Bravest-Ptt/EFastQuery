package bravest.ptt.efastquery.files;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.files.comparator.DRecentModifiedComparator;
import bravest.ptt.efastquery.utils.PLog;

/**
 * Created by root on 2/17/17.
 */

public class FileUtils {
    private static final String TAG = "FileUtils";

    //public static final String ASSETS_GIF_HAPPY = "happy.gif";

    public static final int MODE_JUST_DIR = 1;
    public static final int MODE_NORMAL_EXPORT = 2;
    public static final int MODE_NORMAL_IMPORT = 3;
    public static final int MODE_INCLUDE_HIDDEN = 4;

    public static final String EXTERNAL = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static ArrayList<File> getPathContent(String path, int mode, int order) {
        if (mode != MODE_JUST_DIR
                && mode != MODE_NORMAL_EXPORT
                && mode != MODE_NORMAL_IMPORT
                && mode != MODE_INCLUDE_HIDDEN) {
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

//    public static String giveValidFileName(File file, String expectFileName) {
//        String extension = null;
//        String finalName = expectFileName;
//        if (expectFileName.contains(".")) {
//            extension = expectFileName.substring(expectFileName.lastIndexOf('.'));
//            PLog.log("giveValidFileName + extension = " + extension);
//        }
//        int i = 0;
//        while (true) {
//
//            if (i != 0) {
//                folderName = getString(R.string.file_manager_new_folder, i+"");
//            }
//
//            File file = new File(parentPath + "/" + folderName);
//            if (!file.exists()) {
//                break;
//            }
//            i++;
//        }
//    }
//
//    public static String giveValidFileName(String parentPath, String expectFileName) {
//        File file = new File(filePath);
//        return giveValidFileName(file, expectFileName);
//    }

    public static void openFile(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri contentUri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            contentUri  = FileProvider.getUriForFile(context, "bravest.ptt.efastquery.fileprovider", file);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            contentUri = Uri.fromFile(file);
        }
        intent.setDataAndType(contentUri, getFileMimeType(file));
        context.startActivity(intent);
    }

    public static void shareFile(Context context, File file) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        share.setType(getFileMimeType(file));
        context.startActivity(Intent.createChooser(share, context.getString(R.string.export_success_share)));
    }

    public static String getFileMimeType(File file) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getMimeTypeFromExtension(extension);
    }
}
