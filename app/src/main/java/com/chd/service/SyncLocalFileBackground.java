package com.chd.service;

import android.content.Context;
import android.util.Log;

import com.chd.MediaMgr.utils.MediaFileUtil;
import com.chd.TClient;
import com.chd.Transform.InputTrasnport;
import com.chd.base.MediaMgr;
import com.chd.base.Ui.ActiveProcess;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.net.BigFileOrBreakPointUploadUtil;
import com.chd.yunpan.net.NetworkUtils;

import org.apache.thrift.TException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncLocalFileBackground implements Runnable {

	List<FileInfo0> files = new ArrayList<FileInfo0>();

	private MediaMgr su = null;
	public final String classname =this.getClass().getName();
	private Context context = null;
	private FileInfo0 _item;
	private  final String Tag="SyncLocal";
	private final  int readbuflen=1024*10;

	/**
	 * _itme!=null：立即上传  _item==null：自动备份0
	 * */
	//private int upLoadType = -1;

	/**
	 * 0：立即上传 1：自动备份
	 * */
	public SyncLocalFileBackground(Context context) {
		this.context = context;
		//this.upLoadType = upLoadType;
		su = new MediaMgr(context);
		//su.open();
	}

	/*public SyncLocalFileBackground(Context context,FileInfo0 fileInfo0) {
		this.context = context;
		su = new MediaMgr(context);
		//su.open();
	}*/


	public SyncLocalFileBackground(Context context,int upLoadType) {
		this.context = context;
		//this.upLoadType = upLoadType;
		su = new MediaMgr(context);
		//su.open();
	}

	public void run() {
		upLoad();
		//su.close();

	}

	// 找到所有需要上传的列表
	private void findList2UpLoad() {
		files = su.getUpLoadTask(100);

	}

	// 递归上传所有
	private void upLoad() {
		su.open();
		findList2UpLoad();
		if(files.size()==0){
			su.close();
			return ;
		}


		for(FileInfo0 item:files) {
			if (!NetworkUtils.isNetworkAvailable(context)) {
				break;
			}
			if (upLoadFile(item)) {
				//su.deleteUpLoadingFile(item.getObjid());
				su.addUpLoadedFile(item);
			} else {
				//su.deleteUpLoadingFile(item.getObjid());
			}
		su.close();
		}
	}

	private boolean upLoadFile(FileInfo0 file) {
		Log.v(classname, file.getObjid() + "------------------" + file.getFilePath());
		boolean b = new BigFileOrBreakPointUploadUtil()
				.uploadBigFile(file,null, context);
		System.out.println(b);
		System.gc();
		return b;
	}


	public boolean downloadBigFile(FileInfo0 fileInfo0, ActiveProcess pb)
	{
		int offset=0;
		int readlen=0,remain=0,total=0;
		FileInputStream fis = null;
		FileOutputStream os =null;
		if (fileInfo0.getFilesize()<1) {
			Log.e(Tag,"invalid remote obj size 0");
			return false;
		}
		File f=new File(fileInfo0.getFilePath());
		if (  f.isDirectory()) { //resume download

				return false;
		}
		else {// download new file
			try {
				f.createNewFile();
				//os =new FileOutputStream(f);
			} catch (IOException e) {
				e.printStackTrace();
				return  false;
			}
		}
		if(!NetworkUtils.isNetworkAvailable(context)){
			return false;
		}
		offset=(int)f.length() ;
		InputTrasnport inputTrasnport=new InputTrasnport(fileInfo0.getObjid(),fileInfo0.getFtype());
		try {
			os =new FileOutputStream(f);
			total=inputTrasnport.getobjlength().intValue();
			if (total<0) {
				Log.e(classname, " obj length invild");
				return false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (TException e) {
			e.printStackTrace();
			Log.w(classname," query objlenth fail ");
		}


		byte[] buffer=new byte[readbuflen];
		remain = total - offset;
		if (pb!=null) {
			pb.setMaxProgress(100);
			pb.setProgress((remain) / total * 100);
		}
		if(inputTrasnport==null)
			Log.e(classname,"open inputstrnsport fail");
		while (  (readlen=inputTrasnport.read(buffer,offset,readbuflen)) >-1)
		{
			try {
				os.write(buffer,0,readlen);
				offset+=readlen;
				Log.d(classname,"read:"+offset+" bytes");
				if (pb!=null)
					pb.setProgress((remain - readlen) / total * 100);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(classname,e.getMessage());
				break;
			}
		}
		try {
			os.flush();
			os.close();
		}catch (IOException e)
		{
			Log.e(classname, e.getMessage());
			f.deleteOnExit();
			return false;
		}
		MediaMgr.fileScan(fileInfo0.getFilePath(),context);
		return  true;
	}


	public boolean uploadBigFile(FileInfo0 entity,final ActiveProcess activeProcess) {

		TClient tClient=null;
		File file;
		long size=entity.getFilesize();
		if(!NetworkUtils.isNetworkAvailable(context)){
			return false;
		}
		try {
			tClient= TClient.getinstance();
		} catch (Exception e) {
			Log.w("@@@",e.getLocalizedMessage());
			return false;
		}
		file = new File(entity.getFilePath());
		if (!file.exists() ||  !file.isFile())
			return false;
		if (size<1) {
			size = file.length();
			entity.setFilesize(size);
		}
		if (size<1)
			return  false;

		System.out.println("开始上传喽");
			//先检查 云端是否 有同名文件
			long start=0;
			//是否 需要查询 服务器端是否有同名的 未传完的 文件
			FileInfo fileInfo=tClient.queryFile(entity);
			//fileInfo=null;
			su.open();
			if (fileInfo!=null  ) {
				if (activeProcess != null) {
					activeProcess.setMaxProgress(100);
				}
				Log.e(classname,"upload file invliad");
				return false;
			}
			else
			{
				long oft=tClient.queryUpObjOffset(entity);
				if ( (oft>0))
					start =/*entity.getOffset()*/oft;
				else
				{
					if (oft<0)
					{
						su.close();
						Log.e(classname," query obj failed ");
						return false;
					}
				}
				{
				if (activeProcess!=null)
					activeProcess.setProgress((int) (start / size * 100));
				}
			}
			int len=0;
			byte[] buffer=new byte[1024];
			boolean succed=false;
			TClient.TFilebuilder filebuilder=null;
			try {
					String fname=entity.getFilename()==null? MediaFileUtil.getFnameformPath(entity.getFilePath()):entity.getFilename();
					filebuilder = tClient.new TFilebuilder(fname,entity.getFtype());
					String objid=null;
					if (start==0) {
						objid = filebuilder.ApplyObj();
						if (objid == null) {
							Log.e(Tag,"alloc obj failed ");
							return false;
						}
						entity.setObjid(objid);
						//su.setUploadStatus(entity);
					}
					else
					{
						objid=entity.getObjid();
					}
					RandomAccessFile rf = new RandomAccessFile(entity.getFilePath(), "r");

					rf.seek(start);
					long pz=0;
					while (   (len=rf.read(buffer, 0, 1024)) !=-1)
					{
						pz=pz+len;
						if (filebuilder.Append(/*pz,*/buffer) ) {
							entity.setOffset(pz);
							if(activeProcess!=null)
								activeProcess.updateProgress((int) ( (pz* 100 / size) ));
							Log.d("synclocalupload","progress:"+(int) ( (pz*100 / size) ));
							su.setUploadStatus(entity);
							succed=true;
						}
						else {
							break;
						}
					}
				        Map<String,String> desc=new HashMap();
						desc.put("date", String.valueOf(file.lastModified()/1000));
					if (succed &&  filebuilder.Commit(desc)) {
						su.finishTransform(MediaMgr.DBTAB.UPed, entity);
						succed=true;
						Log.d(Tag,objid+" upload finished !!");
					}
					desc.clear();
					desc=null;

				} catch (Exception e) {
					Log.w(Tag,e.getMessage());
				}finally {
				su.close();
				if (!succed)
				{
					if (filebuilder!=null)
						filebuilder.DestoryObj();
					return  false;
				}
			}


		//filebuilder.DestoryObj();
		//MediaMgr.fileScan(entity.getFilePath(),context);
		if(activeProcess!=null)
			activeProcess.updateProgress(100);
		return true;
	}



}
