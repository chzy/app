package com.chd.yunpan.parse.entity;

import java.io.File;
public class PhotoFileEntity {
	private File file;
	private boolean pitch;//是否选中
	private boolean load;//是否下载
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public boolean isPitch() {
		return pitch;
	}
	public void setPitch(boolean pitch) {
		this.pitch = pitch;
	}
	public boolean isLoad() {
		return load;
	}
	public void setLoad(boolean load) {
		this.load = load;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + (load ? 1231 : 1237);
		result = prime * result + (pitch ? 1231 : 1237);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PhotoFileEntity other = (PhotoFileEntity) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (load != other.load)
			return false;
		return pitch == other.pitch;
	}
	@Override
	public String toString() {
		return "PhotoFileEntity [file=" + file + ", pitch=" + pitch + ", load="
				+ load + "]";
	}
	public PhotoFileEntity(File file, boolean pitch, boolean load) {
		super();
		this.file = file;
		this.pitch = pitch;
		this.load = load;
	}
	public PhotoFileEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
