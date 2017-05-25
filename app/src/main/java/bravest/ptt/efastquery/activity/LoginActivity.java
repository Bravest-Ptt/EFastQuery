package bravest.ptt.efastquery.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import bravest.ptt.androidlib.activity.AbstractBaseActivity;
import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.entity.User;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    public static final String REGISTER_SUCCESS_PROFILE_FAILED
            = "register_success_profile_failed";

    @Override
    protected void initVariables() {
        super.initVariables();
        Intent intent = getIntent();
        if (intent != null 
                && TextUtils.equals(intent.getAction(), REGISTER_SUCCESS_PROFILE_FAILED)) {
            User user = User.getInstance(mContext);
            Log.d(TAG, "initVariables: user = " + user);
        } else {
            Log.d(TAG, "initVariables:  user is null!");
        }
    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initData() {
    }
}
