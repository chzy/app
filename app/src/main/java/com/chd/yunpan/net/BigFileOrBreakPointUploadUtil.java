package com.chd.yunpan.net;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import com.chd.TClient;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.util.HashUtil;
import com.chd.yunpan.db.DBManager;
import com.chd.yunpan.parse.entity.PrepareFileEntity;
import com.chd.yunpan.share.ShareUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class BigFileOrBreakPointUploadUtil {
	
	
	private static int sizeK = 1024;
	

	/* 上传文件至Server的方法 */
	public static  PrepareFileEntity uploadBigFileMethod(
			String uploadFile, long size, long startPosition, int pid, int aid,int sizeK1,Context context) {
		PrepareFileEntity entity2return = null;
		ShareUtils utils = new ShareUtils(context);
		String actionUrl = utils.getURL()+"/a1/resumeUpload?";

		System.out.println("uploadBigFileMethod-1");

//		System.setProperty("http.keepAlive", "false");  
		File file = new File(uploadFile);
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		HttpURLConnection con = null;
		long uptime = System.currentTimeMillis();
		try {
			
			URL url = new URL(actionUrl + "sha1="
					+ HashUtil.getFileSHA1(uploadFile) + "&size=" + size
					+ "");
			 con = (HttpURLConnection) url.openConnection();
			 
			/* 允许Input、Output，不使用Cache */
			System.out.println("uploadBigFileMethod-3");
			con.setDoInput(true);
			con.setChunkedStreamingMode(0); 
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			con.setRequestProperty("Connection", "keep-live");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
//			con.setRequestProperty("Cookie", "fuid=" + fuid + "; token="
//					+ token + "");
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			System.out.println("uploadBigFileMethod-4");

			StringBuffer buf = new StringBuffer();
			buf.append(twoHyphens + boundary + end);
			buf.append("Content-Disposition: form-data; " + "name=\"aid\""
					+ end + end + aid + end);
			buf.append(twoHyphens + boundary + end);
			buf.append("Content-Disposition: form-data; " + "name=\"pid\""
					+ end + end + pid + end);
			buf.append(twoHyphens + boundary + end);
			buf.append("Content-Disposition: form-data; " + "name=\"name\""
					+ end + end + file.getName()+ end);
			buf.append(twoHyphens + boundary + end);
			buf.append("Content-Disposition: form-data; "
					+ "name=\"Filedata\";filename=\"" +file.getName() + "\""
					+ end);
			buf.append(end);
			ds.write(buf.toString().getBytes());

			/* 取得文件的FileInputStream */
			//FileInputStream fStream = new FileInputStream(uploadFile);


			RandomAccessFile fStream = new RandomAccessFile(uploadFile, "r");
			/* 设置每次写入1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			System.out.println("uploadBigFileMethod-5");

			fStream.seek(startPosition);


			Log.e("aa", startPosition+"");
		
			int sum = 0;
			/* 从文件读取数据至缓冲区 */
			while (sum < sizeK && (length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
				ds.write(buffer, 0, length);
				sum++;
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			/* close streams */
			fStream.close();
			ds.flush();
			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			Log.v("wang",UTF2GBK.Unicode2GBK(b.toString()));
			entity2return = PrepareFileUploadUtil.parse(UTF2GBK.Unicode2GBK(b.toString()));
			long usedTime = System.currentTimeMillis()-uptime;
			sizeK = (int) (5000*sizeK/usedTime);
			
			/* 将Response显示于Dialog */

			/* 关闭DataOutputStream */
			 
			ds.close();
			is.close();
			return entity2return;
		} catch (Exception e) {
			e.printStackTrace();

		}finally{
			if(con!=null){
				con.disconnect();
			}
		}
		return entity2return;
	}

	private boolean flag = true;

	public void setFlag(boolean flag) {
		this.flag = flag;
	}


	public boolean uploadBigFile(FileInfo0 entity, ProgressBar pb,Context context) {

		TClient tClient=null;
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
		File file = new File(entity.getFilePath());
		if (flag==false)
			return false;
		{
			System.out.println("开始上传喽");
			/*String sha1 = MakeFileHash.getFileSHA1(file);
			long size = file2.length();
*/
			//先检查是否 有同名文件
			FileInfo fileInfo0=tClient.queryFile(entity);

			//是否 需要查询 服务器端是否有同名的 未传完的 文件
			long start=0;
			if (entity.getObjid()==null) {
				if (pb != null)
					pb.setMax(100);
			}
			else
			{
				start = entity.getOffset();
				pb.setProgress((int) (start/size*1000));
			}
			int len=0;
			byte[] buffer=new byte[1024];

			System.out.println(start);
			 {

				 try {
					 DBManager su = new DBManager(context);
					 su.open();
					 TClient.TFilebuilder filebuilder;
					 filebuilder = tClient.new TFilebuilder(entity.getFilename(),entity.getFtype(),(int)size);
					 String objid=filebuilder.ApplyObj();
					 if (objid!=null)
					 {
						 entity.setObjid(objid);
						 su.setUploadStatus(entity);
					 }
					 RandomAccessFile rf = new RandomAccessFile(entity.getFilePath(), "r");
					 rf.seek(start);
					 long pz=0;
					 while (   (len=rf.read(buffer, 0, 1024)) !=-1)
					 {
						 pz=start+len;
						 if (filebuilder.Append(/*pz,*/buffer,len) ) {
							 if(pb!=null)
								 pb.setProgress((int)(pz/size*100));
							 su.updateDownloadingFile(objid, pz);
						 }
						 else
							 return false;
					 }
					// su.deleteUpLoadingFile(objid);
					 su.addDownloadedFile(entity);
				 } catch (Exception e) {
					 Log.w("@@@",e.getMessage());
				 }
//adBigFile(fileDowningId,file, pid, aid,  pb,context);
			}
		}
		return true;
	}

	private long getUploadedFileSize(FileInfo0 entity, Context context) {

	/*	DBManager su = new DBManager(context);
		su.open();
		// 删除upLoading表中记录
		*//*su.deleteUpLoadingFile(entity.getData().getFid());
		// 添加upLoaded表中记录
		FileDataDBEntity localFile = new FileDataDBEntity();
		localFile.setAid(entity.getData().getAid());
		System.out.println(entity.getData().getFid()+"fid-------------------------------------+++++++++++++++++++++++");
		localFile.setFid(entity.getData().getFid());
		localFile.setPid(pid);

		localFile.setN(file2.getName());
		localFile.setPc(entity.getData().getPc());

		localFile.setT(entity.getData().getT());
		localFile.setPath(file2.getAbsolutePath());*//*
		su.addUpLoadedFile(localFile);
		su.close();
		return -1;*/
		return 0;
	}


	public boolean uploadBigFile(int fileDowningId,String file, int pid, int aid, ProgressBar pb,Context context) {
		

		if(!NetworkUtils.isNetworkAvailable(context)){
			return false;
		}
		File file2 = new File(file);
		if (flag) {
			System.out.println("开始上传喽");
			String sha1 = HashUtil.getFileSHA1(file);
			long size = file2.length();
			
			if(pb!=null)
			pb.setMax(100);
			
			long start = getUploadedFileSize(fileDowningId,pid,file2, sha1,context);
			System.out.println(start);
			if (start == -2) {
				// 网络异常
				errorNum = 0;
				return false;
			} else if (start == -1) {
				// 上传已经成功
				if(pb!=null)
				pb.setProgress(100);
				errorNum = 0;
				System.out.println("上传成功");
				
				return true;
			} else if(start==-3){
				if(pb!=null)
					pb.setProgress((int)(start/size*100));

					
			}else {
				
				if(pb!=null)
				pb.setProgress((int)(start/size*100));
				PrepareFileEntity entity =  BigFileOrBreakPointUploadUtil.uploadBigFileMethod(
						 file, file2.length(),
						start, pid, 1,1024,context);
				if(entity==null){
					// 删除upLoading表中记录
					//su.deleteUserUpingFile(fileDowningId);
					errorNum++;
					if(errorNum==3){
						errorNum=0;
						return true;
					}
				}else {
					if(errorNum!=0){
						errorNum=0;
					}
				}
				file2 =null;
				Log.e("kk", "go");
				uploadBigFile(fileDowningId,file, pid, aid,  pb,context);
			}
		}
		return true;
	}
	
	private static int errorNum = 0;

	/**
	 * 
	 * @param file2
	 * @param sha1
	 * @return -1 已经上传完成 0 表示没有上传 -2 网络有问题 其他 已经上传的数据大小
	 * id 为文件在downing 中的id
	 */
	private long getUploadedFileSize(int id,int pid,File file2, String sha1,Context context) {
		
		long size = file2.length();
		/*PrepareFileEntity entity = PrepareFileUploadUtil.getPrepareFileEntity(
				sha1, size, file2.getName(), pid,context);
		*/



	/*	if (entity != null) {
			if (entity.getData() == null) {
				return 0;
			}
			String us = entity.getData().getUs();
			String s = entity.getData().getS();
			if (s != null && s.equals(us)) {
				// 说明网上有这个文件
//					
//					return -3; 
//				} else {
					DBManager su = new DBManager(context);
					su.open();
					// 删除upLoading表中记录
					su.deleteUpLoadingFile(entity.getData().getFid());
					// 添加upLoaded表中记录
					FileDataDBEntity localFile = new FileDataDBEntity();
					localFile.setAid(entity.getData().getAid());
					System.out.println(entity.getData().getFid()+"fid-------------------------------------+++++++++++++++++++++++");
					localFile.setFid(entity.getData().getFid());
					localFile.setPid(pid);
				
					localFile.setN(file2.getName());
					localFile.setPc(entity.getData().getPc());
				
					localFile.setT(entity.getData().getT());
					localFile.setPath(file2.getAbsolutePath());
					su.addUpLoadedFile(localFile);
					su.close();
					return -1;
				
				
			}
			return Long.parseLong(us);
		}*/
		return -2;
	}
}
