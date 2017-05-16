package com.chd.smsbackup.newservice;

/**
 * Created by lxp1 on 2015/11/21.
 */

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.chd.base.Ui.ActiveProcess;
import com.chd.smsbackup.entity.SmsField;
import com.chd.smsbackup.entity.SmsItem;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ImportSms {

    private  final String TAG =this.getClass().getName() ;
    private ActiveProcess context;
    public static final String SMS_URI_ALL = "content://sms/inbox";

    private ContentResolver conResolver;

    public ImportSms(ActiveProcess context) {
        this.context = context;
        conResolver = context.getContentResolver();
    }

    public void  ImpSMS(final String backfile)
    {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                ImpSms0(backfile);
            }
        });
        thread.start();

    }

    private void InsertSMS(SmsItem item) {

        {

            // 判断短信数据库中是否已包含该条短信，如果有，则不需要恢复
            Cursor cursor = conResolver.query(Uri.parse("content://sms"), new String[] { SmsField.DATE }, SmsField.DATE + "=?",
                    new String[] { item.getDate() }, null);
            Uri smsuri=Uri.parse(SMS_URI_ALL);
            if (!cursor.moveToFirst()) {// 没有该条短信
                ContentValues values = new ContentValues();
                values.put(SmsField.ADDRESS, item.getAddress());
                // 如果是空字符串说明原来的值是null，所以这里还原为null存入数据库
                values.put(SmsField.PERSON, item.getPerson().equals("") ? null : item.getPerson());
                values.put(SmsField.DATE, item.getDate());
                values.put(SmsField.PROTOCOL, item.getProtocol().equals("") ? null : item.getProtocol());
                values.put(SmsField.READ, item.getRead());
                values.put(SmsField.STATUS, item.getStatus());
                values.put(SmsField.TYPE, item.getType());
                values.put(SmsField.REPLY_PATH_PRESENT, item.getReply_path_present().equals("") ? null : item.getReply_path_present());
                values.put(SmsField.BODY, item.getBody());
                values.put(SmsField.LOCKED, item.getLocked());
                values.put(SmsField.ERROR_CODE, item.getError_code());
                values.put(SmsField.SEEN, item.getSeen());
                Uri uri=conResolver.insert(smsuri, values);
                if (uri==null)
                    Log.e(TAG,"insert sms fail "+ item.getBody());
            }
            cursor.close();
        }
    }



    private List<SmsItem> ImpSms0(String bakfile){

        List<SmsItem> smsItems  = new ArrayList<SmsItem>();
        Gson gson=new Gson();
        File file = new File(bakfile);
        String line;
        if (!file.exists()) {
            Looper.prepare();
            Toast.makeText(context, "message.xml短信备份文件不在sd卡中", Toast.LENGTH_SHORT).show();
            Looper.loop();//退出线程
        }
        try {
            FileInputStream finput = new FileInputStream(file);
            int rd, pos = 0;
            ByteBuffer readbuf=ByteBuffer.allocate(1024);
            int len = 0, dimcout = 0;
            String tag;
            while ((rd = finput.read()) != -1) {

                if (rd == '}' )
                {
                    if (dimcout !=1)
                    {
                        pos=0;
                        len=0;
                        dimcout=0;
                        continue;
                    }
                     readbuf.put((byte)rd);

                    readbuf.flip();
                    byte[] strbuf=new byte[readbuf.remaining()];
                    readbuf.get(strbuf,0,strbuf.length);
                    tag = new String(strbuf);
                    strbuf=null;
                    SmsItem item = gson.fromJson(tag, SmsItem.class);
                    if (item != null)
                        InsertSMS(item);
                    else {
                        Log.e(TAG, "parse file error");
                        throw new Exception("parse file error");
                    }
                    pos = 0;
                    len = 0;
                    dimcout=0;
                }

                if (rd=='{') {
                    dimcout++;
                    readbuf.clear();
                }
                if (dimcout==1)
                { readbuf.put( (byte) rd);
                    pos++;
                    len++;
                }
            }



            finput.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
           // Looper.prepare();
            Toast.makeText(context, "短信恢复出错", Toast.LENGTH_LONG).show();
            //Looper.loop();
            e.printStackTrace();
            smsItems.clear();
            return  null;
        }finally {
            context.finishProgress();
        }
        return smsItems;
    }

}