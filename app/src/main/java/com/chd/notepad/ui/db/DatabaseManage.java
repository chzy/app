package com.chd.notepad.ui.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.chd.notepad.ui.item.NoteItemtag;

import java.util.ArrayList;
import java.util.List;



//import net.jpountz.xxhash.XXHash32;
//import net.jpountz.xxhash.XXHashFactory;


public class DatabaseManage {

	private Context mContext = null;
	//private final XXHash32 _hash;
	//private int _hashSeed=0x9747b28c;
	private SQLiteDatabase mSQLiteDatabase = null;//用于操作数据库的对象
	private DatabaseHelper dh = null;//用于创建数据库的对象
	
	private String dbName = "notedb";
	private int dbVersion = 1;
	private Thread t;
	public DatabaseManage(Context context){
		mContext = context;

		//XXHashFactory factory = XXHashFactory.fastestInstance();
		//_hash = factory.hash32();
	}



	public  static  class SYNC_STAT
	{
		public final static int DONE=1;
		public final static int DEL=-1;
		public final static int DEF=0;
		public final static int UNKNOW=7;
	}

	/**
	 * 打开数据库
	 */
	public void open(){
		
		try{
			dh = new DatabaseHelper(mContext, dbName, null, dbVersion);
			if(dh == null){
				Log.v("msg", "is null");
				return ;
			}
			mSQLiteDatabase = dh.getWritableDatabase();
			//dh.onOpen(mSQLiteDatabase);
			
		}catch(SQLiteException se){
			se.printStackTrace();
		}
	}
	
	/**
	 * 关闭数据库
	 */
	public void close(){
		
		mSQLiteDatabase.close();
		dh.close();
		
	}



//	public int hashCode() {
//		int h = hash;
//		if (h == 0) {
//			int off = offset;
//			char val[] = value;
//			int len = count;
//			for (int i = 0; i < len; i++) {
//				h = 31*h + val[off++];
//			}
//			hash = h;
//		}
//		return h;
//	}

	//获取列表
	public Cursor selectAll(){
		Cursor cursor = null;
		try{
			String sql = "select * from "+dh.getTableName()+ " order by time desc";
			cursor = mSQLiteDatabase.rawQuery(sql, null);
		}catch(Exception ex){
			ex.printStackTrace();
			cursor = null;
		}
		return cursor;
	}
	
	public Cursor selectById(int id){
		
		//String result[] = {};
		Cursor cursor = null;
		try{
			String sql = "select * from "+ dh.getTableName() + " where id='" + id +"'";
			cursor = mSQLiteDatabase.rawQuery(sql, null);
		}catch(Exception ex){
			ex.printStackTrace();
			cursor = null;
		}
		
		return cursor;
	}
	
	//插入数据
	public long insert(String title, String content){
		
		long datetime = System.currentTimeMillis();
		long l = -1;
		try{
			ContentValues cv = new ContentValues();
			/*cv.put("title", title);*/
			cv.put("content", content);
			cv.put("id",content.hashCode());
			cv.put("time", datetime);
			cv.put("syncstate", SYNC_STAT.DEF);
			l = mSQLiteDatabase.insertWithOnConflict(dh.getTableName(), null, cv,SQLiteDatabase.CONFLICT_IGNORE);
		//	Log.v("datetime", datetime+""+l);
		}catch(Exception ex){
			ex.printStackTrace();
			l = -1;
		}
		return l;
		
	}
	
	//删除数据
	public int deleteByhash(long id){
		int affect = 0;
		try{
			affect = mSQLiteDatabase.delete(dh.getTableName(), "hashcode=?", new String[]{id + ""});
		}catch(Exception ex){
			ex.printStackTrace();
			affect = -1;
		}
		
		return affect;
	}

	public int delete(long id){
		int affect = 0;
		try{
			ContentValues cv = new ContentValues();
			cv.put("syncstate", SYNC_STAT.DEL);
			String w[] = {id+""};
			affect = mSQLiteDatabase.update(dh.getTableName(), cv, "id=?", w);
		}catch(Exception ex){
			ex.printStackTrace();
			affect = -1;
		}

		return affect;
	}


	//修改数据
//	public int update(int id, String title, String content){
//		int affect = 0;
//		try{
//			ContentValues cv = new ContentValues();
//
//			cv.put("title", title);
//			cv.put("content", content);
//			cv.put("syncstate", SYNC_STAT.DEF);
//			String w[] = {id+""};
//			affect = mSQLiteDatabase.update(dh.getTableName(), cv, "hashcode=?", w);
//		}catch(Exception ex){
//			ex.printStackTrace();
//			affect = -1;
//		}
//		return affect;
//	}

	public int update(int id, String title, String content){
		int affect = 0;
		try{
			delete(id);
			affect=(int)insert(title,content);
		}catch(Exception ex){
			ex.printStackTrace();
			affect = -1;
		}
		return affect;
	}

	public void markSyncbyId(Integer id)
	{
		try{
			ContentValues cv = new ContentValues();
			cv.put("syncstate", SYNC_STAT.DONE);
			String w[] = {id+""};
			mSQLiteDatabase.update(dh.getTableName(), cv, "hashcode=?", w);
		}catch(Exception ex){
			ex.printStackTrace();

		}
	}

	public List<NoteItemtag> selectAllview(){
		return getAllRtype(SYNC_STAT.UNKNOW);
	}

	public List<NoteItemtag> getSyncTasks(){
		return getAllRtype(SYNC_STAT.DONE);
	}

	//resver type
	private List<NoteItemtag> getAllRtype(int type)
	{
		Cursor cursor = null;
		List<NoteItemtag> lists=new ArrayList<NoteItemtag>();
		try{
			String sql = "select * from "+dh.getTableName()+ "  order by time";
			cursor = mSQLiteDatabase.rawQuery(sql, null);
			while (cursor.moveToNext()) {

				if (cursor.getInt(cursor.getColumnIndex("syncstate"))==type)
					continue;
				NoteItemtag item = new NoteItemtag();
				item.id =cursor.getInt(cursor.getColumnIndex("id"));
				item.content=cursor.getString(cursor.getColumnIndex("content"));
				/*item.title=cursor.getString(cursor.getColumnIndex("title"));*/
				//item.time=cursor.getLong(cursor.getColumnIndex("time"));
				/*item.hashcode=cursor.getInt(cursor.getColumnIndex("hashcode"));*/
				lists.add(item);
			}
			cursor.close();


		}catch(Exception ex){
			ex.printStackTrace();
			cursor = null;
		}
		return  lists;
	}

}
