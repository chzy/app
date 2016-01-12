package com.chd.yunpan.utils;

import android.os.Handler;
import android.os.Message;

import com.chd.TClient;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.parse.entity.FileDataFatherEntity;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.ui.fragment.FileListFragment;

public class RenameRun implements Runnable {
	private String name;
	private String id;
	private FileListFragment context;
	private int t;
	protected ShareUtils shareutils;
	private String fname;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				context.refresh();
				ToastUtils.toast(context.getActivity(), "修改成功");
				break;

			case -1:
				ToastUtils.toast(context.getActivity(), "修改失败");
				break;
			}
		}
	};
	//private FileDataFatherEntity fileLocalFile;

	public RenameRun(int type, final FileListFragment  context, int id, String name,
			Handler handler, FileDataFatherEntity file) {
		//this.fileLocalFile = file;
		this.t = type;
		this.context = context;
		//this.id = id;
		this.name = name;

	}

	public RenameRun(final FileListFragment frame, String newname, Handler handler, FileInfo0 dataEntity) {
		this.context = context;
		shareutils=new ShareUtils(frame.getActivity());
		//this.fileLocalFile = dataEntity.getfPath(shareutils.getStorePathStr());
		//this.t = type;
		this.fname=dataEntity.getFilePath();
		this.id = dataEntity.getObjid();
		this.name = newname;
	}

	@Override
	public void run() {
		if (t == 1) {
			
			//AlterFileEntity file = AlterFile.down(context.getActivity(), id, name, null);
			boolean file= false;
			try {
				file = TClient.getinstance().Renamefile(this.fname,name,FTYPE.ADDRESS);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if(file/*!=null*/){
				
				//ToastUtils.toast(context.getActivity(), "修改成功");
				handler.sendEmptyMessage(1);
			}else {
				//ToastUtils.toast(context.getActivity(), "修改失败");
				handler.sendEmptyMessage(-1);
			}
		}else/* if (t == 2)*/ {

				/*AlterFolderEntity entity = AlterFolder
					.down(context.getActivity(), id, name, null);
				if(entity!=null){
				//	ToastUtils.toast(context.getActivity(), "修改成功");
					handler.sendEmptyMessage(1);
			}else {
				//ToastUtils.toast(context.getActivity(), "修改失败");
				handler.sendEmptyMessage(-1);
			}*/
			assert (false);
		}

	}

}
