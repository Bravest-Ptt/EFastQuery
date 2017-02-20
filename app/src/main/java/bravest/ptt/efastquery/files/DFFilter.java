package bravest.ptt.efastquery.files;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileFilter;

import static bravest.ptt.efastquery.files.FileUtils.*;

/**
 * Created by root on 2/17/17.
 */

public class DFFilter implements FileFilter {

    private int mMode = MODE_NORMAL_EXPORT;

    public void setMode(int mode) {
        mMode = mode;
    }

    @Override
    public boolean accept(File file) {
        switch (mMode) {
            case MODE_NORMAL_EXPORT:
                if (file.isHidden()){
                    return false;
                }
                if (file.isFile()) {
                    String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                    if (!(TextUtils.equals("xml",extension)
                            || TextUtils.equals("doc",extension)
                            || TextUtils.equals("txt",extension))) {
                        return false;
                    }
                }
                return true;
            case MODE_NORMAL_IMPORT:
                if (file.isHidden()){
                    return false;
                }
                if (file.isFile()) {
                    String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                    if (!TextUtils.equals("xml",extension)) {
                        return false;
                    }
                }
                return true;
            case MODE_JUST_DIR:
                if (file.isHidden() && file.isFile()){
                    return false;
                }
                return true;
            case MODE_INCLUDE_HIDDEN:
                return true;
        }
        return true;
    }
}
