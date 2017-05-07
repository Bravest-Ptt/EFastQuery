package bravest.ptt.efastquery;

import android.app.Application;
import android.content.Context;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import cn.bmob.v3.Bmob;

/**
 * Created by root on 3/2/17.
 */

public class App extends Application {
    private static final String MSC_APPID = "587a0f52";

    private static final String BMOB_APPID = "ce21dd4747a54ca096dd4d93f5ef4fac";

    @Override
    public void onCreate() {
        super.onCreate();
        initMsc();
        initBmob();
    }

    private void initMsc() {
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=" + MSC_APPID);
    }

    private void initBmob() {
        //第一：默认初始化
        Bmob.initialize(this, BMOB_APPID);

        //第二：自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
        //BmobConfig config =new BmobConfig.Builder(this)
        ////设置appkey
        //.setApplicationId("Your Application ID")
        ////请求超时时间（单位为秒）：默认15s
        //.setConnectTimeout(30)
        ////文件分片上传时每片的大小（单位字节），默认512*1024
        //.setUploadBlockSize(1024*1024)
        ////文件的过期时间(单位为秒)：默认1800s
        //.setFileExpiration(2500)
        //.build();
        //Bmob.initialize(config);
    }
}
