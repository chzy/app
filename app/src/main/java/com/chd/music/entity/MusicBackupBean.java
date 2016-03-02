package com.chd.music.entity;
import java.io.Serializable;

import com.chd.proto.FileInfo0;

public class MusicBackupBean /*extends FileInfo*/ implements Serializable {

	private String title;
	private String pic;
	private boolean select;
	private FileInfo0 fileInfo0;

	public MusicBackupBean(String title, String pic, boolean select) {
		super();
		this.title = title;
		this.pic = pic;
		this.select = select;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public FileInfo0 getFileInfo0() {
		return fileInfo0;
	}

	public void setFileInfo0(FileInfo0 fileInfo0) {
		this.fileInfo0 = fileInfo0;
	}

}
