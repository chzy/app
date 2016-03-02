package com.chd.yunpan.ui.adapter;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.yunpan.R;
import com.chd.yunpan.myclass.FileWithBoolean;
import com.chd.yunpan.ui.fragment.UpLoadOtherFileFragment;
import com.chd.yunpan.utils.FindType;
import com.chd.yunpan.utils.TimeAndSizeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UpLoadOtherAdapter extends BaseAdapter {

	private List<FileWithBoolean> fileList = new ArrayList<FileWithBoolean>();

	private Fragment fragment = null;


	public UpLoadOtherAdapter(Fragment fragment,
			List<FileWithBoolean> fileList) {
		this.fragment = fragment;
		this.fileList = fileList;
	}

	public int getCount() {
		return fileList.size();
	}

	class ViewHolder {
		ImageView image;
		TextView name;
		TextView size;
		ImageView check;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;

		if (convertView == null) {
			vh = new ViewHolder();
			convertView = View.inflate(fragment.getActivity(),
					R.layout.lz_upload_item, null);
			vh.check = (ImageView) convertView
					.findViewById(R.id.lzUpLoadOtherItemCheck);
			
			vh.image = (ImageView) convertView
					.findViewById(R.id.lzUpLoadOtherItemImage);
			vh.name = (TextView) convertView
					.findViewById(R.id.lzUpLoadOtherItemName);
			vh.size = (TextView) convertView
					.findViewById(R.id.lzUpLoadOtherItemSize);

			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fileList.get(position).setCheck(!fileList.get(position).isCheck());

				if (fileList.get(position).isCheck()) {
					((ImageView) v).setImageResource(R.drawable.btn_checkbox_on);
//					imageLoaderUtil.loadImage("drawable://"
//							+ R.drawable.btn_checkbox_on, (ImageView) v);
				} else {
					((ImageView) v).setImageResource(R.drawable.btn_checkbox_off);
//					imageLoaderUtil.loadImage("drawable://"
//							+ R.drawable.btn_checkbox_off, (ImageView) v);

				}
				((UpLoadOtherFileFragment) fragment).checkFileCheck();
				// UpLoadOtherAdapter.this.notifyDataSetChanged();

			}
		});
		// vh.check.setChecked(currentItem.isCheck());
		// vh.check.setSelected(currentItem.isCheck());
		if (fileList.get(position).isCheck()) {
			vh.check.setImageResource(R.drawable.btn_checkbox_on);
//			imageLoaderUtil.loadImage("drawable://"
//					+ R.drawable.btn_checkbox_on, vh.check);
		} else {
			vh.check.setImageResource(R.drawable.btn_checkbox_off);
//			imageLoaderUtil.loadImage("drawable://"
//					+ R.drawable.btn_checkbox_off, vh.check);
		}

		vh.name.setText(fileList.get(position).getFile().getName());
		
		int res = 0;
		File f=fileList.get(position).getFile();
		if (f.isDirectory()) {
			res = R.drawable.cloud_dir_icon;
			vh.size.setText("");
		} else {
			res = FindType.findImage(fileList.get(position).getFile().getAbsolutePath());
			String size = TimeAndSizeUtil.getSize(fileList.get(position).getFile().length()
					+ "");
			vh.size.setText(size);
		}
		vh.image.setImageResource(res);


		return convertView;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

}
