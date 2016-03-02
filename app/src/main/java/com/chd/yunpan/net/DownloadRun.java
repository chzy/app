package com.chd.yunpan.net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.chd.proto.FileInfo0;
import com.chd.yunpan.db.DBManager;

import java.util.List;

public class DownloadRun implements Runnable {
	private int id = 0;
	private int t = 0;
	private Context context;
	private Handler handler;
	private Message msg;
	private DownloadRun thread;
	private List<FileInfo0> fileInfoList;
	private DBManager dbManager;

	private Handler threadHandler = null;

	private DownloadRun(int t, int id, Context context, Handler handler,
			List<FileInfo0> file) {

		this.id = id;
		this.fileInfoList = file;
		this.t = t;
		this.thread = this;
		this.context = context;
		this.handler = handler;
		
	}

	


	public DownloadRun() {
		super();
		// TODO Auto-generated constructor stub
	}




	public static DownloadRun getDownLoadRun() {
		return new DownloadRun();
	}

	public Handler getThreadHandler() {
		return threadHandler;
	}

	
	public void addToDB(  Context context, Handler handler, List<FileInfo0> file)
	{
		//this.id = id;
		this.fileInfoList = file;
		//this.t = t;
		this.thread = this;
		this.context = context;
		this.handler = handler;
		dbManager = new DBManager(context);
		dbManager.open();
		new Thread(this).start();
		

	}
	
	

	

	public void add() {
		//下载 列表
		/*String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Environment.DIRECTORY_DCIM;
		              Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/" + new ShareUtils(getActivity()).getLoginEntity().getId() + "/";
*/
		//for (int i = 0; i < fileInfoList.size(); i++)

		for (FileInfo0 f: fileInfoList)
		{
			/*FileDataDBEntity dataDBEntity = fileInfoList.get(i).getFileDataDBEntity();
			dataDBEntity.setFilePath(path);*/
			dbManager.addDownloadingFile(/*dataDBEntity*/f );
		}
		msg = new Message();
		msg.what = 1;
		handler.sendMessage(msg);
	}

	@Override
	public void run() {
					add();
	}

}
