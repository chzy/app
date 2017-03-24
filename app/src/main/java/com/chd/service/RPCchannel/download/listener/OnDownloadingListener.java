package com.chd.service.RPCchannel.download.listener;




import com.chd.proto.Errcode;
import com.chd.service.RPCchannel.download.FileDownloadTask;
import com.chd.service.RPCchannel.upload.ErrorType;

import java.io.File;

/**
 * Created by hjy on 15/5/13.<br>
 */
public interface OnDownloadingListener {

    /**
     * 下载失败
     *
     * @param task Downdload task
     * @param errorType
     * @param msg 错误信息
     */
    public void onDownloadFailed(final  FileDownloadTask task, final int errorType, final  String msg);

    /**
     * 下载成功
     *
     * @param task Download task
     * @param outFile 下载成功后的文件
     */
    public void onDownloadSucc(FileDownloadTask task, File outFile);

}
