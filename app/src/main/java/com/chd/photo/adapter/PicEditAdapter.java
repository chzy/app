package com.chd.photo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
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

import java.util.List;


public class PicEditAdapter extends BaseAdapter {

	private Activity context;
	private List<PicEditBean> list;
	private LayoutInflater mInflater;
	private boolean isEdit;

	public PicEditAdapter(Activity context, List<PicEditBean> list) {
		this.context = context;
		this.list = list;
		mInflater=LayoutInflater.from(context);
	}

	public void setData(List<PicEditBean> data){
		this.list=data;
		this.notifyDataSetChanged();
	}

	public void setEdit(boolean isEdit){
		this.isEdit=isEdit;
		notifyDataSetChanged();
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
			converView = mInflater.inflate(R.layout.item_pic_edit_adapter, parent,false);
			holder = new ViewHolder();
			holder.tv_pic_edit_date = (TextView) converView.findViewById(R.id.tv_pic_edit_date);
			holder.tv_pic_edit_group_check = (ImageView) converView.findViewById(R.id.tv_pic_edit_group_check);
			holder.mlv_pic_edit_gridview = (MyGridView) converView.findViewById(R.id.mlv_pic_edit_gridview);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}
		PicEditBean picEditBean = list.get(position);
		holder.tv_pic_edit_date.setText(picEditBean.getDate());
		holder.tv_pic_edit_group_check.setImageResource(picEditBean.isSelect() ? R.drawable.pic_edit_photo_checked : R.drawable.pic_edit_photo_group_check);
			List<PicEditItemBean> data = picEditBean.getList();
			holder.mlv_pic_edit_gridview.setAdapter(new PicEditItemAdapter(context, data,isEdit));

			holder.mlv_pic_edit_gridview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					if (isEdit)
					{
						boolean bSel = list.get(position).getList().get(i).isSelect();
						list.get(position).getList().get(i).setSelect(!bSel);
						((PicEditItemAdapter)adapterView.getAdapter()).notifyDataSetChanged();
					}
					else
					{
						Intent intent = new Intent(context, PicDetailActivity.class);
						intent.putExtra("ubklist", list.get(position).getList().get(i).isbIsUbkList());
						intent.putExtra("bean",list.get(position).getList().get(i));
						intent.putExtra("pos",position);
						intent.putExtra("pos2",i);
						context.startActivityForResult(intent, 0x12);
					}
				}
			});
//		}

		holder.tv_pic_edit_group_check.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				boolean bSel = list.get(position).isSelect();
				((ImageView)view).setImageResource(bSel ? R.drawable.pic_edit_photo_group_check : R.drawable.pic_edit_photo_checked);
				for (PicEditItemBean picEditItemBean : list.get(position).getList())
				{
					picEditItemBean.setSelect(!bSel);
				}
				list.get(position).setSelect(!bSel);
				notifyDataSetChanged();
			}
		});
		if(isEdit){
			holder.tv_pic_edit_group_check.setVisibility(View.VISIBLE);
		}else{
			holder.tv_pic_edit_group_check.setVisibility(View.GONE);
		}

		return converView;
	}

	private class ViewHolder {
		TextView tv_pic_edit_date;
		ImageView tv_pic_edit_group_check;
		MyGridView mlv_pic_edit_gridview;
	}




}
