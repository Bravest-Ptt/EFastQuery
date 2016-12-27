package bravest.ptt.efastquery.data;


import android.os.Handler;

/**
 * Created by root on 12/27/16.
 */

class Request {

    public String mRequest;
    public Handler mHandler;
    public Handler mErrHandler;

    private boolean mHasResponse = false;

    private long key;

    public Request(String request, Handler handler, Handler errHandler) {
        key = System.currentTimeMillis();
        this.mRequest = request;
        this.mHandler = handler;
        this.mErrHandler = errHandler;
    }

    public long getKey() {
        return key;
    }

    public boolean hasResponse() {
        return mHasResponse;
    }

    public void setHasResponse(boolean has) {
        mHasResponse = has;
    }
}
