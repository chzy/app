package com.chd.yunpan.parse.entity;

public class PrepareFileEntity {

	private boolean state;
	private String error;
	private String errno;
	private PrepareFileDataEntity data;
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
	public PrepareFileDataEntity getData() {
		return data;
	}
	public void setData(PrepareFileDataEntity data) {
		this.data = data;
	}
	
	
	
}
