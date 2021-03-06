package com.chd.yunpan.share;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

import com.chd.proto.LoginResult;
import com.chd.yunpan.net.CookieUtil;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class ShareUtils {
    private SharedPreferences sp = null;
    private Editor editor = null;
    private Context context = null;
    private String DOWNLOAD = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "com.chd.yunpan";
    private String PHOTO;
    private String MUSIC;
    private String NOTEPAD;
    private String CONTACT;
    private String SMS;
    private String OTHER;
    private String APK;
    private String DFlOW;
    private String NORMAL;
    private String RECORD;
    private String VIDEO;

    public ShareUtils(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sp.edit();
        initFilePath();
    }

    private void initFilePath() {
        String s = getUserid() == 0 ? "" : getUserid()+"";
        DOWNLOAD=DOWNLOAD+"/"+s;
        PHOTO = DOWNLOAD + "/" + "pic";
        MUSIC = DOWNLOAD + "/" + "music";
        NOTEPAD = DOWNLOAD + "/" + "notepad";
        CONTACT = DOWNLOAD + "/" + "contact";
        SMS = DOWNLOAD + "/" + "sms";
        OTHER = DOWNLOAD + "/" + "other";
        APK = DOWNLOAD + "/" + "apk";
        DFlOW = DOWNLOAD + "/" + "dflow";
        NORMAL = DOWNLOAD + "/" + "normal";
        RECORD = DOWNLOAD + "/" + "record";
        VIDEO = DOWNLOAD + "/" + "video";
    }


    public void setuseSDcard(boolean use) {
        editor.putBoolean("usesdcard", use);
    }

    public boolean getuseSDcard() {
        return sp.getBoolean("usesdcard", false);
    }

    public boolean sdCardExist() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

	/*public File getIntpath(Context context)
    {

		File dir=context.getFilesDir();
		return dir;
	}
*/

    public String getStorePathStr() {
        String dir = null;
        if (getuseSDcard() && sdCardExist()) {
            dir = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + context.getPackageName();
        } else {
            dir = context.getFilesDir().getAbsolutePath();
        }
        return dir;
    }

    public File getNotepadDir() {
        File f = new File(NOTEPAD);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }


    public File getStorePath() {
        File dir = null;
        if (getuseSDcard() && sdCardExist()) {
            dir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + File.pathSeparator + context.getPackageName());
            //dir=context.getExternalFilesDir(null);
        } else {
            dir = context.getFilesDir();
        }
        return dir;
    }


    public OutputStream getStoreOs(String fname) {
        OutputStream os = null;
        String dir = null, file;
        if (getuseSDcard() && sdCardExist()) {
            dir = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.pathSeparator + context.getPackageName();
            //dir=context.getExternalFilesDir(null);
        } else {
            dir = context.getFilesDir().getAbsolutePath();
        }
        file = dir + File.pathSeparator + fname;

        try {
            os = new FileOutputStream(new File(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return os;
    }

    public void addDownPath(String path) {
        Set<String> set = sp.getStringSet("downpath", new HashSet<String>());
        set.add(path);
        editor.putStringSet("path", set);
        editor.commit();
    }

    public Set<String> getDownPath() {
        return sp.getStringSet("downpath", new HashSet<String>());
    }

    /**
     * 图片下载地址
     *
     * @return
     */
    public File getPhotoFile() {
        File f = new File(PHOTO);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    /**
     * 音乐下载地址
     *
     * @return
     */
    public File getMusicFile() {
        File f = new File(MUSIC);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    /**
     * 视频下载地址
     *
     * @return
     */
    public File getVideoFile() {
        File f = new File(VIDEO);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    /**
     * 录音下载地址
     *
     * @return
     */
    public File getRecordFile() {
        File f = new File(RECORD);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    /**
     * DFLOW下载地址
     *
     * @return
     */
    public File getDFLOWFile() {
        File f = new File(DFlOW);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    /**
     * 普通下载地址
     *
     * @return
     */
    public File getNormalFile() {
        File f = new File(NORMAL);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    /**
     * 短信下载地址
     *
     * @return
     */
    public File getSmsFile() {
        File f = new File(SMS);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    /**
     * 联系人下载地址
     *
     * @return
     */
    public File getContactFile() {
        File f = new File(CONTACT);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }


    /**
     * 其他下载地址
     *
     * @return
     */
    public File getOtherFile() {
        File f = new File(OTHER);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    /**
     * APP下载地址
     *
     * @return
     */
    public File getApkFile() {
        File f = new File(APK);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }


    public void removeDownPath(String path) {
        Set<String> set = sp.getStringSet("downpath", new HashSet<String>());
        set.remove(path);
        editor.putStringSet("path", set);
        editor.commit();
    }

    public void setAutoPhotoBack(boolean flag) {
        editor.putBoolean("autophotoback", flag);
        editor.commit();
    }

    public void addPath(String path) {
        Set<String> set = sp.getStringSet("path", new HashSet<String>());
        set.add(path);
        editor.putStringSet("path", set);
        editor.commit();
    }

    public void clearPath() {
        editor.putStringSet("path", new HashSet<String>());
        editor.commit();
    }

    public boolean isExistPath(String path) {
        Set<String> set = sp.getStringSet("path", new HashSet<String>());
        return set.contains(path);
    }

    public boolean getAutoPhotoBack() {
        return sp.getBoolean("autophotoback", false);
    }

    public void setURL(String url) {
        editor.putString("url", url);
        editor.commit();
    }

    public String getURL() {
        return sp.getString("url", "");
    }

    public void setLoginEntity(LoginResult entity) {
        if (entity != null) {
            String json = null;
            json = new Gson().toJson(entity);
            editor.putString("entity", json);
            editor.commit();
        }
        //LoginEntity dd=getLoginEntity();
    }

    public LoginResult getLoginEntity() {
        String e = sp.getString("entity", "");
        Gson g = new Gson();
        return g.fromJson(e, LoginResult.class);
    }

    public int getUserid() {
        LoginResult lg = getLoginEntity();
        if (lg == null)
            return 0;
        else
            return lg.getUserid();
    }

    public void setAutoLogin(boolean isAuto) {
        editor.putBoolean("autoLogin", isAuto);
        editor.commit();
    }

    public boolean isAutoLogin() {
        return sp.getBoolean("autoLogin", false);
    }

    public CookieUtil getCookieUtil() {
//		LoginEntity entity =  getLoginEntity();
//		CookieUtil cookieUtil = new CookieUtil(Integer.parseInt(entity.getId()), entity.getToken());
//		return cookieUtil;
        //throw new Exception("");
        return null;
    }

    public void setUsername(String name) {
        editor.putString("username", name);
        editor.commit();
    }

    public void setPwd(String pwd) {
        editor.putString("pwd", pwd);
        editor.commit();
    }

    public String getUsername() {
        return sp.getString("username", "");
    }

    public String getPwd() {
        return sp.getString("pwd", "");
    }

    public String getInstalledUrl() {
        return sp.getString("apk_path", "");
    }

    public void setInstalledUrl(String path) {
        editor.putString("apk_path",path);
        editor.commit();
    }


}
