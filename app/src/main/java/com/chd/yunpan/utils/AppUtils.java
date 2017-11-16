package com.chd.yunpan.utils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class AppUtils {

    /**
     * 获取IMEI码
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.READ_PHONE_STATE"/>}</p>
     *
     * @return IMEI码
     */
    @SuppressLint("HardwareIds")
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getDeviceId() : null;
    }


    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 打开APP
     */
    public static void openApp(Context ctx, String packageName, Bundle bundle) {
        //要调用另一个APP的activity名字
        ComponentName component = null;
        Intent intent = new Intent();
        if ("cn.heartfree.xinqing".equals(packageName)) {
            String activity = "com.xinqing.login.Login";
            component = new ComponentName(packageName, activity);
        } else {
            intent = ctx.getPackageManager().getLaunchIntentForPackage(packageName);
        }
        if (component != null)
            intent.setComponent(component);
        intent.setFlags(101);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }

    /**
     * 判断手机是否安装某个应用
     *
     * @param context
     * @param packageName 应用包名
     * @return true：安装，false：未安装
     * 该方法容易报错：java.lang.RuntimeException: Package manager has died
     */
    public static boolean isAppInstallen(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
            installed = false;
        }
        return installed;

    }
}
