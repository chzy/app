package com.chd.base.backend;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.view.circleimage.CircularProgressButton;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @description
 * @FileName: com.chd.base.backend.DownFileUtils
 * @author: liumj
 * @date:2016-01-28 11:19
 * OS:Mac 10.10
 * Developer Kits:AndroidStudio 1.5
 */

public class DownFileUtils {
    private Activity mAct;
    private CircularProgressButton mBtn;
    private  String appPath;
    public DownFileUtils(Activity act,CircularProgressButton btn, final String url){
        this.mAct=act;
        this.mBtn=btn;
        appPath=new ShareUtils(act).getApkFile().getPath();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String filename = url.substring(url.lastIndexOf("/") + 1);
                    down_file(url,appPath+"/"+filename);
                } catch (IOException e) {
                    sendMsg(3,-1);
                }
            }
        }).start();
    }
    private Handler mHandler=new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg) {
            int progress=0;
            switch (msg.what){
                case 0:
                    //开始
                    break;
                case 1:
                    //下载中
                    progress= (Integer) msg.obj;
                    mBtn.setProgress(progress);
                    break;
                case 2:
                    //完成
                    progress= (Integer) msg.obj;
                    mBtn.setProgress(progress);
                    break;
                case 3:
                    //异常
                    progress= (Integer) msg.obj;
                    mBtn.setProgress(progress);
                    Toast.makeText(mAct, "地址异常", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };



    public void down_file(String url,String path) throws IOException {

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
        sendMsg(0);
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
            int count=downLoadFileSize*100/fileSize;
            sendMsg(1,count);//更新进度条
        } while (true);
        sendMsg(2,100);//通知下载完成
        try
        {
            is.close();
        } catch (Exception ex)
        {
            Log.e("tag", "error: " + ex.getMessage(), ex);
        }

    }

    private void sendMsg(int flag, int count) {
        Message msg = new Message();
        msg.what = flag;
        msg.obj=count;
        mHandler.sendMessage(msg);
    }

    private void sendMsg(int flag)
    {
        Message msg = new Message();
        msg.what = flag;
        mHandler.sendMessage(msg);
    }

}
