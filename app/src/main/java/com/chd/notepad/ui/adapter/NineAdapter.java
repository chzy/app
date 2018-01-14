package com.chd.notepad.ui.adapter;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chd.yunpan.GlideApp;
import com.chd.yunpan.R;
import com.chd.yunpan.utils.Base64Utils;

import java.util.List;

/**
 * @description
 * @FileName: com.chd.notepad.ui.adapter.NineAdapter
 * @author: liumj
 * @date:2016-02-04 21:05
 * OS:Mac 10.10
 * Developer Kits:AndroidStudio 1.5
 */

public class NineAdapter extends BaseQuickAdapter<String,BaseViewHolder> {
        public NineAdapter( List<String> list) {
            super(R.layout.item_gridview, list);
        }


    @Override
    protected void convert(BaseViewHolder helper, String item) {
        if(item==null){
            return ;
        }
        if ("assets://add_photo.png".equals(item)){
            if (helper.getAdapterPosition() == 5) {
                helper.setVisible(R.id.iv_gv_item,false);
            } else {
                helper.setVisible(R.id.iv_gv_item,true);
                GlideApp.with(mContext)
                        .load(R.drawable.add_photo)
                        .into((ImageView) helper.getView(R.id.iv_gv_item));
            }
        } else {
            if(item.startsWith("file")){
                helper.setVisible(R.id.iv_gv_item,true);
                GlideApp.with(mContext)
                        .load(item)
                        .into((ImageView) helper.getView(R.id.iv_gv_item));
            }else{
                Bitmap bitmap = Base64Utils.base64ToBitmap(item);
                ( (ImageView) helper.getView(R.id.iv_gv_item)).setImageBitmap(bitmap);
            }
        }
    }


}
