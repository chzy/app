/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.chd.yunpan.application;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.chd.TClient;
import com.chd.base.Entity.FilelistEntity;
import com.chd.service.RPCchannel.download.DownloadConfiguration;
import com.chd.service.RPCchannel.download.DownloadManager;
import com.chd.service.RPCchannel.upload.FileUploadConfiguration;
import com.chd.service.RPCchannel.upload.FileUploadManager;
import com.chd.service.RPCchannel.upload.parser.TrpcResponseParse;
import com.chd.service.RPCchannel.upload.uploader.TrpcUploader;
import com.lockscreen.view.LockPatternUtils;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.bugly.crashreport.CrashReport;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class UILApplication extends Application {


    private static FilelistEntity filelistEntity;
    private static UILApplication mInstance;
    private LockPatternUtils mLockPatternUtils;


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressWarnings("unused")
    @Override
    public void onCreate() {
        if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
        /*	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());*/
        }
        filelistEntity = new FilelistEntity();
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "14a809b6e8", false);
        CrashHandler.getInstance().init(this);
        mInstance = this;
        mLockPatternUtils = new LockPatternUtils(this);
        initImageLoader(getApplicationContext());
        initDownUP(getApplicationContext());
//        try {
//            TClient tClient=new TClient(new String[]{"http://221.7.13.207:8079/chdserver.php"});
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        CloseableHttpClient httpClient = HttpClients.createSystem();
        // application 初始化
        FunctionOptions options = new FunctionOptions.Builder()
                .setType(FunctionConfig.TYPE_IMAGE)
                .setCompress(true)
                .setGrade(Luban.THIRD_GEAR)
                .create();
        PictureConfig.getInstance().init(options);
        try {
            TClient.getinstance(httpClient);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Application", e.getMessage());
        }

    }

    //@Override
    public void Wlog(String tag, String msg) {
        try {
            throw new Exception(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FilelistEntity getFilelistEntity() {

        return filelistEntity;
    }

    public static void ClearFileEntity() {
        if (filelistEntity != null) {
            filelistEntity.getBklist().clear();
            filelistEntity.getLocallist().clear();
        }

    }

    public static class Config {
        public static final boolean DEVELOPER_MODE = false;
    }


    public static UILApplication getInstance() {
        return mInstance;
    }

    public LockPatternUtils getLockPatternUtils() {
        return mLockPatternUtils;
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(5)
                //.memoryCache(new WeakMemoryCache())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .diskCacheSize(500 * 1024 * 1024) // 500 Mb
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.

        ImageLoader.getInstance().init(config);
        //ImageLoader.getInstance().clearDiskCache();
    }

    private static void initDownUP(Context context) {

        ExecutorService exService = Executors.newCachedThreadPool();
        //ExecutorService exService = Executors.newFixedThreadPool(2);

        FileUploadConfiguration fileUploadConfiguration = new FileUploadConfiguration.Builder(context)
                .setResponseProcessor(new TrpcResponseParse())  //设置http response字符串的结果解析器，如果不设置，则默认返回response字符串
                .setThreadPoolSize(2)         //设置线程池大小，如果采用默认的线程池则有效
                .setThreadPriority(Thread.NORM_PRIORITY - 1)  //设置线程优先级，如果采用默认的线程池则有效
                .setTaskExecutor(exService)     //设置自定义的线程池
                .setFileUploader(new TrpcUploader())    //设置自定义的文件上传功能，如果不设置则采用默认的文件上传功能
                .build();
        FileUploadManager.getInstance().init(fileUploadConfiguration);

        DownloadConfiguration downloadConfiguration = new DownloadConfiguration.Builder(context)
                //.setCacheDir()        //设置下载缓存目录，必须设置
                //.setTaskExecutor(...)    //同上传类似
                //.setThreadPriority(...)  //同上传类似
                .setThreadPoolCoreSize(2)  //同上传类似
                .build();
        DownloadManager.getInstance(context).init(downloadConfiguration);
    }
}