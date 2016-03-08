package com.chd.smsbackup.newservice;

/**
 * Created by lxp1 on 2015/11/21.
 */

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.chd.MediaMgr.utils.MediaFileUtil;
import com.chd.TClient;
import com.chd.base.Ui.ActiveProcess;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.service.SyncLocalFileBackground;
import com.chd.smsbackup.entity.SmsField;
import com.chd.smsbackup.entity.SmsItem;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

public class ExportSms {

    ActiveProcess context;
    public static final String SMS_URI_ALL = "content://sms/";
    private final String  TAG=this.getClass().getName();
    public ExportSms(ActiveProcess context) {
        this.context = context;
    }



    /**
     * 下载联系人信息
     *
     * @param filePath 存放导出信息的文件
     * @param activity 主窗口
     */

    public boolean download(final String filePath, final ActiveProcess activity) {
        // 线程中执行
        FileInfo0 info = new FileInfo0();
        info.setObjid(MediaFileUtil.getNameFromFilepath(filePath));
        info.setFilePath(filePath);
        info.setFtype(FTYPE.SMS);
        return new SyncLocalFileBackground(activity).downloadBigFile(info, activity);

    }

    public void ExpSMS(final String fpath) {
        boolean b = false;
        try {
            b = ExpSMS0(fpath);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getCount(final Handler handler){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                int count=0;
                try {

                    ContentResolver conResolver = context.getContentResolver();
                    String[] projection = new String[] { SmsField.ADDRESS, SmsField.PERSON, SmsField.DATE, SmsField.PROTOCOL,
                            SmsField.READ, SmsField.STATUS, SmsField.TYPE, SmsField.REPLY_PATH_PRESENT,
                            SmsField.BODY,SmsField.LOCKED,SmsField.ERROR_CODE, SmsField.SEEN }; // type=1是收件箱，==2是发件箱;read=0表示未读，read=1表示读过，seen=0表示未读，seen=1表示读过
                    Uri uri = Uri.parse(SMS_URI_ALL);
                   Cursor cursor = conResolver.query(uri, projection, null, null, "_id asc");
                    if(cursor!=null){
                    count=cursor.getCount();
                    }else{
                        count=0;
                    }

                } catch (Exception e) {
                    count=0;
                }finally {
                    Message msg=new Message();
                    msg.what=998;
                    msg.obj=count;
                    handler.sendMessage(msg);
                }
            }

        });
        thread.start();
    }


    private boolean ExpSMS0(String fpath) throws Exception {



        File fout = new File(fpath);
        FileOutputStream fos;
        if (fout.isDirectory())
            return  false;
        fout.deleteOnExit();
        fos= new FileOutputStream(fout);


        Cursor cursor = null;
        int lines=0;
        int count=0;
        Gson gson = new Gson();
        boolean successed=true;
        try {
            ContentResolver conResolver = context.getContentResolver();
            String[] projection = new String[] { SmsField.ADDRESS, SmsField.PERSON, SmsField.DATE, SmsField.PROTOCOL,
                    SmsField.READ, SmsField.STATUS, SmsField.TYPE, SmsField.REPLY_PATH_PRESENT,
                    SmsField.BODY,SmsField.LOCKED,SmsField.ERROR_CODE, SmsField.SEEN }; // type=1是收件箱，==2是发件箱;read=0表示未读，read=1表示读过，seen=0表示未读，seen=1表示读过
            Uri uri = Uri.parse(SMS_URI_ALL);
            cursor = conResolver.query(uri, projection, null, null, "_id asc");
            count=cursor.getCount();
            StringBuilder stringBuilder=new StringBuilder();
            if (cursor.moveToFirst()) {
                // 查看数据库sms表得知 subject和service_center始终是null所以这里就不获取它们的数据了。
                String address;
                String person;
                String date;
                String protocol;
                String read;
                String status;
                String type;
                String reply_path_present;
                String body;
                String locked;
                String error_code;
                String seen;

                String  line;
                int len=0;
                char le;

                do {
                    // 如果address == null，xml文件中是不会生成该属性的,为了保证解析时，属性能够根据索引一一对应，必须要保证所有的item标记的属性数量和顺序是一致的
                    SmsItem smsItem=new SmsItem();
                    address = cursor.getString(cursor.getColumnIndex(SmsField.ADDRESS));
                    if (address == null) {
                        address = "";
                    }
                    len=address.length();
                    le=(char)len;
                    stringBuilder.append(SmsItem.dim+String.valueOf(le) + address);
                    smsItem.setAddress(address);

                    person = cursor.getString(cursor.getColumnIndex(SmsField.PERSON));
                    if (person == null) {
                        person = "";
                    }
                    smsItem.setPerson(person);
                    stringBuilder.append( String.valueOf((char)(person.length())) + person);

                    date = cursor.getString(cursor.getColumnIndex(SmsField.DATE));
                    if (date == null) {
                        date = "";
                    }
                    smsItem.setDate(date);
                    stringBuilder.append( String.valueOf((char)(date.length())) + date);
                    protocol = cursor.getString(cursor.getColumnIndex(SmsField.PROTOCOL));
                    if (protocol == null) {
                        protocol = "";
                    }
                    smsItem.setProtocol(protocol);
                    stringBuilder.append( String.valueOf((char)(protocol.length())) + protocol);
                    read = cursor.getString(cursor.getColumnIndex(SmsField.READ));
                    if (read == null) {
                        read = "";
                    }
                    smsItem.setRead(read);
                   stringBuilder.append( String.valueOf((char)(read.length())) + read);
                    status = cursor.getString(cursor.getColumnIndex(SmsField.STATUS));
                    if (status == null) {
                        status = "";
                    }
                    smsItem.setStatus(status);
                    stringBuilder.append( String.valueOf((char)(status.length())) + status);
                    type = cursor.getString(cursor.getColumnIndex(SmsField.TYPE));
                    if (type == null) {
                        type = "";
                    }
                    smsItem.setType(type);
                    stringBuilder.append( String.valueOf((char)(type.length())) + type);
                    reply_path_present = cursor.getString(cursor.getColumnIndex(SmsField.REPLY_PATH_PRESENT));
                    if (reply_path_present == null) {// 为了便于XML解析
                        reply_path_present = "";
                    }
                    smsItem.setReply_path_present(reply_path_present);
                   stringBuilder.append( String.valueOf((char)(reply_path_present.length())) + reply_path_present);
                    body = cursor.getString(cursor.getColumnIndex(SmsField.BODY));
                    if (body == null) {
                        body = "";
                    }
                    smsItem.setBody(body);
                    stringBuilder.append( String.valueOf((char)(body.length())) + body);
                    locked = cursor.getString(cursor.getColumnIndex(SmsField.LOCKED));
                    if (locked == null) {
                        locked = "";
                    }
                    smsItem.setLocked(locked);
                    stringBuilder.append( String.valueOf((char)(locked.length())) + locked);
                    error_code = cursor.getString(cursor.getColumnIndex(SmsField.ERROR_CODE));
                    if (error_code == null) {
                        error_code = "";
                    }
                    smsItem.setError_code(error_code);
                   stringBuilder.append( String.valueOf((char)(error_code.length())) + error_code);
                    seen = cursor.getString(cursor.getColumnIndex(SmsField.SEEN));
                    if (seen == null) {
                        seen = "";
                    }
                    smsItem.setSeen(seen);

                    stringBuilder.append( String.valueOf((char)(seen.length())) + seen+SmsItem.dim);
                    line= gson.toJson(smsItem,SmsItem.class);
                    //Log.d(TAG,line);

                    byte[] buffer=(  /*String.valueOf(SmsItem.dim)+*/line/*+String.valueOf(SmsItem.dim)*/).getBytes("utf8");
                    lines++;
                    fos.write(buffer);
                    context.updateProgress(lines,count);
/*
                    if (!filebuilder.Append(buffer)) {
                        successed = false;
                        break;
                    }
*/
                    stringBuilder.delete(0, stringBuilder.length());
                } while (cursor.moveToNext());
                context.finishProgress();
            } else {
                return false;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, ex.getMessage());
            successed=false;
        }finally {
            if(cursor != null) {
                cursor.close();//手动关闭cursor，及时回收
            }
        }
        fos.flush();
        fos.close();
            if (successed){
                boolean upload = upload(fpath, context,lines);
                if(upload){

                }
            }else{
                    return false;
            }
        return true;
    }


    public void getRemontCount(final String fpath, final Handler mHandler){

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg=new Message();
                msg.what=999;
                try {
                    TClient tClient = TClient.getinstance();
                    int last=fpath.lastIndexOf("/");
                    String name=fpath.substring(last + 1);
                    String lines=tClient.queryAttribute(name, FTYPE.SMS, "lines");
                    int size=0;
                    if (lines!=null) {
                        size = Integer.parseInt(lines);
                    }
                    msg.obj=size;
                } catch (Exception e) {
                    e.printStackTrace();
                   msg.obj=0;
                }finally {
                    mHandler.sendMessage(msg);
                }

            }
        }).start();

    }



    /**
     * 导出联系人信息
     *
     * @param filePath 存放导出信息的文件
     * @param activity 主窗口
     */

    public boolean upload(final String filePath, final ActiveProcess activity,int lines) {
        // 线程中执行
        int last=filePath.lastIndexOf("/");
        String name=filePath.substring(last+1);
        FileInfo0 info = new FileInfo0();
        info.setObjid(MediaFileUtil.getNameFromFilepath(filePath));
        info.setFilePath(filePath);
        info.setFilename(name);
        info.setFtype(FTYPE.SMS);
        HashMap<String,String> desc=new HashMap<String, String>();
        desc.put("lines",""+lines);
        return new SyncLocalFileBackground(activity).uploadFileOvWrite(info, activity,desc);
    }




}