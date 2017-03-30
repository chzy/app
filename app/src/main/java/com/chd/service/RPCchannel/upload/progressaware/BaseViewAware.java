package com.chd.service.RPCchannel.upload.progressaware;

import android.os.Looper;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

//import com.chd.service.RPCchannel.upload.progressaware.ProgressAware;

/**
 * Created by hjy on 7/9/15.<br>
 */
public abstract class BaseViewAware implements ProgressAware {

    protected Reference< MaterialDialog> mViewRef;

    public BaseViewAware( MaterialDialog view) {
        mViewRef = new WeakReference< MaterialDialog>(view);
    }

    @Override
    public int getId() {
        MaterialDialog view = mViewRef.get();
        if(view == null)
            return super.hashCode();
        else
            return view.hashCode();
    }

    @Override
    public boolean isCollected() {
        return mViewRef.get() == null;
    }

    @Override
    public boolean setProgress(int progress) {
        if(Looper.myLooper() == Looper.getMainLooper()) {
            MaterialDialog view = mViewRef.get();
            if(view != null) {
                setProgress(progress, view);
                return true;
            }
        }
        return false;
    }

    @Override
    public  MaterialDialog getWrappedView() {
        return mViewRef.get();
    }

    @Override
    public void setVisibility(int visibility) {
        MaterialDialog view = mViewRef.get();
        if(view != null) {
            if(visibility==View.VISIBLE){
                view.show();
            }else{
                view.dismiss();
            }
        }
    }

    public abstract void setProgress(int progress, MaterialDialog view);

}
