package com.chd.yunpan.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.chd.yunpan.FileProvider;

import java.io.File;

/**
 * @description
 * @FileName: com.chd.yunpan.utils.AutoInstall
 * @author: liumj
 * @date:2016-01-28 13:38
 * OS:Mac 10.10
 * Developer Kits:AndroidStudio 1.5
 */

public class AutoInstall {
        private static String mUrl;
        private static Context mContext;

        /**
         * 外部传进来的url以便定位需要安装的APK
         *
         * @param url
         */
        public static void setUrl(String url) {
            mUrl = url;
        }

        /**
         * 安装
         *
         * @param context
         *            接收外部传进来的context
         */
        public static void install(Context context) {
            mContext = context;
            // 核心是下面几句代码
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Log.d("lmj",mUrl);
            //判读版本是否在7.0以上
            if (Build.VERSION.SDK_INT >= 24) {
                //provider authorities
                Uri apkUri = FileProvider.getUriForFile(context, "com.chd.yunpan.android7.fileprovider", new File(mUrl));
                //Granting Temporary Permissions to a URI
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(new File(mUrl)), "application/vnd.android.package-archive");
            }

            mContext.startActivity(intent);
        }
}
