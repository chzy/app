package com.chd.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.chd.yunpan.net.NetworkUtils;

public class SyncFileService extends Service {


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		isStarting = true;
		handler.sendEmptyMessage(0);
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		isStarting = true;
		
	}
	
	
	private Thread t;
	
	private boolean isStarting = true;

	public Handler handler =new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			if(isStarting){
				if(NetworkUtils.isNetworkAvailable(SyncFileService.this)){
					if(t==null||!t.isAlive()){
						System.out.println("上传吗？");
						t =  new Thread(new SyncLocalFileBackground(SyncFileService.this, 0));
						System.out.println("开始了");
						t.start();
					}
				}else {
					//Toast.makeText(UpLoadService.this, "网络不可用", 0).show();
				}
				handler.sendEmptyMessageDelayed(0, 10000);
			}
		}
	};
	
	
	public void onDestroy() {
		isStarting =false;
		super.onDestroy();
	}

	public IBinder onBind(Intent intent) {
		
		return null;
	}

}
