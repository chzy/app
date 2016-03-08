package com.chd.notepad.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.chd.yunpan.R;
import com.chd.yunpan.utils.Base64Utils;
import com.chd.yunpan.view.NineGridAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * @description
 * @FileName: com.chd.notepad.ui.adapter.NineAdapter
 * @author: liumj
 * @date:2016-02-04 21:05
 * OS:Mac 10.10
 * Developer Kits:AndroidStudio 1.5
 */

public class NineAdapter extends NineGridAdapter {
        private List<String> list;
        public NineAdapter(Context context, List<String> list) {
            super(context, list);

            this.list = list;
        }

        @Override
        public int getCount() {
            return (list == null) ? 0 : list.size();
        }

        @Override
        public String getUrl(int position) {
            return null;
        }

        @Override
        public Object getItem(int position) {
            return (list == null) ? null : list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View view) {
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = View.inflate(context, R.layout.item_gridview, null);
                viewHolder.img = (ImageView) view.findViewById(R.id.iv_gv_item);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            if (list.get(i).equals("assets://add_photo.png")) {
                if (i == 5) {
                    viewHolder.img.setVisibility(View.GONE);
                } else {
                    viewHolder.img.setVisibility(View.VISIBLE);
                    Picasso.with(context)
                            .load(R.drawable.add_photo)
                            .resize(150, 150)
                            .into(viewHolder.img);
                }
            } else {
                if(list.get(i).startsWith("file")){
                    viewHolder.img.setVisibility(View.VISIBLE);
                    Picasso.with(context)
                            .load((String) list.get(i))
                            .resize(150, 150)
                            .into(viewHolder.img);
                }else{
                    Bitmap bitmap = Base64Utils.base64ToBitmap(list.get(i));
                    viewHolder.img.setImageBitmap(bitmap);
                }
            }

            return view;
        }

        public final class ViewHolder {
            ImageView img;
        }

}
