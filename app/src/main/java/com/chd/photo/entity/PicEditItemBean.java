package com.chd.photo.entity;

import java.io.Serializable;

public class PicEditItemBean implements Serializable {

	private int picUrl;
	private boolean select;
	private boolean edit;
	//private String Picpath;
	private int Picid;
	private String objId;
	private boolean bIsUbkList;
	//private FileInfo0 fileInfo0;
	private  String url;
	
	public PicEditItemBean() {
	}
	
	public PicEditItemBean(int picUrl, boolean select) 
	{
		super();
		this.picUrl = picUrl;
		this.select = select;
	}

	public String getObjId() {
		return objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
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




	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
