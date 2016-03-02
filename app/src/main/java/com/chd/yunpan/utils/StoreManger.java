package com.chd.yunpan.utils;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * Created by lxp1 on 2015/11/12.
 */
public class StoreManger {


    public long getAvailableInternalMemorySize(){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks*blockSize;
    }

    public long getTotalInternalMemorySize(){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks*blockSize;
    }

    public boolean externalMemoryAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public long getAvailableExternalMemorySize(){
        if(externalMemoryAvailable()){
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks*blockSize;
        }
        else{
            return -1;
        }
    }

    public long getTotalExternalMemorySize(){
        if(externalMemoryAvailable()){
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks*blockSize;
        }
        else{
            return -1;
        }
    }

}
