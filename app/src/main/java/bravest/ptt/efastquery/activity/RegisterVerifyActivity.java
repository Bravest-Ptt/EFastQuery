package bravest.ptt.efastquery.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;

import bravest.ptt.androidlib.activity.BaseActivity;
import bravest.ptt.androidlib.utils.plog.PLog;
import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.entity.SmsCodeEntity;
import bravest.ptt.efastquery.entity.User;

public class RegisterVerifyActivity extends BaseActivity {

    @Override
    protected void initVariables() {
        Intent intent = getIntent();
        if (intent != null) {
            SmsCodeEntity entity = (SmsCodeEntity) intent.getExtras().get(SmsCodeEntity.getName());
            PLog.log(entity);
            String password = intent.getStringExtra(User.PASSWORD);
            PLog.log(password);
        }
    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_register_verify);

    }

    @Override
    protected void initData() {

    }
}
