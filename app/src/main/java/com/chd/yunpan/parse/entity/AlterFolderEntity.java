package com.chd.yunpan.parse.entity;


public class AlterFolderEntity {
	private boolean state;
	private String error;
	private String errno;
	private String file_name;
	

	private int id;
	private int type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public AlterFolderEntity(boolean state, String error, String errno,
			String file_name) {
		super();
		this.state = state;
		this.error = error;
		this.errno = errno;
		this.file_name = file_name;
	}

	public AlterFolderEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

}
