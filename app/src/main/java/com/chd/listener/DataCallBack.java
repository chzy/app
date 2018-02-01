package com.chd.listener;

import com.chd.base.Entity.FileLocal;
import com.chd.proto.FileInfo0;

import java.util.List;

public interface DataCallBack {
    void success(List<FileInfo0> datas, int begin,int end);

}
