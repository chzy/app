package com.chd.app.backend;

import android.graphics.drawable.Drawable;

import com.chd.proto.AppInfo;


/**
 * Created by lxp1 on 2015/11/25.
 */
public class AppInfo0 extends AppInfo {

	private int index;
	
   /* private String AppName;
    private String AppVersion;
    private String PackageName;*/

    private Drawable drawable; //本地app的 ico
    private boolean Installed=false;
    private boolean NeedUp=false;

    //private String Dlurl; //下载链接

    //private String Icourl;// 用imageload 载入 app的图标

    public AppInfo0(AppInfo appInfo)
    {
        super();
        super.setPackageName(appInfo.getPackageName());
        super.setAppName(appInfo.getAppName());
        super.setAppVersion(appInfo.getAppVersion());
        super.setUrl(appInfo.getUrl());
        super.setIco_url(appInfo.getIco_url());
    }

    public AppInfo0()
    {
       super();
    }
    public String getAppName() {
        return AppName;
    }

   /* public void setAppName(String appName) {
        AppName = appName;
    }*/

  /*  public String getAppVersion() {
        return AppVersion;
    }*/

   /* public void setAppVersion(String appVersion) {
        AppVersion = appVersion;
    }*/

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}


    public boolean isInstalled() {
        return Installed;
    }

    public void setInstalled(boolean installed) {
        Installed = installed;
    }

    public boolean isNeedUp() {
        return NeedUp;
    }

    public void setNeedUp(boolean needUp) {
        NeedUp = needUp;
    }
}
