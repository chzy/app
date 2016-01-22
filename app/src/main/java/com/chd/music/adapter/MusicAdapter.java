package com.chd.music.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.contacts.vcard.StringUtils;
import com.chd.music.backend.MediaUtil;
import com.chd.music.entity.MusicBean;
import com.chd.yunpan.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

public class MusicAdapter extends BaseAdapter {

	private Context context;
	private List<MusicBean> mMusiclist;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	public MusicAdapter(Context context, List<MusicBean> list) {
		this.context = context;
		this.mMusiclist = list;
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.pic_test1)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
	}

	@Override
	public int getCount() {
		return mMusiclist.size();
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
			converView = View.inflate(context, R.layout.item_music_adapter,
					null);
			holder = new ViewHolder();
			holder.iv_photo = (ImageView) converView
					.findViewById(R.id.iv_photo);
			holder.tv_title = (TextView) converView.findViewById(R.id.tv_title);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}

		holder.tv_title.setText(mMusiclist.get(position).getTitle());
		
		String pic = mMusiclist.get(position).getFileInfo0().getFilePath();
		String albumArt=null;
		if(!StringUtils.isNullOrEmpty(pic)){
			albumArt= MediaUtil.getAlbumArt(context,pic);
		}
		if (albumArt == null) {
			Bitmap bm = BitmapFactory.decodeResource(context.getResources(),R.drawable.pic_test1);
			BitmapDrawable bmpDraw = new BitmapDrawable(bm);
			holder.iv_photo.setImageDrawable(bmpDraw);
		} else {
			Bitmap bm = BitmapFactory.decodeFile(albumArt);
			BitmapDrawable bmpDraw = new BitmapDrawable(bm);
			holder.iv_photo.setImageDrawable(bmpDraw);
		}
//		if (mMusiclist.get(position).getFileInfo0() != null)
//		{
//			pic = mMusiclist.get(position).getFileInfo0().getFilePath();
//		}
//
//		imageLoader.displayImage(pic, holder.iv_photo,
//				options, new SimpleImageLoadingListener() {
//					@Override
//					public void onLoadingStarted(String imageUri, View view) {
//						/*vh.progressBar.setProgress(0);
//						vh.progressBar.setVisibility(View.VISIBLE);*/
//					}
//
//					@Override
//					public void onLoadingFailed(String imageUri, View view,
//							FailReason failReason) {
//						/*vh.progressBar.setVisibility(View.GONE);*/
//					}
//
//					@Override
//					public void onLoadingComplete(String imageUri, View view,
//							Bitmap loadedImage) {
//						/*vh.progressBar.setVisibility(View.GONE);*/
//					}
//				}, new ImageLoadingProgressListener() {
//					@Override
//					public void onProgressUpdate(String imageUri, View view,
//							int current, int total) {
//						/*vh.progressBar.setProgress(Math.round(100.0f * current
//								/ total));*/
//					}
//				});
		return converView;
	}

	private class ViewHolder {
		ImageView iv_photo;
		TextView tv_title;
	}
	

}
