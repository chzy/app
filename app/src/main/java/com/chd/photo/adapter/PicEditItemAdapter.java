package com.chd.photo.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
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
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;


public class PicEditItemAdapter extends BaseAdapter {

	private Activity context;
	private List<PicEditItemBean> list;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	public PicEditItemAdapter(Activity context, List<PicEditItemBean> list) {
		this.context = context;
		this.list = list;
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.pic_test1)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.extraForDownloader(new ShareUtils(context).getStorePathStr()) // imageload 加载图片时 会在程序目录下载对应的原文件
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View converView, ViewGroup parent) {
		final ViewHolder holder;
		if (converView == null) {
			converView = View.inflate(context, R.layout.item_pic_edit_item_adapter, null);
			holder = new ViewHolder();
			holder.iv_pic_info_photo = (ImageView) converView.findViewById(R.id.iv_pic_edit_item_photo);
			holder.iv_pic_edit_check = (ImageView) converView.findViewById(R.id.iv_pic_edit_item_photo_check);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}

		holder.iv_pic_info_photo.setImageResource(list.get(position).getPicUrl());
		holder.iv_pic_edit_check.setVisibility(list.get(position).isEdit() ? View.VISIBLE : View.GONE);
		holder.iv_pic_edit_check.setImageResource(list.get(position).isSelect() ? R.drawable.pic_edit_photo_checked : R.drawable.pic_edit_photo_check);
		

		final String url = list.get(position).getPicpath();
		/*if (  ThumUtil.isStartWithTrpc(url))
		{
			Thread thread = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					final Bitmap bitmap = imageLoader.loadImageSync(url);
					if (bitmap == null)
					{
						return;
					}
					context.runOnUiThread(new Runnable() 
					{
						@Override
						public void run() 
						{
							holder.iv_pic_info_photo.setImageBitmap(bitmap);
						}
					});
				}
			});
			thread.start();
		}
		else*/
		{
			imageLoader.displayImage(list.get(position).getPicpath(), holder.iv_pic_info_photo,
					options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							/*vh.progressBar.setProgress(0);
							vh.progressBar.setVisibility(View.VISIBLE);*/
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							/*vh.progressBar.setVisibility(View.GONE);*/
						}

						@Override
						public void onLoadingComplete(String imageUri, View view,
								Bitmap loadedImage) {
							/*vh.progressBar.setVisibility(View.GONE);*/
						}
					}, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String imageUri, View view,
								int current, int total) {
							/*vh.progressBar.setProgress(Math.round(100.0f * current
									/ total));*/
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
