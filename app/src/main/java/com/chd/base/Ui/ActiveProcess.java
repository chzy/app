package com.chd.base.Ui;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.chd.photo.ui.CustomProgressDialog;

/**
 * Created by lxp1 on 2015/12/6.
 */
public abstract class ActiveProcess extends Activity {


    //public abstract void updateProgress(final int progress);
    protected  CustomProgressDialog dialog;

    @Override
    protected void onResume() {
        super.onResume();
        if(dialog==null){
        dialog = CustomProgressDialog.createDialog(this);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        }
    }

    private  int progress;
    private int max;
    private String msg;
    public synchronized void updateProgress( int progress)
    {
        this.progress=progress;
        runOnUiThread(updata);
    }

    public synchronized void updateProgress( int progress,int max)
    {
        this.progress=progress;
        this.max=max;
        runOnUiThread(updata2);
    }

    public void toastMain(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ActiveProcess.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Runnable updata2=new Runnable(){

        @Override
        public void run() {
            Log.d("lmj", msg+":" + progress);
            if (progress < max && dialog.isShowing()) {
                dialog.setMessage( msg+ progress+"/"+max);
            } else if (progress > 0 && !dialog.isShowing()) {
                dialog.show();
            } else {
                dialog.dismiss();
            }
        }
    };




    public Runnable updata=new Runnable(){

        @Override
        public void run() {
            Log.d("lmj", msg+":" + progress);
            if (progress < 100 && dialog.isShowing()) {
                dialog.setMessage( msg+ progress + "%");
            } else if (progress > 0 && !dialog.isShowing()) {
                dialog.show();
            } else {
                dialog.dismiss();
            }
        }
    };

    public  void setMaxProgress(int max)
    {
        this.max=max;
    }

    public synchronized  void finishProgress(){
        if(dialog!=null){
        dialog.dismiss();
        }
    }


    //设置progressbar上的提示信息
    public void setParMessage(String message)
    {
        msg=message;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog=null;
    }
}
