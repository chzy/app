package com.chd.photo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.UILActivity;
import com.chd.base.backend.SyncTask;
import com.chd.photo.adapter.PicAdapter;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.List;


public class PicActivity extends UILActivity implements OnClickListener {

	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	private TextView mTvNumber;
	private RecyclerView mLvPic;
	private boolean bIsUbkList;
	private List<FileInfo> cloudUnits;  //云端文件
	private List<FileLocal> localUnits; //未备份的本地文件
	private ImageLoader imageLoader;
	private SyncTask syncTask;
	//private List<PicBean<PicInfoBeanMonth>> localList = new ArrayList();
	private FilelistEntity filelistEntity;

	private Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(Message msg) {

			if (bIsUbkList) {

				dismissDialog();
				dismissWaitDialog();
				//Collections.sort(localList);
				findViewById(R.id.rl_pic_ubk_layout).setVisibility(View.GONE);
				mTvCenter.setText("未备份照片");

				mLvPic.setAdapter(new PicAdapter(PicActivity.this, localUnits, bIsUbkList, imageLoader));
			} else {

				dismissWaitDialog();
				dismissDialog();
				//Collections.sort(mPicList);
				int size = 0;
				size = filelistEntity.getUnbakNumber();
				Log.d("liumj", "数量" + size);
				mTvNumber.setText(String.format("未备份照片%d张", size));
				mLvPic.setAdapter(new PicAdapter(PicActivity.this, cloudUnits, bIsUbkList, imageLoader));
			}
		}

	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pic);

		bIsUbkList = false;
		imageLoader = ImageLoader.getInstance();
		initTitle();
		initResourceId();
		initListener();
		onNewThreadRequest(false);
	}

	private void onNewThreadRequest(final boolean bIsUbkList) {

		showWaitDialog();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				filelistEntity = UILApplication.getFilelistEntity();
				if (syncTask == null)
					syncTask = new SyncTask(PicActivity.this, FTYPE.PICTURE);
				//未备份文件 ==  backedlist . removeAll(localist);
				if (cloudUnits == null || cloudUnits.isEmpty()) {
					// 0-100 分批取文件
					cloudUnits = syncTask.getCloudUnits(0, 10000);
					if (cloudUnits == null) {
						System.out.print("query cloudUnits remote failed");
						return;
					}
					syncTask.analyPhotoUnits(cloudUnits, filelistEntity);

//					cloudUnits = filelistEntity.getBklist();
					localUnits = filelistEntity.getLocallist();

				}
				if (bIsUbkList) {
					initLocal();
				} else {
					initData();
				}
			}
		});
		thread.start();
	}

	/*void add2YearMap(FileInfo01 info) {

		int year = TimeUtils.getYearWithTimeMillis(info.getLastModified() * 1000L);
		int month = TimeUtils.getMonthWithTimeMillis(info.getLastModified() * 1000L);
		Map<Integer, List<PicInfoBean>> tmpMonthMap;

		if (YearMap.containsKey(year)) {
			tmpMonthMap = YearMap.get(year);
		} else {
			tmpMonthMap = new HashMap();
		}

		PicInfoBean picInfoBean = new PicInfoBean();


		if (info.getSysid() > 0) {
			picInfoBean.setSysid(info.getSysid());
			String uri = info.getUri();
			picInfoBean.setUrl(uri);
			if (uri == null *//*&& syncTask.haveLocalCopy(info)*//*)
				picInfoBean.setUrl("file://" + info.getFilePath());

		} else {
			picInfoBean.setUrl("ttrpc://" + info.getObjid());
		}
		picInfoBean.setTimeStamp(info.getLastModified());
		picInfoBean.setDay(TimeUtils.getTime(info.getLastModified() * 1000L, new SimpleDateFormat("MM月dd日")));

		if (tmpMonthMap.containsKey(month)) {
			tmpMonthMap.get(month).add(picInfoBean);
		} else {
			List<PicInfoBean> monthPicInfoBeans = new ArrayList<PicInfoBean>();
			monthPicInfoBeans.add(picInfoBean);
			tmpMonthMap.put(month, monthPicInfoBeans);
		}
		YearMap.put(year, tmpMonthMap);
	}*/


	private void initData() {

		if (filelistEntity != null) {
/*
			if (YearMap == null)
				YearMap = new HashMap<>();
			for (FileInfo info : cloudUnits) {
				add2YearMap(info);
			}
*/


			//mPicList = initData(mPicList);
			//YearMap.clear();
		}
		//要有提示用户等待的画面
		handler.sendEmptyMessage(0);
	}


	/*private List<PicBean<PicInfoBeanMonth>> initData(List<PicBean<PicInfoBeanMonth>> data) {
		if (!YearMap.isEmpty()) {
			data.clear();
			for (Map.Entry<Integer, Map<Integer, List<PicInfoBean>>> entryYear : YearMap.entrySet()) {

				int midx = 0;
				int year = entryYear.getKey();
				for (Map.Entry<Integer, List<PicInfoBean>> entryMonth :
						entryYear.getValue().entrySet()) {
					PicInfoBeanMonth<PicInfoBean> monthInfoBean = new PicInfoBeanMonth();
					midx = entryMonth.getKey();
					List<PicInfoBean> value = entryMonth.getValue();
					//Collections.sort(value);
					int index = value.size() - 1;
					monthInfoBean.setUrl(value.get(index).getUrl());//第一张图片
					monthInfoBean.setPicunits(value);
					if (!monthInfoBean.getPicunits().isEmpty()) {
						PicBean<PicInfoBeanMonth> picBean = new PicBean(String.valueOf(year), monthInfoBean);
						picBean.setMonth(midx);
						data.add(picBean);
					}
				}
			}
		}
		return data;

	}
*/
	private void initResourceId() {
		mTvNumber = (TextView) findViewById(R.id.tv_pic_number);
		mLvPic = (RecyclerView) findViewById(R.id.lv_pic);
		mTvNumber.setText("未备份照片0张");
		mLvPic.setLayoutManager(new LinearLayoutManager(this));
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mTvRight.setOnClickListener(this);
		mTvNumber.setOnClickListener(this);
		mLvPic.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true));
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
		mTvRight = (TextView) findViewById(R.id.tv_right);

		mTvCenter.setText("照片");
