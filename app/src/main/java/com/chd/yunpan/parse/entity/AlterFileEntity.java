package com.chd.yunpan.parse.entity;


public class AlterFileEntity {
	// {"state":true,"error":"","errno":"",file_name: "sss",file_desc:
	// "jksdfkf"}

	// {"data":{"file_desc":"jkdsd","file_name":"sss"},"state":true,"error"="","errno"=""}
	private Data data;
	private boolean state;
	private String error;
	private String errno;



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

	@Override
	public String toString() {
		return "AlterFileEntity [data=" + data + ", state=" + state
				+ ", error=" + error + ", errno=" + errno + "]";
	}

	public AlterFileEntity(Data data, boolean state, String error, String errno) {
		super();
		this.data = data;
		this.state = state;
		this.error = error;
		this.errno = errno;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public AlterFileEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

}
