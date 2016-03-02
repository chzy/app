package com.chd.szt.util.fileisexist;

import android.content.Context;

import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;


public class FileIisExistUtil {

	public static FileIsExistEntity run(Context context , int pid1, String name1) {
		/*List<NameValuePair> list = new ArrayList<NameValuePair>();
		ShareUtils shareUtils = new ShareUtils(context);
		String url =shareUtils.getURL()+"/a1/index?ct=file&ac=check";
		list.add(new BasicNameValuePair("pid", pid1 + ""));
		list.add(new BasicNameValuePair("name", name1));
		FileIsExistEntity entity = new FileIsExistEntity();
		try {
			String result = HttpUtils.GetStringForHttpPost(shareUtils.getCookieUtil(), list, url,
					3);
			if (result == null) {
				return null;
			} else {
				System.out.println(result);
				JSONObject jsonObject = new JSONObject(result);
				entity = new FileIsExistEntity();
				entity.setState(jsonObject.getBoolean("state"));
				entity.setError(jsonObject.getString("error"));
				entity.setErrno(jsonObject.getString("errno"));
				if (jsonObject.getBoolean("state")) {
					entity.setType(jsonObject.getString("type"));
				} else {
					entity.setType(null);
				}
				return entity ;

			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
		return /*entity*/null;
	}


	public static FileInfo0 run(Context context , FTYPE ftype, String fname)
	{

		//TClient.getinstance().
		return null;

	}


}
