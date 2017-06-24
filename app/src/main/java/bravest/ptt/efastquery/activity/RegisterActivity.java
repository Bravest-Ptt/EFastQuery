package bravest.ptt.efastquery.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import bravest.ptt.androidlib.net.RemoteService;
import bravest.ptt.androidlib.net.RequestParam;
import bravest.ptt.androidlib.utils.RegularUtils;
import bravest.ptt.androidlib.utils.ToastUtils;
import bravest.ptt.androidlib.utils.plog.PLog;
import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.activity.base.BaseToolBarActivity;
import bravest.ptt.efastquery.entity.SmsCodeEntity;
import bravest.ptt.efastquery.entity.User;
import bravest.ptt.efastquery.net.AbstractRequestCallback;
import bravest.ptt.efastquery.utils.API;
import bravest.ptt.efastquery.utils.UserUtils;
import bravest.ptt.efastquery.utils.Utils;

public class RegisterActivity extends BaseToolBarActivity {

    private static final String TAG = "RegisterActivity";

    private static final int WHAT_COUNT_DOWN = 1;

    private static final int WHAT_COUNT_OVER = 2;

    public static final int LENGTH_PASSWORD = 6;

    /**
     * The delay for request sms code
     */
    private static final int DEFAULT_SENDER_REBOOT = 60;

    private int mSmsSendCounter = 0;

    private int mSmsCountDownSecond = DEFAULT_SENDER_REBOOT;

    private EditText mPhoneNumberEditor;

    private EditText mPasswordEditor;

    private EditText mUserNameEditor;

   // private View mRegister;

    private TextView mRegisterAlready;

    private View mWaitingView;

    private ProgressDialog mWaitingDialog;
//
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case WHAT_COUNT_DOWN:
//                    countDownSmsSender();
//                    break;
//                case WHAT_COUNT_OVER:
//                    countOverSmsSender();
//                    break;
//                default:
//                    break;
//            }
//        }
//    };

    @Override
    protected void initVariables() {
        mWaitingDialog = Utils.newFullScreenProgressDialog(this);
    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);

        initToolbar();

        mPhoneNumberEditor = (EditText) findViewById(R.id.phoneNumber);
        mUserNameEditor = (EditText) findViewById(R.id.username_editor);
        mPasswordEditor = (EditText) findViewById(R.id.passWord);
        //mRegister = findViewById(R.id.register);
        mRegisterAlready = (TextView) findViewById(R.id.register_already);
        mWaitingView = findViewById(R.id.register_progress);

//        mRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                handleRequestSmsCode();
//                //startVerifyActivity(null);
//            }
//        });

        mRegisterAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegisterAlreadyClick();
            }
        });

        //mVerificationEditor = (EditText) findViewById(R.id.verification_code);
        //mVerificationSender = (Button) findViewById(R.id.verification_code_send);
    }

    private void initToolbar() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.toolbar_right_button, null);
        Button registerButton = (Button) view.findViewById(R.id.toolbar_right_button);
        registerButton.setText(R.string.register_get_sms_code);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRequestSmsCode();
                //startVerifyActivity(null);
            }
        });

        int height = (int) getResources().getDimension(R.dimen.toolbar_confirm_height);
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
        params.gravity = Gravity.END;
        params.rightMargin = Utils.dp2px(10);
        mToolbar.addView(view, params);
    }

    @Override
    protected void initData() {
    }

    /**
     * 【尚未使用】
     * 处理注册点击事件
     */
    private void handleRegisterClick() {
        final User user = new User();
        String phoneNumber = mPhoneNumberEditor.getText().toString();
        String password = mPasswordEditor.getText().toString();
        PLog.d(TAG, "onClick: phonenumber = " + phoneNumber);
        PLog.d(TAG, "onClick: password = " + password);
        user.setMobilePhoneNumber(phoneNumber);
        user.setUsername(UserUtils.generateUserName(UserUtils.USER_NAME_LENGTH));
        user.setPassword(password);

        String jsonString = JSON.toJSONString(user);
        jsonString = UserUtils.appendPassword(jsonString, User.PASSWORD, password);
        PLog.log(jsonString);
        ToastUtils.showToast(this, jsonString);

        RemoteService.getInstance().invoke(
                this,
                API.REGISTER,
                new RequestParam(null, jsonString),
                new AbstractRequestCallback(mContext) {
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

    /**
     * 请求验证码验证主逻辑
     */
    private void handleRequestSmsCode() {
        final String number = mPhoneNumberEditor.getText().toString();
        final String password = mPasswordEditor.getText().toString();
        final String name = mUserNameEditor.getText().toString();


        //1、判断手机号格式,判断密码,用户名是否满足要求
        if (!RegularUtils.isMobile(number)) {
            ToastUtils.showToast(mContext,
                    getString(R.string.register_phone_number_invalid));
            return;
        }

        if (TextUtils.isEmpty(password) ||
                TextUtils.getTrimmedLength(password) < LENGTH_PASSWORD) {
            ToastUtils.showToast(mContext, getString(R.string.register_password_hint));
            return;
        }

        if (TextUtils.isEmpty(name)) {
            ToastUtils.showToast(mContext, getString(R.string.verify_please_input_name));
            return;
        }

        showWaitProgressBar();
        //2、判断手机号是否被使用
        askMobilePhoneNumberUsed();
    }

    private void registerFailedUserHasRegistered() {
        ToastUtils.showToast(mContext,
                getString(R.string.verify_user_name_used));
        hideWaitProgressBar();
    }

    /**
     * 2、判断手机号是否被使用
     */
    private void askMobilePhoneNumberUsed() {
        final String number = mPhoneNumberEditor.getText().toString();
        final User user = new User();
        user.setMobilePhoneNumber(number);
        _NET(API.IS_MOBILE_USED,
                new RequestParam(null, JSON.toJSONString(user)),
                new InnerRequestCallback() {
                    @Override
                    public void onSuccess(String content) {
                        super.onSuccess(content);
                        PLog.log(content);
                        if (!TextUtils.isEmpty(content) &&
                                content.contains(User.MOBILE_PHONE_NUMBER)) {
                            ToastUtils.showToast(mContext,
                                    getString(R.string.register_phone_registed));
                            hideWaitProgressBar();
                        } else {
                            //3、判断用户名是否使用
                            askUserNameUsed();
                        }
                    }

                    @Override
                    public void onFail(String errorMessage) {
                        super.onFail(errorMessage);
                        hideWaitProgressBar();
                    }
                });
    }

    //3、判断用户名是否使用
    private void askUserNameUsed() {
        final String name = mUserNameEditor.getText().toString();
        User user = new User();
        user.setUsername(name);
        _NET(API.IS_USER_NAME_USED,
                new RequestParam(null, JSON.toJSONString(user)),
                new InnerRequestCallback() {
                    @Override
                    public void onSuccess(String content) {
                        if (!TextUtils.isEmpty(content) &&
                                content.contains(User.USERNAME)) {
                            registerFailedUserHasRegistered();
                        } else {
                            //3、判断手机号是否被使用
                            requestSendSmsCode();
                        }
                    }

                    @Override
                    public void onFail(String errorMessage) {
                        super.onFail(errorMessage);
                        hideWaitProgressBar();
                    }
                }
        );
    }

    /**
     * 4、请求发送验证码
     */
    private void requestSendSmsCode() {
        final String number = mPhoneNumberEditor.getText().toString();
        final SmsCodeEntity entity = new SmsCodeEntity();
        entity.setMobilePhoneNumber(number);
        RequestParam param = new RequestParam(JSON.toJSONString(entity));

        PLog.log(param);

        _NET(API.REQUEST_SMS_CODE, param,
                new InnerRequestCallback() {
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
                }
        );
    }

    /**
     * 5、查询验证码状态
     *
     * @param content
     * @param entity
     */
    private void querySmsCodeState(final String content,
                                   final SmsCodeEntity entity) {
        SmsCodeEntity requestResponse = JSON.parseObject(content,
                SmsCodeEntity.class);
        entity.setSmsId(requestResponse.getSmsId());
        PLog.log(entity);

        _NET(API.QUERY_SMS_STATE,
                //":" is for api
                //url ：https://api.bmob.cn/1/querySms/:smsId （注意smsId前有冒号(:)）
                // new RequestParam(":"+entity.getSmsId(), null),
                new RequestParam(entity.getSmsId(), null),
                new InnerRequestCallback() {
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
                                startVerifyActivity(entity);
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
                }
        );
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

    private void startVerifyActivity(final SmsCodeEntity entity) {
        hideWaitProgressBar();
        Intent intent = new Intent(mContext, RegisterVerifyActivity.class);
        intent.putExtra(User.USERNAME, mUserNameEditor.getText().toString());
        intent.putExtra(User.PASSWORD, mPasswordEditor.getText().toString());
        intent.putExtra(SmsCodeEntity.getName(), entity);
        startActivity(intent);
    }

    private void showWaitProgressBar() {
        mWaitingDialog.show();
    }

    private void hideWaitProgressBar() {
        mWaitingDialog.dismiss();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mHandler.removeMessages(WHAT_COUNT_DOWN);
//        mHandler.removeMessages(WHAT_COUNT_OVER);
//        mHandler = null;
//    }
//
//    /**
//     * 【尚未使用】
//     * 触发验证码按钮的倒计时
//     */
//    private void toggleSmsSender() {
//        mSmsSendCounter++;
//        mSmsCountDownSecond = mSmsSendCounter * DEFAULT_SENDER_REBOOT;
//        mVerificationSender.setEnabled(false);
//        mVerificationSender.setClickable(false);
//        mHandler.sendEmptyMessage(WHAT_COUNT_DOWN);
//        mHandler.sendEmptyMessageDelayed(WHAT_COUNT_OVER, mSmsCountDownSecond * 1000);
//    }
//
//    /**
//     * 【尚未使用】
//     * 验证码按钮倒计时读秒
//     */
//    private void countDownSmsSender() {
//        if (mSmsCountDownSecond != 0 && !mVerificationSender.isEnabled()) {
//            mVerificationSender.setText("" + mSmsCountDownSecond-- + "S");
//            mHandler.sendEmptyMessageDelayed(WHAT_COUNT_DOWN, 1000);
//        }
//    }
//
//    /**
//     * 【尚未使用】
//     * 验证码按钮倒计时结束
//     */
//    private void countOverSmsSender() {
//        mVerificationSender.setEnabled(true);
//        mVerificationSender.setClickable(true);
//        mVerificationSender.setText(getText(R.string.register_verification_code));
//    }
}
