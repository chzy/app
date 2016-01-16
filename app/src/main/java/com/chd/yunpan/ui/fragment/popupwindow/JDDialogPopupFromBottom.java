package com.chd.yunpan.ui.fragment.popupwindow;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chd.MediaMgr.utils.MediaFileUtil;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.ui.fragment.FileListFragment;
import com.chd.yunpan.utils.FindType;
import com.chd.yunpan.utils.TimeUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/*
import com.chd.yunpan.parse.entity.FileDataEntity;
import com.chd.yunpan.parse.entity.FileDataFatherEntity;
import com.chd.yunpan.parse.entity.FileDirDataEntity;
*/

public class JDDialogPopupFromBottom extends Dialog {

	public JDDialogPopupFromBottom(FileListFragment fragment) {
		super(fragment.getActivity());
//		for (int i = 0; i < fragment.getFilesListEntity().getCount(); i++)
		for (  FileInfo0 f : fragment.getFilesListEntity().getList() )
			{
			if (f.isSelected()) {
				items.add(f);
			}
		}
		this.fragment = fragment;

		view = View
				.inflate(fragment.getActivity(), R.layout.jd_pop_clear, null);

		lv = (ListView) view.findViewById(R.id.delete_lv);
		MyAdapter adapter = new MyAdapter();

		lv.setAdapter(adapter);

		setContentView(view);

		delete = findViewById(R.id.settingConfirmClear);

		cancel = (Button) findViewById(R.id.settingCancelClear);
		delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				JDDialogPopupFromBottom.this.fragment.delete();
				dismiss();
			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				dismiss();
			}
		});

		init();
	}

	private View view = null;

	private View delete = null;

	private Button cancel = null;

	private ListView lv;

	DisplayImageOptions options;

	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	List<FileInfo0> items = new ArrayList<FileInfo0>();

	private FileListFragment fragment;

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (items.size() > 4) {
				return 1;
			}
			return items.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = View.inflate(fragment.getActivity(),
					R.layout.delete_item, null);
			ImageView imv = (ImageView) convertView
					.findViewById(R.id.delete_item_image);
			TextView name = (TextView) convertView
					.findViewById(R.id.delete_item_name);
			TextView time = (TextView) convertView
					.findViewById(R.id.delete_item_time);
			TextView size = (TextView) convertView
					.findViewById(R.id.delete_item_size);
			if (items.size() > 4) {
				name.setText(items.get(items.size() - 1).getFilename() + " 等"
						+ items.size() + "项");
				return convertView;
			}
			FileInfo0 entity = items.get(position);
/*
			if (entity instanceof FileDirDataEntity) {
				// 文件夹
				imv.setImageResource(FindType.findDir(entity.getN()));
			} else
			*/
			{
				// 文件
				FileInfo0 entity2 = entity;
				String filepath=entity2.getFilePath();
				File fm=new File(filepath);

				if ( fm.exists() && fm.isFile())

				//if (TextUtils.isEmpty(entity2.getU()))
				{
					imageLoader.displayImage(
							"drawable://" + FindType.findImage(entity2.getFilename()),
							imv, options, animateFirstListener);
				}
				 /*else {
					imageLoader.displayImage(entity2.getU().trim() + "&tsha1="
							+ entity2.getShal() + "_100_100", imv, options,
							animateFirstListener);
				}
*/
				size.setText(MediaFileUtil.convertStorage(entity2.getFilesize()));
			}

			name.setText(items.get(position).getFilename());

			time.setText(TimeUtils.getTime(items.get(position).getLastModified()));
			return convertView;
		}

	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
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

	private void init() {

		Window window = getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.mystyle); // 添加动画
		show();
		WindowManager windowManager = ((Activity) fragment.getActivity())
				.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = display.getWidth();
		getWindow().setAttributes(params);
		window.setBackgroundDrawableResource(R.drawable.background_dialog);

	}



}
