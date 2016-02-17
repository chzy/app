package com.chd.base.Entity;

import com.chd.proto.FileInfo0;

import java.util.List;

/**
 * Created by lxp1 on 2015/12/6.
 */
public class FilelistEntity {
   private  List<FileInfo0> bklist;
    private List<FileLocal> locallist;
    private int bakNumber=0;



    public List<FileInfo0> getBklist() {
        return bklist;
    }

    public void setBklist(List<FileInfo0> bklist) {
        this.bklist = bklist;
    }


    public List<FileLocal> getLocallist() {
        return locallist;
    }

    public void setLocallist(List<FileLocal> locallist) {
        this.locallist = locallist;
    }


    public void addbakNumber() {
        this.bakNumber++;
    }

    public int getUnbakNumber()
    {
        if(locallist!=null){
        return locallist.size()-bakNumber;
        }
        return 0;
    }
}
