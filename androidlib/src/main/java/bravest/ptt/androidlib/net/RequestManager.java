package bravest.ptt.androidlib.net;

import java.util.ArrayList;
import java.util.List;

import bravest.ptt.androidlib.activity.BaseActivity;

public class RequestManager {
    private ArrayList<OkHttpRequest> requestList = null;
    private BaseActivity activity;


    public RequestManager(final BaseActivity activity) {
        this.activity = activity;
        // 异步请求列表
        requestList = new ArrayList<>();
    }

    /**
     * 添加Request到列表
     */
    public void addRequest(final OkHttpRequest request) {
        requestList.add(request);
    }

    /**
     * 取消网络请求
     */
    public void cancelRequest() {
        if ((requestList != null) && (requestList.size() > 0)) {
            for (final OkHttpRequest request : requestList) {
                if (request.getCall() != null) {
                    try {
                        request.getCall().cancel();
                        requestList.remove(request);
                    } catch (final UnsupportedOperationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 无参数调用
     */
    public OkHttpRequest createRequest(final URLData urlData, final RequestCallback requestCallback) {
        final OkHttpRequest request = new OkHttpRequest(activity,urlData, null, requestCallback);
        addRequest(request);
        return request;
    }

    /**
     * 有参数调用
     */
    public OkHttpRequest createRequest(final URLData urlData, final List<RequestParameter> params, final RequestCallback requestCallback) {
        final OkHttpRequest request = new OkHttpRequest(activity,urlData, params, requestCallback);
        addRequest(request);
        return request;
    }

}