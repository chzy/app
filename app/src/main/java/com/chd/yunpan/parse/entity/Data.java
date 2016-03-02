package com.chd.yunpan.parse.entity;

public class Data {
	private String file_name;
	private String file_desc;
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public String getFile_desc() {
		return file_desc;
	}
	public void setFile_desc(String file_desc) {
		this.file_desc = file_desc;
	}
	public Data(String file_name, String file_desc) {
		super();
		this.file_name = file_name;
		this.file_desc = file_desc;
	}
	public Data() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
