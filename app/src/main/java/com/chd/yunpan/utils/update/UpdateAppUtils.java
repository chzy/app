package com.chd.yunpan.utils.update;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.chd.yunpan.R;
import com.chd.yunpan.utils.ToastUtils;

public class UpdateAppUtils {

    private static final int PERMISSION_WRITE = 1001;
    private static VersionModel versionModel;
    private static Activity mAct;

    public static void launch(Activity act, VersionModel model) {
        mAct = act;
        versionModel = model;
        checkVersion();
    }


    private static void checkVersion() {
        if (versionModel != null) {
            downloadApk();
            //对比版本号，更新，versionCode自行网络获取
//            if (VersionUtils.getVersionCode(mAct) < versionModel.versionCode) {
//                checkPermission();
//            } else {
//                ToastUtils.toast(mAct,"已经是最新版本");
//            }
        }
    }


    private static void checkPermission() {
        downloadApk();
    }

    //下载更新包
    private static void downloadApk() {
        //判断是否强制更新
        if (versionModel.forced == 1) {
            showForcedDialog();
        } else {
            showDownloadDialog();
        }
    }

    private static void showDownloadDialog() {
        new AlertDialog.Builder(mAct).setTitle(mAct.getResources().getString(R.string.update_check)).setMessage(versionModel.desc).setPositiveButton(mAct.getResources().getString(R.string.update_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkWifi();
            }
        }).setNegativeButton(mAct.getResources().getString(R.string.update_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAct.finish();
            }
        }).setCancelable(false).show();
    }

    private static void showForcedDialog() {
        new AlertDialog.Builder(mAct).setTitle(mAct.getResources().getString(R.string.update_check)).setMessage(versionModel.desc).setPositiveButton(mAct.getResources().getString(R.string.update_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkWifi();

            }
        }).setNegativeButton(mAct.getResources().getString(R.string.update_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).setCancelable(false).show();
    }

    //启动下载
    private static void startDownload() {
        String url = versionModel.url;
        if (TextUtils.isEmpty(url)) {
            ToastUtils.toast(mAct,"无效的下载地址");
            mAct.finish();
        } else {
            UpdateUtils.startDownload(url, mAct);
        }
    }

    private static void checkWifi() {
        boolean isWifi = NetUtil.getNetworkState(mAct) == 1 ? true : false;
        if (isWifi) {
            startDownload();
        } else {
            showWifiDialog();
        }
    }

    private static void showWifiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mAct);
        builder.setTitle("提示");
        builder.setMessage("是否要用流量进行下载更新");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                mAct.finish();
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startDownload();
            }
        });
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        //设置不可取消对话框
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }


}
