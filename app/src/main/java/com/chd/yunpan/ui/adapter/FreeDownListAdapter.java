package com.chd.yunpan.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.app.backend.AppInfo0;
import com.chd.yunpan.R;

public class FreeDownListAdapter extends BaseAdapter {
    private Context mContext;
    private List<AppInfo0> _list;

    public  FreeDownListAdapter(Context context, List<AppInfo0> list)
    {
        mContext=context;
        _list=list;
    }

    @Override
    public int getCount() {
        return _list==null ?  0: _list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MenuItem item = null;
        if (convertView == null) {
            item = new MenuItem();
            convertView = View.inflate(mContext, R.layout.freedown_listitem, null);
            item.text_index = (TextView) convertView.findViewById(R.id.freedown_list_item_index);
            item.text_appname = (TextView) convertView.findViewById(R.id.freedown_list_item_appname);
            item.text_appintro = (TextView) convertView.findViewById(R.id.freedown_list_item_appintro);
            item.btn_get = (Button) convertView.findViewById(R.id.freedown_list_item_btn);
            item.img_url = (ImageView) convertView.findViewById(R.id.freedown_list_item_img);
            convertView.setTag(item);
		} else {
			item = (MenuItem) convertView.getTag();
		}

        if (_list.get(position)!=null)
        {
            item.text_index.setText(String.format("%d", _list.get(position).getIndex() + 1));
            item.text_appname.setText(_list.get(position).getAppName());
            item.text_appintro.setText(_list.get(position).getAppVersion());
            item.img_url.setImageDrawable(_list.get(position).getDrawable());
            item.btn_get.setOnClickListener(new View.OnClickListener() 
            {
				
				@Override
				public void onClick(View v) 
				{
					
				}
				
			});
        }

        return convertView;
    }


    class MenuItem
    {
        TextView text_index, text_appname, text_appintro;
        Button btn_get;
        ImageView img_url;
    }


}
