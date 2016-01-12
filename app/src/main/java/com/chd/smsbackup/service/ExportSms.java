package com.chd.smsbackup.service;

/**
 * Created by lxp1 on 2015/11/21.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.chd.TClient;
import com.chd.proto.FTYPE;
import com.chd.smsbackup.entity.SmsField;
import com.chd.smsbackup.entity.SmsItem;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class ExportSms {

    Context context;
    public static final String SMS_URI_ALL = "content://sms/";
    //private FileOutputStream outStream = null;
    private XmlSerializer serializer;

    public ExportSms(Context context) {
        this.context = context;
    }

    /*public void xmlStart() {

        String path = Environment.getExternalStorageDirectory().getPath() + "/SMSBackup";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(path, "message.xml");
        try {
            outStream = new FileOutputStream(file2);
            serializer = Xml.newSerializer();
            serializer.setOutput(outStream, "UTF-8");
            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, "sms");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static void writeFile1() throws IOException {
        File fout = new File("out.txt");
        FileOutputStream fos = new FileOutputStream(fout);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (int i = 0; i < 10; i++) {
            bw.write("something");
            bw.newLine();
        }

        bw.close();
    }


    public boolean CreateTxtfile(String fpath) throws Exception {

        File fout = new File(fpath);
        FileOutputStream fos;
        if (fout.isDirectory())
            return  false;
        fout.deleteOnExit();
        fos= new FileOutputStream(fout);
        //ObjectOutputStream oos = new ObjectOutputStream(fos);
        //,FileOutputStream outStream
        TClient tClient =TClient.getinstance();
        TClient.TFilebuilder filebuilder;
        filebuilder = tClient.new TFilebuilder(fout.getName(), FTYPE.SMS);
        String objid=null;
        {
            objid = filebuilder.ApplyObj();
            if (objid == null) {
                Log.e("smsexport","alloc obj failed ");
                return false;
            }
        }

        Cursor cursor = null;
        int lines=0;
        Gson gson = new Gson();
        boolean successed=true;
        try {
            ContentResolver conResolver = context.getContentResolver();
            String[] projection = new String[] { SmsField.ADDRESS, SmsField.PERSON, SmsField.DATE, SmsField.PROTOCOL,
                    SmsField.READ, SmsField.STATUS, SmsField.TYPE, SmsField.REPLY_PATH_PRESENT,
                    SmsField.BODY,SmsField.LOCKED,SmsField.ERROR_CODE, SmsField.SEEN }; // type=1是收件箱，==2是发件箱;read=0表示未读，read=1表示读过，seen=0表示未读，seen=1表示读过
            Uri uri = Uri.parse(SMS_URI_ALL);
            cursor = conResolver.query(uri, projection, null, null, "_id asc");
            int all = cursor.getCount();
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
                    //String dd=stringBuilder.toString();
                    //len=dd.charAt(0);
                    smsItem.setAddress(address);

                    person = cursor.getString(cursor.getColumnIndex(SmsField.PERSON));
                    if (person == null) {
                        person = "";
                    }
                    smsItem.setPerson(person);
                   // byte[] bytes=person.getBytes("utf8");
                    //stringBuilder.append(person+SmsItem.dim);
                    stringBuilder.append( String.valueOf((char)(person.length())) + person);

                    date = cursor.getString(cursor.getColumnIndex(SmsField.DATE));
                    if (date == null) {
                        date = "";
                    }
                    smsItem.setDate(date);
                    //stringBuilder.append(date+SmsItem.dim);
                    stringBuilder.append( String.valueOf((char)(date.length())) + date);
                    protocol = cursor.getString(cursor.getColumnIndex(SmsField.PROTOCOL));
                    if (protocol == null) {// 为了便于xml解析
                        protocol = "";
                    }
                    smsItem.setProtocol(protocol);
                   // stringBuilder.append(protocol+SmsItem.dim);
                    stringBuilder.append( String.valueOf((char)(protocol.length())) + protocol);
                    read = cursor.getString(cursor.getColumnIndex(SmsField.READ));
                    if (read == null) {
                        read = "";
                    }
                    smsItem.setRead(read);
                    //stringBuilder.append(read+SmsItem.dim);
                   stringBuilder.append( String.valueOf((char)(read.length())) + read);
                    status = cursor.getString(cursor.getColumnIndex(SmsField.STATUS));
                    if (status == null) {
                        status = "";
                    }
                    smsItem.setStatus(status);
                    //stringBuilder.append(status+SmsItem.dim);
                    stringBuilder.append( String.valueOf((char)(status.length())) + status);
                    type = cursor.getString(cursor.getColumnIndex(SmsField.TYPE));
                    if (type == null) {
                        type = "";
                    }
                    smsItem.setType(type);
                   // stringBuilder.append(type + SmsItem.dim);
                    stringBuilder.append( String.valueOf((char)(type.length())) + type);
                    reply_path_present = cursor.getString(cursor.getColumnIndex(SmsField.REPLY_PATH_PRESENT));
                    if (reply_path_present == null) {// 为了便于XML解析
                        reply_path_present = "";
                    }
                    smsItem.setReply_path_present(reply_path_present);
                    //stringBuilder.append(reply_path_present + SmsItem.dim);
                   stringBuilder.append( String.valueOf((char)(reply_path_present.length())) + reply_path_present);
                    body = cursor.getString(cursor.getColumnIndex(SmsField.BODY));
                    if (body == null) {
                        body = "";
                    }
                    smsItem.setBody(body);
                    //stringBuilder.append(body + SmsItem.dim);
                    stringBuilder.append( String.valueOf((char)(body.length())) + body);
                    locked = cursor.getString(cursor.getColumnIndex(SmsField.LOCKED));
                    if (locked == null) {
                        locked = "";
                    }
                    smsItem.setLocked(locked);
                   //stringBuilder.append(locked + SmsItem.dim);
                    stringBuilder.append( String.valueOf((char)(locked.length())) + locked);
                    error_code = cursor.getString(cursor.getColumnIndex(SmsField.ERROR_CODE));
                    if (error_code == null) {
                        error_code = "";
                    }
                    smsItem.setError_code(error_code);
                    //stringBuilder.append(error_code + SmsItem.dim);
                   stringBuilder.append( String.valueOf((char)(error_code.length())) + error_code);
                    seen = cursor.getString(cursor.getColumnIndex(SmsField.SEEN));
                    if (seen == null) {
                        seen = "";
                    }
                    smsItem.setSeen(seen);
                    //stringBuilder.append(seen + "\n");
                    //String line=stringBuilder.toString();
                    stringBuilder.append( String.valueOf((char)(seen.length())) + seen+SmsItem.dim);
                    line= gson.toJson(smsItem,SmsItem.class);
                   // line=stringBuilder.toString();
                    Log.d("@@@",line);

                    //byte[] buffer=stringBuilder.toString().getBytes("utf8");
                    byte[] buffer=(  /*String.valueOf(SmsItem.dim)+*/line/*+String.valueOf(SmsItem.dim)*/).getBytes("utf8");
                    //line=new String(buffer,"utf8");
                    lines++;
                    fos.write(buffer);
                    if (!filebuilder.Append(buffer)) {
                        successed = false;
                        break;
                    }
                   /* if (outStream!=null)
                        outStream.write(buffer);*/
                    stringBuilder.delete(0, stringBuilder.length());

                    //break;

                } while (cursor.moveToNext());


            } else {

                return false;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("SQLiteException:", ex.getMessage());
            successed=false;
        }finally {
            if(cursor != null) {
                cursor.close();//手动关闭cursor，及时回收
            }
        }
        fos.flush();
        fos.close();

        if (filebuilder!=null) {
            if (!successed)
            {
                filebuilder.DestoryObj();
                return false;
            }
            else {
                Map<String, String> desc = new HashMap<String, String>();
                desc.put("lines", String.valueOf(lines));
                filebuilder.Commit(desc);
            }
        }
        Toast.makeText(context,"back ok ",Toast.LENGTH_LONG).show();
        return true;
    }


  /*  public boolean createXml() throws Exception {

        this.xmlStart();
        Cursor cursor = null;
        try {
            ContentResolver conResolver = context.getContentResolver();
            String[] projection = new String[] { SmsField.ADDRESS, SmsField.PERSON, SmsField.DATE, SmsField.PROTOCOL,
                    SmsField.READ, SmsField.STATUS, SmsField.TYPE, SmsField.REPLY_PATH_PRESENT,
                    SmsField.BODY,SmsField.LOCKED,SmsField.ERROR_CODE, SmsField.SEEN }; // type=1是收件箱，==2是发件箱;read=0表示未读，read=1表示读过，seen=0表示未读，seen=1表示读过
            Uri uri = Uri.parse(SMS_URI_ALL);
            cursor = conResolver.query(uri, projection, null, null, "_id asc");
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
                    serializer.attribute(null, SmsField.BODY, body);
                    serializer.attribute(null, SmsField.LOCKED, locked);
                    serializer.attribute(null, SmsField.ERROR_CODE, error_code);
                    serializer.attribute(null, SmsField.SEEN, seen);
                    // 结束标记
                    serializer.endTag(null, "item");

                } while (cursor.moveToNext());
            } else {

                return false;
            }

        } catch (SQLiteException ex) {
            ex.printStackTrace();
            Log.d("SQLiteException:", ex.getMessage());
        }finally {
            if(cursor != null) {
                cursor.close();//手动关闭cursor，及时回收
            }
        }
        serializer.endTag(null, "sms");
        serializer.endDocument();
        outStream.flush();
        outStream.close();
        return true;
    }*/
}