package com.chd.base.Entity;

import com.chd.proto.FileInfo0;

/**
 * Created by lxp on 2017/3/3.
 */

public class FileLocal extends FileInfo0 {
    public boolean bakuped=false;

    public int getPathid() {
        return pathid;
    }

    public void setPathid(int pathid) {
        this.pathid = pathid;
    }

    private  int pathid;
    public FileLocal()
    {
        super();
        bakuped=false;
    }


    public String getId() {
        return ""+pathid+"-"+super.getObjid();
    }
}
