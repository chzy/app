package com.chd.MediaMgr.utils;

import java.util.HashSet;

/**
 * Created by lxp1 on 2015/12/18.
 */
public class MFileFilter {
    private boolean _contains;

   // private HashMap<StoreUtil.FileCategory, FilenameExtFilter> filters = null;
   private HashSet<String> filters = null;

    public MFileFilter()
    {
        filters = new HashSet();
    }

    public void setCustomCategory(String[] exts,boolean contain) {
        if (filters.isEmpty()==false)
            filters.clear();
        for (String ext:exts)
        {
            filters.add(ext);
        }
        _contains=contain;
    }

    public boolean contains(String path) {
        int dotPosition = path.lastIndexOf('.');
        if (dotPosition != -1) {
            String ext = (String) path.subSequence(dotPosition + 1, path.length());

            //return  (filters.(path)==_contains);
            return filters.contains(ext) == _contains;
        }
    return false;
    }

}