package com.chd.yunpan.ui.adapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.chd.yunpan.R;
import com.chd.yunpan.parse.entity.PhotoFileEntity;
import com.chd.yunpan.ui.fragment.PhotoUploadingFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class PhotoLoadGridAdapter extends BaseAdapter {
	private Context context = null;
	private List<PhotoFileEntity> fileEntities=null;
	private LayoutInflater inflater;
	private PhotoUploadingFragment fragment;
	private PhotoPopMenuLoad load;
	DisplayImageOptions options;

	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	public PhotoLoadGridAdapter(Context context, List<PhotoFileEntity> fileEntities,PhotoUploadingFragment fragment,PhotoPopMenuLoad load) {
		super();
		this.context = context;
		this.fileEntities = fileEntities;
		this.fragment=fragment;
		this.load=load;
		inflater=LayoutInflater.from(context);
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
	}

	@Override
	public int getCount() {
		return fileEntities.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if (convertView==null) {
			holder=new ViewHolder();
			convertView=inflater.inflate(R.layout.photo_load_griditem, null);
			holder.image=(ImageView) convertView.findViewById(R.id.photo_lg_imageView);
			holder.box=(CheckBox) convertView.findViewById(R.id.photo_lg_checkBox);
			holder.imageload=(ImageView) convertView.findViewById(R.id.photo_lg_loadimageView);
			convertView.setTag(holder);
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		CheckBox cb = (CheckBox) convertView
				.findViewById(R.id.photo_lg_checkBox);
		if (fileEntities.get(position).isLoad()) {
			holder.imageload.setVisibility(View.VISIBLE);
		}else{
			holder.imageload.setVisibility(View.INVISIBLE);
		}
		if (fileEntities.get(position).isPitch()) {
			cb.setChecked(true);
		} else {
			cb.setChecked(false);
		}
		if (fragment.isChecked()){
			load.showPopupWindows(convertView);
		} else {
			load.dis();
		}
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					fileEntities.get(position).setPitch(true);	
				} else {
					fileEntities.get(position).setPitch(false);
				}		
				fragment.notifyChecked2(position);
			}
		});
		imageLoader.displayImage("file://"+fileEntities.get(position).getFile().getAbsolutePath(), holder.image, options, animateFirstListener);
		
		return convertView;
	}
	public int getCheckedSize() {
		int size = 0;
		for (int i = 0; i < fileEntities.size(); i++) {
			if (fileEntities.get(i).isPitch()) {
				size++;
			}
		}
		return size;
	}
	class ViewHolder{
		public ImageView image;
		public CheckBox box;
		public ImageView imageload;
	}

}
