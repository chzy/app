package com.chd.service.RPCchannel.upload.progressaware;

import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by hjy on 7/9/15.<br>
 */
public class ProgressBarAware extends com.chd.service.RPCchannel.upload.progressaware.BaseViewAware {

    public ProgressBarAware( MaterialDialog view) {
        super(view);
    }

    @Override
    public void setProgress(int progress, MaterialDialog view) {
        Log.d("liumj","进度："+progress);
        view.setProgress(progress);
        view.setContent("正在上传:"+progress+"%");
    }
}
