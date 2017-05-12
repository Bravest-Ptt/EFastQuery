package bravest.ptt.androidlib.net;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class RequestParam extends JSONObject implements java.io.Serializable {

    private static final long serialVersionUID = 1274906854152052510L;

    private String body;

    public RequestParam(String body) {
        this.body = body;
    }

    public RequestParam() {
    }

    public void param(String key, String value) {
        try {
            this.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }



    //----------------------------------------------------------------------------------------
//    public int compareTo(final Object another) {
//        int compared;
//        /**
//         * 值比较
//         */
//        final RequestParameter parameter = (RequestParameter) another;
//        compared = name.compareTo(parameter.name);
//        if (compared == 0) {
//            compared = value.compareTo(parameter.value);
//        }
//        return compared;
//    }
//
//    public boolean equals(final Object o) {
//        if (null == o) {
//            return false;
//        }
//
//        if (this == o) {
//            return true;
//        }
//
//        if (o instanceof RequestParameter) {
//            final RequestParameter parameter = (RequestParameter) o;
//            return name.equals(parameter.name) && value.equals(parameter.value);
//        }
//
//        return false;
//    }
}