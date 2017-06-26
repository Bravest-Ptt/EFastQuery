package bravest.ptt.androidlib.net;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;


/**
 * How to use the RequestParam
 * if you have a parameter need append to the url, if the original url is https://a.pengtian.com/user, append objectId, the url will look like https://a.pengtian.com/user/objectid
 * if you have a body to update, so give me a JSON string which contains your body data.
 */
public class RequestParam implements java.io.Serializable {

    private static final long serialVersionUID = 1274906854152052510L;

    private String body;

    private String objectId;

    /**
     *
     * @param objectId for url
     * @param body the body
     */
    public RequestParam(String objectId, String body) {
        this.objectId = objectId;
        this.body = body;
    }

    /**
     * @param body the body
     */
    public RequestParam(String body) {
        checkParam(body);
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public RequestParam setBody(String body) {
        checkParam(body);
        this.body = body;
        return this;
    }

    public String getObjectId() {
        return objectId;
    }

    public RequestParam setObjectId(String objectId) {
        checkParam(objectId);
        this.objectId = objectId;
        return this;
    }

    public boolean hasId() {
        return !TextUtils.isEmpty(objectId);
    }

    public boolean hasBody() {
        return !TextUtils.isEmpty(body);
    }

    private void checkParam(String... param) {
        for (String p:param) {
            if (TextUtils.isEmpty(p)) {
                throw new NullPointerException();
            }
        }
    }

    @Override
    public String toString() {
        return "RequestParam{" +
                "body='" + body + '\'' +
                ", objectId='" + objectId + '\'' +
                '}';
    }
}