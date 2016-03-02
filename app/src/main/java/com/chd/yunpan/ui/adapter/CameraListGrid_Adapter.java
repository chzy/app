package com.chd.yunpan.ui.adapter;

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

import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.db.DBManager;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.ui.PhotoShowActivity;
import com.chd.yunpan.ui.fragment.PhotoListFragment;
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

public class CameraListGrid_Adapter extends BaseAdapter {
	
	private PhotoListFragment fragment;
	
	private boolean checked;
	
	private List<ViewHolder> holders = new ArrayList<CameraListGrid_Adapter.ViewHolder>();
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
	DisplayImageOptions options;
	
	private DBManager dbManager;

	private ShareUtils shareUtils;

	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	public CameraListGrid_Adapter(PhotoListFragment fragment) {
		dbManager = new DBManager(fragment.getActivity());
		dbManager.open();
		shareUtils =new ShareUtils(fragment.getActivity());
		this.fragment = fragment;
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
		return fragment.getList().size();
		
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

	}

	/**
	 * 强制保存所有图片容易内存异常
	 */
	// private Map<Integer, View> vvv = new HashMap<Integer, View>();

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Object entity = fragment.getList().get(position);
		
		ViewHolder vh = null;
		if (convertView != null) {
			vh = (ViewHolder) convertView.getTag();
		} else {
			vh = new ViewHolder();
			holders.add(vh);
			vh.vh1 = new ViewHolderV();
			vh.vh2 = new ViewHolderV();
			vh.vh3 = new ViewHolderV();
			convertView = View
					.inflate(fragment.getActivity(), R.layout.camera_item_wang, null);
			vh.v = convertView.findViewById(R.id.v);
			vh.v1 = convertView.findViewById(R.id.v1);
			vh.v2 = convertView.findViewById(R.id.v2);
			vh.v3 = convertView.findViewById(R.id.v3);
			vh.tv = (TextView) convertView
					.findViewById(R.id.camera_lt_textView);
			vh.lv = convertView.findViewById(R.id.lv);

			vh.vh1.image = (ImageView) vh.v1
					.findViewById(R.id.camera_lg_imageView);
			vh.vh1.checkbox = (CheckBox) vh.v1
					.findViewById(R.id.camera_lg_checkBox);
			vh.vh1.imageDown = (ImageView) vh.v1
					.findViewById(R.id.camera_lg_downimageView);
			vh.vh1.imageDownFail = (ImageView) vh.v1
					.findViewById(R.id.camera_lg_downimagefail);
			vh.vh1.layoutDowning = (RelativeLayout) vh.v1
					.findViewById(R.id.camera_downing);
			vh.vh1.layoutWaiting = (LinearLayout) vh.v1
					.findViewById(R.id.camera_waiting);
			vh.vh1.layoutStop = (LinearLayout) vh.v1
					.findViewById(R.id.camera_downingstop);
			vh.vh1.progressBar = (ProgressBar) vh.v1
					.findViewById(R.id.progress);

			vh.vh2.image = (ImageView) vh.v2
					.findViewById(R.id.camera_lg_imageView);
			vh.vh2.checkbox = (CheckBox) vh.v2
					.findViewById(R.id.camera_lg_checkBox);
			vh.vh2.imageDown = (ImageView) vh.v2
					.findViewById(R.id.camera_lg_downimageView);
			vh.vh2.imageDownFail = (ImageView) vh.v2
					.findViewById(R.id.camera_lg_downimagefail);
			vh.vh2.layoutDowning = (RelativeLayout) vh.v2
					.findViewById(R.id.camera_downing);
			vh.vh2.layoutWaiting = (LinearLayout) vh.v2
					.findViewById(R.id.camera_waiting);
			vh.vh2.layoutStop = (LinearLayout) vh.v2
					.findViewById(R.id.camera_downingstop);
			vh.vh2.progressBar = (ProgressBar) vh.v2
					.findViewById(R.id.progress);

			vh.vh3.image = (ImageView) vh.v3
					.findViewById(R.id.camera_lg_imageView);
			vh.vh3.checkbox = (CheckBox) vh.v3
					.findViewById(R.id.camera_lg_checkBox);
			vh.vh3.imageDown = (ImageView) vh.v3
					.findViewById(R.id.camera_lg_downimageView);
			vh.vh3.imageDownFail = (ImageView) vh.v3
					.findViewById(R.id.camera_lg_downimagefail);
			vh.vh3.layoutDowning = (RelativeLayout) vh.v3
					.findViewById(R.id.camera_downing);
			vh.vh3.layoutWaiting = (LinearLayout) vh.v3
					.findViewById(R.id.camera_waiting);
			vh.vh3.layoutStop = (LinearLayout) vh.v3
					.findViewById(R.id.camera_downingstop);
			vh.vh3.progressBar = (ProgressBar) vh.v3
					.findViewById(R.id.progress);
			convertView.setTag(vh);
		}

	/*	if (entity instanceof List) {
			vh.v.setVisibility(View.VISIBLE);
			vh.lv.setVisibility(View.GONE);
			List<FileInfo0> entities = (List<FileInfo0>) entity;
			if (entities.size() > 0) {

				vh.v1.setVisibility(View.VISIBLE);
				vh.v1.setClickable(true);
				setView(entities.get(0), vh.vh1, position);
			} else {
				vh.v1.setVisibility(View.INVISIBLE);
				vh.v1.setClickable(false);
			}
			 if (entities.size() > 1) {
				vh.v2.setClickable(true);
				vh.v2.setVisibility(View.VISIBLE);
				setView(entities.get(1), vh.vh2, position);
			} else {
				vh.v2.setClickable(false);
				vh.v2.setVisibility(View.INVISIBLE);
			}
			 if (entities.size() > 2) {
				vh.v3.setClickable(true);
				vh.v3.setVisibility(View.VISIBLE);
				setView(entities.get(2), vh.vh3, position);
			} else {
				vh.v3.setClickable(false);
				vh.v3.setVisibility(View.INVISIBLE);
			}
		} else
		*/
		{
			vh.v.setVisibility(View.GONE);
			vh.lv.setVisibility(View.VISIBLE);
			vh.tv.setText(entity.toString());
		}
		return convertView;

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
	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
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
			CameraListGrid_Adapter.this.notifyDataSetChanged();
			handler.sendEmptyMessageDelayed(1, 3000);
		}
	};

}
