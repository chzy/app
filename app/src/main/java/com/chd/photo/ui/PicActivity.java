package com.chd.photo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.UILActivity;
import com.chd.base.backend.SyncTask;
import com.chd.contacts.vcard.StringUtils;
import com.chd.photo.adapter.PicAdapter;
import com.chd.photo.entity.PicBean;
import com.chd.photo.entity.PicInfoBean;
import com.chd.photo.entity.PicInfoBeanMonth;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.utils.TimeUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PicActivity extends UILActivity implements OnClickListener {

	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	private TextView mTvNumber;
	private ListView mLvPic;
	private Map<Integer, Map<Integer, List<PicInfoBean>>> YearMap;
	private List<PicBean<PicInfoBeanMonth>> mPicList = new ArrayList();
	private boolean bIsUbkList;
	private List<FileInfo0> cloudUnits;
	private ImageLoader imageLoader;
	private SyncTask syncTask;
	private List<PicBean<PicInfoBeanMonth>> localList = new ArrayList();

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (bIsUbkList) {
				dismissDialog();
				dismissWaitDialog();
				Collections.sort(localList);
				findViewById(R.id.rl_pic_ubk_layout).setVisibility(View.GONE);
				mTvCenter.setText("未备份照片");
				mLvPic.setAdapter(new PicAdapter(PicActivity.this, localList, bIsUbkList,imageLoader));
			} else {
				dismissWaitDialog();
				dismissDialog();
				Collections.sort(mPicList);
				int size = 0;
				if (localList != null&&localList.size()>0) {
					for (PicBean<PicInfoBeanMonth> bean :
							localList) {
						size += bean.getList().getPicunits().size();
					}
				} else {
					size = filelistEntity.getUnbakNumber();
				}
				Log.d("liumj", "数量" + size);
				mTvNumber.setText(String.format("未备份照片%d张", size));
				mLvPic.setAdapter(new PicAdapter(PicActivity.this, mPicList, bIsUbkList,imageLoader));
			}
		}

	};
	private FilelistEntity filelistEntity;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pic);

		bIsUbkList = false;
		imageLoader=ImageLoader.getInstance();
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
					filelistEntity = syncTask.analyPhotoUnits(cloudUnits);
					cloudUnits.clear();
					cloudUnits = null;
					cloudUnits = filelistEntity.getBklist();
				}
				if(bIsUbkList){
					initLocal();
				}else{
					initData();
				}
			}
		});
		thread.start();
	}

	void add2YearMap(FileInfo0 info) {

		int year = TimeUtils.getYearWithTimeMillis(info.getLastModified() * 1000L);
		int month = TimeUtils.getMonthWithTimeMillis(info.getLastModified() * 1000L);
		Map<Integer, List<PicInfoBean>> tmpMonthMap;

		if (YearMap.containsKey(year)) {
			tmpMonthMap = YearMap.get(year);
		} else {
			tmpMonthMap = new HashMap();
		}
		//List list=tmpMonthMap.get(month);
//		boolean fillfrist = false;
		//if (list==null || list.isEmpty())
//		if (tmpMonthMap.isEmpty() || tmpMonthMap.containsKey(month) == false || tmpMonthMap.get(month).isEmpty()) {
//			fillfrist = true;
//		}
		PicInfoBean picInfoBean = new PicInfoBean();


		if (info.getSysid() > 0) {
			picInfoBean.setSysid(info.getSysid());
				String uri = info.getUri();
				picInfoBean.setUrl(uri);
				if (uri == null /*&& syncTask.haveLocalCopy(info)*/)
					picInfoBean.setUrl("file://" + info.getFilePath());

		} else {
				picInfoBean.setUrl("trpc://" + info.getObjid());
		}

		picInfoBean.setDay(TimeUtils.getTime(info.getLastModified() * 1000L, new SimpleDateFormat("MM月dd日")));

		if (tmpMonthMap.containsKey(month)) {
			tmpMonthMap.get(month).add(picInfoBean);
		} else {
			List<PicInfoBean> monthPicInfoBeans = new ArrayList<PicInfoBean>();
			monthPicInfoBeans.add(picInfoBean);
			tmpMonthMap.put(month, monthPicInfoBeans);
		}
		YearMap.put(year, tmpMonthMap);
	}

	private void initData() {

		//产生1 个 已备份的本地文件  list
		// 产生 1个 未备份的文件        list

		//本地已经备份的文件 list 对象名 FilesListEntity 从sqlite里面取出   可以调用 dbmanger.getUploadeFiles 得到本地已备份文件  backedlist

		// localfiles 调用系统方法得到本地所有文件库. 文件对象需要用 fileinfo0 类封装 构造后 要set filepath, size , 变成list .构造成 FilesListEntity locallist




		//未备份的列表  LIST<int>
		//网盘文件列表  LIST<FileInfo>   fileinfo.getfilepath() 得到本地资源路径  如果 getfilepath为 null 则需要下载


		//显示未备份文件

		if (filelistEntity != null) {
			if (YearMap == null)
				YearMap = new HashMap<>();
			for (FileInfo0 info0 : cloudUnits) {
				add2YearMap(info0);
			}
			mPicList = initData(mPicList);
			YearMap.clear();
		}
		//要有提示用户等待的画面
		handler.sendEmptyMessage(0);
	}


	private List<PicBean<PicInfoBeanMonth>> initData(List<PicBean<PicInfoBeanMonth>> data) {
		if (!YearMap.isEmpty()) {
			data.clear();
			for (Map.Entry<Integer, Map<Integer, List<PicInfoBean>>> entryYear : YearMap.entrySet()) {

				int midx = 0;
				int year = entryYear.getKey();
				for (Map.Entry<Integer, List<PicInfoBean>> entryMonth :
						entryYear.getValue().entrySet()) {
					PicInfoBeanMonth<PicInfoBean> monthInfoBean = new PicInfoBeanMonth();
					midx = entryMonth.getKey();
					monthInfoBean.setUrl(entryMonth.getValue().get(0).getUrl());//第一张图片
					monthInfoBean.setPicunits(entryMonth.getValue());
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

	private void initResourceId() {
		mTvNumber = (TextView) findViewById(R.id.tv_pic_number);
		mLvPic = (ListView) findViewById(R.id.lv_pic);

		mTvNumber.setText("未备份照片0张");
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mTvRight.setOnClickListener(this);
		mTvNumber.setOnClickListener(this);
		mLvPic.setOnScrollListener(new PauseOnScrollListener(imageLoader,true,true));
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
		localList.clear();
		new Thread() {
			@Override
			public void run() {
				List<FileLocal> locallist = filelistEntity.getLocallist();
				for (FileLocal fileLocal : locallist) {
					if (fileLocal.bakuped)
						continue;
					FileInfo0 info = syncTask.queryLocalInfo(fileLocal.sysid);

					if (info == null) {
						Log.d("PicActity", fileLocal.fname + " not found in mediaStore");
						continue;
					}
					if (StringUtils.isNullOrEmpty(info.getFilename())) {
						info.setFilename(fileLocal.fname);
					}
					add2YearMap(info);
				}
				localList = initData(localList);
				YearMap.clear();
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
					cloudUnits = null;
					localList.clear();
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

}
