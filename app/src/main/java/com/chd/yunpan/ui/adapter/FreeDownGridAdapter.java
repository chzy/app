package com.chd.yunpan.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.app.backend.AppInfo0;
import com.chd.yunpan.R;

public class FreeDownGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<AppInfo0> _list;

    public  FreeDownGridAdapter(Context context, List<AppInfo0> list)
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
            convertView = View.inflate(mContext, R.layout.freedown_griditem, null);
            item.menu_name = (TextView) convertView.findViewById(R.id.freedown_grid_item_text);
            item.menu_img = (ImageView) convertView.findViewById(R.id.freedown_grid_item_img);
            convertView.setTag(item);
		} else {
			item = (MenuItem) convertView.getTag();
		}

        if (_list.get(position)!=null)
        {
            item.menu_img.setImageDrawable(_list.get(position).getDrawable());
            item.menu_name.setText(_list.get(position).getAppName());
        }

        return convertView;
    }


    class MenuItem
    {
        TextView menu_name;
        ImageView menu_img;
    }


}
