package com.chd.yunpan.utils;

import android.content.Context;

import com.chd.yunpan.parse.entity.AlterFolderEntity;
import com.chd.yunpan.share.ShareUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AlterFolder {

	public static AlterFolderEntity down(Context context,int fid, String file_name, String file_desc) {
		ShareUtils sp = new ShareUtils(context);
		String url = sp.getURL()+"/a1/index?ct=dir&ac=edit";
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		AlterFolderEntity entity=null;
		list.add(new BasicNameValuePair("fid", fid + ""));
		list.add(new BasicNameValuePair("file_name", file_name));
//		list.add(new BasicNameValuePair("file_desc", file_desc));
		String result = null;
		try {
			//result = com.chd.yunpan.net.HttpUtils.GetStringForHttpPost(sp.getCookieUtil(), list, url, 3);
			System.out.println("222" + result);
			if (result != null) {

				JSONObject jsonObject = new JSONObject(result);
				boolean state = jsonObject.getBoolean("state");
				String error = jsonObject.getString("error");
				String errno = jsonObject.getString("errno");
				String file_name2 = jsonObject.getString("file_name");

				entity = new AlterFolderEntity(state, error, errno, file_name2);
				return entity; 
			} else {
				return null; 
			}

		} catch (/*ClientProtocol*/Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /*catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return null;
	}
}
