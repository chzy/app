package com.chd.notepad.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


public class SyncService extends Service {

//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		isStarting = true;
//		handler.sendEmptyMessage(0);
//		return super.onStartCommand(intent, flags, startId);
//	}

	private final IBinder mBinder = new SyncBinder();
	private SyncBackground syncthread= new SyncBackground(SyncService.this);
	//private static boolean created=false;
	private static boolean runing=false;
	//private SyncBackground sync = new SyncBackground(SyncService.this);

	@Override
	public void onCreate() {

//		if (runing)
//			return;
		super.onCreate();
		//created=true;
		runing=true;
		//isStarting = true;
		syncthread.start();
	}
	
	
	//private Thread t;
	
	//private boolean isStarting = true;;
	
//	public Handler handler =new Handler(){
//
//		public void handleMessage(android.os.Message msg) {
//			if(isStarting){
//				//if(NetworkUtils.isNetworkAvailable(SyncService.this))
//				if (true)
//				{
//					if(t==null||!t.isAlive()){
//						System.out.println("上传吗？");
//						t =  new Thread(new SyncBackground(SyncService.this, 0));
//						System.out.println("开始了");
//						t.start();
//					}
//				}else {
//					//Toast.makeText(SyncService.this, "网络不可用", 0).show();
//				}
//
//				handler.sendEmptyMessageDelayed(0, 10000);
//			}
//		};
//	};



	public void NotifySync()
	{
//				int op = bundle.getInt("op");
//				switch (op) {
//					case 1:
//						t =  new Thread(new SyncBackground(SyncService.this, 1));
//						break;
//					case 2:
//						t =  new Thread(new SyncBackground(SyncService.this, 2));
//						break;
//					default:
//						t =  new Thread(new SyncBackground(SyncService.this, 3));
//						break;
		syncthread.wakeup(1);
		Log.d("$$$", "Service->notifysnc");
		//Log("dddddddd");
		//System.out.print("d22222222222");
	}

	@Override
	public void onDestroy() {
		Log.d("$$$", "Service ->OnDestory");
//		if (runing)
//			return;
		//syncthread.safeshutdown();
		super.onDestroy();
	}

	public  void stop()
	{
		runing=false;
	}
	public class SyncBinder extends Binder {
		public SyncService getService() {
			// Return this instance of LocalService so clients can call public methods
			return SyncService.this;
		}
	}
	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		//System.out.print("ddddddddddddd");
		super.onRebind(intent);

	}


	@Override
	public IBinder onBind(Intent intent) {
		
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent)
	{

		System.out.print("onunbind");
		return true;
		//return  super.onUnbind(intent);
	}

}
