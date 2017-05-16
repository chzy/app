package com.chd.service.RPCchannel.upload.progressaware;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by hjy on 7/9/15.<br>
 */
public interface ProgressAware {

    int getId();

    /**
     * 是否被回收
     *
     * @return
     */
    boolean isCollected();

    /**
     * 设置进度
     *
     * @param progress
     */
    boolean setProgress(int progress);

    MaterialDialog getWrappedView();

    void setVisibility(int visibility);

}
