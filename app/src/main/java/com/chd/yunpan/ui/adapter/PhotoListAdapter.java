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
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.yunpan.R;
import com.chd.yunpan.myclass.Album;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class PhotoListAdapter extends BaseAdapter {
	private List<Album> albums=null;
	private Context context=null;
	private LayoutInflater inflater=null;
	
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
	
	public PhotoListAdapter(List<Album> albums, Context context) {
		super();
		this.albums = albums;
		this.context = context;
		inflater = LayoutInflater.from(context);
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
	}

	@Override
	public int getCount() {
		return albums.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder holder=null;
		if (convertView==null) {
			holder=new ViewHolder();
			convertView=inflater.inflate(R.layout.photo_load_list, null);
			holder.imageView=(ImageView) convertView.findViewById(R.id.photo_load_list_iamge);
			holder.fileName=(TextView) convertView.findViewById(R.id.photo_load_list_file);
			holder.numText=(TextView) convertView.findViewById(R.id.photo_load_list_num);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		imageLoader.displayImage("file://"+albums.get(position).getFirstImagePath(), holder.imageView, options, animateFirstListener);
		holder.fileName.setText(albums.get(position).getFilePath());
		holder.numText.setText(albums.get(position).getPhotoCount()+"张");
		return convertView;
	}
		class ViewHolder{
		public ImageView imageView;
		public TextView fileName;
		public TextView numText;
	}

}