//		mTvRight.setText("编辑");
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.iv_left:
				finish();
				break;
			case R.id.tv_right: // 编辑
				break;
			case R.id.tv_pic_number:
				initLocal();
				break;
		}
	}

	private void initLocal() {
		waitDialog.show();
		new Thread() {
			@Override
			public void run() {
				List<FileLocal> locallist = filelistEntity.getLocallist();
				for (FileLocal fileLocal : locallist) {
					//if (fileLocal.getSysid()>0)
					//	continue;
//				/*	FileInfo0 info = syncTask.queryLocalInfo(fileLocal.getSysid());
					//filelistEntity.
//					if (info == null) {
//						Log.d("PicActity", fileLocal.getSysid() + " not found in mediaStore");
//						continue;
//					}
//					if (StringUtils.isNullOrEmpty(info.getFilename())) {
//						info.setFilename(fileLocal.getFilename());
//					}*/
					//add2YearMap(info);
				}
				//localList = initData(localList);
				//YearMap.clear();
				bIsUbkList = true;
				handler.sendEmptyMessage(0);
			}
		}.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
				case 0x11:
					Log.i("lmj", "返回执行了");
					onNewThreadRequest(bIsUbkList);
					break;
			}
		}


	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onStop() {
		super.onStop();
	}


	/*private class ComparatorByDate implements Comparator {

		public int compare(Object arg0, Object arg1) {
			FileInfo  item0=(FileInfo) arg0;
			FileInfo item1=(FileInfo) arg1;
			int flag=item0.getLastModified()-item1.getLastModified();
			return flag;
		}
	}*/
}
