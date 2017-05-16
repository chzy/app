package com.chd.base.Entity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lxp1 on 2015/12/6.
 */
public class LocalEntityUnits {
    private List<FileLocal> locallist;
    private HashMap<Integer,String> pathmap;



    public List<FileLocal> getLocallist() {
        return locallist;
    }

    public void setLocallist(List<FileLocal> locallist) {
        this.locallist = locallist;
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

}
