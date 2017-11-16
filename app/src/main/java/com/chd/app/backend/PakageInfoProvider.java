package com.chd.app.backend;

import android.content.Context;

import com.chd.TClient;
import com.chd.proto.AppInfo;
import com.chd.yunpan.R;
import com.chd.yunpan.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxp1 on 2015/11/25.
 */
public class PakageInfoProvider {

    private static final String tag = "GetappinfoActivity";
    private Context context;
    private List<AppInfo0> localApps;
    private List<AppInfo0> downApps;
    private List<AppInfo0> unDownApps;
    private AppInfo0 appInfo;
    public PakageInfoProvider(Context context) {
        super();
        this.context = context;
        localApps=new ArrayList();
        downApps=new ArrayList();
        unDownApps=new ArrayList();

    }
    private void compareApps() {
        for (int i = 0; i < remoteApps.size(); i++) {
            AppInfo0 appInfo0 = remoteApps.get(i);
            String packageName = appInfo0.getPackageName();
            boolean appInstallen = AppUtils.isAppInstallen(context, packageName);
            if(appInstallen){
                downApps.add(appInfo0);
            }else{
                unDownApps.add(appInfo0);
            }
        }
        AppInfo0 appInfo0=new AppInfo0();
        appInfo0.setInstalled(true);
        appInfo0.setDrawable(context.getResources().getDrawable(R.drawable.ic_xinqitian));
        appInfo0.setAppName("心期天");
        appInfo0.setPackageName("cn.heartfree.xinqing");
        appInfo0.setUrl("http://221.7.13.207:8080/App/heartfree110.apk");
        downApps.add(0,appInfo0);
    }


    public List<AppInfo0> getAppInfo() {
        compareApps();
        return localApps;
    }

    public List<AppInfo0> getRemoteApps(){
        return remoteApps;
    }



    public List<AppInfo0> getLocalApps() {
        return localApps;
    }

    public void setLocalApps(List<AppInfo0> localApps) {
        this.localApps = localApps;
    }

    public List<AppInfo0> getDownApps() {
        return downApps;
    }

    public void setDownApps(List<AppInfo0> downApps) {
        this.downApps = downApps;
    }

    public List<AppInfo0> getUnDownApps() {

        return unDownApps;
    }

    public void setUnDownApps(List<AppInfo0> unDownApps) {
        this.unDownApps = unDownApps;
    }

    static List<AppInfo0> remoteApps;

    public boolean queryRemoteApps()
    {
        remoteApps=new ArrayList<AppInfo0>();

        try {
            List<AppInfo> list= TClient.getinstance().QueryApps();
            if (list!=null)
            {
                for(AppInfo appInfo:list)
                {
                    AppInfo0 item=new AppInfo0(appInfo);
                    remoteApps.add(item);
                }
                compareApps();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
           return false;
        }
    }
}