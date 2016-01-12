package com.chd.yunpan.parse.entity;

public class FileDirDataEntity extends FileDataFatherEntity{
	private int cid;
	private String cc;
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public FileDirDataEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	public FileDirDataEntity(int cid, String cc) {
		super();
		this.cid = cid;
		this.cc = cc;
	}
	@Override
	public String toString() {
		return "FileDirDataEntity [cid=" + cid + ", cc=" + cc +"]";
	}
	
	

}
