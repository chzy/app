package com.chd.photo.entity;

import java.io.Serializable;

public class PicInfoBean  implements Serializable,Comparable<PicInfoBean>{
	
	
	private String url;

	private String day;

	private int sysid;


	private long timeStamp;

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

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

	@Override
	public int compareTo(PicInfoBean picInfoBean) {
		if(this.timeStamp>picInfoBean.timeStamp){
			return -1;
		}else if(this.timeStamp<picInfoBean.timeStamp){
			return 1;
		}
		return 0;
	}
}
