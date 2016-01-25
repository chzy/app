package com.chd.notepad.service;

import android.content.Context;

import com.chd.notepad.ui.db.DatabaseManage;
import com.chd.notepad.ui.item.NoteItemtag;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class SyncBackground extends Thread {

	private List<NoteItemtag> tasks ;

	private DatabaseManage su = null;

	private Context context = null;

	private boolean runing=true;

	/**
	 * 0：更新同步 1：全量同步 2:单条同步
	 * */
	private int syncType = -1;

	/**

	 * */
	public SyncBackground(Context context) {
		this.context = context;
		this.syncType = syncType;
		su = new DatabaseManage(context);

		//su.open();
	}


    public void safeshutdown()
	{
		this.runing=false;
	}
	public void wakeup(int type)
	{

		synchronized (this)
		{
			this.notify();
		}
	}
	public void run() {

		// if (syncType == 1) {
		// if (SharePreUtil.getAutoPhotoBackupThread(context)) {
		// return;
		// }
		// }
		// if (syncType == 1) {
		// SharePreUtil.saveAutoPhotoBackupThread(context, true);
		// }

		while (runing) {
			synchronized (this) {

				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//su.open();
			sync();
		}
		// if (syncType == 1) {
		// SharePreUtil.saveAutoPhotoBackupThread(context, false);
		// }
		//su.close();

	}

	// 找到所有需要上传的列表
	private void getTasks() {
		//su.open();
		tasks = su.getSyncTasks();

	}

	// 递归上传所有
	private void sync() {
		su.open();

		switch (syncType)
		{
			case 0:
			{
				getTasks();
				if(tasks.size()==0)
					break;
				for(NoteItemtag item :tasks)
				{
					SyncSingle(item);
				}
				tasks.clear();
			}
			case 1:
				break;
		}
		
//		if(upLoadFile(tasks.get(0))){
//
//			su.deleteUpLoadingFile(tasks.get(0).getFid());
//			su.addUpLoadedFile(tasks.get(0));
//		}else{
//			su.deleteUpLoadingFile(tasks.get(0).getFid());
//		}
//
//
//		if(NetworkUtils.isNetworkAvailable(context)){
//			sync();
//		}
		

	}

	private boolean SyncSingle(NoteItemtag itemtag) {


       //if (itemtag.syncstate== DatabaseManage.SYNC_STAT.DONE)
	//		return false;

	   /*
	   * TODO call sync method
	   * */
		//if (itemtag.syncstate!= DatabaseManage.SYNC_STAT.DEL)
		//	su.delete(itemtag.id);
		//else
		//	su.markSyncbyId(itemtag.id);
		System.gc();

		//return b;
		return true;
	}

	private boolean SyncFull(List<NoteItemtag> locallists,Set<NoteItemtag> remotelist) {

//		if (itemtag.syncstate== DatabaseManage.SYNC_STAT.DONE)
//			return false;
//
//	   /*
//	   * TODO call sync method
//	   * */
//		if (itemtag.syncstate!= DatabaseManage.SYNC_STAT.DEL)
//			su.delete(itemtag.hashcode);
//		else
//			su.markSyncbyId(itemtag.hashcode);
//		System.gc();

		//return b;
		List<NoteItemtag> synclist_R=new ArrayList<NoteItemtag>();
		for(NoteItemtag itemtag:locallists)
		{
			synclist_R.add(itemtag);
		}
		synclist_R.removeAll(remotelist);// need sync to cloude; in local not in remote;

 		locallists.removeAll(remotelist);
		locallists.addAll(remotelist);

		return true;
	}

}
