package com.chd.yunpan.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.utils.FindType;
import com.chd.yunpan.utils.TimeAndSizeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.List;

public class DownAndUpAdapter extends BaseAdapter {
	private List<FileInfo0> files;
	private Context context;
	private ImageLoader util= ImageLoader.getInstance();

	public DownAndUpAdapter(Context context, List<FileInfo0> files) {
		this.files = files;
		this.context = context;
	
	}

	public class ViewHoder {
		public ImageView imageView;
		public TextView name;
		public TextView size;
		public TextView time;
		public CheckBox box;

	}

	@Override
	public int getCount() {

		return files.size();

	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHoder hoder = null;
		if (convertView == null) {
			hoder = new ViewHoder();
			convertView = View.inflate(context,
					R.layout.jd_downlond_and_upload_item, null);
			hoder.imageView = (ImageView) convertView
					.findViewById(R.id.dowup_item_image);
			hoder.name = (TextView) convertView
					.findViewById(R.id.dowup_item_name);
			hoder.time = (TextView) convertView
					.findViewById(R.id.dowup_item_time);
			hoder.size = (TextView) convertView
					.findViewById(R.id.dowup_item_size);
			hoder.box = (CheckBox) convertView
					.findViewById(R.id.dowup_item_btn);
			hoder.box.setTag(position);
			convertView.setTag(hoder);
		} else {
			hoder = (ViewHoder) convertView.getTag();
		}
		hoder.box.setVisibility(View.GONE);
		//util.loadImage(files.get(position).getU(), hoder.imageView);
		
		 hoder.imageView
		 .setImageResource(FindType.findImage(files.get(position).getFilePath()));
		hoder.name.setText(files.get(position).getFilename());
		
			File f =  new File(files.get(position).getFilePath());
		if(f.isFile()){
			hoder.size.setText(TimeAndSizeUtil.getSize(f.length()+""));
			hoder.time.setText(TimeAndSizeUtil.getTime(f.lastModified()/1000+""));
		} else{
			hoder.size.setText(TimeAndSizeUtil.getSize(files.get(position).getsizeS()));
			//hoder.time.setText(TimeAndSizeUtil.getTime(files.get(position).getTimeTxt()));
		}
		

		return convertView;
	}


	public void setFiles(List<FileInfo0> files) {
		this.files = files;
	}
	
	
	
}
