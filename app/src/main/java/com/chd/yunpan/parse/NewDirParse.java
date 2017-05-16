package com.chd.yunpan.parse;

import org.json.JSONException;
import org.json.JSONObject;

import com.chd.yunpan.parse.entity.NewDirEntity;

public class NewDirParse implements Parse<NewDirEntity> {

	@Override
	public NewDirEntity parse(String str) {
		NewDirEntity entity = null;
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(str);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		int aid = 0;
		try {
			aid = jsonObject.getInt("aid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int cid = 0;
		try {
			cid = jsonObject.getInt("cid");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String cname2 = null;
		try {
			cname2 = jsonObject.getString("cname");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int pid2 = 0;
		try {
			pid2 = jsonObject.getInt("pid");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean state = false;
		try {
			state = jsonObject.getBoolean("state");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String error = null;
		try {
			error = jsonObject.getString("error");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String errno = null;
		try {
			errno = jsonObject.getString("errno");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		entity = new NewDirEntity(aid, cid, cname2, pid2, state,
				error, errno);
		return entity;
	}

}
