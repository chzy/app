package com.chd.listener;

import com.chd.base.Entity.FileLocal;
import com.chd.proto.FileInfo0;

import java.util.List;

public abstract class DataCallBack {
    public int callbackThreshold=10;
    public DataCallBack(int ct)
    {
        callbackThreshold=ct;
    }
    public DataCallBack()
    {
    }
    public abstract void success(List<FileInfo0> datas, int begin, int end);

}
