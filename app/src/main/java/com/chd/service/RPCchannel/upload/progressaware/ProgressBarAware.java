package com.chd.service.RPCchannel.upload.progressaware;

import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by hjy on 7/9/15.<br>
 */
public class ProgressBarAware extends com.chd.service.RPCchannel.upload.progressaware.BaseViewAware {
    private int index=0;
    public ProgressBarAware( MaterialDialog view,int index) {
        super(view);
        this.index=index;

    }
    public ProgressBarAware( MaterialDialog view) {
        super(view);
    }

    @Override
    public void setProgress(int progress, MaterialDialog view) {
        Log.d("liumj","进度："+progress);
        view.setProgress(progress);
        if(index!=0){

            view.setContent("第"+index+"个文件正在上传:"+progress+"%");
        }else{

            view.setContent("正在上传:"+progress+"%");
        }
    }

}
