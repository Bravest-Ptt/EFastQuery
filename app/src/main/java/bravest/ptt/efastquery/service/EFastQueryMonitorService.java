package bravest.ptt.efastquery.service;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import bravest.ptt.efastquery.activity.HomeActivity;
//import bravest.ptt.efastquery.entity.copy.CopyNode;
import bravest.ptt.efastquery.utils.Utils;

public class EFastQueryMonitorService extends AccessibilityService {

    private static final String TAG = "EFastQueryMonitorServic";

    private static final int TYPE_VIEW_CLICKED = AccessibilityEvent.TYPE_VIEW_CLICKED;
    private static final int TYPE_VIEW_LONG_CLICKED = AccessibilityEvent.TYPE_VIEW_LONG_CLICKED;
    private static final int TYPE_VIEW_DOUBLD_CLICKED = 3;
    private static final int TYPE_VIEW_NONE = 0;

    public static final int DOUBLE_CLICK_TIME = 1000;

    private CharSequence mWindowClassName;
    private String mCurrentPackage;
    private int mCurrentType;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int type = accessibilityEvent.getEventType();
        switch (type) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                mWindowClassName = accessibilityEvent.getClassName();
                mCurrentPackage = accessibilityEvent.getPackageName() == null ?
                        "" : accessibilityEvent.getPackageName().toString();

                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:

                break;
            default:
                break;
        }
    }

    private synchronized void getText(AccessibilityEvent event) {
        Log.d(TAG, "getText: " + event);

        int type = getClickType(event);
        CharSequence className = event.getClassName();
        if (mWindowClassName == null) {
            return;
        }
        if (mWindowClassName.toString().startsWith("bravest.ptt.efastquery")) {
            return;
        }

        if (!mCurrentPackage.equals(event.getPackageName())) {
            return;
        }

        if (className == null || className.equals("android.widget.EditText")) {
            return;
        }

        AccessibilityNodeInfo info = event.getSource();
        if (info == null) {
            return;
        }

        CharSequence txt = info.getText();
        if (TextUtils.isEmpty(txt)) {
            List<CharSequence> txts = event.getText();
            if (txts != null) {
                StringBuilder sb = new StringBuilder();
                for (CharSequence t : txts) {
                    sb.append(t);
                }
                txt = sb.toString();
            }
        }

        if (!TextUtils.isEmpty(txt)) {
            //Intent intent = new Intent(this, SplitTextActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.
        }
    }

    /*
    private int retryTimes = 0;
    private Handler mHandler = new Handler();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void UniversalCopy() {
        boolean isSuccess = false;
        labelOut:
        {
            AccessibilityNodeInfo rootInActiveWindow = this.getRootInActiveWindow();
            if (retryTimes < 10) {
                String packageName;
                if (rootInActiveWindow != null) {
                    packageName = String.valueOf(rootInActiveWindow.getPackageName());
                } else {
                    packageName = null;
                }

                if (rootInActiveWindow == null) {
                    ++retryTimes;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            UniversalCopy();
                        }
                    }, 100);
                    return;
                }

                //获取屏幕高宽，用于遍历数据时确定边界。
                int heightPixels = Utils.getScreenHeight(this);
                int widthPixels = Utils.getScreenWidth(this);

                ArrayList nodeList = traverseNode(
                        new AccessibilityNodeInfoCompat(
                                rootInActiveWindow), widthPixels, heightPixels);

                if (nodeList.size() > 0) {
                    Intent intent = null;// = new Intent(this, .class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putParcelableArrayListExtra("copy_nodes", nodeList);
                    intent.putExtra("source_package", packageName);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(
                            this.getBaseContext(),
                            android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    startActivity(intent, options.toBundle());
                    isSuccess = true;
                    break labelOut;
                }
            }

            isSuccess = false;
        }

        if (!isSuccess) {
            if (!Utils.isAccessibilitySettingsOn(this, this.getClass())) {
                //ToastUtil.show(R.string.error_in_permission);
                Log.d(TAG, "UniversalCopy: error_in_permission");
            } else {
                //ToastUtil.show(R.string.error_in_copy);
                Log.d(TAG, "UniversalCopy: error_in_copy");
            }

        }

        retryTimes = 0;
    }

    private ArrayList<CopyNode> traverseNode(AccessibilityNodeInfoCompat nodeInfo, int width, int height) {
        ArrayList<CopyNode> nodeList = new ArrayList();
        if (nodeInfo != null && nodeInfo.getInfo() != null) {
            nodeInfo.refresh();

            for (int i = 0; i < nodeInfo.getChildCount(); ++i) {
                //递归遍历nodeInfo
                nodeList.addAll(traverseNode(nodeInfo.getChild(i), width, height));
            }

            if (nodeInfo.getClassName() != null && nodeInfo.getClassName().equals("android.webkit.WebView")) {
                return nodeList;
            } else {
                String content = null;
                String description = content;
                if (nodeInfo.getContentDescription() != null) {
                    description = content;
                    if (!"".equals(nodeInfo.getContentDescription())) {
                        description = nodeInfo.getContentDescription().toString();
                    }
                }

                content = description;
                if (nodeInfo.getText() != null) {
                    content = description;
                    if (!"".equals(nodeInfo.getText())) {
                        content = nodeInfo.getText().toString();
                    }
                }

                if (content != null) {
                    Rect outBounds = new Rect();
                    nodeInfo.getBoundsInScreen(outBounds);
                    if (checkBound(outBounds, width, height)) {
                        nodeList.add(new CopyNode(outBounds, content));
                    }
                }

                return nodeList;
            }
        } else {
            return nodeList;
        }
    }


    private boolean checkBound(Rect var1, int var2, int var3) {
        //检测边界是否符合规范
        return var1.bottom >= 0 && var1.right >= 0 && var1.top <= var3 && var1.left <= var2;
    }
*/
    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

    }

    private int getClickType(AccessibilityEvent event) {
        int type = event.getEventType();
        long time = event.getEventTime();
        long id = getSourceNodeId(event);
        if (type != TYPE_VIEW_CLICKED) {
            mLastClickTime = time;
            mLastSourceNodeId = -1;
            return type;
        }
        if (id == -1) {
            mLastClickTime = time;
            mLastSourceNodeId = -1;
            return type;
        }
        if (type == TYPE_VIEW_CLICKED
                && time - mLastClickTime <= DOUBLE_CLICK_TIME && id == mLastSourceNodeId) {
            mLastClickTime = -1;
            mLastSourceNodeId = -1;
            return TYPE_VIEW_DOUBLD_CLICKED;
        } else {
            mLastClickTime = time;
            mLastSourceNodeId = id;
            return type;
        }
    }

    private Method getSourceNodeIdMethod;
    private long mLastSourceNodeId;
    private long mLastClickTime;

    private long getSourceNodeId(AccessibilityEvent event) {
        //用于获取点击的View的id，用于检测双击操作
        if (getSourceNodeIdMethod == null) {
            Class<AccessibilityEvent> eventClass = AccessibilityEvent.class;
            try {
                getSourceNodeIdMethod = eventClass.getMethod("getSourceNodeId");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        if (getSourceNodeIdMethod != null) {
            try {
                return (long) getSourceNodeIdMethod.invoke(event);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}
