package com.chd.yunpan.share;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

import com.chd.proto.LoginResult;
import com.chd.yunpan.net.CookieUtil;
import com.chd.yunpan.parse.entity.LoginEntity;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class ShareUtils {
	private SharedPreferences sp = null;
	private Editor editor = null;
	private Context context=null;
	public ShareUtils(Context context) {
		this.context=context;
		sp = context.getSharedPreferences("data",Context.MODE_PRIVATE);
		editor = sp.edit();

	}

	public void setuseSDcard(boolean use)
	{
		editor.putBoolean("usesdcard",use);
	}

	public boolean getuseSDcard()
	{
		return  sp.getBoolean("usesdcard", false);
	}

	public boolean sdCardExist()
	{
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	/*public File getIntpath(Context context)
	{

		File dir=context.getFilesDir();
		return dir;
	}
*/

	public String getStorePathStr()
	{
		String dir=null;
		if (getuseSDcard()&&sdCardExist())
		{
			dir= Environment.getExternalStorageDirectory().getAbsoluteFile()+File.pathSeparator+context.getPackageName();
			//dir=context.getExternalFilesDir(null);
		}
		else {
			dir = context.getFilesDir().getAbsolutePath();
		}
		return dir;
	}


	public File getStorePath()
	{
		File dir=null;
		if (getuseSDcard()&&sdCardExist())
		{
			dir= new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+File.pathSeparator+context.getPackageName());
			//dir=context.getExternalFilesDir(null);
		}
		else {
			 dir = context.getFilesDir();
		}
		return dir;
	}


	public OutputStream getStoreOs(String fname)
	{
		OutputStream os=null;
		String  dir=null,file;
		if (getuseSDcard()&&sdCardExist())
		{
			dir= Environment.getExternalStorageDirectory().getAbsoluteFile()+File.pathSeparator+context.getPackageName();
			//dir=context.getExternalFilesDir(null);
		}
		else {
			dir = context.getFilesDir().getAbsolutePath();
		}
		file=dir+File.pathSeparator+fname;

		try {
			os= new FileOutputStream(new File(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


		return os;
	}

	public void addDownPath(String path){
		Set<String> set = sp.getStringSet("downpath",new HashSet<String>());
		set.add(path);
		editor.putStringSet("path", set);
		editor.commit();
	}
	
	public Set<String> getDownPath(){
		return sp.getStringSet("downpath", new HashSet<String>());
	}




	public void removeDownPath(String path){
		Set<String> set = sp.getStringSet("downpath",new HashSet<String>());
		set.remove(path);
		editor.putStringSet("path", set);
		editor.commit();
	}
	
	public void setAutoPhotoBack(boolean flag){
		editor.putBoolean("autophotoback", flag);
		editor.commit();
	}
	
	public void addPath(String path){
		Set<String> set = sp.getStringSet("path",new HashSet<String>());
		set.add(path);
		editor.putStringSet("path", set);
		editor.commit();
	}
	
	public void clearPath(){
		editor.putStringSet("path", new HashSet<String>());
		editor.commit();
	}
	
	public boolean isExistPath(String path){
		Set<String> set = sp.getStringSet("path",new HashSet<String>());
		return set.contains(path);
	}
	
	public boolean getAutoPhotoBack(){
		return sp.getBoolean("autophotoback", false);
	}
	
	public void setURL(String url){
		editor.putString("url", url);
		editor.commit();
	}
	
	public String getURL(){
		return sp.getString("url", "");
	}
	
	public void setLoginEntity(LoginResult entity){
//		TSerializer serializer = new TSerializer(new TSimpleJSONProtocol.Factory());
		String json = null;
//		try {
//			 json = serializer.toString(entity);
//		} catch (TException e) {
//			e.printStackTrace();
//		}
		//json=entity.toString();
		//Gson gson = new Gson();
		//editor.putString("entity", gson.toJson(entity));
		json=entity.getJson();
		editor.putString("entity", json);
		editor.commit();
		//LoginEntity dd=getLoginEntity();
	}
	
	public LoginEntity getLoginEntity(){
		String e = sp.getString("entity","");
//		loginResult entity=new loginResult();
//		TMemoryBuffer tmb = new TMemoryBuffer(32);
//		//TProtocol tp = new org.apache.thrift.protocol.TBinaryProtocol(tmb);
//
//		try {
//			tmb.write(e.getBytes());
//			TSimpleJSONProtocol tp = new org.apache.thrift.protocol.TSimpleJSONProtocol(tmb);
//			entity.read(tp);
//		} catch (Exception e1) {
//
//		}
//		int sp=entity.getSpace();
//		return null;

		Gson g = new Gson();

		//g.fromJson(e, LoginEntity.class);

		return g.fromJson(e, LoginEntity.class);
	}
	
	public void setAutoLogin(boolean isAuto){
		editor.putBoolean("autoLogin", isAuto);
		editor.commit();
	}
	public boolean isAutoLogin(){
		return sp.getBoolean("autoLogin", false);
	}
	
	public CookieUtil getCookieUtil(){
//		LoginEntity entity =  getLoginEntity();
//		CookieUtil cookieUtil = new CookieUtil(Integer.parseInt(entity.getId()), entity.getToken());
//		return cookieUtil;
		//throw new Exception("");
		return  null;
	}
	
	public void setUsername(String name){
		editor.putString("username", name);
		editor.commit();
	}
	public void setPwd(String pwd){
		editor.putString("pwd", pwd);
		editor.commit();
	}
	
	public String getUsername(){
		return sp.getString("username", "");
	}
	
	public String getPwd(){
		return sp.getString("pwd", "");
	}




}
