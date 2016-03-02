package com.chd.yunpan.myclass;

import android.os.Parcel;
import android.os.Parcelable;

public class WyfItem implements Parcelable {
	private String name;
	private String time;
	private String size;
	private int pyte;
	private String path;
	private boolean check;
	private boolean download;

	public String getSize() {
		return size;
	}

	public WyfItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setSize(String size) {
		this.size = size;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getPyte() {
		return pyte;
	}

	public void setPyte(int pyte) {
		this.pyte = pyte;
	}

	public boolean isDownload() {
		return download;
	}

	public void setDownload(boolean download) {
		this.download = download;
	}

	public WyfItem(String name, String time, String size, int pyte, String path,
			boolean check, boolean download) {
		super();
		this.name = name;
		this.time = time;
		this.size = size;
		this.pyte = pyte;
		this.path = path;
		this.check = check;
		this.download = download;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

}
