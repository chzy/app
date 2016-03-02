package com.chd.yunpan.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chd.TClient;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.db.DBManager;
import com.chd.yunpan.net.ExecRunable;
import com.chd.yunpan.net.RequestParam;
import com.chd.yunpan.share.ShareUtils;

import java.util.List;

/*import com.chd.yunpan.parse.entity.FileDataDBEntity;
import com.chd.yunpan.parse.entity.FileDataEntity;*/
//import com.chd.yunpan.parse.entity.FileDirDataEntity;
//import com.chd.yunpan.parse.entity.FilesListEntity;
//import com.chd.yunpan.utils.FileDownLoadLinkedUtil;

public abstract class BaseFragment extends Fragment {
	DBManager dbManager ;

	ShareUtils shareUtils;
	/**
	 * 时间排序
	 */
	public static final String USER_PTIME = "user_ptime";
	/**
	 * 大小排序
	 */
	public static final String FILE_SIZE="file_size";
	/**
	 * 文件名
	 */
	public static final String FILE_NAME = "file_name";
	
	/**
	 * 降序
	 */
	public static final int DESC = 1;
	
	/**
	 * 升序
	 */
	public static final int ASC = 0;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		dbManager= new DBManager(getActivity());

		dbManager.open();
		shareUtils=new ShareUtils(getActivity());
		return createView(inflater);
	}
	/**
	 * 着这个方法中设置布局
	 */
	public abstract View createView(LayoutInflater inflater);
	
	/**
	 * 
	 */
	public abstract void initViews();

	
	/**
	 * 
	 */
	public abstract void setLogic();
	
	public abstract void setAdapter();
	
	// 联网
	/*public void lianwang(RequestParam qinQiu,OnLianWangFinishLisenter lisenter){
		MyHandler handler = new MyHandler(lisenter);
		//启动线程，执行

		ExecRunable.execRun(new MyThread(qinQiu,handler));


	}*/

	public void lianwang(RequestParam param0,OnLianWangFinishLisenter lisenter){
		MyHandler handler = new MyHandler(lisenter);
		//启动线程，执行
		ExecRunable.execRun(new MyThread(param0,handler));


	}

	public class MyHandler extends Handler {
		OnLianWangFinishLisenter lisenter;
		public MyHandler(OnLianWangFinishLisenter lisenter) {
			// TODO Auto-generated constructor stub
			this.lisenter = lisenter;
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0://网络访问成功
				if(msg.obj!=null){
					lisenter.onFinish(msg.obj);
				}else {
					lisenter.onError(1);
				}
				break;

			case -1://联网失败
				lisenter.onError(msg.arg1);
				break;
			}
		}
	}

	@Override
	public void onActivityCreated( Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initViews();
		setLogic();
		setAdapter();
		
	}
	
	
	public class MyThread extends Thread {

		MyHandler handler;
		RequestParam param;
		Message message ;
		public MyThread(RequestParam param0,MyHandler handler) {
			// TODO Auto-generated constructor stub
			this.handler = handler;
			this.param = param0;

		}
		
		@Override
		public void run() {
			Object result = null;
			TClient tclient =null ;
			Message message = new Message();
			//if(qinQiu.method==RequestParam.GET)
			{
				//get
				try {
					tclient=TClient.getinstance();

					switch (param.getMethod()) {
						// 获取文件列表
						case  1:
							 result = tclient.queryFileList(param.getFtype(),0,0);
							break;
						//删除文件
						case 2:
							//result= tclient.delObj(param.getList());
							break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			//联网结束
			if(result==null){
				message.what = -1;
				message.arg1 = 404;
				handler.sendMessage(message);
				return;
			}

			message.what=0;
			message.obj = result;
			handler.sendMessage(message);

			
		}
	}
	/*public class MyThread extends Thread {
		RequestParam qinQiu ;
		MyHandler handler;
		public MyThread(RequestParam qinQiu,MyHandler handler) {
			// TODO Auto-generated constructor stub
			this.qinQiu = qinQiu;
			this.handler = handler;
		}

		@Override
		public void run() {
			String result = null;
			ShareUtils utils = new ShareUtils(getActivity());
			//if(qinQiu.method==RequestParam.GET)
			{
				//get
				try {

					// 获取文件列表
					result = HttpUtils.GetStringForHttpGet(utils.getCookieUtil(), qinQiu.pairs, qinQiu.url, 1);

					TClient.getinstance().queryfile()
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//post
			*//*else {

				try {
					result = HttpUtils.GetStringForHttpPost(utils.getCookieUtil(), qinQiu.pairs, qinQiu.url, 1);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}*//*
			//联网结束
			if(result==null){
				Message message = new Message();
				message.what = -1;
				message.arg1 = 404;
				handler.sendMessage(message);
				return;
			}
			//解析
			try{
				Object obj = qinQiu.parse.parse(result);
				Message m1 = new Message();
				m1.what=0;
				m1.obj = obj;
				handler.sendMessage(m1);
			}catch (Exception e) {
				Message m = new Message();
				m.what = -1;
				m.arg1 = -1;
				handler.sendMessage(m);
			}

		}
	}*/

	public interface OnLianWangFinishLisenter<T>{
		void onFinish(T t);
		void onError(int errorCode);
	}

	//替换新的 api
	/*public RequestParam getFileList(int type,int offset,int cid,String order,int ascOrdesc){
		RequestParam param = new RequestParam();
		ShareUtils utils = new ShareUtils(getActivity());
		if(type==-1){
			param.url = utils.getURL()+"/a1/index?ct=list&aid=1&cid="+cid+"&o="+order+"&asc="+ascOrdesc+"&offset="+offset+"&limit=100";
		}else {
			param.url = utils.getURL()+"/a1/index?ct=list&aid=1&cid="+cid+"&o="+order+"&asc="+ascOrdesc+"&offset="+offset+"&limit=100&type="+type;
		}
		param.method = RequestParam.GET;
		param.parse = new FileListParse();
		return param;
	}*/


	public RequestParam getFileList(FTYPE ftype,int offset) {
		RequestParam param = new RequestParam();
		param.setMethod(1);
		param.setBegin(offset);
		param.setFtype(ftype);
		return param;
	}

	public abstract boolean onBack();



/*
	public void down(FilesListEntity entity,FileDataEntity entity2){
		final FileInfo0 entity3  = entity2.getFileDataDBEntity();
		entity3.setFilePath(getfPath(entity));
		dbManager.addDownloadingFile(entity3);
		downFileFork();
	}
*/

	public void down(FileInfo0 entity3){
		/*final FileInfo0 entity3 = entity2.getFileDataDBEntity();
		entity3.setFilePath(getfPath(entity));*/
		dbManager.addDownloadingFile(entity3);
		downFileFork();
	}


	/*public String getPrefixPath() {
		StringBuffer buf = new StringBuffer();
		buf.append(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/" + new ShareUtils(getActivity()).getLoginEntity().getId() + "/");
		*//*for(int i=0;i<entity.getFilePathEntities().size();i++){
			buf.append(entity.getFilePathEntities().get(i).getName()+"/");
		}*//*
		return buf.toString();
	}*/

	/*public String getfPath(FilesListEntity entity){
		StringBuffer buf = new StringBuffer();
		buf.append(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+getResources().getString(R.string.app_name)+"/"+new ShareUtils(getActivity()).getLoginEntity().getId()+"/");
		for(int i=0;i<entity.getFilePathEntities().size();i++){
			buf.append(entity.getFilePathEntities().get(i).getName()+"/");
		}
		return buf.toString();
	}*/
	
	public void down(final /* FileDirDataEntity */ FileInfo0 entity2,int offset)
	{
		//RequestParam param = getFileList(-1, offset, entity2.getCid(), FILE_NAME, ASC);

		//FTYPE ftype = null;

		lianwang(getFileList(FTYPE.NORMAL, offset), new OnLianWangFinishLisenter<FileInfo0>() {

			@Override
			public void onFinish(FileInfo0 t) {

				downR(t,t.getOffset());

			}

			@Override
			public void onError(int errorCode) {

			}
		});
	}
	
	public void downFileFork(){
		
		ExecRunable.execDwon(new Runnable() {
			public void run() {
				int fid = 0;
				while (dbManager.getDownloadingFiles().size() > 0) {
					List<FileInfo0> entities = dbManager.getDownloadingFiles();
					for (FileInfo0 entity : entities)
					{
						try {
							System.out.println("开始瞎子啊了");

							downR(entity,entity.getOffset());

							System.out.println("下载完成");
							//}
						} catch (Exception e) {
							System.out.println("下载异常");
							//dbManager.deleteDownloadingFile(entity.getObjid());
						}
					}
				}
			}
		});
	}
	
	/**
	 * 在子线程中执行
	 *
	 * @param offset
	 * @param entity
	 */
	/*public void downR(File f,String url1,FileDataDBEntity entity)*/
	public void downR(FileInfo0 entity,long offset)
	{
		shareUtils.getStorePath();

		try {

			/*if (TClient.getinstance().downloadfile(new File(shareUtils.getStorePath(),entity.getFilename()), entity.getObjid())) {
				//dbManager.updateDownloadingFile(entity);
				dbManager.addDownloadedFile(entity);
				dbManager.deleteDownloadingFile(entity.getObjid());

			}*/
		} /*catch (IOException e) {
			e.printStackTrace();
		}*/ catch (Exception e) {
			e.printStackTrace();
		}


	}


	public abstract void pageChange();
}
