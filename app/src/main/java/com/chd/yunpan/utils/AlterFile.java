package com.chd.yunpan.utils;

import android.content.Context;

import com.chd.yunpan.parse.entity.AlterFileEntity;
import com.chd.yunpan.parse.entity.Data;
import com.chd.yunpan.share.ShareUtils;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AlterFile {

	public static AlterFileEntity down(Context context, int fid,
			String file_name, String file_desc) {

		ShareUtils sp = new ShareUtils(context);
		String url = sp.getURL()+"/a1/index?ct=file&ac=edit";
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("fid", fid + ""));
		list.add(new BasicNameValuePair("file_name", file_name));
//		list.add(new BasicNameValuePair("file_desc", file_desc));
		AlterFileEntity entity = null;
		String result = null;
		try {
			result = ""/*com.chd.yunpan.net.HttpUtils.GetStringForHttpPost(sp.getCookieUtil(), list, url, 3)*/;
			System.out.println("111111" + result);
			if (result != null) {
				try {
				
					// {"data":{"file_desc":"jkdsd","file_name":"sss"},"state":true,"error"="","errno"=""}
					JSONObject jsonObject = new JSONObject(result);
					boolean state = jsonObject.getBoolean("state");
					String error = jsonObject.getString("error");
					String errno = jsonObject.getString("errno");

					String data = jsonObject.getString("data");

					Gson gson = new Gson();
					Data data1 = gson.fromJson(data, Data.class);

					entity = new AlterFileEntity(data1, state, error, errno);
					return entity;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				return entity;
			}
		} catch (/*ClientProtocol*/Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /*catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return entity;
	}

}
