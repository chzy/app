package com.chd.yunpan.parse.entity;

public class PrepareFileDataEntity {
	private int fid;//文件ID只有size==usize时存在
	private int aid;//只有size==usize时存在
	private int pid;//所在文件夹ID    只有size==usize时存在
	private String n; //文件名   只有size==usize时存在
	private String s;//文件大小
	private String us;//已经上传文件大小
	private String pc;  //只有size==usize时存在
	private String m;//只有size==usize时存在
	private String t; //文件创建时间只有size==usize时存在
	private String u;//文件缩略图     只有size==usize时存在
	public int getFid() {
		return fid;
	}
	public void setFid(int fid) {
		this.fid = fid;
	}
	public int getAid() {
		return aid;
	}
	public void setAid(int aid) {
		this.aid = aid;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getN() {
		return n;
	}
	public void setN(String n) {
		this.n = n;
	}
	public String getS() {
		return s;
	}
	public void setS(String s) {
		this.s = s;
	}
	public String getUs() {
		return us;
	}
	public void setUs(String us) {
		this.us = us;
	}
	public String getPc() {
		return pc;
	}
	public void setPc(String pc) {
		this.pc = pc;
	}
	public String getM() {
		return m;
	}
	public void setM(String m) {
		this.m = m;
	}
	public String getT() {
		return t;
	}
	public void setT(String t) {
		this.t = t;
	}
	public String getU() {
		return u;
	}
	public void setU(String u) {
		this.u = u;
	}
	
	
}
