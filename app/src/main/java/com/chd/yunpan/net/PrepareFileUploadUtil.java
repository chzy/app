package com.chd.yunpan.net;

import android.content.Context;

import com.chd.yunpan.parse.entity.PrepareFileDataEntity;
import com.chd.yunpan.parse.entity.PrepareFileEntity;
import com.chd.yunpan.share.ShareUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 准备文件上传
 * 
 * @author Administrator Post
 *         sha1=21f3d92050626fc5a9069bcf06b7c781&size=420213&pid
 *         =1232&name=aaa.txt
 * 
 *         sha1 为该文件的sha1值 文件经过sha1加密后的值。 size 为文件大小 name 文件名 pid 为该文件上传目录ID
 */
public class PrepareFileUploadUtil {

	

	public static PrepareFileEntity getPrepareFileEntity(String sha1,
			long size, String name, int pid, Context context) {
		ShareUtils utils = new ShareUtils(context);
		String url = utils.getURL()+"/a1/requestUpload";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("sha1", sha1));
		nameValuePairs.add(new BasicNameValuePair("size", size + ""));
		nameValuePairs.add(new BasicNameValuePair("name", name));
		nameValuePairs.add(new BasicNameValuePair("pid", pid + ""));
	
		try {
			String result =/* HttpUtils.GetStringForHttpPost(utils.getCookieUtil(),
					nameValuePairs, url, 3)*/"";
			if (result != null) {
				System.out.println(result);
				return parse(result);
			} else {
			}
		} catch (/*ClientProtocol*/Exception e) {

			e.printStackTrace();
			return null;
		} /*catch (IOException e) {
			e.printStackTrace();
			return null;
		}*/
		return null;
	}

	public static PrepareFileEntity parse(String result) {
		try {
			JSONObject object = new JSONObject(result);
			boolean state = object.getBoolean("state");
			String error = null;
			String errno = null;
			try{
			error = object.getString("error");
			}
			catch (Exception e) {
			}
			try{
			 errno = object.getString("errno");
			}
			catch (Exception e) {
			}

			PrepareFileDataEntity dataEntity = null;
			if (object.has("data")) {
				dataEntity = new PrepareFileDataEntity();
				JSONObject object2 = object.getJSONObject("data");
				String s = null;
				String us = null;
				try {
					s = object2.getString("s");
					us = object2.getString("us");
				} catch (Exception e) {
					// TODO: handle exception
					s = object2.getInt("s") + "";
					us = object2.getInt("us") + "";
				}

			
				dataEntity.setS(s);
				dataEntity.setUs(us);
				try {
					if (s != null & s.equals(us)) {
						dataEntity.setFid(object2.getInt("fid"));
						
						dataEntity.setAid(object2.getInt("aid"));
						dataEntity.setPid(object2.getInt("pid"));
						dataEntity.setN(object2.getString("n"));
						dataEntity.setPc(object2.getString("pc"));
						System.out.println("s==us");
						try {
							dataEntity.setM(object2.getString("m"));
						} catch (Exception e) {
							// TODO: handle exception
							dataEntity.setM(object2.getInt("m") + "");
						}
						try {
							dataEntity.setT(object2.getString("t"));
							dataEntity.setU(object2.getString("u"));
						} catch (Exception e) {
						}
					}
				} catch (Exception e) {
				}

			}

			PrepareFileEntity entity = new PrepareFileEntity();
			entity.setData(dataEntity);
			entity.setErrno(errno);
			entity.setState(state);
			entity.setError(error);
			return entity;
		} catch (JSONException e) {

			e.printStackTrace();
			return null;
		}
	}
}
