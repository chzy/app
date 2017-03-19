package com.chd.photo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chd.photo.ui.PicActivity;
import com.chd.photo.ui.PicDetailActivity;
import com.chd.proto.FileInfo;
import com.chd.yunpan.R;
import com.chd.yunpan.utils.TimeUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class PicAdapter<E extends FileInfo> extends BaseQuickAdapter<List<E>, BaseViewHolder> {

	private Activity context;
	private List<List<E>> list;
	private boolean bIsUbkList;
	private ImageLoader imageLoader;


	public PicAdapter(PicActivity picActivity, List<List<E>> localList, boolean bIsUbkList, ImageLoader imageLoader) {
		super(R.layout.item_pic_adapter, localList);
		this.list = localList;
		this.bIsUbkList = bIsUbkList;
		this.context = picActivity;
		this.imageLoader = imageLoader;

	}

	private boolean showSelect;

	public void setShowSelect(boolean showSelect) {
		this.showSelect = showSelect;
		notifyDataSetChanged();
	}

	@Override
	protected void convert(final BaseViewHolder helper, final List<E> item) {
		if (item != null && item.size() > 0) {
			if (item.get(0) != null) {
				String start = TimeUtils.getDay(item.get(0).getLastModified());
				String end = TimeUtils.getDay(item.get(item.size() - 1).getLastModified());
				if (start.equals(end)) {
					helper.setText(R.id.tv_pic_date, start);
				} else {
					helper.setText(R.id.tv_pic_date, start + "至" + end);
				}
				RecyclerView recyclerView = helper.getView(R.id.mlv_pic);

				recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4, LinearLayoutManager.VERTICAL, false));
				final PicInfoAdapter<E> infoAdapter = new PicInfoAdapter<E>(item
						, imageLoader, showSelect);
				infoAdapter.setPosition(helper.getAdapterPosition());
				recyclerView.setAdapter(infoAdapter);
				recyclerView.setHasFixedSize(true);
				recyclerView.addOnScrollListener(new PauseOnScrollListener(imageLoader, true, true));
				recyclerView.addOnItemTouchListener(onItemClickListener);
			}

		}

	}


	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
			Log.d("liumj", "执行了几次");
			PicInfoAdapter<E> infoAdapter = ((PicInfoAdapter) adapter);
			if (showSelect) {
				//执行了几次
				String s = infoAdapter.getGroupPos() + " " + position;
				boolean contains = selectList.contains(s);
				if (contains) {
					selectList.remove(s);
				} else {
					selectList.add(s);
				}
				infoAdapter.changeItem(position, contains);
			} else {
				Intent intent = new Intent(context, PicDetailActivity.class);
				intent.putExtra("bean", infoAdapter.getItem(position));
				intent.putExtra("pos1", infoAdapter.getGroupPos());
				intent.putExtra("pos2", position);
				intent.putExtra("ubklist", bIsUbkList);
				context.startActivityForResult(intent, 0x12);
			}
		}
	};
	private ArrayList<String> selectList = new ArrayList<>();

	public ArrayList<String> getSelectData() {
		return this.selectList;
	}

	public void remove(int pos1, int pos2) {
		getItem(pos1).remove(pos2);
		notifyItemChanged(pos1);
	}

	public E getFileInfo(int pos1, int pos2) {
		return getItem(pos1).get(pos2);
	}


}
