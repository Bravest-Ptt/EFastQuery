package bravest.ptt.efastquery;

import android.app.Application;
import android.content.Context;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by root on 3/2/17.
 */

public class App extends Application {
    private static final String MSC_APPID = "587a0f52";

    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=" + MSC_APPID);
    }
}
