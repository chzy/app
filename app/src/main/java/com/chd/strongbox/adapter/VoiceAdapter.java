package com.chd.strongbox.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chd.strongbox.domain.VoiceEntity;
import com.chd.yunpan.R;

import java.util.List;

/**
 * User: Liumj(liumengjie@365tang.cn)
 * Date: 2017-03-03
 * Time: 14:19
 * describe:
 */
public class VoiceAdapter extends BaseQuickAdapter<VoiceEntity,BaseViewHolder>{
	public VoiceAdapter(List<VoiceEntity> data) {
		super(R.layout.item_voice,data);
	}

	@Override
	protected void convert(BaseViewHolder helper, VoiceEntity item) {
		helper.setText(R.id.tv_item_voice_time,item.getTime());
		helper.setText(R.id.tv_item_voice_name,item.getTitle());
		helper.setText(R.id.tv_item_voice_date,item.getDate());
		helper.setText(R.id.tv_item_voice_duration,item.getDuration());
	}
}
