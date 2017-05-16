package com.chd.yunpan.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.yunpan.R;
import com.chd.yunpan.ui.entity.MySpaceBean;

/**
 * Created by lxp1 on 2015/10/24.
 */
public class MenuGridAdapter extends BaseAdapter {
    private Context mContext; // 程序的上下文
    private List<MySpaceBean> _list; // 菜单信息

    public  MenuGridAdapter(Context context, List<MySpaceBean> list)
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
            convertView = View.inflate(mContext, R.layout.myspace_griditem, null);
            item.menu_name = (TextView) convertView.findViewById(R.id.myspace_grid_item_text);
            item.menu_img = (ImageView) convertView.findViewById(R.id.myspace_grid_item_img);
            convertView.setTag(item);
		} else {
			item = (MenuItem) convertView.getTag();
		}

        if (_list.get(position)!=null)
        {
            item.menu_img.setImageResource(_list.get(position).getPicurl());
            item.menu_name.setText(_list.get(position).getText());
        }

        return convertView;
    }


    class MenuItem
    {
        TextView menu_name;
        ImageView menu_img;
    }


}
