package com.chd.notepad.ui.db;

import android.content.Context;
import android.util.Log;

import com.chd.proto.LoginResult;
import com.chd.yunpan.share.ShareUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lxp on 2016/1/25.
 */
public class FileDBmager {
    private Context _context;
    private  String _path;
    private final String TAG=this.getClass().getName();
    private final String file_ext=".ntp";

    public  FileDBmager(Context context)
    {
        this._context=context;
        _path=new ShareUtils(_context).getStorePathStr();
        ShareUtils shareUtils = new ShareUtils(context);
        LoginResult loginEntity = shareUtils.getLoginEntity();
        _path=_path+"/"+loginEntity.getUserid();
        if(!new File(_path).exists()){
            new File(_path).mkdir();
        }
    }



    private List<String> files=new ArrayList<String>();
    public Iterator getLocallist()
    {
        File dir=new File(_path);
        MFileFilter fileFilter=new MFileFilter(file_ext);
        files=Arrays.asList(dir.list(fileFilter));
        //Arrays.sort(files);
        Collections.sort(files);
        return  files.iterator();
    }

    public boolean delFile(String fname) {
        String delfile=_path + File.separator + fname+file_ext;
       File file=new File(delfile);
        if (file.exists()) {
            Log.d(TAG,delfile+" file not exists");
            if ( !file.delete())
                return _context.deleteFile(delfile);
            else
                return  true;
        }
        else
            return false;
       // return  _context.deleteFile(_path + File.separator + fname+file_ext);
    }

    public void editFile(String s, String contentText) {
        delFile(s);
        writeFile(System.currentTimeMillis()+"",contentText);
    }

    protected class SortBydesc implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return  (o1.compareTo(o2));
        }
    }

    class MFileFilter implements FilenameFilter {
        String _ext;
        int _min;

        public MFileFilter(String ext) {
            _ext = ext.toLowerCase();
            _min = _ext.length();
        }

        @Override
        public boolean accept(File dir, String filename) {
            //int idx=filename.toLowerCase().lastIndexOf(_ext);
            if (filename.length() < _min)
                return false;
            int idx = filename.lastIndexOf(_ext);
            if (idx < 0)
                return false;

            if (filename.length() - idx != _min)
                return false;

            for (int i = idx; --i >= 0; ) {
                if (!Character.isDigit(filename.charAt(i)))
                    return false;
            }
            return true;

        }
    }
    public String readFile(String fileName)  {

        String res="";
        File file = new File(_path+File.separator+fileName+file_ext);
                            //_path + File.separator + fname+file_ext;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            int length = fis.available();

            char [] buffer = new char[length];

            InputStreamReader reader=new InputStreamReader(fis,"UTF-8");
            int len = reader.read(buffer);
            res=new String(buffer,0,len);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }


        return res;
    }

    //写文件
    public   boolean writeFile(String fileName, String write_str){
        File file = new File(_path+File.separator+fileName+file_ext);
                            //_path + File.separator + fname+file_ext;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte [] bytes = write_str.getBytes();
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
            return false;
        }
        return true;
    }
}
