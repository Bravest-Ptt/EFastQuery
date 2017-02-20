package bravest.ptt.efastquery.callback;

import java.io.File;

/**
 * Created by root on 2/20/17.
 */

public interface BuildListener {
    void onBuildingResult(File file, boolean success);
}
