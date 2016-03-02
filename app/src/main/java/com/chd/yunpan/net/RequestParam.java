package com.chd.yunpan.net;

import com.chd.proto.FTYPE;

import java.util.List;

/**
 * Created by lxp1 on 2015/11/10.
 */
public class RequestParam {
    /*
    *  method 1 getlist;
    *         2 del obj
    * */

    public static final  int getlist=1;

    public static  final int delobj=2;
    private int method;

    private FTYPE ftype;

    private String objid;

    private int sortby;

    private List<?> list;

    //列表,从第几位开始
    private  int begin;
    private int offset;


    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public FTYPE getFtype() {
        return ftype;
    }

    public void setFtype(FTYPE ftype) {
        this.ftype = ftype;
    }

  /*  public String getObjid() throws Exception {
        if (objid==null)
            throw new Exception("knnow objid");
        return objid;
    }

    public void setObjid(String objid) {
        this.objid = objid;
    }
*/
    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public void setOffset(int offset) {
        this.offset=offset;
    }

    public int getOffset() {
        return offset;
    }
}
