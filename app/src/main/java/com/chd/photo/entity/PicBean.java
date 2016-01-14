package com.chd.photo.entity;

import java.io.Serializable;
import java.util.List;

public class PicBean implements Serializable,Comparable<PicBean>{
	
	private String date;
	
	private List<PicInfoBean> list;
	

	public PicBean(String date, List<PicInfoBean> list) {
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

	public List<PicInfoBean> getList() {
		return list;
	}

	public void setList(List<PicInfoBean> list) {
		this.list = list;
	}


	@Override
	public int compareTo(PicBean picBean) {
		int year=Integer.parseInt(this.date);
		int picYear=Integer.parseInt(picBean.date);
		if(year<picYear){
			return -1;
		}else if(year==picYear){
			return 0;
		}else{
			return 1;
		}
	}
}
