package bravest.ptt.efastquery.files;

import java.io.File;
import java.io.FileFilter;

import static bravest.ptt.efastquery.files.FileUtils.*;

/**
 * Created by root on 2/17/17.
 */

public class DFFilter implements FileFilter {

    private int mMode = MODE_NORMAL;

    public void setMode(int mode) {
        mMode = mode;
    }

    @Override
    public boolean accept(File file) {
        switch (mMode) {
            case MODE_NORMAL:
                if (file.isHidden()){
                    return false;
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
