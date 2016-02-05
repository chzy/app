package com.chd.yunpan.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.chd.proto.VersionResult;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.utils.AutoInstall;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @description
 * @FileName: com.chd.yunpan.ui.dialog.UpdateDialog
 * @author: liumj
 * @date:2016-02-05 11:17
 * OS:Mac 10.10
 * Developer Kits:AndroidStudio 1.5
 */

public class UpdateDialog{
    /**版本更新实体*/
    private Activity mAct;
    private VersionResult result;
    private Dialog mDialog;
    private ProgressDialog mBar;
    private String path;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Toast.makeText(mAct, "下载失败", Toast.LENGTH_SHORT).show();
                    if(mBar!=null&&mBar.isShowing()){
                        mBar.dismiss();
                        mBar=null;
                    }
                    if(mDialog!=null&&mDialog.isShowing()){
                        mDialog.dismiss();
                        mDialog=null;
                    }
                    mAct.finish();
                    break;
                case 2:
                    Toast.makeText(mAct, "下载成功", Toast.LENGTH_SHORT).show();
                    if(mBar!=null&&mBar.isShowing()){
                        mBar.dismiss();
                        mBar=null;
                    }
                    if(mDialog!=null&&mDialog.isShowing()){
                        mDialog.dismiss();
                        mDialog=null;
                    }
                    AutoInstall.setUrl(path);
                    AutoInstall.install(mAct);
                    break;
                case 3:
                    break;


            }


        }
    };
    public UpdateDialog(Activity act, final VersionResult result){
        this.mAct=act;
        this.result=result;
        String msg=result.getWhatsnew();

       path=new ShareUtils(act).getApkFile().getPath()+"";
        this.mDialog=new AlertDialog.Builder(mAct).setMessage("系统更新").setMessage(msg).setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mBar=new ProgressDialog(mAct);
                mBar.setTitle("正在下载");
                mBar.setMessage("请稍候");
                mBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                new Thread(new Runnable() {
                @Override
                public void run() {
                    String url=result.getUrl();
                    String filename = url.substring(url.lastIndexOf("/") + 1);
                    path=path+"/"+filename;
                    try {
                        down_file(url,path);
                    } catch (IOException e) {
                        mHandler.sendEmptyMessage(1);
                    }
                }
            }).start();

            }
        }).create();
        this.mDialog.setCancelable(false);
        this.mDialog.setCanceledOnTouchOutside(false);
    }


    public void show(){
        mDialog.show();
    }


    private void down_file(String url,String path) throws IOException {

        //获取文件名
        URL myURL = new URL(url);
        URLConnection conn = myURL.openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        int fileSize = conn.getContentLength();//根据响应获取文件大小
        if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
        if (is == null) throw new RuntimeException("stream is null");
        FileOutputStream fos = new FileOutputStream(path);
        //把数据存入路径+文件名
        byte buf[] = new byte[1024];
        int downLoadFileSize = 0;
        do
        {
            //循环读取
            int numread = is.read(buf);
            if (numread == -1)
            {
                break;
            }
            fos.write(buf, 0, numread);
            downLoadFileSize += numread;
        } while (true);
        mHandler.sendEmptyMessage(2);
        try
        {
            is.close();
        } catch (Exception ex)
        {
            Log.e("tag", "error: " + ex.getMessage(), ex);
        }

    }


}
