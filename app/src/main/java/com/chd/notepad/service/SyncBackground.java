package com.chd.notepad.service;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.chd.TClient;
import com.chd.base.backend.SyncTask;
import com.chd.notepad.ui.db.DatabaseManage;
import com.chd.notepad.ui.db.FileDBmager;
import com.chd.notepad.ui.item.NoteItemtag;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.proto.LoginResult;
import com.chd.yunpan.share.ShareUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class SyncBackground extends Thread {

	private List<String> tasks ;

	private DatabaseManage su = null;

	private Context context = null;

	private boolean runing=true;

	private File file=null;
	private SyncTask syncTask;

	private FileDBmager fdb;
	/**
	 * 0：更新同步 1：全量同步 2:单条同步
	 * */
	private int syncType = -1;

	private List<FileInfo0> cloudlist=null;
	private String _workpath;
	private final String TAG="SyncnoteService";
	private Handler mHandler;
	public static final int SUCESS=0x1002;
	//private int sleepsecond=6;
	/**

	 * */
	public SyncBackground(Context context,Handler mHandler) {
		this.context = context;
		this.syncType = syncType;
		//su = new DatabaseManage(context);
		this.mHandler=mHandler;
		syncTask=new SyncTask(context, FTYPE.NOTEPAD);
		fdb=new FileDBmager(context);

		ShareUtils shareUtils = new ShareUtils(context);
		_workpath=shareUtils.getStorePathStr();
		tasks=new ArrayList<>();
		LoginResult loginEntity = shareUtils.getLoginEntity();
		_workpath=_workpath+"/"+loginEntity.getUserid();
		if(!new File(_workpath).exists()){
			new File(_workpath).mkdir();
		}
		//su.open();
	}

	public String getWorkPath(){
		return this._workpath;
	}

    public void safeshutdown()
	{

		wakeup(1);
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
	}

	// 找到所有需要上传的列表
	private void getTasks() {

		cloudlist=syncTask.getCloudUnits(0,10000);
		int total=cloudlist.size();
		for(int i=0;i<total;i++) {
			FileInfo info=cloudlist.get(i);
			FileInfo0 fileInfo0 = new FileInfo0(cloudlist.get(i));
			cloudlist.set(i,fileInfo0);
			info=null;

		}
		Iterator<String> iterator=fdb.getLocallist();
		String fname;
		while (iterator.hasNext())
		{
			fname=iterator.next();
			if (!contains(fname ))
				tasks.add(fname);

		}
		//su.open();
		//tasks = su.getSyncTasks();


	}

	boolean contains(String fname)
	{

		for(FileInfo0 fileInfo0:cloudlist)
		{
			if (fileInfo0.getSysid()>0)
				continue;
			if (fname.compareToIgnoreCase(fileInfo0.getObjid())==0) {
				cloudlist.remove(fileInfo0);
				fileInfo0.setSysid(1);
				return true;
			}
		}
		return  false;
	}


	private void sync() {

				getTasks();
				if(tasks.size()==0)
				{
					Log.d(TAG,"no taks to sync");
				}
				for (String fname:tasks)
				{
					FileInfo0 fileInfo0=new FileInfo0();
					fileInfo0.setFilePath(_workpath+File.separator+fname);
					fileInfo0.setObjid(fname);
					syncTask.upload(fileInfo0,null,false);
				}
				tasks.clear();
				for (FileInfo0 fileInfo0:cloudlist)
				{
					if (fileInfo0.getSysid()==0)
						try {
							TClient.getinstance().delObj(fileInfo0.getObjid(),fileInfo0.getFtype());
						} catch (Exception e) {
							e.printStackTrace();
							Log.e(TAG,e.getMessage());
						}

				}
		mHandler.sendEmptyMessage(SUCESS);
				Log.d(TAG,"all task finished ");

	}


	private void work() {


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

	public void setFile(File file) {
		this.file = file;
	}
}
