package bravest.ptt.efastquery.listeners;

import java.io.File;

/**
 * Created by root on 2/20/17.
 */

public interface OnBuildListener {
    void onBuildingResult(File file, boolean success);
}
