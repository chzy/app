package com.chd.notepad.ui.item;

import java.io.Serializable;

/**
 * Created by lxp1 on 2015/10/28.
 */
public class NoteItemtag implements Serializable {
    public String content;
    //public long time;
    private String _fname;
    //protected Integer hashcode;
    private String dateStr;
    public int  id;
    public  boolean isHead=false;


   static public String getTitle0(String fname)
    {
        int idx=-1;
        idx= fname.indexOf("_");
        if (idx<0)
            return null;
        return  fname.substring(idx + 1);
    }

    static   public Long  getStamp0( String fname)
    {
     /*   int idx=-1;
        if (_fname ==null)
            return 0;
        idx= _fname.indexOf("_");
        if (idx<0)
            return 0;
        return  Integer.valueOf(_fname.substring(0, idx - 1))*1000;
        */
        if(fname==null){
            return 0L;
        }
        return Long.valueOf(fname);
    }

    public Long getStamp()
    {
        return getStamp0(_fname);
    }
    public void set_fname(String _fname) {
        this._fname = _fname;
    }

    /*
    作为显示时间用
    * */
    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String get_fname() {
        return _fname;
    }
   /* @Override
    public boolean equals(Object o) {
        return this.id>((NoteItemtag)o).id;
    }*/
}