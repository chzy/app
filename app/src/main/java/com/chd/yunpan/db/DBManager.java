package com.chd.yunpan.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.chd.DBhandler;
import com.chd.Entity.FilesListEntity;
import com.chd.Entity.LocalFileEntity;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.utils.TimeUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DBManager implements DBhandler {
	private SQLiteDatabase db;
	private String pfpath;

	private Context context;

	public DBManager(Context context) {
		this.context = context;
	}

	public DBManager(Context context, String prefixPath) {
		this.context = context;
		//pfpath=context.getFilesDir().getAbsolutePath();

		//pfpath= Environment.getDataDirectory();
		/*context.getExternalFilesDir(null);
		Environment.getDataDirectory();
		Environment.getRootDirectory();*/
	}


	public void saveToSdcard(String filename, String content) throws IOException {
		context.getExternalFilesDir(Environment.DIRECTORY_DCIM);
	}
	/*private String getppath()
	{
		getExternalFilesDir()
	}
*/
	public void open(){
		ShareUtils shareUtils = new ShareUtils(context);
		db = new MySQLHelper(context, "photo"+shareUtils.getLoginEntity().getId()+".db", 1).getWritableDatabase();
	}
	public void close(){
		if(db!=null){
			if(db.isOpen()){
				db.close();
			}
		}
	}

	private String getpath(String fname)
	{
		return  pfpath+ File.pathSeparator+fname;
	}
/*
	db.execSQL("CREATE TABLE download_finish (objid TEXT PRIMARY KEY ,time Text ,size INTEGER,name text )");
		db.execSQL("CREATE TABLE download_inter ( objid TEXT PRIMARY KEY,size  INTEGER , time Text ,offset INTEGER,name text )");
		db.execSQL("CREATE TABLE upload_finish (objid TEXT PRIMARY KEY, time Text , sie INTEGER,path text )");
		db.execSQL("CREATE TABLE upload_inter (hash TEXT PRIMARY KEY,path text,name text,size  INTEGER , time Text , offset INTEGER,objid text )");
		db.execSQL("CREATE UNIQUE INDEX upload_oid_idx ON upload_inter (objid)");

	;*/

	public List<Long> getUploadPicsID(){
		Cursor cursor = db.rawQuery("select sysid from upload_finish union select sysid from upload_inter", null);
		List<Long> lists = new ArrayList<Long>();
		while (cursor.moveToNext()) {
			lists.add(cursor.getLong(0));
		}
		cursor.close();
		//close();
		Collections.sort(lists);
		return lists;
	}


	public  void  setUploadinfFile0(FileInfo0 entity)
	{
		ContentValues values = new ContentValues();
		values.put("objid",entity.getObjid());
		/*values.put("path", entity.getFilePath());
		values.put("size",entity.getSize());
		values.put("name",entity.getFname());
	*/
		//int hash=entity.getFilePath().hashCode();
		values.put("offset",entity.getOffset());
		//values.put("time", TimeUtils.getCurrentTimeInLong());
		//	FileDataDBEntity userLocalFile2 = getUpLoadingFile(entity.getFid());
		//if (userLocalFile2  == null) {
		db.update("upload_inter", values, "sysid=?", new String[]{entity.getSysid() + ""});
//
	}

	public void addUpLoadingFile(FileInfo0 entity){
		ContentValues values = new ContentValues();
		values.put(/*"hash"*/"sysid",/*entity.getFilePath().hashCode()*/entity.getSysid());
		values.put("objid",entity.getObjid());
		values.put("path", entity.getFilePath());
		values.put("size",entity.getSize());
		//values.put("name",entity.getFilename());
		values.put("offset",0);
		values.put("time", /*TimeUtils.getCurrentTimeInLong()*/entity.getLastModified());
		//	FileDataDBEntity userLocalFile2 = getUpLoadingFile(entity.getFid());
		//if (userLocalFile2  == null) {
		db.insertWithOnConflict("upload_inter", null, values, SQLiteDatabase.CONFLICT_IGNORE);
//
	}

	public  void setUploadStatus(FileInfo0 entity)
	{
		ContentValues values = new ContentValues();
		values.put("sysid",entity.getSysid());
		values.put("objid",entity.getObjid());

		values.put("offset",entity.getOffset());

		db.update("upload_inter", values, "sysid=?", new String[]{entity.getSysid() + ""});
//
	}

	public void addDownloadingFile(FileInfo0 entity){
		ContentValues values = new ContentValues();
		values.put("objid", entity.getObjid());
		values.put("size",entity.getFilesize());
		values.put("name",entity.getFilename());
		values.put("offset",0);
		values.put("time", entity.getLastModified());
		db.insertWithOnConflict("download_inter", null, values, SQLiteDatabase.CONFLICT_IGNORE);
	}




	public void addUpLoadingFile(LocalFileEntity entity) {
		ContentValues values = new ContentValues();
		values.put("hash",entity.getPath().hashCode());
		values.put("path", entity.getPath());
		values.put("size",entity.getSize());
		values.put("name",entity.getFname());
		values.put("offset",0);
		values.put("time", TimeUtils.getCurrentTimeInLong());
		//	FileDataDBEntity userLocalFile2 = getUpLoadingFile(entity.getFid());
		//if (userLocalFile2  == null) {
		db.insertWithOnConflict("upload_inter", null, values, SQLiteDatabase.CONFLICT_IGNORE);
	}


	/*public void addUpLoadingFile(FileInfo0 entity){
		ContentValues values = new ContentValues();
		//values.put("objid", entity.getObjid());
		values.put("size",entity.getFilesize());
		values.put("name",entity.getFilename());
		values.put("offset",0);
		values.put("time",entity.getLastModified());
	//	FileDataDBEntity userLocalFile2 = getUpLoadingFile(entity.getFid());
		//if (userLocalFile2  == null) {
		db.insertWithOnConflict("upload_inter", null, values, SQLiteDatabase.CONFLICT_IGNORE);
//		} else {
//			updateUpLoadingFile(entity);
//		}
	}
*/



	public void updateDownloadingFile(FileInfo0 entity) throws IOException{
		if(getDownloadingFile(entity.getObjid())==null){
			throw new IOException();
		}
		ContentValues values = new ContentValues();
		values.put("objid", entity.getObjid());
		values.put("offset", entity.getOffset());

		db.update("download_inter", values, "objid=?", new String[]{entity.getObjid()+""});
		Log.d("@@@","call db update updateDownloadingFile");
	}

	@Override
	public void updateDownloadingFile(String Objid,long offset) throws Exception{
		if(getDownloadingFile(Objid)==null){
			throw new Exception();
		}
		ContentValues values = new ContentValues();
		values.put("objid", Objid);
		values.put("offset", offset);

		db.update("download_inter", values, "objid=?", new String[]{Objid + ""});
		Log.d("@@@", "call db update updateDownloadingFile");
	}

	@Override
	       //void updateDownloadingFile(String Objid,long offset)  throws Exception
	public void updateUploadingFile(String Objid, long offset) throws Exception {
			if(getUpLoadedFile(Objid)==null){
				throw new Exception();
			}
			ContentValues values = new ContentValues();
			values.put("objid", Objid);
			values.put("offset", offset);

			db.update("upload_inter", values, "objid=?", new String[]{Objid + ""});
		}




	public void addDownloadedFile(FileInfo0 entity){
		ContentValues values = new ContentValues();
		values.put("objid", entity.getObjid());
		values.put("name", entity.getFilename());
		values.put("size", entity.getFilesize());
		values.put("time", entity.getLastModified());

		/*
		values.put("pid", entity.getPid());
		values.put("fid", entity.getFid());
		values.put("m", entity.getM());
		values.put("n", entity.getN());
		values.put("pc", entity.getPc());
		values.put("t", entity.getT());
		values.put("u", entity.getU());
		values.put("s", entity.getS());
		values.put("sha1", entity.getShal());
		values.put("path", entity.getfPath());
		values.put("spare", entity.getSpare());
		values.put("flag", entity.getFlag());
		values.put("spare2", entity.getSpare2());
		*/

		/*FileDataDBEntity userLocalFile2 = getDownloadedFile(entity.getFid());
		if (userLocalFile2  == null) {
			db.insert("download_finish", null, values);
		} else {
			updateDownloadedFile(entity);
		}*/
		deleteUpLoadingFile(entity.getObjid());
		db.insertWithOnConflict("upload_finish", null, values, SQLiteDatabase.CONFLICT_IGNORE);

	}
	public void addUpLoadedFile(FileInfo0 entity){
		ContentValues values = new ContentValues();
		values.put("objid", entity.getObjid());
		values.put("size",entity.getFilesize());
		values.put("name",entity.getFilename());

		values.put("time",entity.getLastModified());

		db.insertWithOnConflict("upload_finish", null, values, SQLiteDatabase.CONFLICT_IGNORE);
	}

	public void updateUpLoadedFile(FileInfo0 entity){
		/*ContentValues values = new ContentValues();
		values.put("aid", entity.getAid());
		values.put("pid", entity.getPid());
		values.put("fid", entity.getFid());
		values.put("m", entity.getM());
		values.put("n", entity.getN());
		values.put("pc", entity.getPc());
		values.put("t", entity.getT());
		values.put("u", entity.getU());
		values.put("s", entity.getS());
		values.put("sha1", entity.getShal());
		values.put("path", entity.getfPath());
		values.put("spare", entity.getSpare());
		values.put("flag", entity.getFlag());
		values.put("spare1", entity.getSpare());
		values.put("spare2", entity.getSpare2());
		db.update("upload_finish", values, "fid=?", new String[]{entity.getFid()+""});*/
		Log.d("@@@", "call db update updateUpLoadedFile");

	}



	
	public void updateUpLoadingFile(FileInfo0 entity){
		ContentValues values = new ContentValues();
		values.put("objid", entity.getObjid());
		values.put("offset",entity.getOffset());
		/*values.put("pid", entity.getPid());
		values.put("fid", entity.getFid());
		values.put("m", entity.getM());
		values.put("n", entity.getN());
		values.put("pc", entity.getPc());
		values.put("t", entity.getT());
		values.put("u", entity.getU());
		values.put("s", entity.getS());
		values.put("sha1", entity.getShal());
		values.put("path", entity.getfPath());
		values.put("spare", entity.getSpare());
		values.put("flag", entity.getFlag());
		values.put("spare1", entity.getSpare());
		values.put("spare2", entity.getSpare2());*/
		db.update("upload_inter", values, "objid=?", new String[]{entity.getObjid() + ""});
	}
	
	public void updateDownloadedFile(FileInfo0 entity){
		/*ContentValues values = new ContentValues();
		values.put("aid", entity.getAid());
		values.put("pid", entity.getPid());
		values.put("fid", entity.getFid());
		values.put("m", entity.getM());
		values.put("n", entity.getN());
		values.put("pc", entity.getPc());
		values.put("t", entity.getT());
		values.put("u", entity.getU());
		values.put("s", entity.getS());
		values.put("sha1", entity.getShal());
		values.put("path", entity.getfPath());
		values.put("spare", entity.getSpare());
		values.put("flag", entity.getFlag());
		values.put("spare1", entity.getSpare());
		values.put("spare2", entity.getSpare2());
		db.update("download_finish", values, "fid=?", new String[]{entity.getFid()+""});*/
	}

	
	public List<FileInfo0> getDownloadingFiles(){
		
		return getFileDataDBEntities("download_inter",false);
	}
	
	private List<FileInfo0> getFileDataDBEntities(String db1,boolean finished){
		Cursor cursor = db.rawQuery("select * from "+db1, null);
		List<FileInfo0> lists = new ArrayList<FileInfo0>();
		while (cursor.moveToNext()) {
			FileInfo0 file=new FileInfo0();
			file.setObjid(cursor.getString(cursor.getColumnIndex("objid")));
			file.setFilename(cursor.getString(cursor.getColumnIndex("name")));
			if (!finished)
				file.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));
			lists.add(file);
		}
		cursor.close();
		return lists;
	}



	/*private FilesListEntity<FileInfo0> getFileDataDBEntities(){
		Cursor cursor = db.rawQuery("select * from "+db1, null);
		FilesListEntity filesListEntity=null;
		List<FileInfo0> lists = new ArrayList<FileInfo0>();
		while (cursor.moveToNext()) {
			FileInfo0 file=new FileInfo0();
			file.setObjid(cursor.getString(cursor.getColumnIndex("objid")));
			file.setFilename(cursor.getString(cursor.getColumnIndex("name")));
			if (!finished)
				file.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));
			lists.add(file);
		}
		cursor.close();
		filesListEntity=new FilesListEntity();
		filesListEntity.setList(lists);
		filesListEntity.setFtype();
		return lists;
	}*/

	public FilesListEntity<FileInfo0> getUploadeFiles(FTYPE ftype){
		//Cursor cursor = db.rawQuery("select * from upload_finish where ftype = "+ftype, null);
		FilesListEntity filesListEntity=null;
		List<FileInfo0> list=getUpfinishedDBEntity(ftype);
		filesListEntity = new FilesListEntity(0, list);
		return filesListEntity;

	}


	private List<FileInfo0> getUpfinishedDBEntity(FTYPE ftype){
		Cursor cursor = db.rawQuery("select * from upload_finish where ftype = "+ftype+" order by time desc" , null);
		List<FileInfo0> lists = new ArrayList<FileInfo0>();
		while (cursor.moveToNext()) {
			FileInfo0 file=new FileInfo0();
			file.setObjid(cursor.getString(cursor.getColumnIndex("objid")));
			file.setFilename(cursor.getString(cursor.getColumnIndex("name")));
			file.setFilePath(cursor.getString(cursor.getColumnIndex("path")));
			file.setLastModified(cursor.getColumnIndex("time"));
			lists.add(file);
		}
		cursor.close();
		return lists;
	}

	private List<FileInfo0> getFileDataDBEntitiesU(String db1,boolean finished){
		Cursor cursor = db.rawQuery("select * from "+db1, null);
		List<FileInfo0> lists = new ArrayList<FileInfo0>();
		while (cursor.moveToNext()) {
			FileInfo0 file=new FileInfo0();
			file.setObjid(cursor.getString(cursor.getColumnIndex("objid")));
			file.setFilename(cursor.getString(cursor.getColumnIndex("name")));
			if (!finished)
				file.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));
			lists.add(file);
		}
		cursor.close();
		return lists;
	}


	
	public List<FileInfo0> getUpLoadingFiles(){
		return getFileDataDBEntitiesU("upload_inter", false);
	}
	
	public List<FileInfo0> getDownloadedFiles(){
		return getFileDataDBEntities("download_finish", true);
	}
	public List<FileInfo0> getUpLoadedFiles(){
		return getFileDataDBEntitiesU("upload_finish", true);
	}
	
	private FileInfo0 getFileDataDBEntity(String db1,String objid,boolean finished){
		FileInfo0 file=null;
		Cursor cursor = db.rawQuery("select * from "+db1 +" where objid="+objid, null);
		if (cursor.moveToNext()) {
			file.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));
			file.setFilename(getpath(cursor.getString(cursor.getColumnIndex("name"))));
			file.setFilename(cursor.getString(cursor.getColumnIndex("name")));
			file.setObjid(objid);
			if (!finished)
				file.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));

		}
		cursor.close();
		return null;
	}

	private FileInfo0 getFileDataDBEntityU(String db1,String objid,boolean finished){
		FileInfo0 file=null;
		Cursor cursor = db.rawQuery("select * from "+db1 +" where objid="+objid, null);
		if (cursor.moveToNext()) {

			file.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));
			//file.setFilename(getpath(cursor.getString(cursor.getColumnIndex("name"))));
			file.setFilename(cursor.getString(cursor.getColumnIndex("name")));
			file.setObjid(objid);
			if (!finished)
				file.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));

		}
		cursor.close();
		return null;
	}


	public FileInfo0 getDownloadingFile(/*int fid*/String objid){
		return getFileDataDBEntity("download_inter", objid, false);
	}
	public FileInfo0 getUpLoadingFile(String objid){
		return getFileDataDBEntityU("upload_inter", objid, false);
	}
	
	public FileInfo0 getDownloadedFile(String  objid){
		return getFileDataDBEntity("download_finish", objid, true);
	}
	public FileInfo0 getUpLoadedFile(String objid){
		return getFileDataDBEntityU("upload_finish", objid, true);
	}
	
	public void deleteDownloadingFile(String objid){
		deleteFileDataDBEntity("download_inter", objid);
	}
	public void deleteUpLoadingFile(String objid){
		deleteFileDataDBEntity("upload_inter", objid);
	}
	
	public void deleteDownloadedFile(String objid){
		deleteFileDataDBEntity("download_finish", objid);
		
	}
	public void deleteUpLoadedFile(String objid){
		deleteFileDataDBEntity("upload_finish", objid);
		
	}
	
	public void deleteFileDataDBEntity(String db1,String objid){
		db.delete(db1, "objid=?", new String[]{objid + ""});
	}



}
