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
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;

import bravest.ptt.androidlib.net.RequestParam;
import bravest.ptt.androidlib.utils.ToastUtils;
import bravest.ptt.efastquery.R;
import bravest.ptt.androidlib.utils.plog.PLog;
import bravest.ptt.efastquery.activity.base.BaseActivity;
import bravest.ptt.efastquery.entity.User;
import bravest.ptt.efastquery.utils.API;
import bravest.ptt.efastquery.utils.Utils;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";

    private static final int FINISH = 1;

    private Context mContext;

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

    @Override
    protected void initVariables() {
        super.initVariables();
        mContext = this;
        mActivityActive = true;

        User user = User.getInstance(mContext);
        if (user != null) {
            verifyIsExpired(user);
        }
    }

    private void verifyIsExpired(User user) {
        final ProgressDialog dialog = Utils.newFullScreenProgressDialog(mContext);
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

        dialog.show();
        _NET(API.UPDATE_USER_INFO, param, new InnerRequestCallback() {
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                Log.d(TAG, "onSuccess: content = " + content);
                dialog.dismiss();
                goHome();
            }

            @Override
            public void onFail(String errorMessage) {
                super.onFail(errorMessage);
                Log.d(TAG, "onFail:  = " + errorMessage);
                dialog.dismiss();
            }
        });
    }

    private void goHome() {
        Intent home = new Intent(mContext, HomeActivity.class);
        startActivity(home);
        finish();
    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);
        View login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        View register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
            }
        });

        initSealAnimation();
    }

    @Override
    protected void initData() {

    }

    private void handleLogin() {
        PLog.d(TAG, "handle login");
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void handleRegister() {
        PLog.d(TAG, "handle register");
        startActivity(new Intent(this,RegisterActivity.class));
    }

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

    @Override
    protected void onResume() {
        super.onResume();
        mActivityActive = true;
    }

    private boolean hasLogin = false;

    private void startMainActivity() {
        if (hasLogin) {
            mHandler.sendEmptyMessage(FINISH);
            Intent intent = new Intent(mContext, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            ImageView nameView = (ImageView) findViewById(R.id.splash_app_name);
            nameView.setVisibility(View.GONE);
        }
    }

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

        ImageView nameView = (ImageView) findViewById(R.id.splash_app_name);
        nameView.setVisibility(View.GONE);
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
}
