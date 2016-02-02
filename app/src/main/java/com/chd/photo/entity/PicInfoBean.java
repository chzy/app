package com.chd.photo.entity;

import java.io.Serializable;

public class PicInfoBean  implements Serializable{
	
	
	private String url;

	private String day;

	private int sysid;


	



	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}


    public int getSysid() {
        return sysid;
    }

    public void setSysid(int sysid) {
        this.sysid = sysid;
    }
}
