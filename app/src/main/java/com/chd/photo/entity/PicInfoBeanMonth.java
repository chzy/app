package com.chd.photo.entity;

import java.util.List;

/**
 * Created by lxp1 on 2016/1/15.
 */
public class PicInfoBeanMonth <T>  extends PicInfoBean {
    private List<T> _Picunits;//PicInfoBean

    public PicInfoBeanMonth()
    {       super();
            //setPicunits(new ArrayList<T>());
    }


    public List<T> getPicunits() {
        return _Picunits;
    }

    public void setPicunits(List<T> Picunits) {
        this._Picunits = Picunits;
    }


}
