package com.chd.base.Entity;

import android.util.Log;

import com.chd.proto.FileInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lxp1 on 2015/12/6.
 */
public class FilelistEntity {
    private List<FileInfo> bklist;
    private final List<FileLocal> locallist;
    private int bakNumber = 0;
    private int unBakNumber = 0;
    private HashMap<String, Integer> backmap;
    private HashMap<Integer,String> pathmap;
    private final List<Integer> _GroupCp ;



    /*public List<Integer> getGroupCp() {
        return GroupCp;
    }*/

    public FilelistEntity() {
        backmap = new HashMap<>();
        pathmap=new HashMap<>();
       // bklist=new ArrayList<>();
        locallist=new ArrayList<>();
        _GroupCp = new ArrayList<>();
    }

    public void ReSet()
    {
        backmap.clear();
        pathmap.clear();
        locallist.clear();
        _GroupCp.clear();
    }


    public List<FileInfo> getBklist() {
        return this.bklist;
    }

    public void setBklist(List<FileInfo> list)
    {
        setBklist(list,false);
    }

    public void setBklist(List<FileInfo> list,boolean copy) {
        if (copy) {
            if (this.bklist == null) {
                this.bklist = new ArrayList<>();
            }
            this.bklist.addAll(list);
        }
        else {
           /* if (this.bklist != null) {
                this.bklist.clear();
            }*/
            this.bklist = list;
        }
    }


    public List<FileLocal> getLocallist() {
        return locallist;
    }

   /* public void setLocallist(List<FileLocal> locallist) {
        setLocallist(locallist,false);
    }
*/
    /*public void setLocallist(List<FileLocal> locallist,boolean copy) {
        if (copy )
        {
            if (this.locallist==null)
                this.locallist=new ArrayList<>();
            this.locallist.addAll(locallist);
        }
        else {
            if (this.locallist!=null)
                this.locallist.clear();
            this.locallist = locallist;
        }
    }*/

/*

    public void addbakNumber() {
        this.bakNumber++;
    }
*/

    public void addbakups(String obj,int sid) {
        if (!backmap.containsKey(obj))
                backmap.put(obj,sid);
    }

    public  int queryLocalSysid(String objname)
    {
        return backmap.get(objname);
    }

    public String getFilePath(int pathid)
    {
        return pathmap.get(pathid);
    }

    public int addFilePath(String path)
    {
        int hash=path.hashCode();
        if (!pathmap.containsKey(hash))
            pathmap.put(hash,path);
        return hash;
    }

   /* public void addUnBakNumber(){
        this.setUnBakNumber(this.unBakNumber + 1);
    }
*/
    public int getUnbakNumber()
    {
        return  Math.max((locallist.size()-backmap.size()),0);
    }

   /* public void setUnBakNumber(int unBakNumber) {
        this.unBakNumber = unBakNumber;
    }*/


   /*
   @postion   android view列表中的显示位置
   @list      本地或者云文件的list<Fileinfo>
   @intervalDay 分组的时间间隔 天为单位
   返回 int数组[0] 该分组在list的起始位置，[1]改分组在list中的结束位置
   如：文件list的时间分别为20170101,20170102,20170105,20170107,20170110,20170113
                              0          1        2      3       4         5
   postion 0 : [0,1]
   postion 1:  [2,3]
   postion 2: [4,4]
   postion 3: [5,5]
    */



    public Integer[] getIddByDate(int postion,List<? extends FileInfo> list,int intervalDay)
    {
//        List<? extends FileInfo> list=list0.subList(0,1);
        final  int interval=intervalDay*24*3600;
        int lastday=0,day=0;
        int idx=0,pst=0;

        for(;( (idx<list.size()) && pst<=postion );idx++)
        {
            day=list.get(idx).getLastModified();

            if (day>=lastday) {

                if (day-lastday>=interval)
                {

                    if (_GroupCp.size()<pst)
                    {
                        for(int i=0;i<_GroupCp.size();i++) {
                            if (!_GroupCp.contains(i))
                                _GroupCp.add(i, 0);
                        }
                    }
                    _GroupCp.add(pst,idx);
                    pst++;
                }
                lastday = day;
            }

        }
        int currentPst=pst-1;
        int frontPst=Math.max(0,pst-1-1);
        Integer[] ret=new Integer[2];
        ret[0]=_GroupCp.get(frontPst);
        ret[1]=_GroupCp.get(currentPst);
        Log.i("picadpter",""+ ret[0]+" "+ret[1]);
        return ret;
    }
}


