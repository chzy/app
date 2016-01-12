package com.chd.photo.entity;

import java.io.Serializable;

import com.chd.proto.FileInfo0;

public class PicEditItemBean implements Serializable {

	private int picUrl;
	private boolean select;
	private boolean edit;
	private String Picpath;
	private int Picid;
	private boolean bIsUbkList;
	private FileInfo0 fileInfo0;
	
	public PicEditItemBean() {
	}
	
	public PicEditItemBean(int picUrl, boolean select) 
	{
		super();
		this.picUrl = picUrl;
		this.select = select;
	}

	public int getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(int picUrl) {
		this.picUrl = picUrl;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public String getPicpath() {
		return Picpath;
	}

	public void setPicpath(String picpath) {
		Picpath = picpath;
	}

	public int getPicid() {
		return Picid;
	}

	public void setPicid(int picid) {
		Picid = picid;
	}

	public boolean isbIsUbkList() {
		return bIsUbkList;
	}

	public void setbIsUbkList(boolean bIsUbkList) {
		this.bIsUbkList = bIsUbkList;
	}

	public FileInfo0 getFileInfo0() {
		return fileInfo0;
	}

	public void setFileInfo0(FileInfo0 fileInfo0) {
		this.fileInfo0 = fileInfo0;
	}
}
