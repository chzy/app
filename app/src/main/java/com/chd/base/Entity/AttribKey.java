package com.chd.base.Entity;

/**
 * Created by lxp on 2017/3/21.
 */

public  enum  AttribKey {

    duration {public String getName(){return "duration";}}
    ,title  {public  String getName() {return  "title";}}
    ,lines  {public  String getName() {return  "lines";}};

    public abstract String getName();

}
