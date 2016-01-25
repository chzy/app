package com.chd.notepad.ui.db;


//import net.jpountz.xxhash.XXHash32;
//import net.jpountz.xxhash.XXHashFactory;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DataManage extends DatabaseManage {

	private Context mContext = null;
	//private final XXHash32 _hash;
	//private int _hashSeed=0x9747b28c;
	private SQLiteDatabase mSQLiteDatabase = null;//用于操作数据库的对象
	private DatabaseHelper dh = null;//用于创建数据库的对象

	private String dbName = "notedb";
	private int dbVersion = 1;

	public DataManage(Context context){
		super(context);
		mContext = context;
		//XXHashFactory factory = XXHashFactory.fastestInstance();
		//_hash = factory.hash32();
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
			String sql = "select * from "+dh.getTableName()+ " order by time";
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
			String sql = "select * from "+ dh.getTableName() + " where _id='" + id +"'";
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
			cv.put("hashcode",content.hashCode());
			cv.put("time", datetime);
			l = mSQLiteDatabase.insert(dh.getTableName(), null, cv);
			//	Log.v("datetime", datetime+""+l);
		}catch(Exception ex){
			ex.printStackTrace();
			l = -1;
		}
		return l;

	}

	//删除数据
	public int delete(long id){
		int affect = 0;
		try{
			affect = mSQLiteDatabase.delete(dh.getTableName(), "hashcode=?", new String[]{id+""});
		}catch(Exception ex){
			ex.printStackTrace();
			affect = -1;
		}

		return affect;
	}

	//修改数据
	public int update(int id, String title, String content){
		int affect = 0;
		try{
			ContentValues cv = new ContentValues();

			cv.put("title", title);
			cv.put("content", content);
			String w[] = {id+""};
			affect = mSQLiteDatabase.update(dh.getTableName(), cv, "hashcode=?", w);
		}catch(Exception ex){
			ex.printStackTrace();
			affect = -1;
		}
		return affect;
	}

}
