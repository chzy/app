package com.chd.base.Entity;

import com.chad.library.adapter.base.entity.SectionEntity;


public class PicFile<T> extends SectionEntity<T>{

    public boolean isSelect;
    public PicFile(boolean isHeader, String header) {
        super(isHeader, header);
    }


    public PicFile(T t) {
        super(t);
    }
}
