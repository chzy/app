package com.chd.strongbox.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chd.yunpan.R;

import java.util.HashMap;
import java.util.List;

/**
 * User: Liumj(liumengjie@365tang.cn)
 * Date: 2017-03-03
 * Time: 14:19
 * describe:
 */
public class VoiceAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> {

    public VoiceAdapter(List<HashMap<String, String>> cloudHashMap) {
        super(R.layout.item_voice, cloudHashMap);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, String> item) {
        if(item.containsKey("time")){
        helper.setText(R.id.tv_item_voice_date, item.get("time"));
        }
        if(item.containsKey("title")){
        helper.setText(R.id.tv_item_voice_name, item.get("title"));}
        if(item.containsKey("duration")){
        helper.setText(R.id.tv_item_voice_duration, item.get("duration"));
        }
    }
}
