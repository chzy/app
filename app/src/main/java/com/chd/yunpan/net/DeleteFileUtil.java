package com.chd.yunpan.net;

import android.content.Context;

import com.chd.yunpan.parse.entity.DeleteFileEntity;
import com.chd.yunpan.share.ShareUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeleteFileUtil  {
	// Post fid=123,321,3122
	public static DeleteFileEntity down(Context context, String fid) {
		DeleteFileEntity entity = null;
		ShareUtils  sp = new ShareUtils(context);
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		String url = sp.getURL()+"/a1/index?ct=file&ac=delete";
		if (!fid.matches("\\d+[\\d,]*")) {
			throw new RuntimeException("输入的文件fid不合法");
		} else {
			list.add(new BasicNameValuePair("fid", fid));
		}
		try {
			String result ="";/* HttpUtils
					.GetStringForHttpPost(sp.getCookieUtil(), list, url, 3);*/

			if (result == null) {
				return entity;
			} else {

				JSONObject jsonObject = new JSONObject(result);
				// {"state":true,"error":"","errno":""}
				boolean state = jsonObject.getBoolean("state");
				String error = jsonObject.getString("error");
				String errno = jsonObject.getString("errno");
				entity = new DeleteFileEntity();
				entity.setErrno(errno);
				entity.setError(error);
				entity.setState(state);
				return entity;
			}
		} catch (/*ClientProtocol*/Exception e) {
			e.printStackTrace();
		} /*catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
		return entity;
	}
}
