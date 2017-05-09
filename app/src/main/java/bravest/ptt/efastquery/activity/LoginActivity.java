package bravest.ptt.efastquery.activity;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import bravest.ptt.androidlib.activity.BaseActivity;
import bravest.ptt.efastquery.R;
import cn.bmob.v3.BmobUser;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initData() {
        BmobUser user = BmobUser.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "onCreate: phoneNumber:" + user.getMobilePhoneNumber());
            Log.d(TAG, "onCreate: username :" + user.getUsername());
        }
    }
}
