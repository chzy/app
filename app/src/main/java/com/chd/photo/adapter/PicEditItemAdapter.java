package com.chd.photo.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.chd.photo.entity.PicEditItemBean;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;


public class PicEditItemAdapter extends BaseAdapter {

    protected ImageLoader imageLoader;
    DisplayImageOptions options;
    private Activity context;
    private List<PicEditItemBean> list;
	private LayoutInflater mInflater;

    public PicEditItemAdapter(Activity context, List<PicEditItemBean> list) {
        this.context = context;
        this.list = list;
	    this.mInflater=LayoutInflater.from(context);
	    this.imageLoader=ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.pic_test1)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .extraForDownloader(new ShareUtils(context).getStorePathStr()) // imageload 加载图片时 会在程序目录下载对应的原文件
                .build();
    }

	@Override
	public int getCount() {
		return list==null?0:list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View converView, ViewGroup parent) {
		final ViewHolder holder;
		if (converView == null) {
			converView = mInflater.inflate(R.layout.item_pic_edit_item_adapter, parent,false);
			holder = new ViewHolder();
			holder.iv_pic_info_photo = (ImageView) converView.findViewById(R.id.iv_pic_edit_item_photo);
			holder.iv_pic_edit_check = (ImageView) converView.findViewById(R.id.iv_pic_edit_item_photo_check);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}

		holder.iv_pic_edit_check.setVisibility(list.get(position).isEdit() ? View.VISIBLE : View.GONE);
		holder.iv_pic_edit_check.setImageResource(list.get(position).isSelect() ? R.drawable.pic_edit_photo_checked : R.drawable.pic_edit_photo_check);
		

		final String url = list.get(position).getUrl();
		{
			imageLoader.displayImage(url, holder.iv_pic_info_photo,
					options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
						}

						@Override
						public void onLoadingComplete(String imageUri, View view,
								Bitmap loadedImage) {
						}
					}, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String imageUri, View view,
								int current, int total) {
						}
					});
		}
		
		return converView;
	}

	private class ViewHolder {
		ImageView iv_pic_info_photo;
		ImageView iv_pic_edit_check;
	}

}
