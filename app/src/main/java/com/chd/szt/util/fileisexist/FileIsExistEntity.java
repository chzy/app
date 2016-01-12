package com.chd.szt.util.fileisexist;

public class FileIsExistEntity {
	
//	{"state":true,"error":"","errno":"","type":"file"}
//	State :   True为文件存在， false 为出现错误
//	Error:  当文件不存在时state为false且error值为 FILE_NOT_EXIST, 否则为其他错误原因。
//	Errno:  为错误代码。
//	Type:  当state 为true 时返回type字段，file为文件，dir 为目录

	
	private boolean state;
	private String error;
	private String errno;
	private String type;
	
	@Override
	public String toString() {
		return "FileIsExistEntity [state=" + state + ", error=" + error
				+ ", errno=" + errno + ", type=" + type + "]";
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	

}
