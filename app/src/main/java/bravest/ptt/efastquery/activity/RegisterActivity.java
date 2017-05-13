package bravest.ptt.efastquery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import bravest.ptt.androidlib.activity.BaseActivity;
import bravest.ptt.androidlib.net.RemoteService;
import bravest.ptt.androidlib.net.RequestParam;
import bravest.ptt.androidlib.utils.RegularUtils;
import bravest.ptt.androidlib.utils.ToastUtils;
import bravest.ptt.androidlib.utils.plog.PLog;
import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.entity.User;
import bravest.ptt.efastquery.net.AbstractRequestCallback;
import bravest.ptt.efastquery.utils.API;
import bravest.ptt.efastquery.utils.UserUtil;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity {

    private static final String TAG = "RegisterActivity";

    private static final int WHAT_COUNT_DOWN = 1;

    private static final int WHAT_COUNT_OVER = 2;

    /**
     * The delay for request sms code
     */
    private static final int DEFAULT_SENDER_REBOOT = 60;

    private int mSmsSendCounter = 0;

    private int mSmsCountDownSecond = DEFAULT_SENDER_REBOOT;

    private EditText mPhoneNumberEditor;

    private EditText mVerificationEditor;

    private Button mVerificationSender;

    private EditText mPasswordEditor;

    private View mRegister;

    private TextView mRegisterAlready;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_COUNT_DOWN:
                    countDownSmsSender();
                    break;
                case WHAT_COUNT_OVER:
                    countOverSmsSender();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void initVariables() {
    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);

        mPhoneNumberEditor = (EditText) findViewById(R.id.phoneNumber);
        mVerificationEditor = (EditText) findViewById(R.id.verification_code);
        mVerificationSender = (Button) findViewById(R.id.verification_code_send);
        mPasswordEditor = (EditText) findViewById(R.id.passWord);
        mRegister = findViewById(R.id.register);
        mRegisterAlready = (TextView) findViewById(R.id.register_already);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegisterClick();
            }
        });

        mRegisterAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegisterAlreadyClick();
            }
        });

        mVerificationSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRequestSmscode();
            }
        });
    }

    @Override
    protected void initData() {
    }

    private void handleRegisterClick() {
        final User user = new User();
        String phoneNumber = mPhoneNumberEditor.getText().toString();
        String password = mPasswordEditor.getText().toString();
        PLog.d(TAG, "onClick: phonenumber = " + phoneNumber);
        PLog.d(TAG, "onClick: password = " + password);
        user.setMobilePhoneNumber(phoneNumber);
        user.setUsername(UserUtil.generateUserName(UserUtil.USER_NAME_LENGTH));
        user.setPassword(password);

        String jsonString = JSON.toJSONString(user);
        jsonString = UserUtil.appendPassword(jsonString, User.PASSWORD, password);
        PLog.log(jsonString);
        ToastUtils.showToast(this, jsonString);

        RemoteService.getInstance().invoke(this, API.REGISTER, new RequestParam(jsonString), new AbstractRequestCallback(mContext) {
            @Override
            public void onSuccess(String content) {
                ToastUtils.showToast(mContext, content);
                PLog.log(content);
                User user1 = JSON.parseObject(content, User.class);
                PLog.log(user1.toString());
                RequestParam param = new RequestParam(user1.getObjectId(), null);
                RemoteService.getInstance().invoke(mActivity, API.GET_USER_INFO,
                        param,
                        new AbstractRequestCallback(mContext) {
                            @Override
                            public void onSuccess(String content) {
                                ToastUtils.showToast(mContext, content);
                                PLog.log("user2" + content);
                                User user2 = JSON.parseObject(content, User.class);
                                PLog.log(user2.toString());
                            }
                        }
                );
            }
        });
    }

    private void handleRegisterAlreadyClick() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void handleRequestSmscode() {
        try {
            String number = mPhoneNumberEditor.getText().toString();
            if (RegularUtils.isMobile(number)) {
                RequestParam param = new RequestParam();
                JSONObject object = new JSONObject();
                object.put(User.MOBILE_PHONE_NUMBER, number);
                param.setBody(JSON.toJSONString(object));
//                RemoteService.getInstance().invoke(mActivity, API.REQUEST_SMS_CODE,
//                        param,
//                        new AbstractRequestCallback(mContext) {
//                            @Override
//                            public void onSuccess(String content) {
//                                super.onSuccess(content);
//
//                            }
//                        });
                toggleSmsSender();
            } else {
                ToastUtils.showToast(mContext, "Mobile phone error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void toggleSmsSender() {
        mSmsSendCounter++;
        mSmsCountDownSecond = mSmsSendCounter * DEFAULT_SENDER_REBOOT;
        mVerificationSender.setEnabled(false);
        mVerificationSender.setClickable(false);
        mHandler.sendEmptyMessage(WHAT_COUNT_DOWN);
        mHandler.sendEmptyMessageDelayed(WHAT_COUNT_OVER, mSmsCountDownSecond * 1000);
    }

    private void countDownSmsSender() {
        if (mSmsCountDownSecond != 0 && !mVerificationSender.isEnabled()) {
            mVerificationSender.setText("" + mSmsCountDownSecond-- + "S");
            mHandler.sendEmptyMessageDelayed(WHAT_COUNT_DOWN, 1000);
        }
    }

    private void countOverSmsSender() {
        mVerificationSender.setEnabled(true);
        mVerificationSender.setClickable(true);
        mVerificationSender.setText(getText(R.string.register_verification_code));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(WHAT_COUNT_DOWN);
        mHandler.removeMessages(WHAT_COUNT_OVER);
        mHandler = null;
    }
}
