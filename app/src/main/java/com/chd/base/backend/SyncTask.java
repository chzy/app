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
import com.chd.contacts.vcard.StringUtils;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.service.SyncLocalFileBackground;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.share.ShareUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SyncTask {
	private Context context;
	public  MediaMgr dbManager;
	public List<FileInfo> _CloudList;
	private FilelistEntity filelistEntity;
	private final FTYPE _ftype;
	private final String TAG = this.getClass().getName();
	private SyncLocalFileBackground syncLocalFileBackground;
	private Thread netThread;


	public SyncTask(Context context, FTYPE tp) {
		this.context = context;
		_ftype = tp;

		//_CloudList = new ArrayList<FileInfo>();
		dbManager = new MediaMgr(context, _ftype);
		syncLocalFileBackground = new SyncLocalFileBackground(context);
		//dbManager.open();
	}

	/*public FileInfo getUnitinfo(int id) {
		return filelistEntity.getBklist().get(id);
	}*/


	public void flush() {
		filelistEntity.getBklist().clear();
		//filelistEntity.getUbklist().clear();
		filelistEntity = null;
	}


	public synchronized List<FileInfo> getCloudUnits(int begin, int max) {
		/*if (filelistEntity!=null && filelistEntity.getBklist()!=null)
			return filelistEntity.getBklist();*/
//		List<FileInfo> flist = new ArrayList<>();
		List<FileInfo> flist =  Collections.synchronizedList(new ArrayList<FileInfo>());

		long t1,t0=System.currentTimeMillis();
		try {
			final CloudListEntity cloudListEntity = TClient.getinstance().queryFileList(_ftype, begin, max);
			flist = cloudListEntity.getList();
			if (flist != null) {
				//Collections.sort(flist, new SortBydesc());
			} else
				return new ArrayList<FileInfo>();

		} catch (Exception e) {
			e.printStackTrace();
			flist = new ArrayList<FileInfo>();
		}
		t1=System.currentTimeMillis();
		Log.i(TAG, "getCloudUnits: query remote cost :"+ (t1-t0));
		return flist;
	}

	public FileInfo0 queryLocalInfo(int sysid) {
		FileInfo0 FileInfo = new FileInfo0();
		//FileInfo.setSysid(sysid);
		FileInfo.setFtype(_ftype);
		if ( dbManager.queryLocalInfo(sysid,FileInfo))
			return FileInfo;
		return null;
	}


	public List<FileLocal> getLocalUnits(int begin, int max) {
		return dbManager.GetPartLocalFiles(MediaFileUtil.FileCategory.Picture, new String[]{"jpg", "png", "gif"}, true, begin, max);
	}

	public void analyPhotoUnits(List<FileInfo> remotelist, FilelistEntity filelistEntity) {
		//filelistEntity = new FilelistEntity();
		dbManager.GetLocalFiles(MediaFileUtil.FileCategory.Picture, new String[]{"jpg", "png", "gif"}, true, filelistEntity);
		dbManager.anlayLocalUnits(remotelist, filelistEntity);
		//filelistEntity.setLocallist(dbManager.getLocalUnits());
		//return filelistEntity;
	}

	public List<FileInfo0> QueryLocalFile(String fname)
	{
		return  dbManager.QueryLocalFile(_ftype,fname);
	}


	public void analyVideoUnits(List<FileInfo> remotelist, FilelistEntity filelistEntity) {
		//filelistEntity = new FilelistEntity();
		dbManager.GetLocalFiles(MediaFileUtil.FileCategory.Video, new String[]{"mp4", "3gp"}, true, filelistEntity);
		dbManager.anlayLocalUnits(remotelist, filelistEntity);
		//filelistEntity.setLocallist(dbManager.getLocalUnits());
		//return filelistEntity;
	}

	public void analyRecordUnits(List<FileInfo> remotelist, FilelistEntity filelistEntity) {
		//filelistEntity = new FilelistEntity();
		dbManager.GetLocalFiles(MediaFileUtil.FileCategory.Record, new String[]{"mar", "wav","awb"}, true, filelistEntity);
		dbManager.anlayLocalUnits(remotelist, filelistEntity);
		//filelistEntity.setLocallist(dbManager.getLocalUnits());
		//return filelistEntity;
	}

	public FilelistEntity analyMusicUnits(List<FileInfo> remotelist, FilelistEntity filelistEntity) {
		//filelistEntity = new FilelistEntity();
		dbManager.GetLocalFiles(MediaFileUtil.FileCategory.Music, new String[]{"mp3", "wav", "m4a", "flac", "ape"}, true, filelistEntity);
		dbManager.anlayLocalUnits(remotelist, filelistEntity);
		//filelistEntity.setLocallist(dbManager.getLocalUnits());
		return filelistEntity;
	}

	public FilelistEntity analyOtherUnits(List<FileInfo> remotelist, FilelistEntity filelistEntity) {
		dbManager.GetLocalFiles(MediaFileUtil.FileCategory.Other, new String[]{"pdf", "xls", "doc", "docx"}, true, filelistEntity);
		dbManager.anlayLocalUnits(remotelist, filelistEntity);
//		filelistEntity.setLocallist(dbManager.getLocalUnits());
		return filelistEntity;
	}

	public void analyOtherUnits0(List<FileInfo> remotelist, FilelistEntity filelistEntity) {
		//dbManager.GetLocalFiles(MediaFileUtil.FileCategory.File, new String[]{"pdf", "xls", "doc", "docx"}, true, filelistEntity);
		dbManager.GetLocalFiles0( FTYPE.NORMAL,new String[]{"pdf", "xls", "doc", "docx"}, true, filelistEntity,null);
		dbManager.anlayLocalUnits(remotelist, filelistEntity);
//		filelistEntity.setLocallist(dbManager.getLocalUnits());
		//return filelistEntity;
	}




	public FilelistEntity analyUnits(List<FileInfo> remotelist) {
		filelistEntity = new FilelistEntity();
		//dbManager.GetLocalFiles(MediaFileUtil.FileCategory.Music, new String[]{"mp3", "wav" }, true);
		//dbManager.anlayLocalUnits(remotelist, filelistEntity);
		//filelistEntity.setLocallist(dbManager.getLocalUnits());
		return filelistEntity;
	}

	public List<FileInfo0> getDownList(int max) {
		dbManager.open();
		List<FileInfo0> lst = dbManager.getUpLoadTask(max);
		dbManager.close();
		return lst;
	}


	/*public void MarkBackedItem(  List<FileInfo0> LocalUnits,int offset,int count) {

		long t1, t0 = System.currentTimeMillis();
		FileInfo0 local_item;
		String lclobj;
		FTYPE ftype;
		if (count<LocalUnits.size()-offset-1)
		{
			Log.e(TAG, "anlayLocalUnits: error param count !!!" );
			return;
		}
		for (int idx=offset;idx<count;idx++)
		{
			local_item=LocalUnits.get(idx);
			lclobj=local_item.getObjid();
			ftype=local_item.ftype;
			if (TClient.isBackuped(lclobj,ftype))
				local_item.setBackuped(true);
		}

		t1=System.currentTimeMillis();
		Log.i(TAG, "anlayLocalUnits: compare cost :"+(t1-t0)+" ms");
		return;
	}*/

	public boolean isBacked(  FileInfo0 fileInfo0) {
		boolean ret=false;
		ret=TClient.isBackuped(fileInfo0.getObjid(),_ftype);
		fileInfo0.setBackuped(ret);
		return ret;
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
		dialog = new AlertDialog.Builder(activeProcess)
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
						if (netThread != null) {
							netThread.interrupt();
							dialogInterface.dismiss();
						}
					}
				}).setCancelable(false).create();
		dialog.show();
		netThread = new Thread() {
			@Override
			public void run() {
				int i = 0,ct=files.size();
				ArrayList<Integer> upload = new ArrayList<>();
				try {
					//for (FileInfo0 item : files)

					for (;i<ct;)
					{
						FileInfo0 item= (FileInfo0) files.get(i);
						if (Thread.currentThread().isInterrupted()) {
							throw new InterruptedException();
						}
						i++;
						final String name = item.getObjid();
						final int finalI = i;
						final int process = (int) ((float) (i - 1) / files.size() * 100);
						activeProcess.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dialog.setMessage(name);
								dialog.setTitle("正在上传:" + finalI + "/" + files.size() + "  " + process + "%");
							}
						});
						boolean result = upload(item, activeProcess, false, dialog);
						Log.i("lmj", "第" + i + "个上传状态:" + result);
						if (!result) {
							upload.add(i - 1);
						}
					}

				} catch (Exception e) {
					//中断线程
//					e.printStackTrace();
					Log.e("lmj", "上传中断");
				} finally {
					activeProcess.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dialog.dismiss();
						}
					});
					Message msg = new Message();
					msg.what = 998;
					msg.obj = upload;
					mHandler.sendMessage(msg);
					netThread = null;
				}

			}
		};
		netThread.start();


	}


	/*
	activeProcess 对象 实现进度条展现
	beeque  放入数据库 做队列 通过服务方式后台下载
	* */
	public boolean upload(final FileInfo0 item, final ActiveProcess activeProcess, boolean beeque, AlertDialog dialog) {

		if (!item.isSetFtype())
			item.setFtype(_ftype);
		/*if (beeque)
		{
			dbManager.addUpLoadingFile(item);
			return;
		}*/
		try {
			dbManager.open();
			dbManager.addUpLoadingFile(item);
			dbManager.close();
			//	return;

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "upload fail " + e.getMessage());
			activeProcess.setParMessage("上传失败");
			activeProcess.finishProgress();
		}
		//FileInfo0 info0 = new FileInfo0(item);
		return syncLocalFileBackground.uploadBigFile(item, activeProcess, dialog);
	}

	public void uploadFileOvWrite(final FileInfo0 item, final ActiveProcess activeProcess, boolean beeque) {

		if (!item.isSetFtype())
			item.setFtype(_ftype);
		//FileInfo0 fileInfo0=(FileInfo0) item;
		try {
			dbManager.open();
			dbManager.addUpLoadingFile(item);
			dbManager.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "upload fail " + e.getMessage());
			activeProcess.setParMessage("上传失败");
			activeProcess.finishProgress();
		}
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				FileInfo0 fileInfo0 = new FileInfo0(item);
				new SyncLocalFileBackground(context).uploadFileOvWrite(fileInfo0, activeProcess, null, null);
			}
		});
		thread.start();
	}


	/*
	activeProcess 对象 实现进度条展现
	beeque  放入数据库 做队列 通过服务方式后台下载
	* */
	public void downloadList(final List<FileInfo> files, final ActiveProcess activeProcess, final Handler mHandler) {
		dialog = new AlertDialog.Builder(activeProcess)
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
						if (netThread != null) {
							netThread.interrupt();
							dialogInterface.dismiss();
						}
					}
				}).setCancelable(false).create();
		dialog.show();
		netThread = new Thread() {
			@Override
			public void run() {
				int i = 0;
				ArrayList<Integer> download = new ArrayList<>();
				try {
					for (FileInfo item :
							files) {
						if (Thread.currentThread().isInterrupted()) {
							throw new InterruptedException();
						}
						final String name = item.getObjid();
						i++;
						final int finalI = i;
						final int process = (int) ((float) (i - 1) / files.size() * 100);
						activeProcess.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dialog.setMessage(name);
								dialog.setTitle("正在下载:" + finalI + "/" + files.size() + "  " + process + "%");
							}
						});
						boolean result = download(item, activeProcess, false, dialog);
						if (!result) {
							download.add(i);
						}

					}
				} catch (Exception e) {
					//中断线程
					e.printStackTrace();
					Log.e("lmj", "下载中断");
					return;
				} finally {
					activeProcess.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dialog.dismiss();
						}
					});
					Message msg = new Message();
					msg.what = 997;
					msg.obj = download;
					mHandler.sendMessage(msg);
					netThread = null;
				}
			}
		};
		netThread.start();
	}


	public boolean download(final FileInfo item1, final ActiveProcess activeProcess, boolean beeque, AlertDialog dialog) {

		//ProgressBar bar = activity.getProgressBar();
		FileInfo0 item = (FileInfo0) item1;
		if (!item.isSetFtype())
			item.setFtype(_ftype);
		if (StringUtils.isNullOrEmpty(item.getFilePath())) {
			String url = "";
			FTYPE ftype = item.getFtype();
			switch (ftype) {
				case PICTURE:
					url = new ShareUtils(activeProcess).getPhotoFile().getPath() + "/" + item.getObjid();
					break;
				case NORMAL:
					url = new ShareUtils(activeProcess).getNormalFile().getPath() + "/" + item.getObjid();
					break;
				case SMS:
					url = new ShareUtils(activeProcess).getSmsFile().getPath() + "/" + item.getObjid();
					break;
				case ADDRESS:
					url = new ShareUtils(activeProcess).getContactFile().getPath() + "/" + item.getObjid();
					break;
				case DFlOW:
					url = new ShareUtils(activeProcess).getDFLOWFile().getPath() + "/" + item.getObjid();
					break;
				case STORE:
					url = new ShareUtils(activeProcess).getStorePath().getPath() + "/" + item.getObjid();
					break;
				case MUSIC:
					url = new ShareUtils(activeProcess).getMusicFile().getPath() + "/" + item.getObjid();
					break;
				case VIDEO:
					url = new ShareUtils(activeProcess).getVideoFile().getPath() + "/" + item.getObjid();
					break;
				case RECORD:
					url = new ShareUtils(activeProcess).getRecordFile().getPath() + "/" + item.getObjid();
					break;

			}
			item.setFilePath(url);

		}
		/*if (beeque)
		{
			dbManager.addDownloadingFile(item);
			return;
		}*/
		try {
			dbManager.open();
			dbManager.addUpLoadingFile(item);
			dbManager.close();
			//	return;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "upload fail " + e.getMessage());
			activeProcess.setParMessage("下载失败");
			activeProcess.finishProgress();
		}
		return syncLocalFileBackground.downloadBigFile(item, activeProcess, dialog);
	}


	public boolean DelRemoteObj(FileInfo FileInfo) {
		try {
			FTYPE ftype = FileInfo.getFtype() == null ? this._ftype : FileInfo.getFtype();
			return TClient.getinstance().delObj(FileInfo.getObjid(), ftype);
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
		dialog = new AlertDialog.Builder(activeProcess)
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
						if (netThread != null) {
							netThread.interrupt();
							dialogInterface.dismiss();
						}
					}
				}).setCancelable(false).create();
		dialog.show();
		netThread = new Thread() {
			@Override
			public void run() {
				int i = 0;
				ArrayList<Integer> del = new ArrayList<>();
				try {
					for (FileInfo item : files) {
						boolean result;
						final String name = item.getObjid();
						i++;
						final int finalI = i;
						final int process = (int) ((float) (i - 1) / files.size() * 100);
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
							FileLocal fileLocal = (FileLocal) item;
							File f = new File(UILApplication.getFilelistEntity().getDirPath(fileLocal.getPathid()) + File.separator + fileLocal.getObjid());
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
				} catch (Exception E) {
					//中断线程
					Log.e("lmj", "删除中断");
					return;
				} finally {
					activeProcess.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dialog.dismiss();
						}
					});
					Message msg = new Message();
					msg.what = 996;
					msg.obj = del;
					mHandler.sendMessage(msg);
					netThread = null;
				}

			}
		};
		netThread.start();
	}

}