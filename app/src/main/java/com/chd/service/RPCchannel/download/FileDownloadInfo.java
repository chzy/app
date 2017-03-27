package com.chd.service.RPCchannel.download;


import com.chd.proto.FileInfo0;
import com.chd.service.RPCchannel.download.listener.OnDownloadProgressListener;
import com.chd.service.RPCchannel.download.listener.OnDownloadingListener;

import java.io.File;

/**
 * Created by hjy on 8/5/15.<br>
 */
public class FileDownloadInfo  {

   /* private String id;

    private String url;
    */
    private File outFile;

   public FileInfo0 _item;

    private OnDownloadingListener onDownloadingListener;

    private OnDownloadProgressListener onDownloadProgressListener;


  /*  public FileDownloadInfo(String id, String url, File outFile, OnDownloadingListener onDownloadingListener, OnDownloadProgressListener onDownloadProgressListener) {
        this.id = id;
        this.url = url;
        this.outFile = outFile;
        this.onDownloadingListener = onDownloadingListener;
        this.onDownloadProgressListener = onDownloadProgressListener;
    }*/

    public FileDownloadInfo(FileInfo0 info0, OnDownloadingListener onDownloadingListener, OnDownloadProgressListener onDownloadProgressListener) {
      //  this.id = id;
       // this.url = url;
      //  this.outFile = outFile;
       // _item=info0;
        super();
        this.onDownloadingListener = onDownloadingListener;
        this.onDownloadProgressListener = onDownloadProgressListener;
    }



    public String getId() {
        return ""+_item.getId();
    }

   /* public void setId(String id) {
        this.id = id;
    }*/

    public OnDownloadingListener getOnDownloadingListener() {
        return onDownloadingListener;
    }

    public void setOnDownloadingListener(OnDownloadingListener onDownloadingListener) {
        this.onDownloadingListener = onDownloadingListener;
    }

    public OnDownloadProgressListener getOnDownloadProgressListener() {
        return onDownloadProgressListener;
    }

    public void setOnDownloadProgressListener(OnDownloadProgressListener onDownloadProgressListener) {
        this.onDownloadProgressListener = onDownloadProgressListener;
    }


    public String getUrl() {
        return  _item.getUrl();
    }

   /*public void setUrl(String url) {
        this.url = url;
    }*/

    public File getOutFile() {
        outFile=  new File(_item.getFilePath()+File.pathSeparator+_item.getObjid());
        return outFile;
    }

    public void setOutFile(File outFile) {
        //item.setFilePath();
        this.outFile=outFile;
    }
}