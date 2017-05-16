package com.chd.yunpan.ui.entity;

import java.io.Serializable;
import java.util.List;

public class MySpaceBean implements Serializable {

	private String text;
	private int picurl;
	private Class<?> cls;

	public MySpaceBean(String text, int picurl, Class<?> cls) {
		super();
		this.text = text;
		this.picurl = picurl;
		this.cls = cls;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getPicurl() {
		return picurl;
	}

	public void setPicurl(int picurl) {
		this.picurl = picurl;
	}

	public Class<?> getCls() {
		return cls;
	}

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}
}
