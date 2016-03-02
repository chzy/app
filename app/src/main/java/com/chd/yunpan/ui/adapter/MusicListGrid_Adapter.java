package com.chd.yunpan.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chd.music.entity.MusicBean;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.db.DBManager;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.ui.PhotoShowActivity;
import com.chd.yunpan.ui.fragment.MusicListFragment;
import com.chd.yunpan.utils.ToastUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

//import com.chd.yunpan.parse.entity.FileDataEntity;

public class MusicListGrid_Adapter extends BaseAdapter {

	private final MusicListFragment fragment;
	private Context context;
	private List<MusicBean> mMusiclist;
	private boolean checked;
	DisplayImageOptions options;

	private DBManager dbManager;

	private ShareUtils shareUtils;

	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	private List<ViewHolder> holders = new ArrayList<ViewHolder>();

	private class ViewHolder0 {
		ImageView iv_photo;
		TextView tv_title;

	}


	private class ViewHolderV {
		public ImageView image;
		public CheckBox checkbox;
		public ImageView imageDown;
		public ImageView imageDownFail;
		public RelativeLayout layoutDowning;
		public LinearLayout layoutWaiting;
		public LinearLayout layoutStop;
		public ProgressBar progressBar;
	}

	class ViewHolder {

		private View v1;
		private View v2;
		private View v3;

		private ViewHolderV vh1;
		private ViewHolderV vh2;
		private ViewHolderV vh3;

		private View v;

		private View lv;

		private TextView tv;


		ImageView iv_photo;
		TextView tv_title;

	}


/*
	public MusicListGrid_Adapter(Context context, List<MusicBean> list) {

	}*/

	public MusicListGrid_Adapter(MusicListFragment fragment) {
		this.context = context;
		//this.mMusiclist = list;
		dbManager = new DBManager(fragment.getActivity());
		dbManager.open();
		shareUtils =new ShareUtils(fragment.getActivity());
		this.fragment = fragment;
		mMusiclist=fragment.getTileAndFilesListEntity();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(20))
				.build();
		handler.sendEmptyMessageDelayed(1, 4000);
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

		imageLoader.displayImage(mMusiclist.get(position).getPic(), holder.iv_photo,
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
		holder.tv_title.setText(mMusiclist.get(position).getTitle());
		return converView;
	}




	public void refreshChecked(){
		for (ViewHolder viewHolder : holders) {
			viewHolder.vh1.checkbox.setVisibility(View.GONE);
			viewHolder.vh2.checkbox.setVisibility(View.GONE);
			viewHolder.vh3.checkbox.setVisibility(View.GONE);
		}
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public void setView(final FileInfo0 entity, final ViewHolderV vh,
			int position) {
		int type = 0;
		if(dbManager.getDownloadedFile(entity.getObjid())!=null){
			type = 1;
		}
		if(dbManager.getDownloadingFile(entity.getObjid())!=null){
			type = 2;
		}
		// 判断图片的状态时那种
		switch (type) {
		case 1: // 下载完成
			vh.imageDown.setVisibility(View.VISIBLE);
			vh.layoutWaiting.setVisibility(View.INVISIBLE);
			vh.layoutDowning.setVisibility(View.INVISIBLE);
			vh.layoutStop.setVisibility(View.INVISIBLE);
			vh.imageDownFail.setVisibility(View.INVISIBLE);
			break;
		case 2: // 正在下载
			vh.layoutWaiting.setVisibility(View.INVISIBLE);
			vh.imageDown.setVisibility(View.INVISIBLE);
			vh.layoutDowning.setVisibility(View.VISIBLE);
			vh.layoutStop.setVisibility(View.INVISIBLE);
			vh.imageDownFail.setVisibility(View.INVISIBLE);
			break;
		case 3: //
			vh.layoutDowning.setVisibility(View.INVISIBLE);
			vh.imageDown.setVisibility(View.INVISIBLE);
			vh.layoutWaiting.setVisibility(View.INVISIBLE);
			vh.layoutStop.setVisibility(View.INVISIBLE);
			vh.imageDownFail.setVisibility(View.VISIBLE);
			break;
		case 4:
			vh.layoutStop.setVisibility(View.VISIBLE);
			vh.imageDown.setVisibility(View.INVISIBLE);
			vh.layoutWaiting.setVisibility(View.INVISIBLE);
			vh.layoutDowning.setVisibility(View.INVISIBLE);
			vh.imageDownFail.setVisibility(View.INVISIBLE);
			break;
		case 5:
			vh.imageDownFail.setVisibility(View.VISIBLE);
			vh.imageDown.setVisibility(View.INVISIBLE);
			vh.layoutWaiting.setVisibility(View.INVISIBLE);
			vh.layoutDowning.setVisibility(View.INVISIBLE);
			vh.layoutStop.setVisibility(View.INVISIBLE);
			break;
		default:
			vh.imageDownFail.setVisibility(View.INVISIBLE);
			vh.imageDown.setVisibility(View.INVISIBLE);
			vh.layoutWaiting.setVisibility(View.INVISIBLE);
			vh.layoutDowning.setVisibility(View.INVISIBLE);
			vh.layoutStop.setVisibility(View.INVISIBLE);
			break;
		}
		vh.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				fragment.check(entity, isChecked);
			}
		});
		
		if(checked||fragment.isChecked){
			vh.checkbox.setVisibility(View.VISIBLE);
		}else {
			vh.checkbox.setVisibility(View.GONE);
		}
		if(entity.isSelected()){
			vh.checkbox.setChecked(true);
		}else {
			vh.checkbox.setChecked(false);
		}
		vh.image.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				fragment.showMenu();
				entity.setIsChecked( true);
				fragment.isChecked = true;
				notifyDataSetChanged();
				return true;
			}
		});
		
		vh.image.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(fragment.getActivity(),PhotoShowActivity.class);
				i.putExtra("entity", entity);
				ToastUtils.toast(fragment.getActivity(), "跳过去了");
				fragment.getActivity().startActivityForResult(i, 1);
			}
		});
		imageLoader.displayImage(entity.getFilePath(), vh.image, options, new SimpleImageLoadingListener() {
			 @Override
			 public void onLoadingStarted(String imageUri, View view) {
				 vh.progressBar.setProgress(0);
				 vh.progressBar.setVisibility(View.VISIBLE);
			 }

			 @Override
			 public void onLoadingFailed(String imageUri, View view,
					 FailReason failReason) {
				 vh.progressBar.setVisibility(View.GONE);
			 }

			 @Override
			 public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				 vh.progressBar.setVisibility(View.GONE);
			 }
		 }, new ImageLoadingProgressListener() {
			 @Override
			 public void onProgressUpdate(String imageUri, View view, int current,
					 int total) {
				 vh.progressBar.setProgress(Math.round(100.0f * current / total));
			 }
		 }
);
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
	
	
	public Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			MusicListGrid_Adapter.this.notifyDataSetChanged();
			handler.sendEmptyMessageDelayed(1, 3000);
		}
	};

}
