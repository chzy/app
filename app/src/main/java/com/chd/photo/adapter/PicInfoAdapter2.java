package com.chd.photo.adapter;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.PicFile;
import com.chd.proto.FileInfo;
import com.chd.yunpan.GlideApp;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.utils.DensityUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PicInfoAdapter2<T extends FileInfo> extends BaseSectionQuickAdapter<PicFile<T>, BaseViewHolder> {

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private boolean isVideo;
    private boolean isShowEdit;


    public PicInfoAdapter2(ArrayList<PicFile<T>> data, boolean isVideo) {
        super(R.layout.item_pic_info_adapter,R.layout.item_head_time, data);
        this.imageLoader = ImageLoader.getInstance();
        this.isVideo = isVideo;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true)
                .considerExifParams(true)
                .showImageOnFail(R.drawable.pic_test1).showImageOnLoading(R.drawable.pic_test1)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(false)
                .displayer(new FadeInBitmapDisplayer(0)).build();
    }

    public void setShowEdit(boolean showEdit) {
        isShowEdit = showEdit;
    }

    @Override
    protected void convert(BaseViewHolder helper, PicFile item) {
        helper.addOnClickListener(R.id.iv_pic_edit_item_photo_check);
        if (isShowEdit) {
            helper.setVisible(R.id.iv_pic_edit_item_photo_check, true);
            if (item.isSelect) {
                //是选中的,需要取消
                helper.setImageResource(R.id.iv_pic_edit_item_photo_check, R.drawable.pic_edit_photo_checked);
            } else {
                //是未选中的,需要选中
                helper.setImageResource(R.id.iv_pic_edit_item_photo_check, R.drawable.pic_edit_photo_check);
            }
        } else {
            helper.setVisible(R.id.iv_pic_edit_item_photo_check, false);
        }
        Object itemLocal = item.t;

        String url = "";
        FileLocal fileLocal=null;
        FileInfo fileInfo=null;
        String objId="";
        if(itemLocal instanceof FileLocal){
            fileLocal= (FileLocal) itemLocal;
            objId=fileLocal.getObjid();
        }else{
            fileInfo= (FileInfo) itemLocal;
            objId=fileInfo.getObjid();
        }
        if (!isVideo) {
            url = "ttrpc://" + objId;
            if (fileLocal!=null) {
                url = "file://" + UILApplication.getFilelistEntity().getFilePath(fileLocal.getPathid()) + "/" + objId;
                int i = DensityUtil.dip2px(mContext, 90);
                GlideApp.with(mContext).asDrawable().load(url).thumbnail(0.3f).centerCrop().placeholder(R.drawable.pic_test1).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).override(i,i).into((ImageView) helper.getView(R.id.iv_pic_info_photo));
            } else {
                imageLoader.displayImage(url, (ImageView) helper.getView(R.id.iv_pic_info_photo), options, new SimpleImageLoadingListener());
            }
        } else {
            url = "ttrpc://yunpan_thumb_" + objId;
            url = url.replace("mp4", "jpg");
            imageLoader.displayImage(url, (ImageView) helper.getView(R.id.iv_pic_info_photo), options, new SimpleImageLoadingListener());
        }
    }

    @Override
    protected void convertHead(BaseViewHolder helper, PicFile item) {
        helper.setText(R.id.tv_pic_date,item.header);
    }
}
