package com.chd.service.RPCchannel.upload.progressaware;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by hjy on 7/9/15.<br>
 */
public interface ProgressAware {

    public int getId();

    /**
     * 是否被回收
     *
     * @return
     */
    public boolean isCollected();

    /**
     * 设置进度
     *
     * @param progress
     */
    public boolean setProgress(int progress);

    public  MaterialDialog getWrappedView();

    public void setVisibility(int visibility);

}
