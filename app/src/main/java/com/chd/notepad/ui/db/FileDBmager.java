package com.chd.notepad.ui.db;

import android.content.Context;
import android.util.Log;

import com.chd.proto.FileInfo;
import com.chd.yunpan.share.ShareUtils;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
    }



    private List<String> files=new ArrayList<String>();
    public Iterator getLocallist(String path)
    {
        File dir=new File(path);

        files=Arrays.asList(dir.list(new MFileFilter(file_ext)));
        //Arrays.sort(files);
        Collections.sort(files);
        return  files.iterator();
    }

    public boolean delFile(String fname) {
       /* File file=new File(fname);
        return  file.delete();*/
        return  _context.deleteFile(_path + File.separator+fname);
    }

    protected class SortBydesc implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return  (o1.compareTo(o2));
        }
    }

    class MFileFilter implements FilenameFilter {
        String _ext;
        public MFileFilter(String ext)
        {
            _ext=ext;
        }
        @Override
        public boolean accept(File dir, String filename) {
            return (filename.length()-filename.lastIndexOf(_ext)-_ext.length()==0);
        }
    }



    public String readFile(String fileName)  {

        String res="";
        File file = new File(_path+File.separator+fileName+file_ext);

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            int length = fis.available();

            byte [] buffer = new byte[length];
            fis.read(buffer);

            res = EncodingUtils.getString(buffer, "UTF-8");

            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }


        return res;
    }

    //写文件
    public synchronized  void writeFile(String fileName, String write_str) throws IOException{

        File file = new File(_path+File.separator+fileName+file_ext);

        FileOutputStream fos = new FileOutputStream(file);

        byte [] bytes = write_str.getBytes();

        fos.write(bytes);

        fos.close();
    }
}
