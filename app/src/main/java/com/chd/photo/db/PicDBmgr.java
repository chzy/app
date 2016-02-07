package com.chd.photo.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.chd.DBhandler;
import com.chd.photo.entity.PicDBitem;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.share.ShareUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class PicDBmgr implements DBhandler {
	private SQLiteDatabase db;
/*	private String pfpath;*/
	private  final  String dbname="photo";
	private boolean mExitTasksEarly;
	static ShareUtils shareUtils ;
	private HashMap<Integer,FileInfo0> PicCache;
    private  static List<PicDBitem> _dloadUnits ;
    private  static List<Integer>  _uploadUnits;
	//private  Cursor mcursor;

	private Context context;

	public PicDBmgr(Context context) {
		this.context = context;
		shareUtils = new ShareUtils(context);
		PicCache=new HashMap<Integer, FileInfo0>(20);
	}

	public PicDBmgr(Context context, String prefixPath) {
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
		if (db==null ) {
			db = new MySQLHelper(context, dbname + shareUtils.getLoginEntity().getUserid() + ".db", 1).getWritableDatabase();
			return;
		}
		if (db.isOpen())
		{
			return;
		}

		//ShareUtils shareUtils = new ShareUtils(context);;
	}
	public void close(){
		if(db!=null){
			if(db.isOpen()){
				db.close();
			}
		}
	}

	/*private String getpath(String fname)
	{
		return  pfpath+ File.pathSeparator+fname;
	}*/
/*
		db.execSQL("CREATE TABLE download_finish (objid TEXT PRIMARY KEY ,time Text ,size INTEGER,name text )");
		db.execSQL("CREATE TABLE download_inter ( objid TEXT PRIMARY KEY,size  INTEGER , time Text ,offset INTEGER,name text )");
		db.execSQL("CREATE TABLE upload_finish (objid TEXT PRIMARY KEY,sysid INTEGER, time Text , size INTEGER,path text )");
		db.execSQL("CREATE TABLE upload_inter (sysid INTEGER PRIMARY KEY,path text,name text,size  INTEGER ,time INTEGER , offset INTEGER,objid text )");
	;*/

	public List<PicDBitem> getUploadUnits(){
		open();
		Cursor cursor = db.rawQuery("select sysid,path,time from upload_finish union  select sysid,time from upload_inter order by time desc", null);
		//Cursor cursor = db.rawQuery("select sysid,time from upload_finish, upload_inter order by time desc", null);
		List<PicDBitem> lists = new ArrayList<PicDBitem>();
		while (cursor.moveToNext()) {
			PicDBitem item=new PicDBitem();
			item.setSysid(cursor.getInt(0));
			item.setPath(cursor.getString(1));
			lists.add(item);
		}
		cursor.close();
		close();
		//Collections.sort(lists,new SortBydesc());
		return lists;
	}

	public List<Integer> getUploadUnits_(){
		open();
		Cursor cursor = db.rawQuery("select sysid,path,time from upload_finish union  select sysid,time from upload_inter order by time desc", null);
		//Cursor cursor = db.rawQuery("select sysid,time from upload_finish, upload_inter order by time desc", null);
		List<Integer> lists = new ArrayList<Integer>();
		while (cursor.moveToNext()) {
			lists.add(cursor.getInt(0));
		}
		cursor.close();
		close();
		//Collections.sort(lists,new SortBydesc());
		return lists;
	}

	public List<PicDBitem> getdownoadUnits(){
		open();
		Cursor cursor = db.rawQuery("select path,time from download_finish  order by time desc", null);
		//Cursor cursor = db.rawQuery("select sysid,time from upload_finish, upload_inter order by time desc", null);
		List<PicDBitem> lists = new ArrayList<PicDBitem>();
		while (cursor.moveToNext()) {
			PicDBitem item=new PicDBitem();
			item.setPath(cursor.getString(0));
			lists.add(item);
		}
		cursor.close();
		close();
		//Collections.sort(lists,new SortBydesc());
		return lists;
	}

	public FileInfo0 getPiceParam(int picId) {
	return  null;
	}


		protected class SortBydesc implements Comparator<Object> {
		@Override
		public int compare(Object o1, Object o2) {
			if ((Integer) o1 > (Integer) o2) {
				return 1;
			} else {
				return 0;
			}
		}
	}


	private FileInfo0 getUploadeItem(String objid,FileInfo0 info){
		FileInfo0 file=null;
		Cursor cursor = db.rawQuery("select * from upload_finished" +" where objid="+objid, null);
		if (cursor.moveToNext()) {
			file.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));
			//file.setFilename(getpath(cursor.getString(cursor.getColumnIndex("name"))));
			file.setFilename(cursor.getString(cursor.getColumnIndex("name")));
			file.setObjid(objid);
			//if (!finished)
				file.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));

		}
		cursor.close();
		return null;
	}

	public ArrayList<Integer> unbklist(List<FileInfo0> couldlist) {
		String[] projection = {
				MediaStore.Audio.Media.IS_MUSIC
				, MediaStore.Images.Media._ID
				, MediaStore.Images.Media.DATA
				, MediaStore.Images.Media.DATE_MODIFIED
		};
		Cursor cursor = this.context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				projection,
				null,
				null,
				MediaStore.Audio.Media.DATE_MODIFIED + " desc"
		);
		ArrayList<Integer> unbklist = new ArrayList<Integer>();//未备份的数组
		int origId=0;
		boolean duplicated=false;
		while (cursor.moveToNext() && !mExitTasksEarly) {   //移到指定的位置，遍历数据库
			if (cursor.getInt(0) == 0) {
				continue;
			}
			origId = cursor.getInt(0);
			for(FileInfo0 item:couldlist)
			{
				if (item.getObjid().equalsIgnoreCase(cursor.getString(2)))
				{
					item.setSysid(origId);
					duplicated=true;
					break;
				}
			}
			if (duplicated)
				continue;
			unbklist.add(origId);
		}
		cursor.close();//关闭数据库
		if (mExitTasksEarly) {
			return null;
		}
		return unbklist;
	}

