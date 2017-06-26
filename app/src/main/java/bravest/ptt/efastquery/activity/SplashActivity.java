package bravest.ptt.efastquery.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.alibaba.fastjson.JSON;

import bravest.ptt.androidlib.net.RequestParam;
import bravest.ptt.androidlib.utils.NetworkUtils;
import bravest.ptt.androidlib.utils.ToastUtils;
import bravest.ptt.efastquery.R;
import bravest.ptt.androidlib.utils.plog.PLog;
import bravest.ptt.efastquery.activity.base.BaseActivity;
import bravest.ptt.efastquery.activity.base.BaseToolBarActivity;
import bravest.ptt.efastquery.entity.User;
import bravest.ptt.efastquery.listeners.OnBackgroundToggleListener;
import bravest.ptt.efastquery.utils.API;
import bravest.ptt.efastquery.utils.Utils;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";

    private static final int FINISH = 1;

    private ViewSwitcher mViewSwitcher;

    /**
     *  oncreate and onresume is true
     *  onpause and ondestory is false
     */
    private boolean mActivityActive = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FINISH) {
                if (mSet != null) {
                    mSet.cancel();
                    mSet=null;
                }
            }
        }
    };

    private Animation mSlideInLeft;
    private Animation mSlideOutRight;

    @Override
    protected void initVariables() {
        mActivityActive = true;
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mSlideInLeft = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        mSlideOutRight = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_out_right);
    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);
        View login = findViewById(R.id.login);
        login.setOnTouchListener(new OnBackgroundToggleListener(
                R.color.button_white_normal,
                R.color.button_white_pressed
        ) {
            @Override
            protected void handleClick() {
                handleLogin();
            }
        });

        View register = findViewById(R.id.register);
        register.setOnTouchListener(new OnBackgroundToggleListener(
                R.color.button_color_primary_normal,
                R.color.button_color_primary_pressed
        ) {
            @Override
            protected void handleClick() {
                handleRegister();
            }
        });

        mViewSwitcher = (ViewSwitcher) findViewById(R.id.splash_switcher);
        mViewSwitcher.setInAnimation(mSlideInLeft);
        mViewSwitcher.setOutAnimation(mSlideOutRight);
    }

    @Override
    protected void initData() {
        User user = User.getInstance(mContext);
        if (user != null) {
            if (NetworkUtils.isConnectedByState(mContext)) {
                verifyIsExpired(user);
            } else {
                ToastUtils.showToast(mContext, R.string.network_unreachable);
            }
        } else {
            switchViewToNext();
        }
    }


    private void verifyIsExpired(User user) {
        final String objectId = user.getObjectId();
        final String token = user.getSessionToken();
        if (TextUtils.isEmpty(objectId) || TextUtils.isEmpty(token)) {
            ToastUtils.showToast(mContext, "Please Login again");
            return;
        }

        User tmp = new User();
        tmp.setCounter(1);
        String jsonBody = JSON.toJSONString(tmp);
        RequestParam param = new RequestParam(objectId, jsonBody);

        _NET(API.UPDATE_USER_INFO, param, new InnerRequestCallback() {
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                Log.d(TAG, "onSuccess: content = " + content);
                goHome();
            }

            @Override
            public void onFail(String errorMessage) {
                super.onFail(errorMessage);
                Log.d(TAG, "onFail:  = " + errorMessage);
                switchViewToNext();
            }
        });
    }

    private void goHome() {
        Intent home = new Intent(mContext, HomeActivity.class);
        startActivity(home);
        finish();
    }

    private void switchViewToNext() {
        if (mViewSwitcher != null) {
            mViewSwitcher.showNext();
        }
    }

    private void handleLogin() {
        PLog.d(TAG, "handle login");
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void handleRegister() {
        PLog.d(TAG, "handle register");
        startActivity(new Intent(this,RegisterActivity.class));
    }

    /*
    private void initSealAnimation() {
        final ImageView imageView = (ImageView) findViewById(R.id.splash_seal_start);

        final Drawable drawable = imageView.getDrawable();
        PLog.log("out");
        PLog.log(drawable);

        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
            PLog.log("click");
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (drawable instanceof AnimatedVectorDrawable) {
                ((AnimatedVectorDrawable) drawable).registerAnimationCallback(new Animatable2.AnimationCallback() {
                    @Override
                    public void onAnimationStart(Drawable drawable) {
                        super.onAnimationStart(drawable);
                    }

                    @Override
                    public void onAnimationEnd(Drawable drawable) {
                        super.onAnimationEnd(drawable);
                        doAnimation(imageView);
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                            ((AnimatedVectorDrawable) drawable).unregisterAnimationCallback(this);
                        }
                    }
                });
            }
        } else {
        }
    }
    */

    @Override
    protected void onResume() {
        super.onResume();
        mActivityActive = true;
    }

    private boolean hasLogin = false;

    private AnimatorSet mSet;
    private void doAnimation(final ImageView imageView) {
        Animator anim = AnimatorInflater.loadAnimator(mContext, R.animator.view_seal_anim);
        anim.setInterpolator(new LinearInterpolator());
        anim.setTarget(imageView);

        //ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, "translationY", imageView.getY(), imageView.getY() + 100);
        //objectAnimator.setDuration(1000);
        mSet = new AnimatorSet();
        //mSet.playTogether(anim, objectAnimator);
        mSet.play(anim);
        mSet.start();

       // ImageView nameView = (ImageView) findViewById(R.id.splash_app_name);
       // nameView.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        mActivityActive = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mActivityActive = false;
        super.onDestroy();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(bravest.ptt.androidlib.R.anim.activity_scale_start,
                bravest.ptt.androidlib.R.anim.activity_scale_leave);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(bravest.ptt.androidlib.R.anim.activity_scale_start,
                bravest.ptt.androidlib.R.anim.activity_scale_leave);
    }
}
