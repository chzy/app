package com.chd.szt.util.move;

import android.content.Context;

public class MoveFileOrFolder {
	//{"state":true,"error":"","errno":"" }
	//Https://fyimail.vicp.net:1443/a1/index?ct=dir&ac=move
	//Post fid=1232&pid=123211

	public static MoveFileEntity moveFileOrFolder(Context context,int fid , int pid){
		/*ShareUtils shareUtils = new ShareUtils(context);
		String url = shareUtils.getURL()+"/a1/index?ct=file&ac=move";
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("fid",fid+""));
		list.add(new BasicNameValuePair("pid",pid+""));
		
		MoveFileEntity entity =null;
		try {
			String result = com.chd.yunpan.net.HttpUtils.GetStringForHttpPost(shareUtils.getCookieUtil(), list, url, 3);
			System.out.println(result+"                  result");
			if(result == null){
				return entity;
			}else {
				JSONObject jsonObject = new JSONObject(result);
				boolean state = jsonObject.getBoolean("state");
				String error = jsonObject.getString("error");
				String errno = jsonObject.getString("errno");
				entity = new MoveFileEntity();
				entity.setState(state);
				entity.setError(error);
				entity.setErrno(errno);
			
				return entity;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return entity;*/
		return null;
	}
}
