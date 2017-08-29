package bravest.ptt.efastquery;

import android.app.Application;
import android.content.Context;
import android.view.WindowManager;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.zxy.tiny.Tiny;

/**
 * Created by root on 3/2/17.
 */

public class App extends Application {

    private static final String MSC_APPID = "587a0f52";
    public static App sApp;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        initMsc();
        initBmob();
        initTiny();
    }

    public static App getInstance() {
        return sApp;
    }

    private void initMsc() {
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=" + MSC_APPID);
    }

    private void initBmob() {

    }

    /**
     *  For Image compression
     */
    private void initTiny() {
        Tiny.getInstance().init(this);
    }
}
