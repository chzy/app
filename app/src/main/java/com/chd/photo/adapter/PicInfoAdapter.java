package com.chd.photo.adapter;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chd.base.Entity.FileLocal;
import com.chd.proto.FileInfo;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;


public class PicInfoAdapter<T extends FileInfo> extends BaseQuickAdapter<T, BaseViewHolder> {

	protected ImageLoader imageLoader;
	protected DisplayImageOptions options;
	protected boolean showSelect;

	public PicInfoAdapter(List<T> data, ImageLoader imageLoader, boolean showSelect) {
		super(R.layout.item_pic_info_adapter, data);
		this.imageLoader = imageLoader;
		this.showSelect = showSelect;
		options = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisk(true)
				.considerExifParams(true)
				.showImageOnFail(R.drawable.pic_test1).showImageOnLoading(R.drawable.pic_test1)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.resetViewBeforeLoading(false)
//				.extraForDownloader(new ShareUtils(context).getStorePathStr())
				.displayer(new RoundedBitmapDisplayer(20))
				.displayer(new FadeInBitmapDisplayer(0)).build();
	}


	@Override
	protected void convert(BaseViewHolder helper, T item) {
		if (showSelect) {
			helper.setVisible(R.id.iv_pic_edit_item_photo_check, true);
			Log.d("liumj",isCheck+"");
			if(isCheck){
				//是选中的,需要取消
				helper.setImageResource(R.id.iv_pic_edit_item_photo_check,R.drawable.pic_edit_photo_check);
			}else{
				//是未选中的,需要选中
				helper.setImageResource(R.id.iv_pic_edit_item_photo_check,R.drawable.pic_edit_photo_checked);
			}

		} else {
			helper.setVisible(R.id.iv_pic_edit_item_photo_check, false);
		}


		String url = "";
		url = "ttrpc://" + item.getObjid();
		if (item instanceof FileLocal) {

			url = "file://"+UILApplication.getFilelistEntity().getFilePath(((FileLocal) item).getPathid())+"/"+item.getObjid();
			Log.d("liumj",url);
		}

		imageLoader.displayImage(url, (ImageView) helper.getView(R.id.iv_pic_info_photo), options, new SimpleImageLoadingListener());

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

	private boolean isCheck = true;

	void changeItem(int position, boolean contains) {
		isCheck=contains;
		notifyItemChanged(position);
	}

	private int groupPos;
	public void setPosition(int pos) {
		groupPos=pos;
	}

	public int getGroupPos() {
		return groupPos;
	}

	public void setGroupPos(int groupPos) {
		this.groupPos = groupPos;
	}
}
