package com.chd.service.RPCchannel.upload;




import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.service.RPCchannel.upload.listener.OnUploadListener;
import com.chd.service.RPCchannel.upload.listener.OnUploadProgressListener;
import com.chd.yunpan.application.UILApplication;

import java.io.File;
import java.util.HashMap;

/**
 * Created by hjy on 7/8/15.<br>
 */
public class FileUploadInfo {

    private HashMap<String, String> descAttribMap;

/*
    private String id;
    private String filePath;             //要上传的文件路径
    private String mimeType;
    private String url;
*/
    public FileInfo0 _item;
    private OnUploadListener apiCallback;
    private OnUploadProgressListener progressListener;
    private UploadOptions uploadOptions;
    private FilelistEntity filelistEntity;



    private String preProcessedFile;     //上传前对文件预处理后，生成的临时文件

    public FileUploadInfo(HashMap<String, String> descmap, /*String id, String filePath, String mimeType, String url*/FileInfo0 info0,
                          OnUploadListener apiCallback, OnUploadProgressListener progressListener,UploadOptions uploadOptions ) {
        if (filelistEntity==null)
            filelistEntity=UILApplication.getFilelistEntity();
        this.descAttribMap = descmap;
        _item=new FileInfo0(info0);
        _item.setFilePath(filelistEntity.getFilePath(info0.pathid));
        this.apiCallback = apiCallback;
        this.progressListener = progressListener;
        this.uploadOptions = uploadOptions;
        //filelistEntity= UILApplication.getFilelistEntity();
    }


   /* @Override
    public String toString() {
        return "FileUploadInfo{" +
                "apiCallback=" + apiCallback +
                ", descAttribMap=" + descAttribMap +
                ", id='" + id + '\'' +
                ", filePath='" + filePath + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", url='" + url + '\'' +
                ", progressListener=" + progressListener +
                ", uploadOptions=" + uploadOptions +
                '}';
    }*/

    public String getOriginalFilePath() {
        return _item.getFilePath() ;
    }

    public String getUploadFilePath() {
        if(preProcessedFile != null && !preProcessedFile.trim().equals("")) {
            return preProcessedFile;
        }
        return _item.getFilePath()+ File.separator+_item.getObjid();
    }

    public OnUploadListener getApiCallback() {
        return apiCallback;
    }

    public HashMap<String, String> getDescAttribMap() {
        return descAttribMap;
    }

    public String getId() {
        return ""+_item.getObjid().hashCode();
    }

   public String getType() {
        return _item.getFtype().name();
    }

    /*public void setPreProcessedFile(String preProcessedFile) {
        this.preProcessedFile = preProcessedFile;
    }*/

    public OnUploadProgressListener getProgressListener() {
        return progressListener;
    }

    public UploadOptions getUploadOptions() {
        return uploadOptions;
    }

    /*public String getUrl() {
        return _item.getUrl();
        //return url;
    }*/

}
