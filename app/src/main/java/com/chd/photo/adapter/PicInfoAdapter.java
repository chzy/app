package com.chd.photo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.photo.entity.PicBean;
import com.chd.photo.entity.PicInfoBeanMonth;
import com.chd.yunpan.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;


public class PicInfoAdapter extends BaseAdapter {

	private Context context;
	private List<PicInfoBeanMonth> list;
	protected ImageLoader imageLoader;
	DisplayImageOptions options;
	private  int _month;

	public PicInfoAdapter(Context context,  PicBean<PicInfoBeanMonth> picbean,ImageLoader imageLoader) {

		/*imageLoader.clearDiskCache();
		imageLoader.clearMemoryCache();*/
		_month=picbean.getMonth();
		this.context = context;
		this.list =new ArrayList<PicInfoBeanMonth>();
		this.imageLoader=imageLoader;
		list.add( picbean.getList());
		options = new DisplayImageOptions.Builder()
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.cacheInMemory(false)
				.showImageOnLoading(R.drawable.pic_test1).showImageOnFail(R.drawable.pic_test1)
				.displayer(new RoundedBitmapDisplayer(20))
				.cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.considerExifParams(true)
//		.extraForDownloader(new ShareUtils(context).getStorePathStr())  //增加保存路径
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
	public View getView(int position, View converView, ViewGroup parent) {
		ViewHolder holder;
		if (converView == null) {
			converView = View.inflate(context, R.layout.item_pic_info_adapter,
					null);
			holder = new ViewHolder();
			holder.iv_pic_info_photo = (ImageView) converView
					.findViewById(R.id.iv_pic_info_photo);
			holder.tv_pic_info_month = (TextView) converView
					.findViewById(R.id.tv_pic_info_month);
			holder.tv_pic_info_number = (TextView) converView
					.findViewById(R.id.tv_pic_info_number);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}

		/*holder.iv_pic_info_photo.setImageResource(list.get(position).getPicUrl());*/
		holder.tv_pic_info_month.setText(/*list.get(position).getMonth()*/_month + "月");
		holder.tv_pic_info_number.setText("(" + /*list.get(position).getMonth()*/list.get(position).getPicunits().size()
				+ ")");
		String url=list.get(position).getUrl();

		imageLoader.displayImage(url, holder.iv_pic_info_photo,
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
		
		return converView;
	}

	private class ViewHolder {
		ImageView iv_pic_info_photo;
		TextView tv_pic_info_month;
		TextView tv_pic_info_number;
	}

}
