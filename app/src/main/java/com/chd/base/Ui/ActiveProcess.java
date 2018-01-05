package com.chd.base.Ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.chd.photo.ui.CustomProgressDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.autolayout.AutoLayoutActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

/**
 * Created by lxp1 on 2015/12/6.
 */
public abstract class ActiveProcess extends AutoLayoutActivity {


    //public abstract void updateProgress(final int progress);
    protected CustomProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (dialog == null) {
            WeakReference<Activity> mWeakAct=new WeakReference<Activity>(this);
            dialog = CustomProgressDialog.createDialog(mWeakAct.get());
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        }
        EventBus.getDefault().register(this);

    }

    protected void showDialog(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.setMessage(msg);
                    dialog.show();
                }
            }
        });
    }

    protected void dismissDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.setMessage("");
                    dialog.dismiss();
                }
            }
        });

    }


    private int progress = -1;
    private int max;
    private String msg;

    public synchronized void updateProgress(int progress) {
        this.progress = progress;
        runOnUiThread(updata);
    }

    public synchronized void updateProgress(int progress, int max) {
        this.progress = progress;
        this.max = max;
        runOnUiThread(updata2);
    }

    public void toastMain(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ActiveProcess.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Runnable updata2 = new Runnable() {

        @Override
        public void run() {
            if(dialog!=null){
            if (progress < max && dialog.isShowing()) {
                dialog.setMessage(msg + progress + "/" + max);
            } else if (progress > 0 && !dialog.isShowing()) {
                dialog.show();
            } else {
                dialog.dismiss();
            }
            }
        }
    };


    public Runnable updata = new Runnable() {

        @Override
        public void run() {
            if(dialog!=null){
            Log.d("lmj", msg + ":" + progress);
            if (progress < 100 && dialog.isShowing()) {
                dialog.setMessage(msg + progress + "%");
            } else if (progress >= 0 && !dialog.isShowing()) {
                dialog.show();
            } else if (progress == 0) {
            } else {
                dialog.dismiss();
            }
            }
        }
    };

    public void setMaxProgress(int max) {
        this.max = max;
    }

    public synchronized void finishProgress() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageLoader.getInstance().resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ImageLoader.getInstance().pause();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Object obj){



    }


    //设置progressbar上的提示信息
    public void setParMessage(String message) {
        msg = message;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        dialog = null;
    }
}
