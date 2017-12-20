package com.chd.other.entity;

public class FileInfoL
{
	public static final String FILE_TYPE_ALL = "all";
	public static final String FILE_TYPE_DOC = "doc";
	public static final String FILE_TYPE_DOCX = "docx";
	public static final String FILE_TYPE_XLS = "xls";
	public static final String FILE_TYPE_PDF = "pdf";
	public static final String FILE_TYPE_PPT = "ppt";
	
	private int picid;
	private String filename;
	private String filedate;
	private String filesize;
	private String filetype;
	private String picurl;
	
	public FileInfoL()
	{
	}
	
	public FileInfoL(int picid, String filename, String filedate, String filesize, String filetype)
	{
		this.picid = picid;
		this.filename = filename;
		this.filedate = filedate;
		this.filesize = filesize;
		this.filetype = filetype;
	}
	
	public int getPicid() {
		return picid;
	}
	public void setPicid(int picid) {
		this.picid = picid;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFiledate() {
		return filedate;
	}
	public void setFiledate(String filedate) {
		this.filedate = filedate;
	}
	public String getFilesize() {
		return filesize;
	}
	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}
}
