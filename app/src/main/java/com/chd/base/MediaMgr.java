package com.chd.base;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.chd.MediaMgr.utils.MFileFilter;
import com.chd.MediaMgr.utils.MediaFileUtil;
import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.photo.entity.PicDBitem;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.share.ShareUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class MediaMgr  {
	
	private SQLiteDatabase db;
	private  final  String dbname="CloudStore";
	private FTYPE _ftype;
	private boolean mExitTasksEarly;
	static ShareUtils shareUtils ;
	private   List<FileLocal> LocalUnits;
	public final  static String sZipFileMimeType = "application/zip";

	boolean contains=true;
	private Context context;

	public final int COLUMN_ID = 0;
	public final int COLUMN_PATH = 1;
	public final int COLUMN_SIZE = 2;
	public final int COLUMN_DATE = 3;
	private final String TAG=this.getClass().getName();

	public static enum DBTAB
	{
		Dling,UPing,DLed,UPed
	}


	public List<FileLocal> getLocalUnits() {
		return this.LocalUnits;
	}


	private static String buildTable(DBTAB dbtab)
	{
		String tb=null;
		switch (dbtab)
		{
			case DLed:
				tb="download_finish";
				break;
			case Dling:
				tb="download_inter";
				break;
			case UPed:
				tb="upload_finish";
			case UPing:
				tb="upload_inter";
				break;
			default:
				break;

		}
		return tb;
	}


	public MediaMgr(Context context, FTYPE ftype) {
		this.context = context;
		shareUtils = new ShareUtils(context);
		//PicCache=new HashMap<Integer, FileInfo0>(20);
		_ftype=ftype;
		if (LocalUnits==null)
			LocalUnits=new ArrayList<FileLocal>();
		/*HashSet<FileLocal> fileLocalHashSet=new HashSet<FileLocal>();
		fileLocalHashSet.contains("ddd");
		fileLocalHashSet.addAll(LocalUnits);*/
	}
	public MediaMgr(Context context) {
		this.context = context;
		shareUtils = new ShareUtils(context);
		//PicCache=new HashMap<Integer, FileInfo0>(20);
		_ftype=null;
		if (LocalUnits==null)
			LocalUnits=new ArrayList<FileLocal>();
		//HashSet<FileLocal> fileLocalHashSet=new HashSet<FileLocal>();

	}

	/*public static HashMap<StoreUtil.FileCategory, FilenameExtFilter> filters = new HashMap<StoreUtil.FileCategory, FilenameExtFilter>();
*/

	public  MFileFilter filters=new MFileFilter();
	/*public static HashMap<MediaFileUtil.FileCategory, Integer> categoryNames = new HashMap<MediaFileUtil.FileCategory, Integer>();

	static {
		categoryNames.put(MediaFileUtil.FileCategory.All,*//* R.string.category_all*//*1);
		categoryNames.put(MediaFileUtil.FileCategory.Music, *//*R.string.category_music*//*2);
		categoryNames.put(MediaFileUtil.FileCategory.Video, *//*R.string.category_video*//*3);
		categoryNames.put(MediaFileUtil.FileCategory.Picture, *//*R.string.category_picture*//*4);
		categoryNames.put(MediaFileUtil.FileCategory.Theme, *//*R.string.category_theme*//*5);
		categoryNames.put(MediaFileUtil.FileCategory.Doc, *//*R.string.category_document*//*6);
		categoryNames.put(MediaFileUtil.FileCategory.Zip, *//*R.string.category_zip*//*7);
		categoryNames.put(MediaFileUtil.FileCategory.Apk, *//*R.string.category_apk*//*8);
		categoryNames.put(MediaFileUtil.FileCategory.Other, *//*R.string.category_other*//*9);
		categoryNames.put(MediaFileUtil.FileCategory.Favorite, *//*R.string.category_favorite*//*10);
	}*/


	public void setCustomCategory(String[] exts,boolean contain) {
		/*//mCategory = FileCategory.Custom;
		if (filters.containsKey(StoreUtil.FileCategory.Custom)) {
			filters.remove(StoreUtil.FileCategory.Custom);
		}
		filters.put(StoreUtil.FileCategory.Custom, new FilenameExtFilter(exts));
		this.contains=contain;*/
		filters.setCustomCategory(exts, contain);
	}

	public void saveToSdcard(String filename, String content) throws IOException {
		context.getExternalFilesDir(Environment.DIRECTORY_DCIM);
	}

	public void open(){
		if (db==null ) {
			db = new MedSqlHelper(context, dbname + shareUtils.getLoginEntity().getId() + ".db", 1).getWritableDatabase();
			return;
		}
		if (db.isOpen())
		{
			return;
		}
		else {
			//db.close();
			db = new MedSqlHelper(context, dbname + shareUtils.getLoginEntity().getId() + ".db", 1).getWritableDatabase();
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


	public void   anlayLocalUnits(List<FileInfo0> couldlist,FilelistEntity filelistEntity) {
		int count= getLocalUnits().size();
		int count2=couldlist.size();
		int max=Math.min(count, count2);
		ArrayList<FileInfo0> baklist=new ArrayList(count2);
		int idx=0;
		int j=0,i=0;
		FileInfo0 item = null;
		for(j=0;idx<count2;j++)
		{
			FileInfo fileInfo= couldlist.get(j);
			if (idx==j) {
				item=new FileInfo0(fileInfo);
				baklist.add(item);
				//Math.max(j, idx++);
				idx++;
			}
			//for(int k=0;k<count;k++)

			for (FileLocal fileLocal: LocalUnits)
			{
				if (fileLocal.bakuped )
					continue;
				if (fileLocal.fname.equalsIgnoreCase(fileInfo.getObjid())) {
					item.setSysid(fileLocal.sysid);
					fileLocal.bakuped = true;
					if (filelistEntity != null)
						filelistEntity.addbakNumber();
					break;
				}
			}

		}
	

		couldlist.clear();
		filelistEntity.setBklist(baklist);
		//return  null;
	}


	public void GetLocalFiles(MediaFileUtil.FileCategory fc,String[] exts,boolean include)
	{
		//setCustomCategory(new String[]{"doc", "pdf", "xls", "zip", "rar"}, true);
		if (LocalUnits!=null && !LocalUnits.isEmpty())
			return;
		setCustomCategory(exts, include);
		Cursor c =query(/*MediaFileUtil.FileCategory.File*/fc, MediaFileUtil.FileCategory.All, MediaFileUtil.SortMethod.date);

		while (c.moveToNext())
		{
			String fpath=c.getString(COLUMN_PATH);
			if (filters.contains(fpath)==false)
			{
				continue;
			}
			FileLocal fileLocal =new FileLocal();
			fileLocal.sysid=c.getInt(COLUMN_ID);
			fileLocal.fname= MediaFileUtil.getFnameformPath(c.getString(COLUMN_PATH));
			LocalUnits.add(fileLocal);
		}
		c.close();
		if (c == null) {
			Log.e("", "fail to query uri" );
		}

	}

	public List<FileLocal> GetPartLocalFiles(MediaFileUtil.FileCategory fc, String[] exts, boolean include, int begin, int max)
	{
		List locals=new ArrayList<FileLocal>();
		setCustomCategory(exts, include);
		Cursor c =query(/*MediaFileUtil.FileCategory.File*/fc, MediaFileUtil.FileCategory.All, MediaFileUtil.SortMethod.date);
		int count=0,total=0;
		while (c.moveToNext())
		{
			String fpath=c.getString(COLUMN_PATH);
			if (filters.contains(fpath)==false)
			{
				continue;
			}
			count++;
			if (count<begin)
				continue;
			FileLocal fileLocal =new FileLocal();
			fileLocal.sysid=c.getInt(COLUMN_ID);
			fileLocal.fname= MediaFileUtil.getFnameformPath(c.getString(COLUMN_PATH));
			locals.add(fileLocal);
			total++;
			if (total>=max)
				break;
		}
		c.close();
		if (c == null) {
			Log.e("", "fail to query uri" );
		}
		return locals;
	}

	public  boolean queryLocalInfo(int sysid/*,FTYPE ftype*/,FileInfo0 fileInfo0)
	{

		//MediaFileUtil.FileCategory fc0= MediaFileUtil.FileCategory.File;
		boolean ret=false;
		MediaFileUtil.FileCategory fc;
		switch (fileInfo0 .getFtype())
		{
			case MUSIC:
				fc=MediaFileUtil.FileCategory.Music;
				break;
			case PICTURE:
				fc=MediaFileUtil.FileCategory.Picture;
				break;
			case NORMAL:
				fc=MediaFileUtil.FileCategory.Other;
				break;
			default:
				fc= MediaFileUtil.FileCategory.File;
		}
		Uri uri = getContentUriByCategory(fc);
		String selection = buildSelectionByCategory(MediaFileUtil.FileCategory._ID);
		String sortOrder = null;
	
		if (uri == null) {
			Log.e("", "invalid uri, category:" + fc.name());
			return ret;
		}

		String[] columns = new String[] {
				MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED
		};
		Cursor cursor= context.getContentResolver().query(uri, columns, selection, new String[]{""+sysid }, sortOrder);
		while(cursor.moveToNext())
		{
			fileInfo0.setFilePath( cursor.getString(COLUMN_PATH));
			if (fileInfo0.getObjid()!=null)
				Log.d(TAG,"is a  remote obj");	
		    fileInfo0.setObjid(MediaFileUtil.getNameFromFilepath(cursor.getString(COLUMN_PATH)));
				
			fileInfo0.setFilesize(cursor.getInt(COLUMN_SIZE));
			fileInfo0.setLastModified(cursor.getInt(COLUMN_DATE));
			fileInfo0.setSysid(sysid);

			ret=true;

		}
		cursor.close();
		return ret;
	}

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
		Cursor cursor = db.rawQuery("select sysid,path,time from upload_finish where type=? union  select sysid,time from upload_inter where type=? order by time desc", new String[]{"" + this._ftype, "" + this._ftype});
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
		Cursor cursor = db.rawQuery("select path,time from download_finish where type=? order by time desc", new String[]{""/*+wheresection*/});
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
		Cursor cursor = db.rawQuery("select * from upload_finished" +" where type=? objid="+objid, /*wheresection*/null);
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

	//public abstract ArrayList<Integer> anlayLocalUnits(List<FileInfo0> couldlist);



	public  void  setUploadinfFile0(FileInfo0 entity)
	{
		ContentValues values = new ContentValues();
		values.put("objid",entity.getObjid());
		values.put("offset",entity.getOffset());
		db.update("upload_inter", values, "sysid=?", new String[]{entity.getSysid() + ""});
	}


	private Uri getContentUriByCategory(MediaFileUtil.FileCategory cat) {
		Uri uri;
		String volumeName = "external";
		switch(cat) {
			case Theme:
			case Doc:
			case Zip:
			case Apk:
			case Other:
			case File:
				uri = MediaStore.Files.getContentUri(volumeName);
				break;
			case Music:
				uri = MediaStore.Audio.Media.getContentUri(volumeName);
				break;
			case Video:
				uri = MediaStore.Video.Media.getContentUri(volumeName);
				break;
			case Picture:
				uri = MediaStore.Images.Media.getContentUri(volumeName);
				break;
			default:
				uri = null;
		}
		return uri;
	}

	private String buildSortOrder(MediaFileUtil.SortMethod sort) {
		String sortOrder = null;
		switch (sort) {
			case name:
				sortOrder = MediaStore.Files.FileColumns.TITLE + " asc";
				break;
			case size:
				sortOrder = MediaStore.Files.FileColumns.SIZE + " asc";
				break;
			case date:
				sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " desc";
				break;
			case type:
				sortOrder = MediaStore.Files.FileColumns.MIME_TYPE + " asc, " + MediaStore.Files.FileColumns.TITLE + " asc";
				break;
		}
		return sortOrder;
	}


	private String buildSelectionByCategory(MediaFileUtil.FileCategory cat) {
		String selection = null;
		switch (cat) {
			case Theme:
				selection = MediaStore.Files.FileColumns.DATA + " LIKE '%.mtz'";
				break;
			case Doc:
				selection = buildDocSelection();
				break;
			case Zip:
				selection = "(" + MediaStore.Files.FileColumns.MIME_TYPE + " == '" + sZipFileMimeType + "')";
				break;
			case Apk:
				selection = MediaStore.Files.FileColumns.DATA + " LIKE '%.apk'";
				break;
			/*case File:
				selection = MediaStore.Files.FileColumns._ID +" = ?";
				break;*/
			case _ID:
				selection = MediaStore.Files.FileColumns._ID +" = ?";
				break;
			default:
				selection = null;
		}
		return selection;
	}

	private String buildDocSelection() {
		StringBuilder selection = new StringBuilder();
		Iterator<String> iter = MediaFileUtil.sDocMimeTypesSet.iterator();
		while(iter.hasNext()) {
			selection.append("(" + MediaStore.Files.FileColumns.MIME_TYPE + "=='" + iter.next() + "') OR ");
		}
		return  selection.substring(0, selection.lastIndexOf(")") + 1);
	}

	public Cursor query(MediaFileUtil.FileCategory fc,MediaFileUtil.FileCategory cond, MediaFileUtil.SortMethod sort) {
		Uri uri = getContentUriByCategory(fc);
		String selection = buildSelectionByCategory(cond);
		String sortOrder = buildSortOrder(sort);
		String where=null;

		if (uri == null) {
			Log.e("", "invalid uri, category:" + fc.name());
			return null;
		}

		String[] columns = new String[] {
				MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED
		};
		return context.getContentResolver().query(uri, columns, selection, null, sortOrder);
	}





	public static  void fileScan(String fName,Context context1){
		Uri data = Uri.parse("file:///" + fName);
		context1.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
	}


	public void add2mediaStore(FileInfo0 info0)
	{

		if (1==1) {
			this.LocalUnits.clear();
			fileScan(info0.getFilePath(),context);
			return;
		}


		ContentResolver localContentResolver = context.getContentResolver();

//		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "picPath")));

		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "picPath")));

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

		//int d=localContentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media._ID +"= ?",new String[]{String.valueOf(mid)});

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
		return ;
	}



