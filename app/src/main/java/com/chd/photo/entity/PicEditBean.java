package com.chd.photo.entity;

import java.io.Serializable;
import java.util.List;

public class PicEditBean implements Serializable{
	
	private String date;
	private boolean select;
	private boolean edit;
	private boolean bIsUbkList;
	private List<PicEditItemBean> list;
	

	public PicEditBean(String date, List<PicEditItemBean> list) {
		super();
		this.date = date;
		this.list = list;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<PicEditItemBean> getList() {
		return list;
	}

	public void setList(List<PicEditItemBean> list) {
		this.list = list;
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

	public boolean isbIsUbkList() {
		return bIsUbkList;
	}

	public void setbIsUbkList(boolean bIsUbkList) {
		this.bIsUbkList = bIsUbkList;
	}
	
	
}
