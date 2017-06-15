package bravest.ptt.efastquery.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;

import bravest.ptt.androidlib.activity.toolbar.AbstractToolbarActivity;
import bravest.ptt.efastquery.R;

/**
 * Created by root on 6/6/17.
 */

public class SettingsActivity extends AbstractToolbarActivity {

    @Override
    protected void initVariables() {
    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void initData() {

    }
}
