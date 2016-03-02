package com.chd.yunpan.parse.entity;

import java.io.Serializable;

public class FileDataFatherEntity implements Serializable {
	private int aid;
	private int pid;// 父目录
	private String n;// 文件夹名
	private int m;// 封面
	private String pc;// pick_code唯一码
	private String t;// 上传时间或创建时间
	public boolean isChecked = false;

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

	public int getM() {
		return m;
	}

	public void setM(int m) {
		this.m = m;
	}

	public String getPc() {
		return pc;
	}

	public void setPc(String pc) {
		this.pc = pc;
	}

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	@Override
	public String toString() {
		return "FileDataFatherEntity [aid=" + aid + ", pid=" + pid + ", n=" + n
				+ ", m=" + m + ", pc=" + pc + ", t=" + t + "]";
	}

}