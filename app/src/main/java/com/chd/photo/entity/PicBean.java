package com.chd.photo.entity;

import java.io.Serializable;

public class PicBean  <T> implements Serializable,Comparable<PicBean>{
	
	private String date;
	private int _month;
	//PicBean<PicInfoBeanMonth>
	private T _list;
	

	/*public PicBean(String date, List<PicInfoBean> list) {
		super();
		this.date = date;
		//this.list = list;
	}*/

	public PicBean(String date, T monthUnits) {
		super();
		this.date = date;
		this._list=monthUnits;

	}

	public String getYear() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public T getList() {
		return _list;
	}

	public void setList( T list) {
		this._list = list;
	}


	@Override
	public int compareTo(PicBean picBean) {
		int year=Integer.parseInt(this.date);
		int picYear=Integer.parseInt(picBean.date);
		if(year<picYear){
			return 1;
		}else if(year==picYear){
			return 0;
		}else{
			return -1;
		}
	}

	public int getMonth() {
		return _month;
	}

	public void setMonth(int _month) {
		this._month = _month;
	}
}
