package com.chd.photo.ui;

import android.app.Activity;
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
import com.chd.base.backend.SyncTask;
import com.chd.photo.adapter.PicAdapter;
import com.chd.photo.entity.PicBean;
import com.chd.photo.entity.PicInfoBean;
import com.chd.photo.entity.PicInfoBeanMonth;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PicActivity extends Activity implements OnClickListener {

	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	private TextView mTvNumber;
	private ListView mLvPic;
	private  Map<Integer, Map<Integer, List<PicInfoBean>>> YearMap ;
	private List<PicBean<PicInfoBeanMonth>> mPicList = new ArrayList();
	private boolean bIsUbkList;
	private  List<FileInfo0> cloudUnits;
	private SyncTask syncTask;
	private final String TAG=this.getClass().getName();

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Collections.sort(mPicList);
			mLvPic.setAdapter(new PicAdapter(PicActivity.this, mPicList, bIsUbkList));
		}

	};
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	//private GoogleApiClient client;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pic);

		bIsUbkList = false;

		initTitle();
		initResourceId();
		initListener();
		onNewThreadRequest();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		//client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}
	
	private void onNewThreadRequest()
	{

		Thread thread = new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
			
				if (syncTask==null)
					syncTask =new SyncTask(PicActivity.this, FTYPE.PICTURE);
				//未备份文件 ==  backedlist . removeAll(localist);
				if (cloudUnits==null || cloudUnits.isEmpty())
					// 0-100 分批取文件
					cloudUnits=syncTask.getCloudUnits(0, 100);
				runOnUiThread(new Runnable() {
					public void run() {
						initData();
					}
				});
			}
		});
		thread.start();
	}

	void add2YearMap(FileInfo0 info)
	{
		int year = TimeUtils.getYearWithTimeMillis(info.getLastModified() * 1000L);
		int month = TimeUtils.getMonthWithTimeMillis(info.getLastModified() * 1000L);
		Map<Integer, List<PicInfoBean>> tmpMonthMap;

		if (YearMap.get(year) != null) {
			tmpMonthMap = YearMap.get(year);
		}
		else
		{
			 tmpMonthMap = new HashMap<Integer, List<PicInfoBean>>();
			 YearMap.put(year,tmpMonthMap);
		}

		PicInfoBean picInfoBean = new PicInfoBean();

		//TODO 调用此方法导致本地文件替换远程文件  文件时间显示出错
//		if (info.getSysid()>0) {
//			if (!syncTask.haveLocalCopy(info)) {
//				Log.e(TAG, "unknow file sysid:" + info.getSysid());
//				return;
//			}
//		}
		String uri=info.getUri();
		if (uri==null)
		{
			Log.e(TAG, "unknow file sysid:" + info.getSysid());
			return;
		}
		picInfoBean.setUrl(uri);
		picInfoBean.setDay(TimeUtils.getTime(info.getLastModified()* 1000L, new SimpleDateFormat("MM月dd日")));

		if (tmpMonthMap==null)
			tmpMonthMap = new HashMap<Integer, List<PicInfoBean>>();
		if (tmpMonthMap.get(month) != null) {
			tmpMonthMap.get(month).add(picInfoBean);
		} else {
			List<PicInfoBean> monthPicInfoBeans = new ArrayList<PicInfoBean>();
			monthPicInfoBeans.add(picInfoBean);
			tmpMonthMap.put(month, monthPicInfoBeans);
			YearMap.put(year, tmpMonthMap);
		}

	}

	private void initData() {

		//产生1 个 已备份的本地文件  list
		// 产生 1个 未备份的文件        list

		//本地已经备份的文件 list 对象名 FilesListEntity 从sqlite里面取出   可以调用 dbmanger.getUploadeFiles 得到本地已备份文件  backedlist

		// localfiles 调用系统方法得到本地所有文件库. 文件对象需要用 fileinfo0 类封装 构造后 要set filepath, size , 变成list .构造成 FilesListEntity locallist
	
		
		if (cloudUnits==null)
		{
			System.out.print("query remote failed");
		}
		FilelistEntity filelistEntity=syncTask.analyPhotoUnits(cloudUnits);
		cloudUnits.clear();
		cloudUnits=null;
		List<FileLocal> fileLocals=filelistEntity.getLocallist();
		cloudUnits=filelistEntity.getBklist();


		//未备份的列表  LIST<int>
		//网盘文件列表  LIST<FileInfo>   fileinfo.getfilepath() 得到本地资源路径  如果 getfilepath为 null 则需要下载


		//显示未备份文件

		if (filelistEntity != null) {
			YearMap=new HashMap<Integer, Map<Integer, List<PicInfoBean>>>();
			if (bIsUbkList) {
				findViewById(R.id.rl_pic_ubk_layout).setVisibility(View.GONE);
				mTvCenter.setText("未备份照片");
			} else {
				if (filelistEntity.getLocallist() != null) {
					mTvNumber.setText(String.format("未备份照片%d张", filelistEntity.getUnbakNumber()));
				}
			}

			Map<Integer, List<PicInfoBean>> tmpMonthMap=null;

			if (bIsUbkList) {

				for (FileLocal fileLocal : fileLocals)
				{
					if (fileLocal.bakuped)
						continue;
					FileInfo0 	info = syncTask.queryLocalInfo(fileLocal.sysid);    /*getUnitinfo(id)*/
					if (info==null)
					{
						Log.d("PicActity", fileLocal.fname + " not found in mediaStore");
						continue;
					}
					add2YearMap(info);
				}
			}
			else
			{
				for (FileInfo0 info0:cloudUnits)
				{
					add2YearMap(info0);
				}
			}

				if (!YearMap.isEmpty()) {
					mPicList.clear();
					for (Map.Entry<Integer, Map<Integer, List<PicInfoBean>>> entryYear : YearMap.entrySet()) {
						PicInfoBeanMonth<PicInfoBean> monthInfoBean = new PicInfoBeanMonth();
						int midx=0;
						for (Map.Entry<Integer, List<PicInfoBean>> entryMonth : entryYear.getValue().entrySet()) {
							midx=entryMonth.getKey();
							monthInfoBean.setUrl(entryMonth.getValue().get(0).getUrl());//第一张图片
							monthInfoBean.setPicunits(entryMonth.getValue());
						}
						if (monthInfoBean.getPicunits().isEmpty()==false) {
							PicBean<PicInfoBeanMonth> picBean = new PicBean(String.valueOf(entryYear.getKey()), monthInfoBean);
							picBean.setMonth(midx);
							mPicList.add(picBean);
						}
					}
				}
			}
		//要有提示用户等待的画面
		handler.sendEmptyMessage(0);
	}

	private void initResourceId() {
		mTvNumber = (TextView) findViewById(R.id.tv_pic_number);
		mLvPic = (ListView) findViewById(R.id.lv_pic);

		mTvNumber.setText("未备份照片0张");
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
		//mTvRight.setOnClickListener(this);
		mTvNumber.setOnClickListener(this);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
		mTvRight = (TextView) findViewById(R.id.tv_right);

		mTvCenter.setText("照片");
		//mTvRight.setText("编辑");
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
				bIsUbkList = true;
				mPicList.clear();
				onNewThreadRequest();
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(RESULT_OK==resultCode){
			switch (requestCode){
				case 0x11:
					onNewThreadRequest();
					break;
			}
		}



	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		/*client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Pic Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.chd.photo.ui/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);*/
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		/*Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Pic Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.chd.photo.ui/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();*/
	}
}
