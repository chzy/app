package com.chd.photo.adapter;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chd.base.Entity.FileLocal;
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

import java.util.List;


class PicInfoAdapter<T extends FileInfo> extends BaseQuickAdapter<T, BaseViewHolder> {

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private boolean showSelect;
    private boolean isVideo;


    PicInfoAdapter(List<T> data, ImageLoader imageLoader, boolean showSelect, boolean isVideo, boolean isCheck) {
        super(R.layout.item_pic_info_adapter, data);
        this.imageLoader = imageLoader;
        this.showSelect = showSelect;
        this.isVideo = isVideo;
        this.isCheck = isCheck;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true)
                .considerExifParams(true)
                .showImageOnFail(R.drawable.pic_test1).showImageOnLoading(R.drawable.pic_test1)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(false)
                .displayer(new FadeInBitmapDisplayer(0)).build();
    }


    @Override
    protected void convert(BaseViewHolder helper, T item) {
        helper.addOnClickListener(R.id.iv_pic_edit_item_photo_check);
        if (showSelect) {
            helper.setVisible(R.id.iv_pic_edit_item_photo_check, true);
            if (isCheck) {
                //是选中的,需要取消
                helper.setImageResource(R.id.iv_pic_edit_item_photo_check, R.drawable.pic_edit_photo_check);
            } else {
                //是未选中的,需要选中
                helper.setImageResource(R.id.iv_pic_edit_item_photo_check, R.drawable.pic_edit_photo_checked);
            }
        } else {
            helper.setVisible(R.id.iv_pic_edit_item_photo_check, false);
        }


        String url = "";
        if (!isVideo) {
            url = "ttrpc://" + item.getObjid();
            if (item instanceof FileLocal) {
                url = "file://" + UILApplication.getFilelistEntity().getFilePath(((FileLocal) item).getPathid()) + "/" + item.getObjid();
                int i = DensityUtil.dip2px(mContext, 90);

//                Glide.with(mContext).load(url).thumbnail(0.3f).into((ImageView) helper.getView(R.id.iv_pic_info_photo));
                GlideApp.with(mContext).asDrawable().load(url).thumbnail(0.3f).centerCrop().placeholder(R.drawable.pic_test1).dontAnimate().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).override(i,i).into((ImageView) helper.getView(R.id.iv_pic_info_photo));
            } else {
                imageLoader.displayImage(url, (ImageView) helper.getView(R.id.iv_pic_info_photo), options, new SimpleImageLoadingListener());
            }
        } else {
            url = "ttrpc://yunpan_thumb_" + item.getObjid();
            url = url.replace("mp4", "jpg");
            imageLoader.displayImage(url, (ImageView) helper.getView(R.id.iv_pic_info_photo), options, new SimpleImageLoadingListener());
        }
    }

    private boolean isCheck = true;

    void changeItem(int position, boolean contains) {
        isCheck = contains;
        notifyItemChanged(position);
    }

    private int groupPos;

    public void setPosition(int pos) {
        groupPos = pos;
    }

    public int getGroupPos() {
        return groupPos;
    }

    public void setGroupPos(int groupPos) {
        this.groupPos = groupPos;
    }
}
