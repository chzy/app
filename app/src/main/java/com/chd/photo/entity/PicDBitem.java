package com.chd.photo.entity;

/**
 * Created by lxp1 on 2015/12/14.
 */
public class PicDBitem {
    private  int sysid=0;
    private  int pathid=0;


    public int getSysid() {
        return sysid;
    }

    public void setSysid(int sysid) {
        this.sysid = sysid;
    }

    public int getPathid() {
        return pathid;
    }

    public void setPath(String path)
    {
        this.pathid=path.hashCode();
    }


    @Override
    public boolean equals(Object o) {
        //PicDBitem that=(PicDBitem)o;
        //if (this.sysid>0 && that.sysid>0)

        return (this.pathid== ((PicDBitem)o).pathid);
    }
}
