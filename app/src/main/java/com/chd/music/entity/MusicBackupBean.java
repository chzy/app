package com.chd.music.entity;
import android.util.Log;

import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.contacts.vcard.StringUtils;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.application.UILApplication;

import java.io.File;
import java.io.Serializable;

public class MusicBackupBean /*extends FileInfo*/ implements Serializable {

	public String title;
	//private String pic;
	//public boolean select;
	private FileInfo0 fileInfo0;
	public String albumArt;
	final private FilelistEntity filelistEntity;
	final String TAG="MusicBackupBean";

	public MusicBackupBean(FileInfo0 info0, boolean select) {
		filelistEntity= UILApplication.getFilelistEntity();
		//super();
		this.title = title;
		//this.pic = pic;
		this.fileInfo0 = info0;
		info0.getAbsFilePath();
		this.fileInfo0.setSelected(select);
	}

	public boolean getFilePath()
	{
		boolean ret=  filelistEntity.getDirPath(this.fileInfo0);
		if (!ret)
		{
			Log.e(TAG, "getDirPath: failed path " );
		}
		return ret;
	}
/*

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
*/

/*
	public String getPic() {
		return pic;
	}
*/

	/*public void setPic(String pic) {
		this.pic = pic;
	}*/


	public boolean isSelect() {
		return this.fileInfo0.IsSelected();
	}


	public void setSelect(boolean select) {
		this.fileInfo0.setSelected( select);
	}

	public FileInfo0 getFileInfo0() {
		return fileInfo0;
	}

	public void setFileInfo0(FileLocal fileInfo0) {
		this.fileInfo0 = fileInfo0;
	}

}
