package com.chd.other.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.proto.FileInfo;
import com.chd.yunpan.R;

import java.util.List;

public class OtherListAdapter extends BaseAdapter {
    private Context mContext;
    private List<FileInfo> _list;

    public  OtherListAdapter(Context context, List<FileInfo> list)
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
            convertView = View.inflate(mContext, R.layout.other_listitem, null);
            item.text_appname = (TextView) convertView.findViewById(R.id.other_list_item_appname);
            item.text_appintro = (TextView) convertView.findViewById(R.id.other_list_item_appintro);
            item.img_url = (ImageView) convertView.findViewById(R.id.other_list_item_img);
            item.text_appsize = (TextView) convertView.findViewById(R.id.other_list_item_appsize);
            convertView.setTag(item);
		} else {
			item = (MenuItem) convertView.getTag();
		}

        if (_list.get(position)!=null)
        {
            item.text_appname.setText(_list.get(position).getObjid());
           /* item.text_appintro.setText(_list.get(position).());
            item.img_url.setImageResource(_list.get(position).getPicid());
            item.text_appsize.setText(_list.get(position).getFilesize());*/
        }

        return convertView;
    }


    class MenuItem
    {
        TextView text_appname, text_appintro, text_appsize;
        ImageView img_url;
    }


}
