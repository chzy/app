package com.chd.yunpan.ui.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.net.ExecRunable;
import com.chd.yunpan.ui.PhotoShowActivity;
import com.chd.yunpan.ui.fragment.popupwindow.PhotoPopMenuShow;
import com.chd.yunpan.utils.FileDownLoadLinkedUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/*import com.chd.yunpan.parse.entity.FileDataEntity;*/

public class PhotoViewPager_Adapter extends PagerAdapter  {

	DisplayImageOptions options;
	private PhotoPopMenuShow menuShow = null;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private PhotoShowActivity context;
	ImageView imageView = null;

	LayoutInflater inflater;
	
	
	

	public PhotoViewPager_Adapter(
			PhotoShowActivity context, PhotoPopMenuShow menuShow) {
		super();
		this.context = context;
		this.menuShow = menuShow;
		options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		inflater = context.getLayoutInflater();

	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);

	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View imageLayout = inflater.inflate(R.layout.photo_show_image,
				container, false);
		assert imageLayout != null;
		final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
		imageView.setVisibility(View.GONE);
		final ProgressBar spinner = (ProgressBar) imageLayout
				.findViewById(R.id.loading);
		FileInfo0 pe = (/*FileDataEntity*/FileInfo0) context.getEntity().getList().get(position);
	

		if(pe.getFilePath()/*picPath*/!=null){
			imageLoader.displayImage(
					pe.getFilePath()/*picPath+"&tsha1="+pe.getShal()*/, imageView,
					options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							String message = null;
							switch (failReason.getType()) {
							case IO_ERROR:
								message = "网络不可用";
								break;
							case DECODING_ERROR:
								message = "图片解析失败";
								break;
							case NETWORK_DENIED:
								message = "网络不可用";
								break;
							case OUT_OF_MEMORY:
								message = "亲，请滑动的慢点";
								break;
							case UNKNOWN:
								message = "图片加载失败";
								break;
							}
							Toast.makeText(context, message, Toast.LENGTH_SHORT)
									.show();

							spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							spinner.setVisibility(View.GONE);
							imageView.setVisibility(View.VISIBLE);
							 
						}
					});
		}
		/*else {
		imageLoader.displayImage(
					pe.getU()+"&tsha1="+pe.getShal()+"_100_100", imageView,
					options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							String message = null;
							switch (failReason.getType()) {
							case IO_ERROR:
								message = "网络不可用";
								break;
							case DECODING_ERROR:
								message = "图片解析失败";
								break;
							case NETWORK_DENIED:
								message = "网络不可用";
								break;
							case OUT_OF_MEMORY:
								message = "亲，请滑动的慢点";
								break;
							case UNKNOWN:
								message = "图片加载失败";
								break;
							}
							Toast.makeText(context, message, Toast.LENGTH_SHORT)
									.show();

							spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							spinner.setVisibility(View.GONE);
							imageView.setVisibility(View.VISIBLE);
							 
						}
					});
			downImage(pe, imageView, spinner);
		}*/
		menuShow.showPopupWindows(imageLayout);
		container.addView(imageLayout, 0);
		
		return imageLayout;
	}
	
	public class TEMP {
		/*public FileDownLoadLinkedEnitity enitity;*/
		public String url;
		public ImageView iv;
		public ProgressBar spinner;
		//public FileInfo0 dataEntity;
	}
	
/*	public void downImage(final FileDataEntity entity ,final ImageView iv,final ProgressBar spinner){
		ExecRunable.execRun(new Runnable() {
			public void run() {
				FileDownLoadLinkedEnitity linkedEnitity = FileDownLoadLinkedUtil.getFileLinked(context, entity.getFid());
				TEMP t = new TEMP();
				t.enitity = linkedEnitity;
				if(linkedEnitity!=null)
				entity.picPath = linkedEnitity.getUrl();
				t.iv = iv;
				t.spinner = spinner;
				Message msg = new Message();
				msg.obj  = t;
				if(linkedEnitity!=null)
				msg.what = 1;
				t.dataEntity = entity;
				handler.sendMessage(msg);
			}
		});
	}*/

	public void downImage(final FileInfo0 entity ,final ImageView iv,final ProgressBar spinner){
		ExecRunable.execRun(new Runnable() {
			public void run() {
				String url = FileDownLoadLinkedUtil.getFileLinked(context, entity.getFtype(),entity.getObjid());
				TEMP t = new TEMP();
				/*t.enitity = linkedEnitity;
				if(linkedEnitity!=null)
					entity.picPath = linkedEnitity.getUrl();
				*/
				t.iv = iv;
				t.spinner = spinner;
				t.url=url;
				Message msg = new Message();

				if(/*linkedEnitity*/url!=null) {
					msg.what = 1;
					msg.obj=t;
				}
				else {
					msg.what = -1;
					msg.obj=null;
				}
				/*t.dataEntity = entity;*/
				handler.sendMessage(msg);
			}
		});
	}

	public Handler  handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case -1:
				
				break;
			case 1:
				
				final TEMP t = (TEMP) msg.obj;
				//FileDownLoadLinkedEnitity pe = t.enitity;

				if(t.iv!=null)
				imageLoader.displayImage(
						/*pe.getUrl()+"&tsha1="+t.dataEntity.getShal()*/ t.url, t.iv,
						options, new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri, View view) {
								t.spinner.setVisibility(View.VISIBLE);
							}

							@Override
							public void onLoadingFailed(String imageUri, View view,
									FailReason failReason) {
								String message = null;
								switch (failReason.getType()) {
								case IO_ERROR:
									message = "网络不可用";
									break;
								case DECODING_ERROR:
									message = "图片解析失败";
									break;
								case NETWORK_DENIED:
									message = "网络不可用";
									break;
								case OUT_OF_MEMORY:
									message = "亲，请滑动的慢点";
									break;
								case UNKNOWN:
									message = "图片加载失败";
									break;
								}
								Toast.makeText(context, message, Toast.LENGTH_SHORT)
										.show();

								t.spinner.setVisibility(View.VISIBLE);
							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								t.spinner.setVisibility(View.GONE);
								t.iv.setVisibility(View.VISIBLE);
								 
							}
						});
				break;
			}
		}
	};

    
    
 
	@Override
	public int getCount() {
		return context.getEntity().getCount();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0.equals(arg1);
	}



}
