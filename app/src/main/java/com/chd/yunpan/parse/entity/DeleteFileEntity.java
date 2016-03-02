package com.chd.yunpan.parse.entity;

public class DeleteFileEntity {

	private boolean state;
	private String error;
	private String errno;

	private Integer fid;
	private Integer cid;
	private int type;
	private Integer pid;

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public Integer getFid() {
		return fid;
	}

	public Integer getCid() {
		return cid;
	}

	public void setCid(Integer cid) {
		this.cid = cid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "DeleteFileEntity [state=" + state + ", error=" + error
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
