package bravest.ptt.efastquery.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ViewSwitcher;

import com.alibaba.fastjson.JSON;

import bravest.ptt.androidlib.net.RequestParam;
import bravest.ptt.androidlib.utils.RegularUtils;
import bravest.ptt.androidlib.utils.ToastUtils;
import bravest.ptt.androidlib.utils.plog.PLog;
import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.activity.base.BaseToolBarActivity;
import bravest.ptt.efastquery.entity.SmsCodeEntity;
import bravest.ptt.efastquery.entity.User;
import bravest.ptt.efastquery.utils.API;
import bravest.ptt.efastquery.utils.UserUtils;
import bravest.ptt.efastquery.utils.Utils;

import static bravest.ptt.efastquery.activity.RegisterActivity.LENGTH_PASSWORD;

public class FindPasswordActivity extends BaseToolBarActivity {

    private static final String TAG = "FindPasswordActivity";

    private ProgressDialog mWaitingDialog;

    private ViewSwitcher mViewSwitcher;
    View mRequestSmsCode;
    View mResetPassword;
    private EditText mPhoneNumberEditor;
    private EditText mSmsCodeEditor;
    private EditText mPasswordEditor;

    private Animation mSlideInLeft;
    private Animation mSlideOutRight;

    private String mPhoneNumber;

    @Override
    protected void initVariables() {
        mSlideInLeft = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        mSlideOutRight = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_out_right);
        mWaitingDialog = Utils.newFullScreenProgressDialog(this);
    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_find_password);
        mViewSwitcher = (ViewSwitcher) findViewById(R.id.find_password_switcher);
        mViewSwitcher.setInAnimation(mSlideInLeft);
        mViewSwitcher.setOutAnimation(mSlideOutRight);

        mRequestSmsCode = findViewById(R.id.request_sms_code);
        mRequestSmsCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSmsCode();
            }
        });

        mResetPassword = findViewById(R.id.reset_password);
        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        mPhoneNumberEditor = (EditText) findViewById(R.id.phoneNumber);
        mSmsCodeEditor = (EditText) findViewById(R.id.verification_editor);
        mPasswordEditor = (EditText) findViewById(R.id.passWord);
    }

    @Override
    protected void initData() {
    }

    private void requestSmsCode() {
        final String phoneNumber = mPhoneNumberEditor.getText().toString();
        mPhoneNumber = phoneNumber;

        //1、判断手机号格式,判断密码,用户名是否满足要求
        if (!RegularUtils.isMobile(phoneNumber)) {
            ToastUtils.showToast(mContext,
                    getString(R.string.register_phone_number_invalid));
            return;
        }

        requestSendSmsCode();
    }

    private void resetPassword() {
        final String password = mPasswordEditor.getText().toString();
        final String smsCode = mSmsCodeEditor.getText().toString();

        if (TextUtils.isEmpty(password) ||
                TextUtils.getTrimmedLength(password) < LENGTH_PASSWORD) {
            ToastUtils.showToast(mContext, getString(R.string.register_password_hint));
            return;
        }

        if (TextUtils.isEmpty(smsCode)) {
            ToastUtils.showToast(mContext, R.string.verify_please_input_code);
            return;
        }

        resetPasswordBySmsCode(password, smsCode);
    }

    private void requestSendSmsCode() {
        showWaitProgressBar();
        final String number = mPhoneNumberEditor.getText().toString();
        final SmsCodeEntity entity = new SmsCodeEntity();
        entity.setMobilePhoneNumber(number);
        RequestParam param = new RequestParam(JSON.toJSONString(entity));

        PLog.log(param);

        _NET(API.REQUEST_SMS_CODE, param, new InnerRequestCallback() {
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                querySmsCodeState(content, entity);
            }

            @Override
            public void onFail(String errorMessage) {
                super.onFail(errorMessage);
                hideWaitProgressBar();
            }
        });
    }

    private void querySmsCodeState(final String content,
                                   final SmsCodeEntity entity) {
        SmsCodeEntity requestResponse = JSON.parseObject(content,
                SmsCodeEntity.class);
        entity.setSmsId(requestResponse.getSmsId());
        PLog.log(entity);

        RequestParam param = new RequestParam(entity.getSmsId(), null);
        _NET(API.QUERY_SMS_STATE, param, new InnerRequestCallback() {
            @Override
            public void onSuccess(String content) {
                hideWaitProgressBar();
                super.onSuccess(content);
                SmsCodeEntity queryResponse = JSON.parseObject(content,
                        SmsCodeEntity.class);
                PLog.log(queryResponse);
                if (queryResponse != null) {
                    entity.setSms_state(queryResponse.getSms_state());
                    entity.setVerify_state(queryResponse.getVerify_state());
                    PLog.log(entity);
                    //如果查询状态为发送中和发送成功，则跳转到验证界面，
                    //否则， 提示验证码发送失败。
                    if (toastSmsState(queryResponse.getSms_state())) {
                        PLog.log("toast sms state true");
                        switchViewToResetPager();
                    } else {
                        hideWaitProgressBar();
                    }
                }
            }

            @Override
            public void onFail(String errorMessage) {
                super.onFail(errorMessage);
                hideWaitProgressBar();
            }
        });
    }

    private void resetPasswordBySmsCode(final String password, final String smsCode) {
        showWaitProgressBar();
        String userJson = UserUtils.formatValuesToJson(User.PASSWORD, password);
        Log.d(TAG, "resetPasswordBySmsCode: after = " + userJson);

        RequestParam param = new RequestParam(smsCode, userJson);
        _NET(API.RESET_PASSWORD_BY_SMS, param, new InnerRequestCallback() {
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                hideWaitProgressBar();
                if (content.contains("ok")) {
                    ToastUtils.showToast(mContext, R.string.reset_password_successful);
                    gotoLoginActivity();
                }
            }

            @Override
            public void onFail(String errorMessage) {
                super.onFail(errorMessage);
                hideWaitProgressBar();
            }
        });
    }

    private void gotoLoginActivity() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.setAction(LoginActivity.ACTION_FIND_PASSWORD_SUCCESSFUL);
        intent.putExtra(User.MOBILE_PHONE_NUMBER, mPhoneNumber);
        startActivity(intent);
        finish();
    }

    private void switchViewToResetPager() {
        mViewSwitcher.showNext();
        hideWaitProgressBar();
    }

    private boolean toastSmsState(String state) {
        String msg = "";
        boolean result = true;
        switch (state) {
            case SmsCodeEntity.SMS_STATE_SENDING:
                msg = getString(R.string.register_sms_sending);
                break;
            case SmsCodeEntity.SMS_STATE_SUCCESS:
                msg = getString(R.string.register_sms_success);
                break;
            case SmsCodeEntity.SMS_STATE_FAIL:
                msg = getString(R.string.register_sms_fail);
                result = false;
                break;
            default:
                result = false;
                break;
        }
        ToastUtils.showToast(mContext, msg);
        return result;
    }

    private void showWaitProgressBar() {
        mWaitingDialog.show();
    }

    private void hideWaitProgressBar() {
        mWaitingDialog.dismiss();
    }
}
