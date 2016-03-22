package com.chd.service;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.chd.MediaMgr.utils.MediaFileUtil;
import com.chd.TClient;
import com.chd.Transform.InputTrasnport;
import com.chd.base.Entity.MessageEvent;
import com.chd.base.MediaMgr;
import com.chd.base.Ui.ActiveProcess;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.net.NetworkUtils;

import org.apache.thrift.TException;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncLocalFileBackground implements Runnable {


    private final String TAG = "SyncLocal";
    private final int Maxbuflen = (int)256 * 1024;
    List<FileInfo0> files = new ArrayList<FileInfo0>();
    private MediaMgr su = null;
    private Context context = null;
    private FileInfo0 _item;

    /**
     * _itme!=null：立即上传  _item==null：自动备份0
     * */
    //private int upLoadType = -1;

    /**
     * 0：立即上传 1：自动备份
     */
    public SyncLocalFileBackground(Context context) {
        this.context = context;
        //this.upLoadType = upLoadType;
        su = new MediaMgr(context);
        //su.open();
    }

	/*public SyncLocalFileBackground(Context context,FileInfo0 fileInfo0) {
        this.context = context;
		su = new MediaMgr(context);
		//su.open();
	}*/


    public SyncLocalFileBackground(Context context, int upLoadType) {
        this.context = context;
        //this.upLoadType = upLoadType;
        su = new MediaMgr(context);
        //su.open();
    }

    public void run() {
        while (true) {
            if (files.isEmpty()) {
                upLoad();
                download();
            } else
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

        }
        //su.close();

    }

    private void download() {
        if (files.isEmpty()) {
            su.open();
            files=su.getDlLoadTask(100);
            su.close();
        }
        if (files.size() == 0) {
            return;
        }

        for (FileInfo0 item : files) {
            if (!NetworkUtils.isNetworkAvailable(context)) {
                break;
            }
            if (downloadBigFile(item, null, null)) {
                //su.deleteUpLoadingFile(item.getObjid());
                //su.addUpLoadedFile(item);
            } else {
               Log.e(TAG,"下载失败");
            }
            su.close();
        }

    }

    private void upLoad() {

        if (files.isEmpty()) {
            su.open();
            files = su.getUpLoadTask(100);
            su.close();
        }
        if (files.size() == 0) {
            return;
        }

        for (FileInfo0 item : files) {
            if (!NetworkUtils.isNetworkAvailable(context)) {
                break;
            }
            if (uploadBigFile(item, null,null)) {
                //su.deleteUpLoadingFile(item.getObjid());
                //su.addUpLoadedFile(item);
            } else {
                Log.e(TAG,"下载失败");
            }
            su.close();
        }
    }

   /* private boolean upLoadFile(FileInfo0 file) {
        Log.v(TAG, file.getObjid() + "------------------" + file.getFilePath());
        boolean b = new BigFileOrBreakPointUploadUtil()
                .uploadBigFile(file, null, context);
        System.out.println(b);
        System.gc();
        return b;
    }*/


    public boolean downloadBigFile(final FileInfo0 fileInfo0, ActiveProcess pb, final AlertDialog dialog) {
        int offset = 0;
        int readlen = 0, remain = 0, total = 0;
        FileInputStream fis = null;
        RandomAccessFile os = null;
        //TODO 下载注释掉了文件大小判断
		/*if (fileInfo0.getFilesize()<1) {
			Log.e(TAG,"invalid remote obj size 0");
			return false;
		}*/
        File f = new File(fileInfo0.getFilePath());
        if (f.isDirectory()) {
            return false;
        } else {// download new file
            try {
                f.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
        if (!NetworkUtils.isNetworkAvailable(context)) {
            return false;
        }
        offset = (int) f.length();
        InputTrasnport inputTrasnport = new InputTrasnport(fileInfo0.getObjid(), fileInfo0.getFtype());
        try {
            os = new RandomAccessFile(f, "rws");
            total = inputTrasnport.getobjlength().intValue();

            if (total <= 0) {
                Log.e(TAG, " obj length invild");
                return false;
            }
            remain = total - offset;
            if (remain <= 0) {
                if (pb != null) {
                    pb.toastMain("文件已存在");
                }
                Log.d(TAG, "file is completed abort download");
                return true;
            }
            if (offset > 0)
                os.seek(offset);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (TException e) {
            e.printStackTrace();
            Log.w(TAG, " query objlenth fail ");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        int buflen=Math.min((int) (total - offset), Maxbuflen);
        byte[] buffer = new byte[buflen];

        if (pb != null&&dialog==null) {
            pb.setMaxProgress(100);
            pb.setParMessage("正在下载");
        }
        if (inputTrasnport == null) {
            Log.e(TAG, "open inputstrnsport fail");
            return false;
        }
        su.open();
        long t0=System.currentTimeMillis();
        long t1=0;
        while ((readlen = inputTrasnport.read(buffer, offset, buflen)) > -1) {
            try {
                os.write(buffer, 0, readlen);
                offset += readlen;
                //Log.d(TAG,"read:"+offset+" bytes");
                final int progress = (offset * 100 / total);
                t1=System.currentTimeMillis();
                Log.d(TAG, "progress :" + progress + " " +(int) (offset/1024 / ((t1 - t0)/1000))  + " k/s");
                //fileInfo0.setOffset(offset);
                //su.setDownloadStatus(fileInfo0);
                if(pb!=null){
                    if(dialog==null){
                        pb.updateProgress(progress);
                    }else{
                        pb.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.setMessage(fileInfo0.getObjid()+"   "+progress+"%");
                            }
                        });
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                break;
            }
        }
        try {
            //os.flush();
            os.close();
            if (offset!=total)
                f.deleteOnExit();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            f.deleteOnExit();
            return false;
        } finally {
            if (pb != null&&dialog==null) {
                pb.toastMain("下载完成");
            }
            su.close();
        }
        MediaMgr.fileScan(fileInfo0.getFilePath(), context);
        return true;
    }

    public boolean uploadBigFile(FileInfo0 entity, final ActiveProcess activeProcess, AlertDialog dialog) {
        return uploadBigFile0(entity, activeProcess, null, false,dialog);
    }

    public boolean uploadFileOvWrite(FileInfo0 entity, final ActiveProcess activeProcess, HashMap<String, String> desc, AlertDialog dialog) {
        return uploadBigFile0(entity, activeProcess, desc, true, dialog);
    }

    public synchronized  boolean uploadBigFile0(final FileInfo0 entity, final ActiveProcess activeProcess, Map<String, String> desc, boolean replace, final AlertDialog dialog) {

        TClient tClient = null;
        File file;
        long size =entity.getFilesize();
        if (!NetworkUtils.isNetworkAvailable(context)) {
            return false;
        }
        try {
            //tClient = new TClient(false); 创建新的实列
			tClient = TClient.getinstance();
        } catch (Exception e) {
            Log.w(TAG, e.getLocalizedMessage());
            return false;
        }
        file = new File(entity.getFilePath());
        if (!file.exists() ) {
            Log.d(TAG,entity.getFilePath()+" not exsits");
            return false;
        }
        if (!file.isFile()) {
            Log.d(TAG,entity.getFilePath()+" not a file");
            return false;
        }
        if (size < 1) {
            size = file.length();
            entity.setFilesize(size);
        }
        if (size < 1)
            return false;

        System.out.println("开始上传喽");
        //先检查 云端是否 有同名文件
        long start = -1;
        if (activeProcess != null&&dialog==null) {
            activeProcess.updateProgress(0);
        }
        //是否 需要查询 服务器端是否有同名的 未传完的 文件

        //fileInfo=null;
        if (replace) {
            Log.d(TAG, "del remote exist obj :" + entity.getObjid());
            if (!tClient.delObj(entity.getObjid(), entity.getFtype()))
                Log.d(TAG, "del remote obj :" + "fail !!!");
        }
        su.open();
        TClient.TFilebuilder filebuilder = null;
        long oft=0l;

        FileInfo fileInfo = tClient.queryFile(entity);
        if (fileInfo != null) {
            Log.e(TAG, "upload file exist !!");
            if (replace) {
                Log.d(TAG, "del remote exist obj :" + entity.getObjid());
               if (! tClient.delObj(entity.getObjid(), entity.getFtype())) {
                   Log.e(TAG, "del remote exist obj :" + entity.getObjid()+ " failed !!");
                  // return  false;
               }
            } else {
                if(activeProcess!=null){
                    activeProcess.setParMessage("云端文件已经存在了");
                    activeProcess.finishProgress();
                }
                //su.close();
                return true;
            }
        } else
        {
            oft = tClient.queryUpObjOffset(entity);
            if ((oft > 0)) {

                if (size == oft) {
                    Log.e(TAG, "remote obj exist!!");
                    boolean ret = false;
                    try {
                        ret=tClient.CommitObj(entity.objid, entity.ftype,null);
                        if(activeProcess!=null){
                        activeProcess.finishProgress();
                        }
                        su.close();
                        return ret;

                    } catch (TException e) {
                        e.printStackTrace();
                    }
                    start =/*entity.getOffset()*/oft;
                   // return ret;
                }
                if (size < oft) {
                    //su.close();
                    Log.e(TAG, "remote file size > local");
                    if (!tClient.delObj(entity.getObjid(),entity.getFtype())) {
                        if (activeProcess != null) {
                            activeProcess.setParMessage("云端文件已经存在");
                            activeProcess.finishProgress();
                        }
                        return false;
                    }
                    else {
                        Log.e(TAG, "del remote upload obj");

                    }
                }

            } else {
                if (oft ==-1) {
                    //start=oft;
                    //su.close();
                    Log.e(TAG, " query  obj not exist !!");
                   // return false;
                }
                else
                {
                    su.close();
                    Log.e(TAG, " query obj failed ");
                    if (activeProcess != null) {
                        activeProcess.setParMessage("查询远程失败");
                        activeProcess.finishProgress();
                    }
                     return false;
                }
            }
            //start=Math.max(1,start);
            if(activeProcess!=null){
            if (dialog != null) {
                final long finalStart = start;
                final long finalSize = size;
                activeProcess.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                     int  pro= (int) ((Math.max(1, finalStart))*100 / finalSize);
                        if(dialog!=null){
                            dialog.setMessage(entity.getObjid()+"   "+pro+"%");
                        }
                    }
                });
            }else{

                activeProcess.setParMessage("正在上传");
                activeProcess.setMaxProgress(100);
                activeProcess.setProgress((int) ((Math.max(1,start))*100 / size));
            }
            }
        }
        int len = 0;

        boolean succed = false;

        try {
            String fname = entity.getObjid() == null ? MediaFileUtil.getFnameformPath(entity.getFilePath()) : entity.getObjid();
            filebuilder = tClient.new TFilebuilder(fname, entity.getFtype(),(int)size);
            String objid = null;
            if (start == -1 ) {
                objid = filebuilder.ApplyObj();
                if (objid == null) {
                    Log.e(TAG, "alloc obj failed ");
                    return false;
                }
                start=0;
                entity.setObjid(objid);
                //su.setUploadStatus(entity);
            } else {
                objid = entity.getObjid();
                filebuilder.setObj(objid);
            }
            RandomAccessFile rf = new RandomAccessFile(entity.getFilePath(), "r");
            int bufflen=  Math.min(Maxbuflen,(int)(size - start));
            byte[] buffer = new byte[bufflen];
            rf.seek(start);
            int proc=0,proc1=0;
            float speed=0f,speed1=0f;
			long pz = 1,t1=0,t0=0,t00=System.currentTimeMillis();
            int bflen=Math.min(1024*64, buffer.length);
            float unit=1000f/1024f;
            while ((len = rf.read(buffer, 0, bflen)) != -1) {
                pz = pz + len;
                t0=System.currentTimeMillis();
                if (filebuilder.Append(/*pz,*/buffer,len)) {
                        t1 = System.currentTimeMillis();
                        speed = (len  / ((t1 - t0)));
                        speed1=(pz/(t1-t00));
                        if (speed > speed1) {
                            //speed1 = speed;
                            bflen +=(1024*3);
                            bflen=Math.min(bflen,Maxbuflen);
                            Log.d(TAG, "upload speed:" + speed*unit + " k/s"+ "  pklen:"+bflen);
                        }
                        entity.setOffset(pz);
                        proc1 = (int) (pz * 100 / size);
                        if (activeProcess != null && proc != (proc1)) {
                            if(dialog!=null){
                            final int finalProc = proc1;
                            activeProcess.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.setMessage(entity.getObjid()+"  "+ finalProc+"%");
                                }
                            });
                            }else{
                                activeProcess.setProgress(proc1);
                            }
                        }
                        //su.setUploadStatus(entity);
                        succed = true;

                } else {
                    break;
                }
            }

            if (succed)
            {
                if (filebuilder.Commit(desc)) {
                    su.finishTransform(MediaMgr.DBTAB.UPed, entity);
                    succed = true;
                    Log.d(TAG, objid + " upload finished !!");
                }
                else
                {
                    Log.d(TAG, objid + " upload commit failed  !!");
                    succed=false;
                }

            }

           /*     desc.clear();
                desc = null;*/


        } catch (Exception e) {
            Log.w(TAG, e.getMessage());
        } finally {
            if (activeProcess != null&&dialog==null) {
                if (succed){
                    if(entity.getFtype()== FTYPE.MUSIC){
                        EventBus.getDefault().post(new MessageEvent(FTYPE.MUSIC,""));
                    }
                    activeProcess.toastMain("上传成功");
                } else
                    activeProcess.toastMain("上传失败");
            }
            su.close();
            if (!succed) {
                if (filebuilder != null)
                    filebuilder.DestoryObj();
                return false;
            }
        }
        return succed;
    }



}
