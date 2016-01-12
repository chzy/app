package com.chd.yunpan.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.yunpan.R;
import com.chd.yunpan.myclass.Album;
import com.chd.yunpan.share.ShareUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AlbumGridAdapter extends BaseAdapter {

	private List<Album> albums = new ArrayList<Album>();

	private Context context = null;

	DisplayImageOptions options;

	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private List<Album> selectPosition = new ArrayList<Album>();

	private Button button = null;

	public AlbumGridAdapter(Context context, List<Album> albums) {
		super();
		this.context = context;
		this.albums = albums;
	}

	class ViewHolder {
		View background;
		ImageView photo;
		ImageView image2;
		TextView name;
		TextView size;
	}

	public int getCount() {
		return albums.size();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		final Album tempAlbum = albums.get(position);
	
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.lz_album, null);
			vh = new ViewHolder();

			vh.background = convertView
					.findViewById(R.id.lzAlbumImageBackground);

			vh.image2 = (ImageView) convertView
					.findViewById(R.id.lzAlbumImage2);
			vh.name = (TextView) convertView.findViewById(R.id.lzAlbumDirName);
			vh.photo = (ImageView) convertView.findViewById(R.id.lzAlbumImage);
			vh.size = (TextView) convertView.findViewById(R.id.lzAlbumSize);
			

			convertView.setTag(vh);

		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.background.setTag(position);
		

		vh.background.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int pos = (Integer) v.getTag();
				System.out.println(tempAlbum == albums.get(pos));
				albums.get(pos).setFlag(!albums.get(pos).isFlag());
				int count = 0;
				for (Album a : albums) {
					if (a.isFlag()) {
						count++;
					}
				}
				if (count > 0) {
					button.setEnabled(true);
					button.setText("ȷ��(" + count + ")");
					button.setTextColor(Color.WHITE);
				} else {
					button.setEnabled(false);
					button.setText("ȷ��");
					button.setTextColor(Color.GRAY);
				}
				AlbumGridAdapter.this.notifyDataSetChanged();
			}
		});

		if (tempAlbum.isFlag()) {
			vh.image2.setImageResource(R.drawable.ab_album_press);
			vh.background.setBackgroundResource(R.drawable.photos_bg_press);

		} else {
			vh.image2.setImageResource(R.drawable.ab_album_normal);
			vh.background.setBackgroundResource(R.drawable.photos_bg_normal);
		}

		imageLoader.displayImage("file://" + tempAlbum.getFirstImagePath(), vh.photo);
		vh.background.setTag(R.id.tag_first, position);

		vh.name.setText(tempAlbum.getFilePath());
		vh.size.setText(tempAlbum.getPhotoCount() + "��");
		vh.background.setTag(position);
		return convertView;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public List<Album> getAlbums() {
		return albums;
	}

	public void setAlbums(List<Album> albums) {
		ShareUtils utils = new ShareUtils(context);
		for (int i = 0; i <albums.size() ; i++) {
			if(utils.isExistPath(albums.get(i).getFirstImagePath().substring(0, albums.get(i).getFirstImagePath().lastIndexOf("/")))){
				albums.get(i).setFlag(true);
			}
		}
		this.albums = albums;
	}

	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
		this.button = button;
	}
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
}
