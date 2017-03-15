package com.chd.base.Entity;

import com.chd.proto.FileInfo;

/**
 * Created by lxp1 on 2015/12/17.
 */
public class FileLocal00 extends FileInfo{
    public int sysid;
    public String fname;
    public boolean bakuped =false;
    public FileLocal00()
    {
        super();
    }


    /* @Override
	 public boolean equals(Object e)
	 {
		 return this.fname.equalsIgnoreCase((String)e);
	 }*/
    public void SetFname(String name)
    {
        super.setObjid(name);
        fname=super.getObjid();
    }
    public  String GetFname()
    {
        return toString();
    }
    @Override
    public int hashCode()
    {
        return super.getObjid().hashCode();
    }

    @Override
    public String toString()
    {
        return  super.getObjid();
    }

    public int GetTimeStamp()
    {
        return  super.getLastModified();
    }
    public void SetTimeStamp( int time)
    {
        super.setLastModified(time);
    }
    /*public int getSysid() {
        return sysid;
    }

    public void setSysid(int sysid) {
        this.sysid = sysid;
    }

    public String get_fname() {
        return fname;
    }

    public void set_fname(String fname) {
        this.fname = fname;
    }*/
}
