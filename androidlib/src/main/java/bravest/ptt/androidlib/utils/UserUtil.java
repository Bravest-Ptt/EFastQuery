package bravest.ptt.androidlib.utils;

import java.util.UUID;

/**
 * Created by pengtian on 2017/5/7.
 */

public class UserUtil {
    public static final int USER = 10;
    public static String generateUserName(int length) {
        String name = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        if (name.length() > length) {
            name = name.substring(length);
        }
        return name;
    }
}