/*

	public ArrayList<Integer> anlayLocalUnits()
	{
		String[] projection = {
				MediaStore.Images.Media._ID
				,MediaStore.Images.Media.DATA
				,MediaStore.Images.Media.DATE_MODIFIED
				};
		Uri ext_uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI*/
/*||MediaStore.Images.Media.INTERNAL_CONTENT_URI*//*
;
		Cursor	 cursor = MediaStore.Images.Media.query(
				context.getContentResolver(),
				ext_uri,
				projection,
				null,
               */
/* new String[] { "order by "+MediaStore.Images.Media.DATA + " desc" },*//*

				null,
               */
/* *//*
*/
/*new String[]{*//*
*/
/**//*
*/
/*1 * 100 * 1024 +*//*
*/
/**//*
*/
/* "0"}*//*
*/
/*null,*//*

				MediaStore.Images.Media.DATE_MODIFIED+" desc");

		int i = 0;
		int year=0;
		int month=0;
		Calendar cal = Calendar.getInstance();



		ArrayList<Integer> anlayLocalUnits=new ArrayList<Integer>();//未备份的图片数组
		List<PicDBitem>	dllist=	getdownoadUnits();
		PicDBitem dBitem=new PicDBitem();
		while (cursor.moveToNext()  && !mExitTasksEarly) {   //移到指定的位置，遍历数据库

			int origId = cursor.getInt(0);
			//dBitem.setSysid(origId);
			dBitem.setPath(cursor.getString(1));
			if (dllist.contains(dBitem))
				continue;
			anlayLocalUnits.add(origId);
		}
		cursor.close();//关闭数据库
		//上传的文件
		List<Integer>  locallist= getUploadUnits_();
		anlayLocalUnits.removeAll(locallist);
		locallist.clear();
		locallist=null;
		dllist.clear();
		dllist=null;

		if (mExitTasksEarly) {
			return null;
		}
		return anlayLocalUnits;
	}
*/





	/*public FileInfo0 getPiceParam(int picId)
	{
		if (PicCache.containsKey(picId))
			return  PicCache.get(picId);
		FileInfo0 info0=null;
		String[] projection = {
				MediaStore.Images.Media._ID
				,MediaStore.Images.Media.DATE_MODIFIED
				,MediaStore.Images.Media.SIZE
				,MediaStore.Images.Media.DATA
		};
		Uri ext_uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI*//*||MediaStore.Images.Media.INTERNAL_CONTENT_URI*//*;
		*//**
		 * 查询得到图片DATA ,如果图片过多 可能存储data 内存可能溢出
		 * 这样是为了节省内存。通过图片的ID可以查询得到指定的图片
		 * 如果这里就把图片数据查询得到，手机中的图片大量的情况下
		 * 内存消耗严重。那么，什么时候查询图片呢？应该是在Adapter
		 * 中完成指定的ID的图片的查询，并不一次性加载全部图片数据
		 *//*
		//if (mcursor==null || mcursor.isClosed())
		Cursor	cursor = MediaStore.Images.Media.query(
				context.getContentResolver(),
				ext_uri,
				projection,
				MediaStore.Images.Media._ID + ">=?" + " and "+ MediaStore.Images.Media._ID + "<?"  ,
                new String[]{String.valueOf(picId),String.valueOf(picId+20)},
				*//*MediaStore.Images.Media.DATE_ADDED+" desc"*//*null);

		int i = 0;
		//if (PicCache.size()==20)
		int count=cursor.getCount();
		PicCache.clear();
		while (cursor.moveToNext()  && !mExitTasksEarly)
		{   //移到指定的位置，遍历数据库
			// long origId = c.getLong(columnIndex);
			info0=new FileInfo0();
			i=0;
			int origId = cursor.getInt(i);
			i++;
			long timestamp = cursor.getLong(*//*c.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)*//*i);
			info0.setLastModified(timestamp);
			i++;
			long lenth=cursor.getLong(i);
			info0.setFilesize(lenth);
			i++;
			String path=cursor.getString(i);
			info0.setFilePath(path);
			PicCache.put(origId,info0);
		}
		cursor.close();//关闭数据库
		if (mExitTasksEarly) {
			return null;
		}
		return info0;
	}
*/


	public  void  setUploadinfFile0(FileInfo0 entity)
	{
		ContentValues values = new ContentValues();
		values.put("objid",entity.getObjid());
		values.put("offset",entity.getOffset());
		db.update("upload_inter", values, "sysid=?", new String[]{entity.getSysid() + ""});
	}

	/*Cursor cursor = db.rawQuery("select last_insert_rowid() from person",null);
	int strid;
	if(cursor.moveToFirst())
	strid = cursor.getInt(0);*/


	public int add2mediaStore(FileInfo0 info0)
	{

		ContentResolver localContentResolver = context.getContentResolver();
//		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "picPath")));
		Cursor cursor=localContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "select max(_ID)", null, null);
		//Cursor cursor=localContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "select _ID,_DATA ",new String[] {"group by _ID having max(ID)"}, null);
		cursor.moveToFirst();
		int mid=cursor.getInt(0);
		String[] projection = {
				MediaStore.Images.Media._ID
				,MediaStore.Images.Media.DATE_MODIFIED
				, MediaStore.Images.Media.SIZE
				,MediaStore.Images.Media.DATA
		};

		cursor.close();
		 cursor=localContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.Media._ID +"= ?", new String[]{String.valueOf(mid)}, null);
		cursor.moveToFirst();


		/*try {
			MediaStore.Images.Media.insertImage(localContentResolver,info0.getFilePath(),"","");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/

		ContentValues localContentValues = new ContentValues();
		localContentValues.put(MediaStore.Images.Media.DATA, cursor.getString(3));
		localContentValues.put(MediaStore.Images.Media.SIZE, cursor.getInt(2));
		localContentValues.put(MediaStore.Images.Media.DATE_MODIFIED,cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)));
		localContentValues.put("mime_type", "image/jpeg");
		cursor.close();

		int d=localContentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media._ID +"= ?",new String[]{String.valueOf(mid)});

		/*localContentValues.put(MediaStore.Images.Media.DATA, info0.getFilePath());
		localContentValues.put(MediaStore.Images.Media.SIZE, info0.getFilesize());
		localContentValues.put(MediaStore.Images.Media.DATE_MODIFIED,info0.getLastModified());
		localContentValues.put("mime_type", "image/jpeg");
*/
		//MediaStore.Images.Media.insertImage()


		Uri uri=localContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);
		//long id = ContentUris.parseId(uri);
		//String path=uri.getPath();


		cursor=localContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,"select last_insert_rowid()",  null, null);
		int id=0;
		if (cursor.moveToFirst())
			 id=cursor.getInt(0);
		//return  id;
		return 0;
	}



	public void addUpLoadingFile(FileInfo0 entity){
		ContentValues values = new ContentValues();
		values.put("sysid",entity.getSysid());
		values.put("path", entity.getFilePath());
		values.put("size",entity.getFilesize());
		values.put("name",entity.getFilename());
		values.put("offset",0);
		values.put("time", entity.getLastModified());
		db.insertWithOnConflict("upload_inter", null, values, SQLiteDatabase.CONFLICT_IGNORE);
//
	}

	public  void  setUploadinfFile(FileInfo0 entity)
	{
		ContentValues values = new ContentValues();
		values.put("sysid",entity.getSysid());
		values.put("offset",entity.getOffset());
		db.update("upload_inter", values, "sysid=?", new String[]{entity.getSysid() + ""});
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


	public void updateDownloadingFile(FileInfo0 entity) throws IOException{
		if(getDownloadingFile(entity.getObjid())==null){
			throw new IOException();
		}
		ContentValues values = new ContentValues();
		values.put("objid", entity.getObjid());
		values.put("offset", entity.getOffset());
		db.update("download_inter", values, "objid=?", new String[]{entity.getObjid() + ""});
		Log.d("@@@", "call db update updateDownloadingFile");
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
		values.put("sysid",entity.getSysid());
		deleteUpLoadingFile(entity.getObjid());
		db.insertWithOnConflict("upload_finish", null, values, SQLiteDatabase.CONFLICT_IGNORE);

	}
	public void addUpLoadedFile(FileInfo0 entity){
		ContentValues values = new ContentValues();
		values.put("objid", entity.getObjid());
		values.put("size",entity.getFilesize());
		values.put("name",entity.getFilename());
		values.put("time", entity.getLastModified());
		db.insertWithOnConflict("upload_finish", null, values, SQLiteDatabase.CONFLICT_IGNORE);
	}

	
	public void updateUpLoadingFile(FileInfo0 entity){
		ContentValues values = new ContentValues();
		values.put("objid", entity.getObjid());
		values.put("offset",entity.getOffset());
		db.update("upload_inter", values, "objid=?", new String[]{entity.getObjid() + ""});
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////
	public List<FileInfo0> getDownloadingFiles(){
		return getFileDataDBEntities("download_inter",false);
	}
	
	private List<FileInfo0> getFileDataDBEntities(String db1,boolean finished){
		Cursor cursor = db.rawQuery("select * from "+db1, null);
		List<FileInfo0> lists = new ArrayList<FileInfo0>();
		while (cursor.moveToNext()) {
			FileInfo0 file=new FileInfo0();
			file.setObjid(cursor.getString(cursor.getColumnIndex("objid")));
			//file.setFilename(cursor.getString(cursor.getColumnIndex("name")));
			if (!finished)
				file.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));
			lists.add(file);
		}
		cursor.close();
		return lists;
	}

	

	private List<FileInfo0> getUpfinishedDBEntity(FTYPE ftype){
		Cursor cursor = db.rawQuery("select * from upload_finish where ftype = "+ftype+" order by time desc" , null);
		List<FileInfo0> lists = new ArrayList<FileInfo0>();
		while (cursor.moveToNext()) {
			FileInfo0 file=new FileInfo0();
			file.setObjid(cursor.getString(cursor.getColumnIndex("objid")));
			file.setFilename(cursor.getString(cursor.getColumnIndex("name")));
			file.setFilePath(cursor.getString(cursor.getColumnIndex("path")));
			file.setLastModified(cursor.getInt(cursor.getColumnIndex("time")));
			lists.add(file);
		}
		cursor.close();
		return lists;
	}

	//////////////////used////////////////
	private List<FileInfo0> getFileDataDBEntitiesU(String db1,boolean finished){
		Cursor cursor = db.rawQuery("select * from "+db1, null);
		List<FileInfo0> lists = new ArrayList<FileInfo0>();
		while (cursor.moveToNext()) {
			FileInfo0 file=new FileInfo0();
			file.setObjid(cursor.getString(cursor.getColumnIndex("objid")).trim());
			/*
			String fName = file.getObjid();
			String fileName = fName.substring(fName.lastIndexOf("\\")+1);
			*/
			file.setFilePath(cursor.getString(cursor.getColumnIndex("path")));
			//file.setFilename(cursor.getString(cursor.getColumnIndex("name")));
			if (!finished)
				file.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));
			lists.add(file);
		}
		cursor.close();
		return lists;
	}

    // 通过文件名找到记录
    public boolean GetDownloadedFile(FileInfo0 info0) {
        open();
        Cursor cursor = db.rawQuery("select objid,path from download_finish where objid=?", new String[]{info0.getObjid()});
        List<FileInfo0> lists = new ArrayList<FileInfo0>();
        boolean ret=false;
        while (cursor.moveToNext()) {
            String path=cursor.getString(cursor.getColumnIndex("path"));
			if (_dloadUnits.isEmpty())
				getdownoadUnits();
            for (PicDBitem item:_dloadUnits)
            {
                if (item.getPathid()==path.hashCode()) {
                    info0.setSysid(item.getSysid());
                    info0.setFilePath(path);
                    ret=true;
                    break;
                }
            }
        }
        cursor.close();
        ContentValues values = new ContentValues();
        values.put("sysid", info0.getSysid());
        db.update("download_finish", values, "objid=?", new String[]{info0.getObjid() + ""});
        close();

        return ret;
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
		Cursor cursor = db.rawQuery("select * from "+db1 +" where objid=" + objid, null);
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
