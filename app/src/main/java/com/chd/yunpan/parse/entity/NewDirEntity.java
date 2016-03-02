package com.chd.yunpan.parse.entity;

public class NewDirEntity {
	// {"aid":1,"cid":"10164","cname":"test","pid":"0","state":true,"error":"","errno":""}
	private int aid;
	private int cid;
	private String cname;
	private int pid;
	private boolean state;
	private String error;
	private String errno;

	@Override
	public String toString() {
		return "Newfolderentity [aid=" + aid + ", cid=" + cid + ", cname="
				+ cname + ", pid=" + pid + ", state=" + state + ", error="
				+ error + ", errno=" + errno + "]";
	}

	public int getAid() {
		return aid;
	}

	public void setAid(int aid) {
		this.aid = aid;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
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

	public NewDirEntity(int aid, int cid, String cname, int pid,
			boolean state, String error, String errno) {
		super();
		this.aid = aid;
		this.cid = cid;
		this.cname = cname;
		this.pid = pid;
		this.state = state;
		this.error = error;
		this.errno = errno;
	}

	public NewDirEntity() {
		super();
	}

}
