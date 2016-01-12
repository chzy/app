package com.chd.yunpan.parse.entity;

public class LoginEntity {
	private boolean state ;
	
	private String id;//用户id
	
	//private String token;
	
	private long space;
	
	private long uspace;
	
	private String name;
	
	private String nick_name;
	
	//private int head_id;

	private  int flow;

	private int uflow;
	
//	@Override
//	public String toString() {
//		return "LoginEntity [state=" + state + ", id=" + id + ", token="
//				+ token + ", space=" + space + ", uspace=" + uspace + ", name="
//				+ name + ", nick_name=" + nick_name + ", head_id=" + head_id
//				+ ", head_url=" + head_url + "]";
//	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public String getId() {
		if (id==null)
			return "";
		return id;
	}

/*
	public void setId(String id) {
		this.id = id;
	}
*/

//	public String getToken() {
//		return token;
//	}

//	public void setToken(String token) {
//		this.token = token;
//	}

	public long getSpace() {

		return Math.max(1,space);
	}

	public void setSpace(long space) {
		this.space = space;
	}

	public long getUspace() {
		return uspace;
	}

	public void setUspace(long uspace) {
		this.uspace = uspace;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

//	public int getHead_id() {
//		return head_id;
//	}

//	public void setHead_id(int head_id) {
//		this.head_id = head_id;
//	}

	public String getHead_url() {
		return head_url;
	}

	public void setHead_url(String head_url) {
		this.head_url = head_url;
	}

	private String head_url;


	public int getFlow() {
		return flow;
	}

	public void setFlow(int flow) {
		this.flow = flow;
	}

	public int getUflow() {
		return uflow;
	}

	public void setUflow(int uflow) {
		this.uflow = uflow;
	}
}
