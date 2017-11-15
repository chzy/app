package com.chd.yunpan.utils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
    public static void openApp(Context ctx, String packageName, Bundle bundle){
        //要调用另一个APP的activity名字
        String activity = "com.xinqing.login.Login";
        ComponentName component = new ComponentName(packageName, activity);
        Intent intent = new Intent();
        intent.setComponent(component);
        intent.setFlags(101);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }


    /**
     * 判断App是否安装
     *
     * @param packageName 包名
     * @return {@code true}: 已安装<br>{@code false}: 未安装
     */
    public static boolean isInstallApp(Context ctx,final String packageName) {
        return !isSpace(packageName) && getLaunchAppIntent(ctx,packageName) != null;
    }

    /**
     * 获取打开App的意图
     *
     * @param packageName 包名
     * @return intent
     */
    public static Intent getLaunchAppIntent(Context ctx,final String packageName) {
        return ctx.getPackageManager().getLaunchIntentForPackage(packageName);
    }
}
