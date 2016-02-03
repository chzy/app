package com.chd.contacts.vcard;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;

import com.chd.MediaMgr.utils.MediaFileUtil;
import com.chd.TClient;
import com.chd.base.Ui.ActiveProcess;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.service.SyncLocalFileBackground;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class VCardIO {
    private final String TAG = this.getClass().getName();
    private Context context;

    public VCardIO(Context context) {
        this.context = context;
    }

    /**
     * 导入联系人信息
     *
     * @param fileName 要导入的文件
     * @param replace  是否替换先有联系人
     * @param activity 主窗口
     */
    public void doImport(final String fileName, final boolean replace,
                         final ActiveProcess activity) {
        new Thread() {
            @Override
            public void run() {
                try {
                    File vcfFile = new File(fileName);

                    final BufferedReader vcfBuffer = new BufferedReader(
                            new FileReader(fileName));
                    final long maxlen = vcfFile.length();

                    // 后台执行导入过程
                    long importStatus = 0;
                    Contact parseContact = new Contact();
                    try {
                        long ret = 0;
                        do {
                            ret = parseContact.parseVCard(vcfBuffer);
                            if (ret < 0) {
                                break;
                            }
                            parseContact.addContact(
                                    context.getApplicationContext(), 0,
                                    replace);
                            importStatus += parseContact.getParseLen();
                            // 更新进度条
                            activity.updateProgress((int) (100 * importStatus / maxlen));

                        } while (true);
                        activity.finishProgress();

                    } catch (IOException e) {
                        activity.finishProgress();
                    }

                } catch (FileNotFoundException e) {
                    activity.finishProgress();
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public void getLocalSize(final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ContentResolver cResolver = context
                        .getContentResolver();
                String[] projection = {ContactsContract.Contacts._ID};
                final Cursor allContacts;
                allContacts = cResolver.query(
                        ContactsContract.Contacts.CONTENT_URI, projection,
                        null, null, null);
                if (allContacts != null) {
                    int maxlen = allContacts.getCount();
                    Message msg = new Message();
                    msg.what = 998;
                    msg.obj = maxlen;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }


    public void getNetSize(final String fileName, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int size = 0;
                Message msg = new Message();
                msg.what = 999;
                try {
//                    File vcfFile = new File(fileName);
//
//                    final BufferedReader vcfBuffer = new BufferedReader(
//                            new FileReader(fileName));
//                    final long maxlen = vcfFile.length();
//
//                    // 后台执行导入过程
//                    long importStatus = 0;
//
//                    Contact parseContact = new Contact();
//                        long ret = 0;
//                        do {
//                            ret = parseContact.parseVCard(vcfBuffer);
//                            if (ret < 0) {
//                                break;
//                            }
//                            size++;
//                            importStatus += parseContact.getParseLen();
//
//                        } while (true);

                    TClient tClient = TClient.getinstance();
                    String lines = tClient.queryAttribute(fileName, FTYPE.ADDRESS, "lines");
                    size = Integer.parseInt(lines);

                    msg.obj = size;

                } catch (Exception e) {
                    msg.obj = 0;
                } finally {
                    handler.sendMessage(msg);
                }

               /* } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }*/

            }
        }).start();
    }


    public void doExport(final String fileName, final ActiveProcess activity) {
                try {
                    /*if (1==1)
                        upload( fileName,  activity);*/
                    final BufferedWriter vcfBuffer = new BufferedWriter(
                            new FileWriter(fileName));

                    final ContentResolver cResolver = context
                            .getContentResolver();
                    String[] projection = {ContactsContract.Contacts._ID};
                    final Cursor allContacts;
                    //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ECLAIR) {
                    allContacts = cResolver.query(
                            ContactsContract.Contacts.CONTENT_URI, projection,
                            null, null, null);
                    //}
                    if (!allContacts.moveToFirst()) {
                        allContacts.close();
                        return;
                    }

                    final long maxlen = allContacts.getCount();
                    // 线程中执行导出
                    {
                        long exportStatus = 0;
                        String id = null;
                        Contact parseContact = new Contact();
                        try {
                            do {
                                id = allContacts.getString(0);
                                parseContact.getContactInfoFromPhone(id, cResolver);
                                parseContact.writeVCard(vcfBuffer);
                                ++exportStatus;
                                // 更新进度条
                                activity.updateProgress((int) (50 * exportStatus / maxlen));
                            } while (allContacts.moveToNext());
                            activity.finishProgress();
                            vcfBuffer.flush();
                            vcfBuffer.close();
                            allContacts.close();
                            if (!upload(fileName, activity)) {
                                Log.e("", "upload failed ");
                            }else{
                                TClient tClient =TClient.getinstance();
                                TClient.TFilebuilder filebuilder;
                                String name=MediaFileUtil.getNameFromFilepath(fileName);
                                filebuilder = tClient.new TFilebuilder(name, FTYPE.ADDRESS);
                                String objid=null;
                                objid = filebuilder.ApplyObj();
                                if (objid == null) {
                                    Log.e("smsexport","alloc obj failed ");
                                }
                                Map<String, String> desc = new HashMap<String, String>();
                                desc.put("lines", maxlen + "");
                                boolean commit = filebuilder.Commit(desc);
                                Log.i("liumj",commit+"提交desc");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            activity.finishProgress();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    activity.finishProgress();
                }


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
        info.setFtype(FTYPE.ADDRESS);
        return new SyncLocalFileBackground(context).uploadFileOvWrite(info, activity);
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
        info.setFtype(FTYPE.ADDRESS);
        return new SyncLocalFileBackground(context).downloadBigFile(info, activity);

    }


}
