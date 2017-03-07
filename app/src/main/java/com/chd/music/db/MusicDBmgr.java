package com.chd.music.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.chd.photo.entity.PicDBitem;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.share.ShareUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MusicDBmgr  {
    private SQLiteDatabase db;
    private String pfpath;
    private final String dbname = "music";
    private boolean mExitTasksEarly;
    static ShareUtils shareUtils;
    private HashMap<Integer, FileInfo0> PicCache;
    private final int MaxCaches = 20;
    //private final static ArrayList<Integer> anlayLocalUnits = new ArrayList<Integer>();//未备份的数组
    private  static List<PicDBitem> _dloadUnits ;
    private  static List<Integer>  _uploadUnits;

    //private  Cursor mcursor;

    private Context context;

    public MusicDBmgr(Context context) {
        this.context = context;
        shareUtils = new ShareUtils(context);
        PicCache = new HashMap<Integer, FileInfo0>(MaxCaches);
        _dloadUnits=new ArrayList<PicDBitem>();
        _uploadUnits=new ArrayList<Integer>();
    }


    public void saveToSdcard(String filename, String content) throws IOException {
        context.getExternalFilesDir(Environment.DIRECTORY_DCIM);
    }

    public void open() {
        if (db == null) {
            db = new MySQLHelper(context, dbname + shareUtils.getLoginEntity().getUserid() + ".db", 1).getWritableDatabase();
            return;
        }
        if (db.isOpen()) {
            return;
        }


    }

    public void close() {
        if (db != null) {
            if (db.isOpen()) {
                db.close();
                db=null;
            }
        }
    }

    private String getpath(String fname) {
        return pfpath + File.pathSeparator + fname;
    }
/*
        db.execSQL("CREATE TABLE download_finish (objid TEXT PRIMARY KEY ,time Text ,size INTEGER,name text )");
		db.execSQL("CREATE TABLE download_inter ( objid TEXT PRIMARY KEY,size  INTEGER , time Text ,offset INTEGER,name text )");
		db.execSQL("CREATE TABLE upload_finish (objid TEXT PRIMARY KEY,sysid INTEGER, time Text , size INTEGER,path text )");
		db.execSQL("CREATE TABLE upload_inter (sysid INTEGER PRIMARY KEY,path text,name text,size  INTEGER ,time INTEGER , offset INTEGER,objid text )");
	;*/


    public List<Integer> getUploadUnits_() {
        open();
        Cursor cursor = db.rawQuery("select sysid,path,time from upload_finish union  select sysid,time from upload_inter order by time desc", null);
        //Cursor cursor = db.rawQuery("select sysid,time from upload_finish, upload_inter order by time desc", null);
        //List<Integer> lists = new ArrayList<Integer>();
        while (cursor.moveToNext()) {
            _uploadUnits.add(cursor.getInt(0));
        }
        cursor.close();
        close();
        //Collections.sort(lists,new SortBydesc());
        return _uploadUnits;
    }


    public List<PicDBitem> getdownoadUnits() {
        //open();
        Cursor cursor = db.rawQuery("select path,time from download_finish  order by time desc", null);
        //Cursor cursor = db.rawQuery("select sysid,time from upload_finish, upload_inter order by time desc", null);
        /*List<PicDBitem> lists =*/ _dloadUnits=new ArrayList<PicDBitem>();
        while (cursor.moveToNext()) {
            PicDBitem item = new PicDBitem();
            item.setPath(cursor.getString(0));
            _dloadUnits.add(item);
        }
        cursor.close();
       // close();
        //Collections.sort(lists,new SortBydesc());
        return _dloadUnits;
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

    public ArrayList<Integer> anlayLocalUnits() {
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
        int i = 0;
        int year = 0;
        int month = 0;
        Calendar cal = Calendar.getInstance();
        ArrayList<Integer> anlayLocalUnits = new ArrayList<Integer>();//未备份的数组
        getdownoadUnits();
        PicDBitem dBitem = new PicDBitem();
		int origId=0;
        Iterator<PicDBitem> iterator= _dloadUnits.iterator();
        boolean duplicated=false;
        while (cursor.moveToNext() && !mExitTasksEarly) {   //移到指定的位置，遍历数据库
            if (cursor.getInt(0) == 0) {
                continue;
            }
            origId = cursor.getInt(0);
            dBitem.setPath(cursor.getString(1));
			//if (_dloadUnits.contains(dBitem))
			//	continue;
           while (iterator.hasNext())
            {
                PicDBitem item=iterator.next();
                if (item.equals(dBitem))
                {
                    item.setSysid(origId);
                    duplicated=true;
                    break;
                }
            }
            if (duplicated)
                continue;
            anlayLocalUnits.add(origId);
        }
        cursor.close();//关闭数据库
		//上传的文件
		List<Integer>  uploadUnits= getUploadUnits_();
		anlayLocalUnits.removeAll(uploadUnits);
		uploadUnits.clear();
		uploadUnits=null;
        if (mExitTasksEarly) {
            return null;
        }
        return anlayLocalUnits;
    }

*/



		public int add2mediaStore(FileInfo0 info0)
	{

		//ContentResolver localContentResolver = context.getContentResolver();
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + /*Environment.getExternalStorageDirectory()*/  info0.getFilePath())));

