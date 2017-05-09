package bravest.ptt.androidlib.net;

import android.app.Activity;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import bravest.ptt.androidlib.R;

public class UrlConfigManager {

    private static final int RESOURCE_ID = R.xml.url;

    private static final String XML_NODE = "Node";

    private static final String XML_KEY = "Key";

    private static final String XML_EXPIRES = "Expires";

    private static final String XML_NETTYPE = "NetType";

    private static final String XML_MOCK_CLASS = "MockClass";

    private static final String XML_URL = "Url";

    private static ArrayList<URLData> urlList;

    private static void fetchUrlDataFromXml(final Activity activity) {
        urlList = new ArrayList<URLData>();

        final XmlResourceParser xmlParser = activity.getApplication()
                .getResources().getXml(RESOURCE_ID);

        int eventCode;
        try {
            eventCode = xmlParser.getEventType();
            while (eventCode != XmlPullParser.END_DOCUMENT) {
                switch (eventCode) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (XML_NODE.equals(xmlParser.getName())) {
                            final String key = xmlParser.getAttributeValue(null,
                                    XML_KEY);
                            final URLData urlData = new URLData();
                            urlData.setKey(key);
                            urlData.setExpires(Long.parseLong(xmlParser
                                    .getAttributeValue(null, XML_EXPIRES)));
                            urlData.setNetType(xmlParser.getAttributeValue(null,
                                    XML_NETTYPE));
                            urlData.setMockClass(xmlParser.getAttributeValue(null,
                                    XML_MOCK_CLASS));
                            urlData.setUrl(xmlParser.getAttributeValue(null, XML_URL));
                            urlList.add(urlData);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventCode = xmlParser.next();
            }
        } catch (final XmlPullParserException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            xmlParser.close();
        }
    }

    public static URLData findURL(final Activity activity,
                                  final String findKey) {
        // 如果urlList还没有数据（第一次），或者被回收了，那么（重新）加载xml
        if (urlList == null || urlList.isEmpty())
            fetchUrlDataFromXml(activity);

        for (URLData data : urlList) {
            if (findKey.equals(data.getKey())) {
                return data;
            }
        }

        return null;
    }
}
