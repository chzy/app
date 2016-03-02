package com.chd.yunpan.net;

public class CookieUtil {
	
	private int id;
	private String token;
	
	public CookieUtil() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CookieUtil(int id, String token) {
		super();
		this.id = id;
		this.token = token;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	@Override
	public String toString() {
		return "CookieUtil [id=" + id + ", token=" + token + "]";
	}
	
	
}
