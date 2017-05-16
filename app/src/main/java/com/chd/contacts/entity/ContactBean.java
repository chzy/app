package com.chd.contacts.entity;

import java.io.Serializable;

public class ContactBean implements Serializable {

	private String time;
	private String number;

	public ContactBean(String time, String number) {
		super();
		this.time = time;
		this.number = number;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	

}
