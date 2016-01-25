package com.chd.notepad.ui.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private final String tableName = "notetb";
	private Context mContext = null;
	private  String[] idxsql={"CREATE INDEX idx0 ON " + getTableName()+"(time)"
			/*, "CREATE INDEX idx1 ON " + getTableName()+ "(hashcode)"*/
	};
	private String sql = "create table if not exists " + getTableName() +
			"(id integer primary key , " + /*AUTOINCREMENT*/
			/*"hashcode integer," +*/
			"content text," +
			"time integer ," +
			"syncstate integer "+
			")";
	//CREATE INDEX testtable_idx ON testtable(first_col);


	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		//创建表
	
		db.execSQL(sql);
		crtableIdx(db);
		
	}
	private void crtableIdx(SQLiteDatabase db)
	{
		for(int i=0;i<idxsql.length;i++)
		{
			db.execSQL(idxsql[i]);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	public String getTableName() {
		return tableName;
	}
}
