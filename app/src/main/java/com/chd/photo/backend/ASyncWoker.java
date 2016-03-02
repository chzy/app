package com.chd.photo.backend;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.widget.ProgressBar;

import com.chd.proto.FileInfo0;

import java.util.ArrayList;

public class ASyncWoker extends android.os.AsyncTask<Context, ProgressBar, Object> {
    private Context mContext;
    private final ContentResolver mContentResolver;
    private boolean mExitTasksEarly = false;//退出任务线程的标志位

    private ArrayList<Uri> uriArray = new ArrayList<Uri>();//存放图片URI
    private ArrayList<Long> origIdArray = new ArrayList<Long>();//存放图片ID
    private ArrayList<FileInfo0> fileInfoArray=new ArrayList<FileInfo0>();

    public ASyncWoker(Context mContext) {
        this.mContext = mContext;
        mContentResolver = mContext.getContentResolver();

    }


    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Object doInBackground(Context... params) {
        //publishProgress((int) ((count / (float) total) * 100));
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if ( !mExitTasksEarly) {
            /**
             * 查询完成之后，设置回调接口中的数据，把数据传递到Activity中
             */
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();    //To change body of overridden methods use File | Settings | File Templates.
        mExitTasksEarly = true;
    }

    public void setExitTasksEarly(boolean exitTasksEarly) {
        this.mExitTasksEarly = exitTasksEarly;
    }


}