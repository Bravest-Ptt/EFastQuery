package bravest.ptt.efastquery.engine;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by root on 12/27/16.
 */

class TranslateEngine {

    static{
        System.loadLibrary("EFastQuery");
    }

    private native Key getKey(AssetManager assetManager);

    private static final String TAG = "TranslateEngine";
    private static final String YOUDAO_ID = "phonekeeper";
    private static final String YOUDAO_SECRET = "377725472";

    private static final int CONNECTION_TIME_OUT = 3000;

    private Request mRequest;
    private Context mContext;

    private int mSetCounter = 0;
    private int mStartCounter = 0;

    public TranslateEngine(Request request) {
        this.mRequest = request;
        mSetCounter += 1;
    }

    public TranslateEngine(Context context) {
        mContext = context;
    }

    public TranslateEngine setRequest(Request request) {
        this.mRequest = request;
        mSetCounter += 1;
        return this;
    }

    public void start() throws NotSetRequestException {
        Key key = getKey(mContext.getAssets());
        if (key == null) {
            key = new Key(YOUDAO_ID, YOUDAO_SECRET);
        }
        Log.d(TAG, "start: key id : " + key.keyId + ", key secret : " + key.keySecret);

        final Key finalKey = key;

        mStartCounter += 1;
        if (mSetCounter < mStartCounter) {
            throw new NotSetRequestException();
        }
        Thread askForYouDao = new Thread() {
            @Override
            public void run() {
                BufferedReader resultReader = null;
                try {
                    String urlPath = "http://fanyi.youdao.com/openapi.do?keyfrom="
                            + finalKey.keyId
                            + "&key="
                            + finalKey.keySecret
                            + "&type=data&doctype=json&version=1.1&q="
                            + URLEncoder.encode(mRequest.mRequest, "utf-8");
                    URL getUrl = new URL(urlPath);
                    HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
                    connection.setConnectTimeout(CONNECTION_TIME_OUT);
                    connection.connect();
                    resultReader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "utf-8")
                    );
                    JSONObject resultJson = new JSONObject(resultReader.readLine());
                    String errorCode = resultJson.getString(Result.YouDaoItem.YOUDAO_ERRORCODE);
                    if (TextUtils.equals(errorCode, S.s(Result.RESULT_SUCCESS))) {
                        Message resultMessage = Message.obtain();
                        resultMessage.what = Result.RESULT_SUCCESS;
                        resultMessage.setData(newResult(resultJson));
                        mRequest.mHandler.sendMessage(resultMessage);
                    } else {
                        mRequest.mErrHandler.sendEmptyMessage(S.i(errorCode));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    mRequest.mErrHandler.sendEmptyMessage(Result.LOCALE_UNSUPPORTED_ENCODER);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    mRequest.mErrHandler.sendEmptyMessage(Result.LOCALE_MALFORMED_URL);
                } catch (IOException e) {
                    e.printStackTrace();
                    mRequest.mErrHandler.sendEmptyMessage(Result.LOCALE_IO_EXCEPTION);
                } catch (JSONException e) {
                    e.printStackTrace();
                    mRequest.mErrHandler.sendEmptyMessage(Result.LOCALE_JSON_EXCEPTION);
                } finally {
                    if (resultReader != null) {
                        try {
                            resultReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        askForYouDao.start();
    }

    private Bundle newResult(JSONObject resultJson) {
        Bundle b = new Bundle();
        b.putSerializable("result", new Result(resultJson));
        return b;
    }
    class NotSetRequestException extends Exception {

    }
}
