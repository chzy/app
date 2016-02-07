package com.chd.notepad.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


public class SyncService extends Service {


	private final IBinder mBinder = new SyncBinder();
	private SyncBackground syncthread;
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
			//System.out.println("sssssssssssssssss");
			return SyncService.this;
		}
	}
	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		System.out.println(" on rebind");
		/*if (syncthread==null)*/
			super.onRebind(intent);
	/*	else
			return;*/
	}


	@Override
	public IBinder onBind(Intent intent) {
		if (syncthread==null) {
//			syncthread = new SyncBackground(getApplication());
			syncthread.start();
			System.out.print("on bind start thread");
		}

		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent)
	{

		System.out.print("onunbind");
		syncthread.safeshutdown();
		try {
			Thread.sleep(10000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//syncthread=null;
		//syncthread.notify();
		return true;
		//return  super.onUnbind(intent);
	}



}
