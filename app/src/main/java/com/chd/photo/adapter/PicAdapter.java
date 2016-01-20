package com.chd.photo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chd.photo.entity.PicBean;
import com.chd.photo.entity.PicInfoBean;
import com.chd.photo.entity.PicInfoBeanMonth;
import com.chd.photo.ui.PicEditActivity;
import com.chd.yunpan.R;

import java.io.Serializable;
import java.util.List;


public class PicAdapter extends BaseAdapter{

	private Context context;
	private List<PicBean<PicInfoBeanMonth>> list;
	private boolean bIsUbkList;

	public PicAdapter(Context context,  List<PicBean<PicInfoBeanMonth>> list, boolean bIsUbkList) {
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
		holder.tv_pic_date.setText(list.get(position).getYear());
		holder.mlv_pic.setAdapter(new PicInfoAdapter(context, list.get(position)
				));
		holder.mlv_pic.setOnItemClickListener(new OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
				Intent intent = new Intent(context, PicEditActivity.class);
				intent.putExtra("month", list.get(position).getMonth());
				intent.putExtra("year", list.get(position).getYear());
				List<PicInfoBean> mlist=list.get(position).getList().getPicunits();
				intent.putExtra("listUnits", (Serializable) (mlist));//LIST<PicInfoBean>
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