/*
		int d=localContentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media._ID +"= ?",new String[]{String.valueOf(mid)});
        ContentValues localContentValues = new ContentValues();
        localContentValues.put(MediaStore.Audio.Media.DATA, cursor.getString(3));
        localContentValues.put(MediaStore.Audio.Media.SIZE, cursor.getInt(2));
        localContentValues.put(MediaStore.Audio.Media.DATE_MODIFIED,cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)));
        localContentValues.put("mime_type", "image/jpeg");

		Uri uri=localContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);
		//long id = ContentUris.parseId(uri);
		//String path=uri.getPath();


		cursor=localContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,"select last_insert_rowid()",  null, null);
		int id=0;
		if (cursor.moveToFirst())
			 id=cursor.getInt(0);*/
		//return  id;
       // MediaStore.Audio.Media.
		return 0;
	}

    public void addUpLoadingFile(FileInfo0 entity) {
        ContentValues values = new ContentValues();
        values.put("sysid", entity.getSysid());
        values.put("path", entity.getFilePath());
        values.put("size", entity.getFilesize());
        values.put("name", entity.getFilename());
        values.put("offset", 0);
        values.put("time", entity.getLastModified());
        db.insertWithOnConflict("upload_inter", null, values, SQLiteDatabase.CONFLICT_IGNORE);
//
    }

    public void setUploadinfFile(FileInfo0 entity) {
        ContentValues values = new ContentValues();
        values.put("sysid", entity.getSysid());
        values.put("offset", entity.getOffset());
        db.update("upload_inter", values, "sysid=?", new String[]{entity.getSysid() + ""});
    }

    public void addDownloadingFile(FileInfo0 entity) {
        ContentValues values = new ContentValues();
        values.put("objid", entity.getObjid());
        values.put("size", entity.getFilesize());
        values.put("name", entity.getFilename());
        values.put("offset", 0);
        values.put("time", entity.getLastModified());
        db.insertWithOnConflict("download_inter", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }


    public void updateDownloadingFile(FileInfo0 entity) throws IOException {
        if (getDownloadingFile(entity.getObjid()) == null) {
            throw new IOException();
        }
        ContentValues values = new ContentValues();
        values.put("objid", entity.getObjid());
        values.put("offset", entity.getOffset());
        db.update("download_inter", values, "objid=?", new String[]{entity.getObjid() + ""});
        Log.d("@@@", "call db update updateDownloadingFile");
    }


    public void updateDownloadingFile(String Objid, long offset) throws Exception {
        if (getDownloadingFile(Objid) == null) {
            throw new Exception();
        }
        ContentValues values = new ContentValues();
        values.put("objid", Objid);
        values.put("offset", offset);

        db.update("download_inter", values, "objid=?", new String[]{Objid + ""});
        Log.d("@@@", "call db update updateDownloadingFile");
    }


    public void updateUploadingFile(String Objid, long offset) throws Exception {
        if (getUpLoadedFile(Objid) == null) {
            throw new Exception();
        }
        ContentValues values = new ContentValues();
        values.put("objid", Objid);
        values.put("offset", offset);
        db.update("upload_inter", values, "objid=?", new String[]{Objid + ""});
    }

    public void addDownloadedFile(FileInfo0 entity) {
        ContentValues values = new ContentValues();
        values.put("objid", entity.getObjid());
        values.put("name", entity.getFilename());
        values.put("size", entity.getFilesize());
        values.put("time", entity.getLastModified());
        deleteUpLoadingFile(entity.getObjid());
        db.insertWithOnConflict("upload_finish", null, values, SQLiteDatabase.CONFLICT_IGNORE);

    }

    public void addUpLoadedFile(FileInfo0 entity) {
        ContentValues values = new ContentValues();
        values.put("objid", entity.getObjid());
        values.put("size", entity.getFilesize());
        values.put("name", entity.getFilename());
        values.put("time", entity.getLastModified());
        db.insertWithOnConflict("upload_finish", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }


    public void updateUpLoadingFile(FileInfo0 entity) {
        ContentValues values = new ContentValues();
        values.put("objid", entity.getObjid());
        values.put("offset", entity.getOffset());
        db.update("upload_inter", values, "objid=?", new String[]{entity.getObjid() + ""});
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public List<FileInfo0> getDownloadingFiles() {
        return getFileDataDBEntities("download_inter", false);
    }

    private List<FileInfo0> getFileDataDBEntities(String db1, boolean finished) {
        Cursor cursor = db.rawQuery("select * from " + db1, null);
        List<FileInfo0> lists = new ArrayList<FileInfo0>();
        while (cursor.moveToNext()) {
            FileInfo0 file = new FileInfo0();
            file.setObjid(cursor.getString(cursor.getColumnIndex("objid")));
            file.setFilePath(cursor.getString(cursor.getColumnIndex("path")));
            //file.setFilename(cursor.getString(cursor.getColumnIndex("name")));
            if (!finished)
                file.setOffset((long)cursor.getInt(cursor.getColumnIndex("offset")));
            lists.add(file);
        }
        cursor.close();
        return lists;
    }




    private List<FileInfo0> getUpfinishedDBEntity(FTYPE ftype) {
        Cursor cursor = db.rawQuery("select * from upload_finish where ftype = " + ftype + " order by time desc", null);
        List<FileInfo0> lists = new ArrayList<FileInfo0>();
        while (cursor.moveToNext()) {
            FileInfo0 file = new FileInfo0();
            file.setObjid(cursor.getString(cursor.getColumnIndex("objid")));
            file.setFilename(cursor.getString(cursor.getColumnIndex("name")));
            file.setFilePath(cursor.getString(cursor.getColumnIndex("path")));
            file.setLastModified(cursor.getInt(cursor.getColumnIndex("time")));
            lists.add(file);
        }
        cursor.close();
        return lists;
    }

    private List<FileInfo0> getFileDataDBEntitiesU(String db1, boolean finished) {
        Cursor cursor = db.rawQuery("select * from " + db1, null);
        List<FileInfo0> lists = new ArrayList<FileInfo0>();
        while (cursor.moveToNext()) {
            FileInfo0 file = new FileInfo0();
            file.setObjid(cursor.getString(cursor.getColumnIndex("objid")));
            file.setFilename(cursor.getString(cursor.getColumnIndex("name")));
            if (!finished)
                file.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));
            lists.add(file);
        }
        cursor.close();
        return lists;
    }

    // 通过文件名找到记录
    public boolean GetDownloadedFile(FileInfo0 info0) {
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
        return ret;
    }


    public List<FileInfo0> getUpLoadingFiles() {
        return getFileDataDBEntitiesU("upload_inter", false);
    }


    public List<FileInfo0> getUpLoadedFiles() {
        return getFileDataDBEntitiesU("upload_finish", true);
    }

    private FileInfo0 getFileDataDBEntity(String db1, String objid, boolean finished) {
        FileInfo0 file = null;
        Cursor cursor = db.rawQuery("select * from " + db1 + " where objid=" + objid, null);
        if (cursor.moveToNext()) {
            file.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));
            file.setFilename(getpath(cursor.getString(cursor.getColumnIndex("name"))));
            file.setFilename(cursor.getString(cursor.getColumnIndex("name")));
            file.setObjid(objid);
            if (!finished)
                file.setOffset((long)cursor.getInt(cursor.getColumnIndex("offset")));

        }
        cursor.close();
        return null;
    }

    private FileInfo0 getFileDataDBEntityU(String db1, String objid, boolean finished) {
        FileInfo0 file = null;
        Cursor cursor = db.rawQuery("select * from " + db1 + " where objid=" + objid, null);
        if (cursor.moveToNext()) {

            file.setOffset((long)cursor.getInt(cursor.getColumnIndex("offset")));
            //file.setFilename(getpath(cursor.getString(cursor.getColumnIndex("name"))));
            file.setFilename(cursor.getString(cursor.getColumnIndex("name")));
            file.setObjid(objid);
            if (!finished)
                file.setOffset((long)cursor.getInt(cursor.getColumnIndex("offset")));

        }
        cursor.close();
        return null;
    }


    public FileInfo0 getDownloadingFile(/*int fid*/String objid) {
        return getFileDataDBEntity("download_inter", objid, false);
    }

    public FileInfo0 getUpLoadingFile(String objid) {
        return getFileDataDBEntityU("upload_inter", objid, false);
    }

    public FileInfo0 getDownloadedFile(String objid) {
        return getFileDataDBEntity("download_finish", objid, true);
    }

    public FileInfo0 getUpLoadedFile(String objid) {
        return getFileDataDBEntityU("upload_finish", objid, true);
    }

    public void deleteDownloadingFile(String objid) {
        deleteFileDataDBEntity("download_inter", objid);
    }

    public void deleteUpLoadingFile(String objid) {
        deleteFileDataDBEntity("upload_inter", objid);
    }

    public void deleteDownloadedFile(String objid) {
        deleteFileDataDBEntity("download_finish", objid);

    }

    public void deleteUpLoadedFile(String objid) {
        deleteFileDataDBEntity("upload_finish", objid);

    }

    public void deleteFileDataDBEntity(String db1, String objid) {
        db.delete(db1, "objid=?", new String[]{objid + ""});
    }


}
