package com.chd.notepad.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


public class SyncService extends Service {


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
			System.out.println("sssssssssssssssss");
			return SyncService.this;
		}
	}
	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		System.out.print("ddddddddddddd");
		super.onRebind(intent);

	}


	@Override
	public IBinder onBind(Intent intent) {
		System.out.print("ddddddddddddd");
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
