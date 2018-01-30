package com.chd.listener;

import com.chd.base.Entity.FileLocal;

import java.util.List;

public interface DataCallBack {
    void success(List<FileLocal> datas, int count);

}
