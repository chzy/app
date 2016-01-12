package com.chd.photo.entity;

import java.io.Serializable;

public class PicInfoBean implements Serializable{
	
	private int picUrl;
	private int month;
	private String number;
	private String picpath;
	private int picid;
	
	public PicInfoBean() 
	{
		super();
	}
	
	public PicInfoBean(int picUrl, int month, String number) {
		super();
		this.picUrl = picUrl;
		this.month = month;
		this.number = number;
	}
	
	public int getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(int picUrl) {
		this.picUrl = picUrl;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}

	public String getPicpath() {
		return picpath;
	}

	public void setPicpath(String picpath) {
		this.picpath = picpath;
	}

	public int getPicid() {
		return picid;
	}

	public void setPicid(int picid) {
		this.picid = picid;
	}

	
}
