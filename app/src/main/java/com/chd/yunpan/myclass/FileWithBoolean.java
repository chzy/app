package com.chd.yunpan.myclass;

import java.io.File;

public class FileWithBoolean {

	private File file = null;

	private boolean check;

	public FileWithBoolean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FileWithBoolean(File file, boolean check) {
		super();
		this.file = file;
		this.check = check;
	}

	public File getFile() {
		//file.getParent();
		file.getName();
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

}
