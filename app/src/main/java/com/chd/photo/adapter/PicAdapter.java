package com.chd.photo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.photo.entity.PicBean;
import com.chd.photo.ui.PicEditActivity;
import com.chd.yunpan.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;


public class PicAdapter extends BaseAdapter{

	private Context context;
	private List<PicBean> list;
	private boolean bIsUbkList;

	public PicAdapter(Context context, List<PicBean> list, boolean bIsUbkList) {
		this.context = context;
		this.list = list;
		this.bIsUbkList = bIsUbkList;
	}

	@Override
	public int getCount() {
		return list.size();
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
	public View getView(final int position, View converView, ViewGroup parent) {
		ViewHolder holder;
		if (converView == null) {
			converView = View.inflate(context, R.layout.item_pic_adapter, null);
			holder = new ViewHolder();
			holder.tv_pic_date = (TextView) converView
					.findViewById(R.id.tv_pic_date);
			holder.mlv_pic = (MyListView) converView.findViewById(R.id.mlv_pic);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}
		holder.tv_pic_date.setText(list.get(position).getDate());
		holder.mlv_pic.setAdapter(new PicInfoAdapter(context, list
				.get(position).getList()));
		holder.mlv_pic.setOnItemClickListener(new OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
				Intent intent = new Intent(context, PicEditActivity.class);
				intent.putExtra("month", list.get(position).getList().get(arg2).getMonth());
				intent.putExtra("year", list.get(position).getDate());
				intent.putExtra("ubklist", bIsUbkList);
				context.startActivity(intent);
			}
			
		});
		
		return converView;
	}

	private class ViewHolder {
		TextView tv_pic_date;
		MyListView mlv_pic;
	}
}
