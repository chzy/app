package com.chd.service.RPCchannel.upload.uploader;

import android.provider.Settings;
import android.util.Log;

import com.chd.MediaMgr.utils.MediaFileUtil;
import com.chd.TClient;
import com.chd.TrpcOutputstream;
import com.chd.base.Entity.MessageEvent;
import com.chd.base.MediaMgr;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.service.RPCchannel.upload.FileUploadInfo;
import com.chd.service.RPCchannel.upload.listener.OnFileTransferredListener;
import com.chd.yunpan.net.NetworkUtils;

import org.apache.thrift.TException;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by lxp on 2017/3/23.
 */

public class TrpcUploader extends BaseUploader {
    private final String TAG = "TrpcUploader";
    private final int Maxbuflen=1024*1024;
    TrpcOutputstream transport;

    @Override
    public String upload(FileUploadInfo fileUploadInfo, OnFileTransferredListener fileTransferredListener) throws IOException {
        try {
            transport = new TrpcOutputstream(fileUploadInfo._item, fileUploadInfo.getDescAttribMap());
            if (upload(fileUploadInfo,transport))
                return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "upload failed !";
    }


    @Override
    public void cancel(FileUploadInfo fileUploadInfo) {

        transport.cancel();
    }

    private String generateTag(FileUploadInfo fileUploadInfo) {
        return fileUploadInfo.getId() + fileUploadInfo.getUploadFilePath().hashCode();
    }


    public boolean upload(FileUploadInfo fileUploadInfo, TrpcOutputstream trpcOutputstream)  {
        long size=fileUploadInfo._item.getFilesize();
        String objid = "";
        FileInfo0 entity=fileUploadInfo._item;

        System.out.println("开始上传喽");
        //先检查 云端是否 有同名文件
        long start = -1l;
        long oft = 0l;
        if (trpcOutputstream.ObjExist()) {
            Log.e(TAG, "upload file exist !!");
            if (fileUploadInfo.getUploadOptions()!=null) {
                if (fileUploadInfo.getUploadOptions().overwrite) {
                    trpcOutputstream.cancel();
                    //del exist obj
                } else {
                    oft = trpcOutputstream.transport.Queryoffset();
                    if (size >= oft) {
                        Log.i(TAG, "remote obj exist!!");
                        try {
                            trpcOutputstream.flush();
                            return true;
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                        if (fileUploadInfo.getUploadOptions().resume) {
                            start = oft;
                        }
                        else
                            start=0;
                    } else {
                        Log.e(TAG, "remote file size > local");
                        return false;
                    }
                }
            }

        }
        int len = 0;
        boolean succed = false;
        if (start == -1) {
                try {
                     objid= trpcOutputstream.transport.ApplyObj();
                    if (objid==null)
                    {
                        Log.i(TAG,"alloc obj failed !!!");
                        return false;
                    }
                    start = 0;
                    entity.setObjid(objid);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
              else {
                objid = entity.getObjid();
            }
        int bufflen = Math.min(Maxbuflen, (int) (size - start));
        byte[] buffer = new byte[bufflen];
        RandomAccessFile rf = null;
        try {
            rf = new RandomAccessFile(fileUploadInfo.getOriginalFilePath(), "r");
            rf.seek(start);
        } catch (FileNotFoundException e) {
            Log.i(TAG,e.getMessage());
            return false;
        } catch (IOException e) {
            Log.i(TAG,e.getMessage());
            return false;
        }
            long pz=0,  t0 = 0, t00 =0;
            int bflen = buffer.length;
        try {
            while ((len = rf.read(buffer, 0, bflen)) != -1) {
                pz = pz + len;
                t0 = System.currentTimeMillis();
                try {
                    trpcOutputstream.write(buffer, 0, len);
                    t00= System.currentTimeMillis();
                    Log.i(TAG,"speed : " +(float) ((len/1024)/(t00-t0)/1024)+ "K/S"  );
                }catch (IOException ex)
                {
                    Log.i(TAG,"upload write failed ");
                    succed=false;
                    break;
                }
                    entity.setOffset(pz);
                    succed = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG,e.getMessage());
            succed=false;
        }
        if (succed) {
            try {
                trpcOutputstream.flush();
                trpcOutputstream.close();
                succed = true;
            } catch (IOException e) {
                Log.i(TAG,e.getMessage());
            }
            Log.d(TAG, objid + " upload finished !!");
                } else {
                    Log.d(TAG, objid + " upload commit failed  !!");
                    succed = false;
                    trpcOutputstream.cancel();
                }
        return succed;
    }
}
