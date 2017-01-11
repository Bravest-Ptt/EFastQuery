package bravest.ptt.efastquery;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import bravest.ptt.efastquery.view.ESearchFloatButton;

/**
 * Created by root on 12/28/16.
 */

public class MainService extends Service {

    public static final String TAG = "MainService";
    private MainBinder mMainBinder = new MainBinder();
    private ESearchFloatButton mView;

    public class MainBinder extends Binder {
        public MainService getService() {
            return MainService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMainBinder;
    }

    @Override
    public void onCreate() {
        this.setTheme(R.style.AppTheme);
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        try {
            mView = new ESearchFloatButton(this);
        } catch (ESearchFloatButton.InflaterNotReadyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void showFloatingWindow() {
        if (mView == null) return;
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(this)) {
                mView.showFloatButton();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else {
            mView.showFloatButton();
        }
    }

    public void hideFloatingWindow() {
        if (mView == null) {
            return;
        }
        mView.hideFloatButton();
    }
}
