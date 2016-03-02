package com.chd.contacts.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chd.contacts.entity.ContactBean;
import com.chd.yunpan.R;

import java.util.List;

public class ContactAdapter extends BaseAdapter {

	private Context context;
	private List<ContactBean> mList;

	public ContactAdapter(Context context, List<ContactBean> list) {
		this.context = context;
		this.mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
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
			converView = View.inflate(context, R.layout.item_contact_adapter,
					null);
			holder = new ViewHolder();
			holder.tv_time = (TextView) converView.findViewById(R.id.tv_time);
			holder.tv_number = (TextView) converView
					.findViewById(R.id.tv_number);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}
		holder.tv_time.setText(mList.get(position).getTime());
		holder.tv_number.setText("共计"+mList.get(position).getNumber()+"条信息");
		return converView;
	}

	private class ViewHolder {
		TextView tv_time;
		TextView tv_number;
	}
}
