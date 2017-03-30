package com.chd.service.RPCchannel.download;


import com.chd.base.Entity.AttribKey;
import com.chd.proto.FileInfo;
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

   public FileInfo _item;

    private OnDownloadingListener onDownloadingListener;

    private OnDownloadProgressListener onDownloadProgressListener;


  /*  public FileDownloadInfo(String id, String url, File outFile, OnDownloadingListener onDownloadingListener, OnDownloadProgressListener onDownloadProgressListener) {
        this.id = id;
        this.url = url;
        this.outFile = outFile;
        this.onDownloadingListener = onDownloadingListener;
        this.onDownloadProgressListener = onDownloadProgressListener;
    }*/

    public FileDownloadInfo(FileInfo fileInfo, File outFile ,OnDownloadingListener onDownloadingListener, OnDownloadProgressListener onDownloadProgressListener) {
      //  this.id = id;
       // this.url = url;
      //  this.outFile = outFile;
       // _item=info0;
       // super();
        //_item=new FileInfo0(fileInfo);
       // _item.setFilePath(downloadpath);
        _item=fileInfo;
        this.outFile=outFile;
        this.onDownloadingListener = onDownloadingListener;
        this.onDownloadProgressListener = onDownloadProgressListener;
    }



    public String getId() {
        return ""+_item.getObjid()+"--"+_item.getFtype();
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
        return AttribKey.protoprefix.getName()+ _item.getObjid();
    }

   /*public void setUrl(String url) {
        this.url = url;
    }*/

    public File getOutFile() {
        //,outFile=  new File(_item.getFilePath()+File.separator+_item.getObjid());
        return outFile;
    }

    public void setOutFile(File outFile) {
        //item.setFilePath();
        this.outFile=outFile;
    }
}