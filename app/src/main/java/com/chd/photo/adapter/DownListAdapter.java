package com.chd.photo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chd.yunpan.R;

/**
 * @description
 * @FileName: com.chd.photo.adapter.DownListAdapter
 * @author: liumj
 * @date:2016-01-20 20:22
 * OS:Mac 10.10
 * Developer Kits:AndroidStudio 1.5
 */

public class DownListAdapter extends BaseAdapter{



    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHodler vh=null;
        Context context=viewGroup.getContext();
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.item_download,viewGroup,false);
            vh=new ViewHodler(view);
            view.setTag(vh);
        }else{
            vh= (ViewHodler) view.getTag();
        }






        return view;
    }

   public class ViewHodler{
        TextView item_title;
        TextView item_status;
        ImageView item_icon;
        SeekBar  item_seekbar;
       public ViewHodler(View itemView){
           item_title= (TextView) itemView.findViewById(R.id.item_download_title);
           item_status= (TextView) itemView.findViewById(R.id.item_download_status);
           item_icon= (ImageView) itemView.findViewById(R.id.item_download_icon);
           item_seekbar= (SeekBar) itemView.findViewById(R.id.item_download_seekbar);
       }


    }

}
