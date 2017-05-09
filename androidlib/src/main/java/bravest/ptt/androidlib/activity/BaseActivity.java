package bravest.ptt.androidlib.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by pengtian on 2017/5/8.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        initVariables();
        initViews();
        initData();
    }
    protected abstract void initVariables();
    protected abstract void initViews();
    protected abstract void initData();
}
