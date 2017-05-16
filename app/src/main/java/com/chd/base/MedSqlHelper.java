package com.chd.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MedSqlHelper extends SQLiteOpenHelper {
	public MedSqlHelper(Context context, String name, int v) {
		super(context, name, null, v);
	}


	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE download_finish (objid TEXT PRIMARY KEY,sysid INTEGER ,time INTEGER ,size INTEGER,path text,type INTEGER )");
		db.execSQL("CREATE TABLE download_inter ( objid TEXT PRIMARY KEY,size  INTEGER , time INTEGER ,offset INTEGER,path text,type INTEGER )");
		db.execSQL("CREATE TABLE upload_finish (objid TEXT PRIMARY KEY,sysid INTEGER, time INTEGER , size INTEGER,path text,type INTEGER )");
		db.execSQL("CREATE TABLE upload_inter (sysid INTEGER PRIMARY KEY,path text,size  INTEGER ,time INTEGER , offset INTEGER,objid text,type INTEGER )");
		db.execSQL("CREATE UNIQUE INDEX upload_oid_idx ON upload_inter (objid)");
		db.execSQL("CREATE UNIQUE INDEX upload_sysid_idx ON upload_finish (sysid)");
		db.execSQL("CREATE UNIQUE INDEX dnload_sysid_idx ON download_finish (sysid)");
		db.execSQL("CREATE INDEX df_type_idx ON download_finish (type)");
		db.execSQL("CREATE INDEX di_type_idx ON download_finish (type)");
		db.execSQL("CREATE INDEX uf_type_idx ON download_finish (type)");
		db.execSQL("CREATE INDEX ui_type_idx ON download_finish (type)");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
