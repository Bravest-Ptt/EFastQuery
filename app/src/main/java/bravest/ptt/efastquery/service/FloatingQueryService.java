package bravest.ptt.efastquery.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.activity.HomeActivity;
import bravest.ptt.efastquery.ui.ESearchOnFloatButton;

/**
 * Created by root on 12/28/16.
 */

public class FloatingQueryService extends Service {

    private static final int FOREGROUND_SERVICE_NOTIFICATION_ID = 528;
    public static final String TAG = "FloatingQueryService";
    private MainBinder mMainBinder = new MainBinder();
    private ESearchOnFloatButton mView;

    public class MainBinder extends Binder {
        public FloatingQueryService getService() {
            return FloatingQueryService.this;
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, HomeActivity.class), 0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("Foreground Service Start");
        builder.setContentTitle("Foreground Service");
        builder.setContentText("Make this service run in the foreground.");
        builder.setPriority(1000);
        builder.setAutoCancel(false);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(FOREGROUND_SERVICE_NOTIFICATION_ID, notification);
        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    public boolean showFloatingWindow() {
        if (mView == null) {
            try {
                mView = new ESearchOnFloatButton(this);
            } catch (ESearchOnFloatButton.InflaterNotReadyException e) {
                e.printStackTrace();
            }
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(this)) {
                mView.showFloatButton();
            } else {
                return false;
            }
        } else {
            mView.showFloatButton();
        }
        return true;
    }

    public void hideFloatingWindow() {
        if (mView == null) {
            return;
        }
        mView.forceCloseFloatButton();
        mView = null;
    }

    public boolean isFloatingWindowShowing() {
        if (mView == null) {
            return false;
        }
        return mView.isFloatingButtonShowing();
    }


    @Override
    public void onDestroy() {
        stopForeground(true);
        if (mView != null) {
            mView.forceCloseFloatButton();
        }
        super.onDestroy();
    }
}
