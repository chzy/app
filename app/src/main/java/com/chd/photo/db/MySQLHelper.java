package com.chd.photo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLHelper extends SQLiteOpenHelper {
	public MySQLHelper(Context context, String name,int v) {
		super(context, name, null, v);
	}
	

	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE download_finish (objid TEXT PRIMARY KEY,sysid INTEGER ,time INTEGER ,size INTEGER,name text )");
		db.execSQL("CREATE TABLE download_inter ( objid TEXT PRIMARY KEY,size  INTEGER , time INTEGER ,offset INTEGER,name text )");
		db.execSQL("CREATE TABLE upload_finish (objid TEXT PRIMARY KEY,sysid INTEGER, time INTEGER , size INTEGER,path text )");
		db.execSQL("CREATE TABLE upload_inter (sysid INTEGER PRIMARY KEY,path text,name text,size  INTEGER ,time INTEGER , offset INTEGER,objid text )");
		db.execSQL("CREATE UNIQUE INDEX upload_oid_idx ON upload_inter (objid)");
		db.execSQL("CREATE UNIQUE INDEX upload_sysid_idx ON upload_finish (sysid)");
		db.execSQL("CREATE UNIQUE INDEX dnload_sysid_idx ON download_finish (sysid)");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}
