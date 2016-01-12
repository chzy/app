package com.chd.music.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.music.entity.MusicBackupBean;
import com.chd.photo.adapter.RoundImageView;
import com.chd.yunpan.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class MusicBackupAdapter extends BaseAdapter {

	private Context context;
	private List<MusicBackupBean> mMusiclist;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	public MusicBackupAdapter(Context context, List<MusicBackupBean> list) {
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
			converView = View.inflate(context, R.layout.item_music_backup_adapter, null);
			holder = new ViewHolder();
			holder.gv_check = (ImageView) converView.findViewById(R.id.gv_music_backup_item_check);
			holder.gv_pic = (RoundImageView) converView.findViewById(R.id.gv_music_backup_item_pic);
			holder.gv_title = (TextView) converView.findViewById(R.id.gv_music_backup_item_txt);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}

		holder.gv_title.setText(mMusiclist.get(position).getTitle());
		holder.gv_check.setImageResource(mMusiclist.get(position).isSelect() ? R.drawable.pic_edit_photo_checked : R.drawable.pic_edit_photo_group_check);
		
		imageLoader.displayImage(mMusiclist.get(position).getPic(), holder.gv_pic,
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
		ImageView gv_check;
		RoundImageView gv_pic;
		TextView gv_title;
	}
	

}
