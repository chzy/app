package com.chd.service.RPCchannel.download;

import android.os.Handler;


import com.chd.TrpcInputstream;
import com.chd.service.RPCchannel.download.listener.OnDownloadProgressListener;
import com.chd.service.RPCchannel.download.listener.OnDownloadingListener;
import com.chd.service.RPCchannel.upload.progressaware.ProgressAware;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;



/**
 * Created by hjy on 8/5/15.<br>
 */
public class FileDownloadTask implements Runnable {

    private DownloadManager downloadManager;
    private FileDownloadInfo fileDownloadInfo;
    private OnDownloadingListener downloadingListener;
    private OnDownloadProgressListener progressListener;
    private volatile ProgressAware progressAware;

    private long currSize;
    private long totalSize;
    private  final int readbuflen=256*1024;

    /**
     * 是否同步加载
     */
    private boolean isSyncLoading = false;

    public FileDownloadTask(FileDownloadInfo fileDownloadInfo, DownloadManager downloadManager, ProgressAware progressAware) {
        this.fileDownloadInfo = fileDownloadInfo;
        downloadingListener = fileDownloadInfo.getOnDownloadingListener();
        progressListener = fileDownloadInfo.getOnDownloadProgressListener();
        this.downloadManager = downloadManager;
        this.progressAware = progressAware;
    }

    public void setSyncLoading(boolean isSyncLoading) {
        this.isSyncLoading = isSyncLoading;
    }

    public boolean isSyncLoading() {
        return isSyncLoading;
    }

    public void resetProgressAware(final ProgressAware progressAware, Handler handler) {
        this.progressAware = progressAware;
        if(progressAware != null) {
            long t = totalSize;
            if(t == 0)
                t = Integer.MAX_VALUE;
            final int progress = (int)((currSize / (float) t) * 100);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressAware.setProgress(progress);
                }
            });
        }
    }

    @Override
    public void run() {
        TrpcInputstream trpcInputstream;
        try {
           trpcInputstream=new TrpcInputstream(fileDownloadInfo._item,fileDownloadInfo.getOutFile());
            this.totalSize=fileDownloadInfo._item.getFilesize();
        } catch (Exception e) {
            e.printStackTrace();
            //url非法
            if(downloadingListener != null)
                downloadingListener.onDownloadFailed(this, DownloadErrorType.ERROR_URL_INVALID, e.getMessage());
            return;
        }
        try {

                byte[] buffer = new byte[readbuflen];
                int size = 0;
                long currentSize = 0;
                while ((size = trpcInputstream.read(buffer)) != -1) {
                    currentSize += size;
                    this.currSize = currentSize;
                    if(progressListener != null) {
                        progressListener.onProgressUpdate(this, currentSize, this.totalSize);
                    }
                }
                trpcInputstream.close();
                if(downloadingListener != null)
                    downloadingListener.onDownloadSucc(this, fileDownloadInfo.getOutFile());
             /*else {
                if(downloadingListener != null)
                    downloadingListener.onDownloadFailed(this, DownloadErrorType.ERROR_OTHER, resp.toString());
            }*/
        } catch (IOException e) {
            e.printStackTrace();
            if(downloadingListener != null)
                downloadingListener.onDownloadFailed(this, DownloadErrorType.ERROR_NETWORK, e.getMessage());
        }

        ProgressAware pa = progressAware;
        if(pa != null) {
            downloadManager.cancelUpdateProgressTaskFor(pa);
        }
    }

    private String generateTag(FileDownloadInfo fileDownloadInfo) {
        return fileDownloadInfo.getId() + fileDownloadInfo.getUrl().hashCode();
    }

    public FileDownloadInfo getFileDownloadInfo() {
        return fileDownloadInfo;
    }

    private boolean isProgressViewCollected(ProgressAware pa) {
        return pa.isCollected();
    }

    private boolean isProgressViewReused(ProgressAware pa) {
        String downloadTaskId = downloadManager.getFileDownloadInfoIdForProgressAware(pa);
        return !fileDownloadInfo.getId().equals(downloadTaskId);
    }

    public void updateProgress(int progress) {
        ProgressAware pa = progressAware;
        if(pa != null) {
            if(!isProgressViewCollected(pa) && !isProgressViewReused(pa)) {
                pa.setProgress(progress);
            }
        }
    }

}