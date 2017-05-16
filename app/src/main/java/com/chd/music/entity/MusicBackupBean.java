package com.chd.music.entity;
import com.chd.base.Entity.FileLocal;

import java.io.Serializable;

public class MusicBackupBean /*extends FileInfo*/ implements Serializable {

	private String title;
	private String pic;
	private boolean select;
	private FileLocal fileInfo0;
	private String albumArt;


	public MusicBackupBean(String title, String pic, boolean select) {
		super();
		this.title = title;
		this.pic = pic;
		this.select = select;
	}

	public String getAlbumArt() {
		return albumArt;
	}

	public void setAlbumArt(String albumArt) {
		this.albumArt = albumArt;
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

	public FileLocal getFileInfo0() {
		return fileInfo0;
	}

	public void setFileInfo0(FileLocal fileInfo0) {
		this.fileInfo0 = fileInfo0;
	}

}
