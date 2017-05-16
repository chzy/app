package com.chd.base.Entity;

import com.chd.proto.FTYPE;

/**
 * @description
 * @FileName: com.chd.base.Entity.MessageEvent
 * @author: liumj
 * @date:2016-02-16 09:12
 * OS:Mac 10.10
 * Developer Kits:AndroidStudio 1.5
 */

public class MessageEvent {

    public MessageEvent(FTYPE type,Object result){
        this.type=type;
        this.result=result;
    }
    public MessageEvent(){

    }

    public FTYPE type;
    public Object result;


}
