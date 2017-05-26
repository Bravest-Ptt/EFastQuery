package bravest.ptt.efastquery;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.zxy.tiny.Tiny;

/**
 * Created by root on 3/2/17.
 */

public class App extends Application {

    private static final String MSC_APPID = "587a0f52";

    @Override
    public void onCreate() {
        super.onCreate();
        initMsc();
        initBmob();
        initTiny();
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
