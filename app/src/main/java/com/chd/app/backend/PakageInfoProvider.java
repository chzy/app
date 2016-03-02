package com.chd.app.backend;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.chd.TClient;
import com.chd.proto.AppInfo;

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
    public List<AppInfo0> compareApps() {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> pakageinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        unDownApps = new ArrayList<AppInfo0>();
        for (PackageInfo packageInfo : pakageinfos) {
            appInfo = new AppInfo0();
            appInfo.setIndex(localApps.size());
            if ( (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) !=0)
                continue;
            //获取字符串方法
            //context.getString(R.string.app_name);
            // context.getResources().getString(R.string.app_name);
            //获取尺寸资源方法
            //context.getResources().getDimension(R.dimen.test);
            //获取xml文件并且返回的是XmlResourceParse类，其继承与XmlPullParse
           // XmlResourceParser xmlrp = context.getResources().getXml(R.xml.yo);
            // 获取应用程序的名称，不是包名，而是清单文件中的labelname
            //appInfo.setPackageName(packageInfo.packageName);
            String str_name = packageInfo.applicationInfo.loadLabel(pm).toString();
            appInfo.setAppName(str_name);
            // 获取应用程序的版本号码
            String version = packageInfo.versionName;
            appInfo.setAppVersion(version);
            //给一同程序设置包名
            appInfo.setPackageName(packageInfo.packageName);

            // 获取应用程序的快捷方式图标
            Drawable drawable = packageInfo.applicationInfo.loadIcon(pm);
            appInfo.setDrawable(drawable);
            // 获取应用程序是否是第三方应用程序
            if (filterApp(appInfo)){
                downApps.add(appInfo);
            }
            //else
             //   appInfo.setIsUserApp(filterApp(packageInfo.applicationInfo));

            //Logger.i(tag, "版本号:" + version + "程序名称:" + str_name);

        }
        unDownApps.addAll(remoteApps);
        unDownApps.removeAll(downApps);
        return downApps;
    }


    public List<AppInfo0> getAppInfo() {
        compareApps();
        return localApps;
    }

    public List<AppInfo0> getRemoteApps(){
        return remoteApps;
    }

    /**
     * 三方应用程序的过滤器
     *
     * @param info
     * @return true 三方应用 false 系统应用
     */
    public boolean filterApp(AppInfo0 info) {
        if (remoteApps==null){
                return false;
        }

        for (AppInfo0 item:remoteApps)
        {
            if (item.getAppName().equalsIgnoreCase(info.getAppName() ) || item.getPackageName().equalsIgnoreCase(info.getPackageName()) )
            {
                item.setInstalled(true);
                int len=Math.min(info.getAppVersion().length(), item.getAppVersion().length());
                String remote=item.getAppVersion();
                String local=info.getAppName();
                for (int idx=0;idx<len;idx++)
                {

                    if (remote.charAt(idx)>local.charAt(idx)) {
                        item.setNeedUp(true);
                        break;
                    }
                }
                break;
            }
        }
        return remoteApps.contains(info);
      /*  if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            // 代表的是系统的应用,但是被用户升级了. 用户应用
            return true;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // 代表的用户的应用
            return true;
        }
        return false;*/

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
           return false;
        }
    }
}