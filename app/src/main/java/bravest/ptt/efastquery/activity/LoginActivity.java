package bravest.ptt.efastquery.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import bravest.ptt.efastquery.R;
import cn.bmob.v3.BmobUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: user= " + BmobUser.getCurrentUser().toString());
        BmobUser user = BmobUser.getCurrentUser();
        Log.d(TAG, "onCreate: phoneNumber:" + user.getMobilePhoneNumber());
        Log.d(TAG, "onCreate: username :" + user.getUsername());
        Log.d(TAG, "onCreate: ");
    }
}
