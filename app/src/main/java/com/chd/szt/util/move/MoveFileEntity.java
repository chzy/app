package com.chd.szt.util.move;

public class MoveFileEntity {

	private boolean state;
	private String error;
	private String errno;
	
	@Override
	public String toString() {
		return "MoveFileEntity [state=" + state + ", error=" + error
				+ ", errno=" + errno + "]";
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
