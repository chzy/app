package com.chd.photo.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chd.base.Entity.FileLocal;
import com.chd.proto.FileInfo;
import com.chd.yunpan.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;


public class PicInfoAdapter<T extends FileInfo> extends BaseQuickAdapter<T,BaseViewHolder> {

	private Context context;
	protected ImageLoader imageLoader;
	DisplayImageOptions options;

	public PicInfoAdapter(List<T> data, ImageLoader imageLoader) {
		super(R.layout.item_pic_info_adapter,data);
		this.imageLoader=imageLoader;
	}



	@Override
	protected void convert(BaseViewHolder helper, T item) {
		String url="";
		if(item instanceof FileInfo){
			FileInfo info=item;
			url="trpc://"+info.getObjid();
		}else if(item instanceof FileLocal){
			FileLocal info= (FileLocal) item;
			url="file://"+info.getPathid();
		}
		imageLoader.displayImage(url, (ImageView) helper.getView(R.id.iv_pic_info_photo),options,new SimpleImageLoadingListener());

			/*holder.iv_pic_info_photo.setImageResource(list.get(position).getPicUrl());*/
//		holder.tv_pic_info_month.setText(/*list.get(position).getMonth()*/_month + "月");
//		holder.tv_pic_info_number.setText("(" + /*list.get(position).getMonth()*/list.get(position).getPicunits().size()
//				+ ")");
//		String url=list.get(position).getUrl();
//
//		imageLoader.displayImage(url, holder.iv_pic_info_photo,
//				options, new SimpleImageLoadingListener() {
//					@Override
//					public void onLoadingStarted(String imageUri, View view) {
//						/*vh.progressBar.setProgress(0);
//						vh.progressBar.setVisibility(View.VISIBLE);*/
//					}
//
//					@Override
//					public void onLoadingFailed(String imageUri, View view,
//					                            FailReason failReason) {
//						/*vh.progressBar.setVisibility(View.GONE);*/
//					}
//
//					@Override
//					public void onLoadingComplete(String imageUri, View view,
//					                              Bitmap loadedImage) {
//						/*vh.progressBar.setVisibility(View.GONE);*/
//					}
//				}, new ImageLoadingProgressListener() {
//					@Override
//					public void onProgressUpdate(String imageUri, View view,
//					                             int current, int total) {
//						/*vh.progressBar.setProgress(Math.round(100.0f * current
//								/ total));*/
//					}
//				});

	}



}
