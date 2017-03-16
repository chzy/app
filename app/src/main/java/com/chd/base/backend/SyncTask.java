package com.chd.base.backend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.chd.Entity.CloudListEntity;
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
import com.chd.yunpan.application.UILApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class SyncTask {
	private Context context;
	MediaMgr dbManager;
	public List<FileInfo> _CloudList;
	private FilelistEntity filelistEntity;
	private final FTYPE _ftype;
	private final String TAG=this.getClass().getName();
	private SyncLocalFileBackground syncLocalFileBackground;
	private Thread netThread;


	public SyncTask(Context context,FTYPE tp) {
		this.context = context;
		_ftype=tp;

		_CloudList=new ArrayList<FileInfo>();
		dbManager = new MediaMgr(context,_ftype);
		syncLocalFileBackground =new SyncLocalFileBackground(context);
		//dbManager.open();
	}

	public FileInfo getUnitinfo(int id) {
		return filelistEntity.getBklist().get(id);
	}

	//查询远程对象是否有本地副本. 根据文件名匹配
	public boolean haveLocalCopy(FileInfo info0) {
		boolean ret=false;
		if (info0.getObjid()==null)
		{
			Log.d(TAG, "not remote file obj");
			return false;
		}
	/*	if (info0.getSysid() > 0)
		{
			if (info0.getFilePath()!=null && info0.getFilePath().indexOf(".")>1 ) {
					File file=new File(info0.getFilePath());
				   return  (file.exists() && file.isFile());
			}
			if (info0.getFtype()==null)
				info0.setFtype(_ftype);
			int time=info0.getLastModified();
			ret=   dbManager.queryLocalInfo(info0.getSysid(),info0);
			*//*
			临时方案 恢复成远程的上传时间
			* *//*
			if (ret)
				info0.setLastModified(time);
		}*/

		return ret;


	}




	public void flush() {
		filelistEntity.getBklist().clear();
		//filelistEntity.getUbklist().clear();
		filelistEntity = null;
	}

	public synchronized List<FileInfo> getCloudUnits(int begin, int max) {
		/*if (filelistEntity!=null && filelistEntity.getBklist()!=null)
			return filelistEntity.getBklist();*/
		List<FileInfo> flist=new ArrayList<>();
		try {
			final CloudListEntity cloudListEntity = TClient.getinstance().queryFileList(_ftype, begin, max);
			flist= cloudListEntity.getList();
			if (flist!=null) {
				//Collections.sort(flist, new SortBydesc());
			}
			else
				return new ArrayList<FileInfo>();

		} catch (Exception e) {
			e.printStackTrace();
			flist=new ArrayList<FileInfo>();
		}
		return  flist;
	}

	public FileInfo queryLocalInfo(int sysid)
	{
		FileInfo FileInfo=new FileInfo();
		//FileInfo.setSysid(sysid);
		FileInfo.setFtype(_ftype);
		//if ( dbManager.queryLocalInfo(sysid,FileInfo))
		//	return FileInfo;
		return  null;
	}



	public List<FileLocal> getLocalUnits(int begin, int max) {
		return dbManager.GetPartLocalFiles(MediaFileUtil.FileCategory.Picture, new String[]{"jpg", "png", "gif"}, true, begin, max);
	}

	public void analyPhotoUnits(List<FileInfo> remotelist,FilelistEntity filelistEntity) {
		//filelistEntity = new FilelistEntity();
		dbManager.GetLocalFiles(MediaFileUtil.FileCategory.Picture, new String[]{"jpg", "png", "gif"}, true,filelistEntity);
		dbManager.anlayLocalUnits( remotelist ,filelistEntity);
		//filelistEntity.setLocallist(dbManager.getLocalUnits());
		//return filelistEntity;
	}

	public FilelistEntity analyMusicUnits(List<FileInfo> remotelist,FilelistEntity filelistEntity) {
		//filelistEntity = new FilelistEntity();
		dbManager.GetLocalFiles(MediaFileUtil.FileCategory.Music, new String[]{"mp3", "wav","m4a","flac","ape" }, true,filelistEntity);
//		dbManager.anlayLocalUnits(remotelist, filelistEntity);
		//filelistEntity.setLocallist(dbManager.getLocalUnits());
		return filelistEntity;
	}

	public FilelistEntity analyOtherUnits(List<FileInfo> remotelist, FilelistEntity filelistEntity) {
		//filelistEntity = new FilelistEntity();
		dbManager.GetLocalFiles(MediaFileUtil.FileCategory.Other, new String[]{"pdf", "xls", "doc","docx"}, true,filelistEntity);
		//dbManager.anlayLocalUnits(remotelist, filelistEntity);
		//filelistEntity.setLocallist(dbManager.getLocalUnits());
		return filelistEntity;
	}

	public FilelistEntity analyUnits(List<FileInfo> remotelist) {
		filelistEntity = new FilelistEntity();
		//dbManager.GetLocalFiles(MediaFileUtil.FileCategory.Music, new String[]{"mp3", "wav" }, true);
		//dbManager.anlayLocalUnits(remotelist, filelistEntity);
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

	AlertDialog dialog;
	/*
	activeProcess 对象 实现进度条展现
	beeque  放入数据库 做队列 通过服务方式后台下载
	* */
	public void uploadList(final List<FileInfo> files, final ActiveProcess activeProcess, final Handler mHandler) {
		dialog=new AlertDialog.Builder(activeProcess)
				.setTitle("正在上传")
				.setMessage("")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				})
				.setPositiveButton("停止", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						if(netThread!=null){
							netThread.interrupt();
							dialogInterface.dismiss();
						}
					}
				}).setCancelable(false).create();
		dialog.show();
		netThread=new Thread(){
			@Override
			public void run() {
				int i=0;
				ArrayList<Integer> upload=new ArrayList<>();
				try{
					for (FileInfo item1 : files) {
						FileInfo0 item=new FileInfo0(item1);
						if(Thread.currentThread().isInterrupted()){
							throw new InterruptedException();
						}
						i++;
						final String name=item.getFilename();
						final int finalI = i;
						final int process= (int) ((float)(i-1)/files.size()*100);
						activeProcess.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Log.d("liumj","文件名:"+name);
								dialog.setMessage(name);
								dialog.setTitle("正在上传:"+ finalI +"/"+files.size()+"  "+process+"%");
							}
						});
						boolean result = upload(item, activeProcess, false,dialog);
						Log.i("lmj","第"+i+"个上传状态:"+result);
						if(!result){
							upload.add(i-1);
						}
					}

				}catch (Exception e){
					//中断线程
					Log.e("lmj","上传中断");
					return ;
				}finally {
					activeProcess.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dialog.dismiss();
						}
					});
					Message msg=new Message();
					msg.what=998;
					msg.obj=upload;
					mHandler.sendMessage(msg);
					netThread=null;
				}

			}
		};
		netThread.start();


	}



	/*
	activeProcess 对象 实现进度条展现
	beeque  放入数据库 做队列 通过服务方式后台下载
	* */
	public boolean upload(final FileInfo item, final ActiveProcess activeProcess, boolean beeque, AlertDialog dialog) {

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
		FileInfo0 info0=new FileInfo0(item);
		return syncLocalFileBackground.uploadBigFile(info0, activeProcess,dialog);
	}

	public void uploadFileOvWrite(final FileInfo item, final ActiveProcess activeProcess, boolean beeque) {

		if (!item.isSetFtype())
			item.setFtype(_ftype);

		try
		{
			dbManager.open();
			dbManager.addUpLoadingFile(item);
			dbManager.close();
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
				FileInfo0 fileInfo0=new FileInfo0(item);
				new SyncLocalFileBackground(context).uploadFileOvWrite(fileInfo0, activeProcess,null,null);
			}
		});
		thread.start();
	}


	/*
	activeProcess 对象 实现进度条展现
	beeque  放入数据库 做队列 通过服务方式后台下载
	* */
	public void downloadList(final List<FileInfo> files, final ActiveProcess activeProcess, final Handler mHandler) {
		dialog=new AlertDialog.Builder(activeProcess)
				.setTitle("正在下载")
				.setMessage("")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				})
				.setPositiveButton("停止", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						if(netThread!=null){
							netThread.interrupt();
							dialogInterface.dismiss();
						}
					}
				}).setCancelable(false).create();
		dialog.show();
		netThread=new Thread(){
			@Override
			public void run() {

				int i=0;
				ArrayList<Integer> download=new ArrayList<>();
				try{
					for (FileInfo item :
							files) {
						if(Thread.currentThread().isInterrupted()){
							throw new InterruptedException();
						}
						final String name=item.getObjid();
						i++;
						final int finalI = i;
						final int process= (int) ((float)(i-1)/files.size()*100);
						activeProcess.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Log.d("lmj","文件名:"+name);
								dialog.setMessage(name);
								dialog.setTitle("正在下载:"+ finalI +"/"+files.size()+"  "+process+"%");
							}
						});
						boolean result = download(item, null, false,dialog);
//					if(!result){
//						download.add(i);
//					}

					}
				}catch (Exception e){
					//中断线程
					Log.e("lmj","下载中断");
					return ;
				}finally {
					activeProcess.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dialog.dismiss();
						}
					});
					Message msg=new Message();
					msg.what=997;
					msg.obj=download;
					mHandler.sendMessage(msg);
					netThread=null;
				}
			}
		};
		netThread.start();
	}






	public boolean download(final FileInfo item1, final ActiveProcess activeProcess, boolean beeque, AlertDialog dialog) {

		//ProgressBar bar = activity.getProgressBar();
		FileInfo0 item=(FileInfo0) item1;
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
		return syncLocalFileBackground.downloadBigFile(item, activeProcess,dialog);
	}


	public boolean DelRemoteObj(FileInfo FileInfo)
	{
		try {
			FTYPE ftype=FileInfo.getFtype()==null?this._ftype:FileInfo.getFtype();
			return  TClient.getinstance().delObj(FileInfo.getObjid(),ftype);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}



	/*
	activeProcess 对象 实现进度条展现
	beeque  放入数据库 做队列 通过服务方式后台下载
	* */
	public void delList(final List<FileInfo> files, final ActiveProcess activeProcess, final Handler mHandler, final boolean bIsUbkList) {
		dialog=new AlertDialog.Builder(activeProcess)
				.setTitle("正在删除")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				})
				.setPositiveButton("停止", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						if(netThread!=null){
							netThread.interrupt();
							dialogInterface.dismiss();
						}
					}
				}).setCancelable(false).create();
		dialog.show();
		netThread=new Thread(){
			@Override
			public void run() {
				int i=0;
				ArrayList<Integer> del=new ArrayList<>();
				try {
					for (FileInfo item : files) {
						boolean result;
						final String name = item.getObjid();
						i++;
						final int finalI = i;
						final int process = (int) ((float) (i-1) / files.size() * 100);
						activeProcess.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Log.d("lmj", "文件名:" + name);
								dialog.setMessage(name);
								dialog.setTitle("正在删除" + finalI + "/" + files.size() + "  " + process + "%");
							}
						});
						if (bIsUbkList) {
							//是未备份
							FileLocal fileLocal=(FileLocal) item;
							File f = new File(UILApplication.getFilelistEntity().getFilePath(fileLocal.getPathid())+File.pathSeparator+fileLocal.getObjid());
							result = f.delete();
							if (result) {
								Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f));
								activeProcess.sendBroadcast(media);
							}
						} else {
							result = DelRemoteObj(item);
						}
						Log.d("lmj", "第" + i + "删除状态:" + result);
						if (!result) {
							del.add(i - 1);
						}
					}
				}catch (Exception E){
					//中断线程
					Log.e("lmj","删除中断");
					return ;
				}finally {
					activeProcess.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dialog.dismiss();
						}
					});
					Message msg=new Message();
					msg.what=996;
					msg.obj=del;
					mHandler.sendMessage(msg);
					netThread=null;
				}

			}
		};
		netThread.start();
	}

}