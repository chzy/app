package com.chd.yunpan.utils;

import android.content.Context;
import android.util.Log;

import com.chd.TClient;
import com.chd.proto.FTYPE;
import com.chd.yunpan.parse.entity.FileDownLoadLinkedEnitity;


public class FileDownLoadLinkedUtil {

	public static FileDownLoadLinkedEnitity getFileLinked(Context context , int fid) {
	/*	ShareUtils utils = new ShareUtils(context);
		String url = utils.getURL()+"/a1/index?ct=file&ac=url";
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("fid",fid+""));
		
		FileDownLoadLinkedEnitity enitity = null;
		try {
			String result = HttpUtils.GetStringForHttpPost(utils.getCookieUtil() , list, url, 3);
			if(result == null){
				return null;
			}else {
				JSONObject jsonObject = new JSONObject(result);
				String u = jsonObject.getString("url");
				boolean state = jsonObject.getBoolean("state");
				String error = jsonObject.getString("error");
				String errno = jsonObject.getString("errno");
				enitity = new FileDownLoadLinkedEnitity();
				enitity.setUrl(u);
				enitity.setErrno(errno);
				enitity.setError(error);
				enitity.setState(state);
				return enitity;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return enitity;*/
		return null;
	}

	public static String getFileLinked(Context context ,FTYPE ftype, String fid) {
		String url=null;
		try {
			url=TClient.getinstance().CreateShare(ftype,fid);
		} catch (Exception e) {
			e.printStackTrace();
			Log.w("@@@",e.getLocalizedMessage());
		}
		return url;
	}


}
