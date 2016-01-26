package com.chd.base.Entity;

/**
 * Created by lxp1 on 2015/12/17.
 */
public class FileLocal {
    public int sysid;
    public String fname;
    public boolean bakuped =false;



   /* @Override
    public boolean equals(Object e)
    {
        return this.fname.equalsIgnoreCase((String)e);
    }*/

    @Override
    public int hashCode()
    {
        return fname.hashCode();
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
