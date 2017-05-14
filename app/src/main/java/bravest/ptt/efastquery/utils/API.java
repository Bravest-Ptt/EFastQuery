package bravest.ptt.efastquery.utils;

/**
 * Created by pengtian on 2017/5/10.
 */

public interface API {
    /**
     * API key must same as url.xml
     */

    String REGISTER = "register";
    String LOGIN = "login";
    String UPDATE = "update";
    String GET_USER_INFO = "getUserInfo";
    String REQUEST_SMS_CODE = "requestSmsCode";
    String VERIFY_SMS_CODE = "verifySmsCode";
    String QUERY_SMS_STATE = "querySmsState";
    String IS_MOBILE_USED = "isMobileUsed";

    public interface BmobError {

    }
}
