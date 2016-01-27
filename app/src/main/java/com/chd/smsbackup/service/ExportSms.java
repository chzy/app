package com.chd.smsbackup.service;

/**
 * Created by lxp1 on 2015/11/21.
 */

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;
import android.util.Xml;

import com.chd.MediaMgr.utils.MediaFileUtil;
import com.chd.base.Ui.ActiveProcess;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.service.SyncLocalFileBackground;
import com.chd.smsbackup.entity.SmsField;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class ExportSms {

    ActiveProcess context;
    public static final String SMS_URI_ALL = "content://sms/";
    private FileOutputStream outStream = null;
    private XmlSerializer serializer;

    public ExportSms(ActiveProcess context) {
        this.context = context;
    }

    public void xmlStart(String path) {

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(path);
        try {
            outStream = new FileOutputStream(file2);
            serializer = Xml.newSerializer();
            serializer.setOutput(outStream, "UTF-8");
            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, "sms");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int getCount(){
        ContentResolver conResolver = context.getContentResolver();
        String[] projection = new String[] { SmsField.ADDRESS, SmsField.PERSON, SmsField.DATE, SmsField.PROTOCOL,
                SmsField.READ, SmsField.STATUS, SmsField.TYPE, SmsField.REPLY_PATH_PRESENT,
                SmsField.BODY,SmsField.LOCKED,SmsField.ERROR_CODE, SmsField.SEEN }; // type=1是收件箱，==2是发件箱;read=0表示未读，read=1表示读过，seen=0表示未读，seen=1表示读过
        Uri uri = Uri.parse(SMS_URI_ALL);
        Cursor cursor = conResolver.query(uri, projection, null, null, "_id asc");
        if(cursor!=null){
            return cursor.getCount();
        }else{
            return 0;
        }

    }



    public boolean createXml(String path) throws Exception {

        this.xmlStart(path);
        Cursor cursor = null;
        try {
            ContentResolver conResolver = context.getContentResolver();
            String[] projection = new String[] { SmsField.ADDRESS, SmsField.PERSON, SmsField.DATE, SmsField.PROTOCOL,
                    SmsField.READ, SmsField.STATUS, SmsField.TYPE, SmsField.REPLY_PATH_PRESENT,
                    SmsField.BODY,SmsField.LOCKED,SmsField.ERROR_CODE, SmsField.SEEN }; // type=1是收件箱，==2是发件箱;read=0表示未读，read=1表示读过，seen=0表示未读，seen=1表示读过
            Uri uri = Uri.parse(SMS_URI_ALL);
            cursor = conResolver.query(uri, projection, null, null, "_id asc");
            int count = cursor.getCount();
            int current=0;
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
                do {
                    // 如果address == null，xml文件中是不会生成该属性的,为了保证解析时，属性能够根据索引一一对应，必须要保证所有的item标记的属性数量和顺序是一致的
                    address = cursor.getString(cursor.getColumnIndex(SmsField.ADDRESS));
                    if (address == null) {
                        address = "";
                    }
                    person = cursor.getString(cursor.getColumnIndex(SmsField.PERSON));
                    if (person == null) {
                        person = "";
                    }
                    date = cursor.getString(cursor.getColumnIndex(SmsField.DATE));
                    if (date == null) {
                        date = "";
                    }
                    protocol = cursor.getString(cursor.getColumnIndex(SmsField.PROTOCOL));
                    if (protocol == null) {// 为了便于xml解析
                        protocol = "";
                    }
                    read = cursor.getString(cursor.getColumnIndex(SmsField.READ));
                    if (read == null) {
                        read = "";
                    }
                    status = cursor.getString(cursor.getColumnIndex(SmsField.STATUS));
                    if (status == null) {
                        status = "";
                    }
                    type = cursor.getString(cursor.getColumnIndex(SmsField.TYPE));
                    if (type == null) {
                        type = "";
                    }
                    reply_path_present = cursor.getString(cursor.getColumnIndex(SmsField.REPLY_PATH_PRESENT));
                    if (reply_path_present == null) {// 为了便于XML解析
                        reply_path_present = "";
                    }
                    body = cursor.getString(cursor.getColumnIndex(SmsField.BODY));
                    if (body == null) {
                        body = "";
                    }
                    locked = cursor.getString(cursor.getColumnIndex(SmsField.LOCKED));
                    if (locked == null) {
                        locked = "";
                    }
                    error_code = cursor.getString(cursor.getColumnIndex(SmsField.ERROR_CODE));
                    if (error_code == null) {
                        error_code = "";
                    }
                    seen = cursor.getString(cursor.getColumnIndex(SmsField.SEEN));
                    if (seen == null) {
                        seen = "";
                    }
                    // 生成xml子标记
                    // 开始标记
                    serializer.startTag(null, "item");
                    // 加入属性
                    serializer.attribute(null, SmsField.ADDRESS, address);
                    serializer.attribute(null, SmsField.PERSON, person);
                    serializer.attribute(null, SmsField.DATE, date);
                    serializer.attribute(null, SmsField.PROTOCOL, protocol);
                    serializer.attribute(null, SmsField.READ, read);
                    serializer.attribute(null, SmsField.STATUS, status);
                    serializer.attribute(null, SmsField.TYPE, type);
                    serializer.attribute(null, SmsField.REPLY_PATH_PRESENT, reply_path_present);
                    try{
                    serializer.attribute(null, SmsField.BODY, body);
                    }catch (Exception e){
                        String bod= URLEncoder.encode(body,"utf-8");
                        bod=bod.replace("%00","");
                        String bo= URLDecoder.decode(bod,"utf-8");
                        serializer.attribute(null,SmsField.BODY,bo);
                    }
                    serializer.attribute(null, SmsField.LOCKED, locked);
                    serializer.attribute(null, SmsField.ERROR_CODE, error_code);
                    serializer.attribute(null, SmsField.SEEN, seen);
                    // 结束标记
                    serializer.endTag(null, "item");
                    context.updateProgress(current+1,count);
                    current++;
                } while (cursor.moveToNext());
            } else {
                return false;
            }

        } catch (SQLiteException ex) {
            ex.printStackTrace();
            Log.d("SQLiteException:", ex.getMessage());
        }finally {
            context.finishProgress();
            if(cursor != null) {
                cursor.close();//手动关闭cursor，及时回收
            }
        }
        serializer.endTag(null, "sms");
        serializer.endDocument();
        outStream.flush();
        outStream.close();
        upload(path,context);
        return true;
    }



    /**
         * 导出联系人信息
         *
         * @param filePath 存放导出信息的文件
         * @param activity 主窗口
         */

    public boolean upload(final String filePath, final ActiveProcess activity) {


        // 线程中执行
        //new Thread(new Runnable() {
        //public void run() {
        FileInfo0 info = new FileInfo0();
        info.setObjid(MediaFileUtil.getNameFromFilepath(filePath));
        info.setFilePath(filePath);
        info.setFtype(FTYPE.SMS);
        return new SyncLocalFileBackground(activity).uploadBigFile(info, activity);
        //}
        //}).start();
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


}