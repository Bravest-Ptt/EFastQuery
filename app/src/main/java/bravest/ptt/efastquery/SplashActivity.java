package bravest.ptt.efastquery;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import bravest.ptt.efastquery.utils.PLog;

public class SplashActivity extends AppCompatActivity {

    private static final int FINISH = 1;
    private Context mContext;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;

        initSealAnimation();
        initData();
    }

    private void initData() {

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
            //delay 3s, and do the animation
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    doAnimation(imageView);
                }
            }, 2500);
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startMainActivity();
            }
        }, 4000);
    }

    private void startMainActivity() {
        mHandler.sendEmptyMessage(FINISH);
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private AnimatorSet mSet;
    private void doAnimation(final ImageView imageView) {
        Animator anim = AnimatorInflater.loadAnimator(mContext, R.animator.view_seal_anim);
        anim.setInterpolator(new LinearInterpolator());
        anim.setTarget(imageView);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, "translationY", imageView.getY(), imageView.getY() + 100);
        objectAnimator.setDuration(1000);
        mSet = new AnimatorSet();
        mSet.playTogether(anim, objectAnimator);
        mSet.start();

        ImageView nameView = (ImageView) findViewById(R.id.splash_app_name);
        nameView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
