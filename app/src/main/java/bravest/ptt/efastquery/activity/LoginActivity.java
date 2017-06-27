package bravest.ptt.efastquery.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;

import bravest.ptt.androidlib.net.RequestParam;
import bravest.ptt.androidlib.utils.ToastUtils;
import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.activity.base.BaseToolBarActivity;
import bravest.ptt.efastquery.entity.User;
import bravest.ptt.efastquery.utils.API;
import bravest.ptt.efastquery.utils.UserUtils;
import bravest.ptt.efastquery.utils.Utils;

import static bravest.ptt.efastquery.activity.RegisterActivity.LENGTH_PASSWORD;

public class LoginActivity extends BaseToolBarActivity {

    private static final String TAG = "LoginActivity";

    public static final String REGISTER_SUCCESS_PROFILE_FAILED
            = "register_success_profile_failed";

    public static final String ACTION_FIND_PASSWORD_SUCCESSFUL = "find_password_successful";

    private EditText mAccountEditor;

    private EditText mPasswordEditor;

    private Button mLoginButton;

    private ProgressDialog mDialog;

    @Override
    protected void initVariables() {
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
        initToolbar();
        mAccountEditor = (EditText) findViewById(R.id.accountEditor);
        mPasswordEditor = (EditText) findViewById(R.id.passWordEditor);
        mLoginButton = (Button) findViewById(R.id.login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLoginClick();
            }
        });

        mDialog = Utils.newFullScreenProgressDialog(mContext);


        Intent intent = getIntent();
        if (intent != null
                && TextUtils.equals(intent.getAction(), ACTION_FIND_PASSWORD_SUCCESSFUL)) {
            String phoneNumber = intent.getExtras().getString(User.MOBILE_PHONE_NUMBER);
            Log.d(TAG, "initViews: phoneNumber = " + phoneNumber);
            if (phoneNumber != null) {
                mAccountEditor.setText(phoneNumber);
            }
        }
    }

    @Override
    protected void initData() {
        Utils.popSoftInput(mContext, mAccountEditor);
    }

    private void initToolbar() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.toolbar_right_button, null);
        Button findPasswordButton = (Button) view.findViewById(R.id.toolbar_right_button);
        findPasswordButton.setText(R.string.login_forget_password);
        findPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFindPasswordActivity();
            }
        });

        int height = (int) getResources().getDimension(R.dimen.toolbar_confirm_height);
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
        params.gravity = Gravity.END;
        params.rightMargin = Utils.dp2px(10);
        mToolbar.addView(view, params);
    }

    private void startFindPasswordActivity() {
        startActivity(new Intent(this, FindPasswordActivity.class));
    }

    private void handleLoginClick() {
        //第一步，验证验证码，用户名，头像是否选择
        final String account = mAccountEditor.getText().toString();
        final String password = mPasswordEditor.getText().toString();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.showToast(mContext, getString(R.string.login_please_input_account));
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < LENGTH_PASSWORD) {
            ToastUtils.showToast(mContext, getString(R.string.register_password_hint));
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(User.USERNAME, account);
        map.put(User.PASSWORD, password);
        String urlBody = UserUtils.convertToUrlParams(map);
        Log.d(TAG, "handleLoginClick: urlBody = " + urlBody);

        mDialog.show();
        RequestParam param = new RequestParam(null, urlBody);
        _NET(API.LOGIN, param, new InnerRequestCallback() {
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                Log.d(TAG, "onSuccess: content = " + content);

                loginSuccess(content);
            }

            @Override
            public void onFail(String errorMessage) {
                Log.d(TAG, "onFail: errorMessage = " + errorMessage);
                loginFailed(errorMessage);
            }
        });
    }

    private void loginSuccess(String content) {
        mDialog.dismiss();

        ToastUtils.showToast(mContext, getString(R.string.login_successful));

        User user = JSON.parseObject(content, User.class);
        storeUserInfo(user);

        Intent intent = new Intent(mContext, HomeActivity.class);
        startActivity(intent);
        setResult(RESULT_OK);
        finish();
    }

    private void loginFailed(String errorMessage) {
        mDialog.dismiss();

        ToastUtils.showToast(mContext, getString(R.string.login_failed));
    }

    private void storeUserInfo(User user) {
        User.saveUserLocal(mContext, user);
    }
}