///////////////////////// 下载记录管理部分////////////////////////////

    // 通过文件名找到downloaded记录
    public boolean QueryDownloadedFile(FileInfo0 info0,boolean autopen) {
		if (autopen)
        	open();
		String path=info0.getFilePath()==null? new ShareUtils(context).getStorePathStr()+File.separator+
				info0.getObjid():info0.getFilePath();
     	if ( new File(path).exists())
			return  true;
		boolean ret=QueryDBEntity(DBTAB.DLed,info0);
		if (autopen)
        	close();
        return ret;
    }

	// 通过文件名找到uploaded记录
	public boolean QueryUploadedFile(FileInfo0 info0,boolean autopen) {
		if (autopen)
			open();
		boolean ret=QueryDBEntity(DBTAB.UPed,info0);
		if (autopen)
			close();
		return ret;
	}


	/*public List<FileInfo0> getUpLoadingFiles(){
		return getFileDataDBEntitiesU("upload_inter", false);
	}
	
	public List<FileInfo0> getDownloadedFiles(){
		return getFileDataDBEntities("download_finish", true);
	}*/
	/*public List<FileInfo0> getUpLoadedFiles(){
		return getFileDataDBEntitiesU("upload_finish", true);
	}*/
	
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

	public boolean QueryDBEntity(DBTAB dbtab,FileInfo0 info0){
		boolean ret=false;
		if (!info0.isSetObjid() )
			return ret;
		if (!info0.isSetFtype() && _ftype==null )
			return ret;
		int type=info0.isSetFtype()?info0.getFtype().getValue():_ftype.getValue();

		Cursor cursor = db.rawQuery("select * from "+buildTable(dbtab) +" where type= "+ type + " and objid='" +info0.getObjid()+"'",null);

		boolean finished=(dbtab.equals(DBTAB.UPing) || dbtab.equals(DBTAB.Dling));
		if (cursor.moveToNext()) {
			if (info0.getFilePath()==null)
				info0.setFilePath(cursor.getString(cursor.getColumnIndex("path")));
			if (info0.getOffset()<1  && !finished )
				info0.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));
			if (!info0.isSetLastModified())
				info0.setLastModified(cursor.getInt(cursor.getColumnIndex("time")));
			if (!info0.isSetFilesize() && !finished && dbtab.equals(DBTAB.Dling))
				info0.setFilesize(cursor.getLong(cursor.getColumnIndex("size")));
			ret=true;
		}
		cursor.close();
		return ret;
	}
	private List<FileInfo0> QueryDBUnits(DBTAB dbtab,FTYPE ftype,int max){
		//boolean ret=false;
		String sel="";
		if (ftype!=null)
			sel="where type= "+ftype.getValue();
		String sql="select sysid,objid,size,offset,time from "+buildTable(dbtab) +sel+ " order by time ";
		Cursor cursor = db.rawQuery(sql,null);
		List<FileInfo0> list=new ArrayList<FileInfo0>();
		boolean finished=(dbtab.equals(DBTAB.UPing) || dbtab.equals(DBTAB.Dling));
		int count=cursor.getCount();
		while (cursor.moveToNext() && max>0) {
			FileInfo0 info0=new FileInfo0();
			info0.setFilePath(cursor.getString(cursor.getColumnIndex("objid")));
			info0.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));
			info0.setLastModified(cursor.getInt(cursor.getColumnIndex("time")));
			info0.setFilesize(cursor.getLong(cursor.getColumnIndex("size")));
			try {
				int cid=0;
				cid=cursor.getColumnIndexOrThrow("sysid");
				info0.setSysid(cursor.getInt(cid));
			}catch (Exception e)
			{
				Log.e(TAG,e.getMessage());
			}
			max--;
			list.add(info0);
		}
		cursor.close();
		db.close();
		return list;
	}

	public void addUpLoadingFile(FileInfo0 entity){
		ContentValues values = new ContentValues();
		//values.put(/*"hash"*/"sysid",/*entity.getFilePath().hashCode()*/entity.getSysid());
		values.put("objid",entity.getObjid());
		values.put("path", entity.getFilePath());
		values.put("size",entity.getFilesize());
		values.put("type", (entity.getFtype()).getValue());
		values.put("offset",0);
		values.put("time", /*TimeUtils.getCurrentTimeInLong()*/entity.getLastModified());
		db.insertWithOnConflict("upload_inter", null, values, SQLiteDatabase.CONFLICT_IGNORE);
	}

	public  void setUploadStatus(FileInfo0 entity)
	{
		ContentValues values = new ContentValues();
		values.put("sysid",entity.getSysid());
		values.put("objid",entity.getObjid());
		values.put("offset", entity.getOffset());
		int ret=db.update("upload_inter", values, "type=? and objid=?", new String[]{String.valueOf(entity.getFtype()), entity.getObjid()});
	}



	public void addDownloadingFile(FileInfo0 entity){
		ContentValues values = new ContentValues();
		//values.put("objid", entity.getObjid());
		values.put("objid",entity.getObjid());
		values.put("size",entity.getFilesize());
		values.put("path",entity.getFilePath());
		values.put("offset",0);
		values.put("type", (entity.getFtype()).getValue());
		values.put("time", entity.getLastModified());
		db.insertWithOnConflict("download_inter", null, values, SQLiteDatabase.CONFLICT_IGNORE);
	}

	public  void setDownloadStatus(FileInfo0 entity)
	{
		ContentValues values = new ContentValues();
		values.put("sysid",entity.getSysid());
		values.put("objid",entity.getObjid());
		values.put("offset", entity.getOffset());
		db.update("download_inter", values, "type=? and objid=?", new String[]{String.valueOf(entity.getFtype()), entity.getObjid()});
	}

	public void finishTransform(DBTAB dbtab,FileInfo0 info0)
	{
		/*boolean dl=dbtab.equals(DBTAB.DLed);
		boolean up=dbtab.equals(DBTAB.UPed);
		*/
		switch (dbtab)
		{
			case DLed:
				addDownloadedFile(info0);
				return;
			case UPed:
				addUpLoadedFile(info0);
				return;

		}
	}

	public  List<FileInfo0> getUpLoadTask(int max)
	{
		return QueryDBUnits(DBTAB.UPing,null,max);
	}

	public  List<FileInfo0> getDlLoadTask(int max)
	{
		return QueryDBUnits(DBTAB.Dling,null,max);
	}

	public void addDownloadedFile(FileInfo0 entity){
		ContentValues values = new ContentValues();
		values.put("objid", entity.getObjid());
		values.put("path", entity.getFilePath());
		values.put("size", entity.getFilesize());
		values.put("time", entity.getLastModified());
		values.put("type", entity.getFtype().getValue());
		deleteUpLoadingFile(entity.getObjid());
		db.insertWithOnConflict("upload_finish", null, values, SQLiteDatabase.CONFLICT_IGNORE);

	}

	public void addUpLoadedFile(FileInfo0 entity){
		ContentValues values = new ContentValues();
		values.put("objid", entity.getObjid());
		values.put("size",entity.getFilesize());
		values.put("path",entity.getFilePath());
		values.put("sysid",entity.getSysid());
		values.put("type", entity.getFtype().getValue());
		values.put("time", entity.getLastModified());
		deleteUpLoadingFile(entity.getObjid());
		db.insertWithOnConflict("upload_finish", null, values, SQLiteDatabase.CONFLICT_IGNORE);
	}


	private void deleteDownloadingFile(String objid){
		deleteFileDataDBEntity("download_inter", objid);
	}
	private void deleteUpLoadingFile(String objid){
		deleteFileDataDBEntity("upload_inter", objid);
	}
	
	private void deleteDownloadedFile(String objid){
		deleteFileDataDBEntity("download_finish", objid);
		
	}

	private void deleteUpLoadedFile(String objid){
		deleteFileDataDBEntity("upload_finish", objid);
		
	}
	
	private void deleteFileDataDBEntity(String db1,String objid){
		db.delete(db1, "objid=?", new String[]{objid + ""});
	}

}
