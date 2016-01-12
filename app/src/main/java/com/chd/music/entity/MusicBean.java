package com.chd.music.entity;
import java.io.Serializable;

import com.chd.proto.FileInfo0;

public class MusicBean /*extends FileInfo*/ implements Serializable {

	private String title;
	private String pic;
	private FileInfo0 fileInfo0;
	private int id;

	public MusicBean(String title, String pic) {
		super();
		this.title = title;
		this.pic = pic;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public FileInfo0 getFileInfo0() {
		return fileInfo0;
	}

	public void setFileInfo0(FileInfo0 fileInfo0) {
		this.fileInfo0 = fileInfo0;
	}

}
