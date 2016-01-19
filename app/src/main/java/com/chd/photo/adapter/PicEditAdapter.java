package com.chd.photo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.photo.entity.PicEditBean;
import com.chd.photo.entity.PicEditItemBean;
import com.chd.photo.ui.PicDetailActivity;
import com.chd.yunpan.R;
import com.chd.yunpan.utils.MyGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.List;


public class PicEditAdapter extends BaseAdapter implements OnItemClickListener, OnClickListener{

	private Activity context;
	private List<PicEditBean> list;
	private ImageLoader imageLoader;

	public PicEditAdapter(Activity context, List<PicEditBean> list) {
		this.context = context;
		this.list = list;
		imageLoader=ImageLoader.getInstance();
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
			converView = View.inflate(context, R.layout.item_pic_edit_adapter, null);
			holder = new ViewHolder();
			holder.tv_pic_edit_date = (TextView) converView.findViewById(R.id.tv_pic_edit_date);
			holder.tv_pic_edit_group_check = (ImageView) converView.findViewById(R.id.tv_pic_edit_group_check);
			holder.mlv_pic_edit_gridview = (MyGridView) converView.findViewById(R.id.mlv_pic_edit_gridview);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}
		holder.tv_pic_edit_date.setText(list.get(position).getDate());
		holder.tv_pic_edit_group_check.setImageResource(list.get(position).isSelect() ? R.drawable.pic_edit_photo_checked : R.drawable.pic_edit_photo_group_check);
		holder.mlv_pic_edit_gridview.setAdapter(new PicEditItemAdapter(context, list.get(position).getList(), imageLoader));
		holder.mlv_pic_edit_gridview.setOnScrollListener(new PauseOnScrollListener(imageLoader,true,true));
		holder.mlv_pic_edit_gridview.setTag(position);
		holder.tv_pic_edit_group_check.setTag(position);
		holder.mlv_pic_edit_gridview.setOnItemClickListener(this);
		holder.tv_pic_edit_group_check.setOnClickListener(this);
		holder.tv_pic_edit_group_check.setVisibility(list.get(position).isEdit() ? View.VISIBLE : View.GONE);

		return converView;
	}

	private class ViewHolder {
		TextView tv_pic_edit_date;
		ImageView tv_pic_edit_group_check;
		MyGridView mlv_pic_edit_gridview;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		int position = (Integer) arg0.getTag();
		if (list.get(position).isEdit())
		{
			boolean bSel = list.get(position).getList().get(arg2).isSelect();
			list.get(position).getList().get(arg2).setSelect(!bSel);
			((PicEditItemAdapter)arg0.getAdapter()).notifyDataSetChanged();
		}
		else
		{
			Intent intent = new Intent(context, PicDetailActivity.class);
			//intent.putExtra("picid", list.get(position).getList().get(arg2).getPicid());
			intent.putExtra("ubklist", list.get(position).getList().get(arg2).isbIsUbkList());
			//intent.putExtra("filepath", list.get(position).getList().get(arg2).getPicpath());
			//intent.putExtra("fileinfo0", list.get(position).getList().get(arg2).getFileInfo0());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			context.startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
		case R.id.tv_pic_edit_group_check:
		{
			int position = (Integer) v.getTag();
			boolean bSel = list.get(position).isSelect();
			((ImageView) v).setImageResource(bSel ? R.drawable.pic_edit_photo_group_check : R.drawable.pic_edit_photo_checked);
			for (PicEditItemBean picEditItemBean : list.get(position).getList())
			{
				picEditItemBean.setSelect(!bSel);
			}
			list.get(position).setSelect(!bSel);
			notifyDataSetChanged();
		}
			break;

		default:
			break;
		}
	}

}
