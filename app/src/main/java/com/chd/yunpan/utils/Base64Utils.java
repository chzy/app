package com.chd.yunpan.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Base64Utils {

    /**
     *
     * @param imgPath
     * @param bitmap
     * @param url 图片地址
     * @return
     *
     * 
     */
    public static String imgToBase64(File file, Bitmap bitmap, String url) {
        if (file !=null) {
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static Bitmap readBitmap(String imgPath) {
        try {
            String replace = imgPath.replace("file://", "");
            return BitmapFactory.decodeFile(replace);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Auto-generated catch block  
            return null;
        }

    }


    /**
     * base64字符串转文件
     * @param base64
     * @return
     */
    public static File base64ToFile(String base64,String pathName) {
        File file = null;
        FileOutputStream out = null;
        try {
            // 解码，然后将字节转换为文件
            file = new File(pathName);
            if (!file.exists())
                file.createNewFile();
            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);// 将字符串转换为byte数组
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            byte[] buffer = new byte[1024];
            out = new FileOutputStream(file);
            int bytesum = 0;
            int byteread = 0;
            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread); // 文件写操作
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (out!= null) {
                    out.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return file;
    }
    /**
     *
     * @param base64Data
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

//        File myCaptureFile = new File("/sdcard/", imgName);
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(myCaptureFile);
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        boolean isTu = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//        if (isTu) {
//            // fos.notifyAll();
//            try {
//                if(fos!=null){
//                fos.flush();
//                fos.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            try {
//                if(fos!=null){
//                fos.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }


    public static Bitmap getBitMBitmap(String urlpath) {
        Bitmap map = null;
        InputStream in = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                in.close();
                map.recycle();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return map;
    }


}