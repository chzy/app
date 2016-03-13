package com.chd.photo.entity;

import java.io.Serializable;
import java.util.List;

public class PicEditBean implements Serializable,Comparable<PicEditBean>{
	
	private String date;
	private boolean select;
	private boolean edit;
	private boolean bIsUbkList;
	private List<PicEditItemBean> list;
	private int day;//日

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public PicEditBean(String date,List<PicEditItemBean> list) {
		super();
		try{
		int start=date.indexOf("月");
		int end=date.indexOf("日");
		day=Integer.parseInt(date.substring(start+1,end));
		}catch (Exception E){
			day=0;
		}
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


	@Override
	public int compareTo(PicEditBean picEditBean) {
		if(this.day<picEditBean.day){
			return 1;
		}else if(this.day>picEditBean.day){
			return -1;
		}

		return 0;
	}
}
