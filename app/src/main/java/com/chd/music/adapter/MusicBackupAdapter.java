package com.chd.music.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.contacts.vcard.StringUtils;
import com.chd.music.backend.MediaUtil;
import com.chd.music.entity.MusicBackupBean;
import com.chd.photo.adapter.RoundImageView;
import com.chd.yunpan.R;

import java.util.List;

public class MusicBackupAdapter extends BaseAdapter {

	private Context context;
	private List<MusicBackupBean> mMusiclist;

	public MusicBackupAdapter(Context context, List<MusicBackupBean> list) {
		this.context = context;
		this.mMusiclist = list;
	}

	@Override
	public int getCount() {
		return mMusiclist.size();
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
			converView = View.inflate(context, R.layout.item_music_backup_adapter, null);
			holder = new ViewHolder();
			holder.gv_check = (ImageView) converView.findViewById(R.id.gv_music_backup_item_check);
			holder.gv_pic = (RoundImageView) converView.findViewById(R.id.gv_music_backup_item_pic);
			holder.gv_title = (TextView) converView.findViewById(R.id.gv_music_backup_item_txt);
			converView.setTag(holder);
		} else {
			holder = (ViewHolder) converView.getTag();
		}
		MusicBackupBean bean = mMusiclist.get(position);
		holder.gv_title.setText(StringUtils.isNullStr(bean.getTitle()));
		holder.gv_check.setImageResource(bean.isSelect() ? R.drawable.pic_edit_photo_checked : R.drawable.pic_edit_photo_group_check);
		String albumArt=null;
		try {
			albumArt = MediaUtil.getAlbumArt(context, bean.getPic());
		}catch (Exception e){
			albumArt="";
		}
		if (StringUtils.isNullOrEmpty(albumArt)) {
			holder.gv_pic.setImageResource(R.drawable.pic_test1);
		} else {
			Bitmap bm = BitmapFactory.decodeFile(albumArt);
			BitmapDrawable bmpDraw = new BitmapDrawable(bm);
			holder.gv_pic.setImageDrawable(bmpDraw);
		}
		return converView;
	}

	private class ViewHolder {
		ImageView gv_check;
		RoundImageView gv_pic;
		TextView gv_title;
	}



}
