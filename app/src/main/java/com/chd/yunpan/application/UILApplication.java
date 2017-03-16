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
import com.lockscreen.view.LockPatternUtils;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import cn.smssdk.SMSSDK;
//import im.fir.sdk.FIR;

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
	public void onCreate(){
		if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
		/*	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());*/
		}
		filelistEntity=new FilelistEntity();
		super.onCreate();
		//FIR.init(this);
		CrashHandler.getInstance().init(this);
		mInstance = this;
		mLockPatternUtils = new LockPatternUtils(this);
		initImageLoader(getApplicationContext());
		CloseableHttpClient httpClient= HttpClients.createSystem();

		try {
			TClient.getinstance(httpClient);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Application", e.getMessage());
		}
		SMSSDK.initSDK(this, "f40f0f41f1d1", "8542792ca37ec28ce85a0ce024c957b2",true);
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

	public static void   ClearFileEntity()
	{
		if (filelistEntity!=null)
		{
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
				.memoryCache(new LruMemoryCache(2*1024*1024))
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
				.diskCacheSize(500 * 1024 * 1024) // 500 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.

		ImageLoader.getInstance().init(config);
		//ImageLoader.getInstance().clearDiskCache();
	}
}