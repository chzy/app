package com.chd.photo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chd.photo.ui.PicActivity;
import com.chd.photo.ui.PicEditActivity;
import com.chd.proto.FileInfo;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.utils.TimeUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.List;


public class PicAdapter<E extends FileInfo> extends BaseQuickAdapter<E ,BaseViewHolder>{

	private Activity context;
	private List<E> list;
	private boolean bIsUbkList;
	private ImageLoader imageLoader;


	public PicAdapter(PicActivity picActivity, List<E> localList, boolean bIsUbkList, ImageLoader imageLoader) {
		super(R.layout.item_pic_adapter,localList);
		this.list=localList;
		this.bIsUbkList=bIsUbkList;
		this.context=picActivity;
		this.imageLoader=imageLoader;

	}

	@Override
	protected void convert(BaseViewHolder helper, E item) {
		Integer[] iddByDate = UILApplication.getFilelistEntity().getIddByDate(helper.getAdapterPosition(), list, 3);
		String start = TimeUtils.getDay(list.get(iddByDate[0]).getLastModified());
		String end=TimeUtils.getDay(list.get(iddByDate[1]).getLastModified());
		helper.setText(R.id.tv_pic_date,start+"至"+end);
		RecyclerView recyclerView=helper.getView(R.id.mlv_pic);
		recyclerView.setLayoutManager(new GridLayoutManager(mContext,5, LinearLayoutManager.VERTICAL,false));
		recyclerView.setAdapter(new PicInfoAdapter(list.subList(iddByDate[0],iddByDate[1])
				,imageLoader));
		recyclerView.addOnScrollListener(new PauseOnScrollListener(imageLoader,true,true));
		recyclerView.addOnItemTouchListener(new OnItemClickListener() {
			@Override
			public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
				Intent intent = new Intent(context, PicEditActivity.class);
				intent.putExtra("month", TimeUtils.getMonthWithTimeMillis(list.get(position).getLastModified())/*getMonth()*/);
				intent.putExtra("year", TimeUtils.getYearWithTimeMillis(list.get(position).getLastModified()));
				//List<PicInfoBean> mlist=list.get(position).getList().getPicunits();
				//intent.putExtra("listUnits", (Serializable) (mlist));//LIST<PicInfoBean>
				intent.putExtra("ubklist", bIsUbkList);
				context.startActivityForResult(intent,0x11);
			}
		});

	}
}
