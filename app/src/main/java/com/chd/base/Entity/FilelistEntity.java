package com.chd.base.Entity;

import com.chd.proto.FileInfo0;

import java.util.List;

/**
 * Created by lxp1 on 2015/12/6.
 */
public class FilelistEntity {
   private  List<FileInfo0> bklist;
    private List<Integer> ubklist;
    private List<FileLocal> locallist;




    public List<Integer> getUbklist() {
        return ubklist;
    }

    public void setUbklist(List<Integer> ubklist) {
        this.ubklist = ubklist;
    }

    public List<FileInfo0> getBklist() {
        return bklist;
    }

    public void setBklist(List<FileInfo0> bklist) {
        this.bklist = bklist;
    }

    boolean iseEnd()
    {
        return false;
    }

    public List<FileLocal> getLocallist() {
        return locallist;
    }

    public void setLocallist(List<FileLocal> locallist) {
        this.locallist = locallist;
    }
}
