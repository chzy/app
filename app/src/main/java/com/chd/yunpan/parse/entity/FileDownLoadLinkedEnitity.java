package com.chd.yunpan.parse.entity;

public class FileDownLoadLinkedEnitity {
//	{"url":,"state":true,"error":"","errno":""}
	private String url;
	private boolean state;
	private String error;
	private String errno;
	
	public FileDownLoadLinkedEnitity() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getErrno() {
		return errno;
	}
	public void setErrno(String errno) {
		this.errno = errno;
	}
	

}
