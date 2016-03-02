package com.chd.yunpan.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLHelper extends SQLiteOpenHelper {
	public MySQLHelper(Context context, String name,int v) {
		super(context, name, null, v);
	}
	

	public void onCreate(SQLiteDatabase db) {
		//db.execSQL("CREATE TABLE download_finish (_id INTEGER PRIMARY KEY AUTOINCREMENT,aid INTEGER,pid INTEGER,n TEXT,m TEXT,ms INTEGER,pc TEXT,t TEXT,fid INTEGER,u TEXT,s TEXT,sha1 TEXT, state  INTEGER , time Text , flag INTEGER,  spare TEXT , spare1 TEXT , spare2 TEXT ,spare3 TEXT ,path text )");
		//db.execSQL("CREATE TABLE download_inter ( _id INTEGER PRIMARY KEY AUTOINCREMENT,aid INTEGER,pid INTEGER,n TEXT,m TEXT,ms INTEGER,pc TEXT,t TEXT,fid INTEGER,u TEXT,s TEXT,sha1 TEXT, state  INTEGER , time Text , flag INTEGER,  spare TEXT , spare1 TEXT , spare2 TEXT ,spare3 TEXT,path text )");
		//db.execSQL("CREATE TABLE upload_finish (_id INTEGER PRIMARY KEY AUTOINCREMENT,aid INTEGER,pid INTEGER,n TEXT,m TEXT,ms INTEGER,pc TEXT,t TEXT,fid INTEGER,u TEXT,s TEXT,sha1 TEXT,state  INTEGER , time Text , flag INTEGER,  spare TEXT , spare1 TEXT , spare2 TEXT ,spare3 TEXT,path text )");
		//db.execSQL("CREATE TABLE upload_inter (_id INTEGER PRIMARY KEY AUTOINCREMENT,aid INTEGER,pid INTEGER,n TEXT,m TEXT,ms INTEGER,pc TEXT,t TEXT,fid INTEGER,u TEXT,s TEXT,sha1 TEXT,state  INTEGER , time Text , flag INTEGER,  spare TEXT , spare1 TEXT , spare2 TEXT ,spare3 TEXT,path text )");

		db.execSQL("CREATE TABLE download_finish (objid TEXT PRIMARY KEY ,time Text ,size INTEGER,name text )");
		db.execSQL("CREATE TABLE download_inter ( objid TEXT PRIMARY KEY,size  INTEGER , time Text ,offset INTEGER,name text )");
		db.execSQL("CREATE TABLE upload_finish (objid TEXT PRIMARY KEY,sysid INTEGER, time Text , size INTEGER,path text )");
		//db.execSQL("CREATE TABLE upload_inter (/*hash*/sysid INTEGER PRIMARY KEY,path text,name text,size  INTEGER ,/*sysID INTEGER,*/ time Text , offset INTEGER,objid text )");
		db.execSQL("CREATE TABLE upload_inter (sysid INTEGER PRIMARY KEY,path text,name text,size  INTEGER ,time INTEGER , offset INTEGER,objid text )");
		db.execSQL("CREATE UNIQUE INDEX upload_oid_idx ON upload_inter (objid)");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}
