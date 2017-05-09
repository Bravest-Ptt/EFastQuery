package bravest.ptt.efastquery.engine;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.listeners.OnTranslateListener;

/**
 * Created by root on 12/27/16.
 */

public class TranslateManager {

    private static final String TAG = "TranslateManager";

    private Context mContext;
    private TranslateEngine mEngine;

    private Request mRequest;
    private long mKey;
    private OnTranslateListener mListener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Result.RESULT_SUCCESS:
                    if (mKey == mRequest.getKey()) {
                        mRequest.setHasResponse(true);
                        if (mListener != null) {
                            Log.d(TAG, "handleMessage: " + ((Result) msg.getData().getSerializable("result")).getTranslateString());
                            mListener.onTranslateSuccess(msg.getData().getSerializable("result"));
                        }
                    }
                    break;
            }
        }
    };

    private Handler mErrorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String error = mContext.getString(R.string.translate_error);
            switch (msg.what) {
                case Result.LOCALE_IO_EXCEPTION:
                    error = "LOCALE_IO_EXCEPTION";
                    break;
                case Result.LOCALE_JSON_EXCEPTION:
                    error = "LOCALE_JSON_EXCEPTION";
                    break;
                case Result.LOCALE_MALFORMED_URL:
                    error = "LOCALE_MALFORMED_URL";
                    break;
                case Result.LOCALE_UNSUPPORTED_ENCODER:
                    error = "LOCALE_UNSUPPORTED_ENCODER";
                    break;
                case Result.RESULT_INVALID:
                    error = "RESULT_INVALID";
                    break;
                case Result.RESULT_KEY_ERROR:
                    error = "RESULT_KEY_ERROR";
                    break;
                case Result.RESULT_LONG:
                    error = "RESULT_LONG";
                    break;
                case Result.RESULT_NO_RESULT:
                    error = "RESULT_NO_RESULT";
                    break;
                case Result.RESULT_UNSUPPORTED:
                    error = "RESULT_UNSUPPORTED";
                    break;
            }
            if (mKey == mRequest.getKey()) {
                mRequest.setHasResponse(true);
                if (mListener != null) {
                    mListener.onTranslateFailed(error);
                }
            }
        }
    };

    public TranslateManager(Context context) {
        this.mContext = context;
        mEngine = new TranslateEngine(context);
    }

    public void translate(String request) {
        try {
            if (mRequest != null && (mRequest.getKey() == mKey) && !mRequest.hasResponse()) {
                return;
            }
            if (mListener != null) {
                mListener.onTranslateStart();
            }
            mRequest = new Request(request, mHandler, mErrorHandler);
            mKey = mRequest.getKey();
            mEngine.setRequest(mRequest).start();
        } catch (TranslateEngine.NotSetRequestException e) {
            e.printStackTrace();
        }
    }

    public void setTranslateListener(OnTranslateListener listener) {
        mListener = listener;
    }
}
