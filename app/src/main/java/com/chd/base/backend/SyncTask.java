package com.chd.base.backend;

import android.content.Context;
import android.util.Log;

import com.chd.Entity.FilesListEntity;
import com.chd.MediaMgr.utils.MediaFileUtil;
import com.chd.TClient;
import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.MediaMgr;
import com.chd.base.Ui.ActiveProcess;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.service.SyncLocalFileBackground;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SyncTask {
	private Context context;
	MediaMgr dbManager;
	private boolean mExitTasksEarly;// 用户终止
	public List<FileInfo> _CloudList;
	private FilelistEntity filelistEntity;
	private final FTYPE _ftype;
	private final String TAG=this.getClass().getName();
	//private MediaFileUtil.FileCategory;



	public SyncTask(Context context,FTYPE tp) {
		this.context = context;
		_ftype=tp;
		_CloudList=new ArrayList<FileInfo>();
		dbManager = new MediaMgr(context,_ftype);
		//dbManager.open();
	}

	public FileInfo0 getUnitinfo(int id) {
		return filelistEntity.getBklist().get(id);
	}

	//查询远程对象是否有本地副本. 根据文件名匹配
	public boolean haveLocalCopy(FileInfo0 info0) {
		boolean ret=false;
		if (info0.getObjid()==null)
		{
			Log.d(TAG, "not remote file obj");
			return false;
		}
		if (info0.getSysid() > 0)
		{
			if (info0.getFilePath()!=null && info0.getFilePath().indexOf(".")>1 ) {
					File file=new File(info0.getFilePath());
				   return  (file.exists() && file.isFile());
			}
			if (info0.getFtype()==null)
				info0.setFtype(_ftype);
			int time=info0.getLastModified();
			ret=   dbManager.queryLocalInfo(info0.getSysid(),info0);
			/*
			临时方案 恢复成远程的上传时间
			* */
			if (ret)
				info0.setLastModified(time);
		}

		////return true;
		//通过上下行记录 来匹配
		/*dbManager.open();
		if (dbManager.QueryDownloadedFile(info0,false))
			ret= true;

		if (dbManager.QueryUploadedFile(info0, false))
			ret= true;
		dbManager.close();*/
		return ret;


	}



	/*public FileInfo0 GetLocalCopy(FileInfo0 info0) {
		if (info0.getSysid() > 0)
		{
			if (info0.getFilePath()!=null && info0.getFilePath().indexOf(".")>1 ) {
				File file=new File(info0.getFilePath());
				if (file.exists() && file.isFile())
				{
					return info0;
				}
			}
			FileInfo0 info01=dbManager.queryLocalInfo(info0.getSysid(),_ftype);
			if (info01!=null) {
				FileInfo0 item=new FileInfo0(info0);
				item.setFilePath(info0.getFilePath());
				item.setSysid(info0.getSysid());
				return item;
			}
		}
		boolean ret=false;
		return  null;
	}*/

	public void flush() {
		filelistEntity.getBklist().clear();
		//filelistEntity.getUbklist().clear();
		filelistEntity = null;
	}

	public List<FileInfo0> getCloudUnits(int begin, int max) {
		/*if (filelistEntity!=null && filelistEntity.getBklist()!=null)
			return filelistEntity.getBklist();*/
		List<FileInfo0> flist=null;
		try {
			final FilesListEntity filesListEntity = TClient.getinstance().queryFileList(_ftype, begin, max);
			flist=filesListEntity.getList();
			Collections.sort(flist, new SortBydesc());
		
		} catch (Exception e) {
			e.printStackTrace();
			flist=new ArrayList<FileInfo0>();
		}
		return  flist;
	}

	public FileInfo0 queryLocalInfo(int sysid)
	{
		FileInfo0 fileInfo0=new FileInfo0();
		fileInfo0.setSysid(sysid);
		fileInfo0.setFtype(_ftype);
		if ( dbManager.queryLocalInfo(sysid,fileInfo0))
			return fileInfo0;
		return  null;
	}



	public List<FileLocal> getLocalUnits(int begin, int max) {
		return dbManager.GetPartLocalFiles(MediaFileUtil.FileCategory.Picture, new String[]{"jpg", "png", "gif"}, true, begin, max);
	}

	public FilelistEntity analyPhotoUnits(List<FileInfo0> remotelist) {
		filelistEntity = new FilelistEntity();
		dbManager.GetLocalFiles(MediaFileUtil.FileCategory.Picture, new String[]{"jpg", "png", "gif"}, true);
		dbManager.anlayLocalUnits(remotelist, filelistEntity);
		filelistEntity.setLocallist(dbManager.getLocalUnits());
		return filelistEntity;
	}

	public FilelistEntity analyMusicUnits(List<FileInfo0> remotelist) {
		filelistEntity = new FilelistEntity();
		dbManager.GetLocalFiles(MediaFileUtil.FileCategory.Music, new String[]{"mp3", "wav" }, true);
		dbManager.anlayLocalUnits(remotelist, filelistEntity);
		filelistEntity.setLocallist(dbManager.getLocalUnits());
		return filelistEntity;
	}

	public FilelistEntity analyOtherUnits(List<FileInfo0> remotelist) {
		filelistEntity = new FilelistEntity();
		dbManager.GetLocalFiles(MediaFileUtil.FileCategory.Other, new String[]{"pdf", "xls", "doc","docx"}, true);
		dbManager.anlayLocalUnits(remotelist, filelistEntity);
		filelistEntity.setLocallist(dbManager.getLocalUnits());
		return filelistEntity;
	}

	public FilelistEntity analyUnits(List<FileInfo0> remotelist) {
		filelistEntity = new FilelistEntity();
		//dbManager.GetLocalFiles(MediaFileUtil.FileCategory.Music, new String[]{"mp3", "wav" }, true);
		dbManager.anlayLocalUnits(remotelist, filelistEntity);
		//filelistEntity.setLocallist(dbManager.getLocalUnits());
		return filelistEntity;
	}

	public List<FileInfo0> getDownList(int max){
		dbManager.open();
		List<FileInfo0> lst= dbManager.getUpLoadTask(max);
		dbManager.close();
		return lst;
	}
	protected class SortBydesc implements Comparator<Object> {
		@Override
		public int compare(Object o1, Object o2) {
			if (((FileInfo) o1).getLastModified() > ((FileInfo) o2).getLastModified()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	/*
	activeProcess 对象 实现进度条展现
	beeque  放入数据库 做队列 通过服务方式后台下载
	* */


	public void upload( final FileInfo0 item, final ActiveProcess activeProcess,boolean beeque) {

		if (!item.isSetFtype())
			item.setFtype(_ftype);
		/*if (beeque)
		{
			dbManager.addUpLoadingFile(item);
			return;
		}*/
		try
		{
				dbManager.open();
				dbManager.addUpLoadingFile(item);
				dbManager.close();
				//	return;

		}catch (Exception e)
		{
			e.printStackTrace();
			Log.e(TAG, "upload fail " + e.getMessage());
			activeProcess.setParMessage("上传失败");
			activeProcess.finishProgress();
		}
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				new SyncLocalFileBackground(context).uploadBigFile(item, activeProcess);
			}
		});
		thread.start();
	}

	public void download(final FileInfo0 item, final ActiveProcess activeProcess,boolean beeque) {

		//ProgressBar bar = activity.getProgressBar();
		if (!item.isSetFtype())
			item.setFtype(_ftype);
		/*if (beeque)
		{
			dbManager.addDownloadingFile(item);
			return;
		}*/
		try
		{
			dbManager.open();
			dbManager.addUpLoadingFile(item);
			dbManager.close();
			//	return;

		}catch (Exception e)
		{
			e.printStackTrace();
			Log.e(TAG,"upload fail "+e.getMessage());
			activeProcess.setParMessage("下载失败");
			activeProcess.finishProgress();
		}
		//new Runnable{}
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				new SyncLocalFileBackground(context).downloadBigFile(item, activeProcess);
			}
		});
		thread.start();
		//new SyncLocalFileBackground(context).downloadBigFile(item, activeProcess);
	}


	public boolean DelRemoteObj(FileInfo0 fileInfo0)
	{
		 try {
			return  TClient.getinstance().delObj(fileInfo0.getObjid(),fileInfo0.getFtype());
		} catch (Exception e) {
			e.printStackTrace();
		}
	return false;
	}
}