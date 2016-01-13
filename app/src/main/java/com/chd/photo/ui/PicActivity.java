package com.chd.photo.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chd.MediaMgr.utils.MFileFilter;
import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.backend.SyncTask;
import com.chd.photo.adapter.PicAdapter;
import com.chd.photo.entity.PicBean;
import com.chd.photo.entity.PicInfoBean;
import com.chd.photo.entity.ThumUtil;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.utils.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PicActivity extends Activity implements OnClickListener {

	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	private TextView mTvNumber;
	private ListView mLvPic;

	private List<PicBean> mPicList = new ArrayList<PicBean>();
	private boolean bIsUbkList;

	private SyncTask syncTask=null;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
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
				SyncTask syncTask =new SyncTask(PicActivity.this, FTYPE.PICTURE);
				//未备份文件 ==  backedlist . removeAll(localist);

				final List<FileInfo0> cloudUnits=syncTask.getCloudUnits(0, 10);
				runOnUiThread(new Runnable() {
					public void run() {
						initData(cloudUnits);
					}
				});
			}
		});
		thread.start();
	}

	private void initData(List<FileInfo0> cloudUnits) {

		//产生1 个 已备份的本地文件  list
		// 产生 1个 未备份的文件        list

		//本地已经备份的文件 list 对象名 FilesListEntity 从sqlite里面取出   可以调用 dbmanger.getUploadeFiles 得到本地已备份文件  backedlist

		// localfiles 调用系统方法得到本地所有文件库. 文件对象需要用 fileinfo0 类封装 构造后 要set filepath, size , 变成list .构造成 FilesListEntity locallist
		if (syncTask==null)
			syncTask =new SyncTask(this, FTYPE.PICTURE);
		
		if (cloudUnits==null)
		{
			System.out.print("query remote failed");
		}
		//cloudUnits=syncTask.getCloudUnits(0, 100);
		FilelistEntity filelistEntity=syncTask.analyPhotoUnits(cloudUnits);
		cloudUnits.clear();
		cloudUnits=null;
		List<FileLocal> fileLocals= syncTask.getLocalUnits(0, 50);
		cloudUnits=filelistEntity.getBklist();
		//显示的时候过滤文件类型
		MFileFilter fileFilter=new MFileFilter();
		fileFilter.setCustomCategory(new String[]{"jpg"},true);

		//批量加载下载任务 调用 dbmnager addUpLoadingFile 方法

		//实时上传 需要 在当前 Activity 里面  增加一个 public的 updateProgress 函数 将该 Activity 传入后台的 Activity .线程里调用  setProcess 显示进度 . .
		//调用 SyncTask 里面的 upload

		//LoadLoacalPhotoCursorTask  loadLoacalPhotoCursorTask =new LoadLoacalPhotoCursorTask(this);
		//loadLoacalPhotoCursorTask.execute();



		//未备份的列表  LIST<int>
		//网盘文件列表  LIST<FileInfo>   fileinfo.getfilepath() 得到本地资源路径  如果 getfilepath为 null 则需要下载

		List<Integer> idList = new ArrayList<Integer>();
		List<String> objList = new ArrayList<String>();
		Map<Integer, FileInfo0> tmpFileMap = new HashMap<Integer, FileInfo0>();
		if (bIsUbkList)
		{
			for (FileLocal fileLocal : fileLocals)
			{
				idList.add(fileLocal.sysid);
			}
		}
		else
		{
			for(FileInfo0 item:cloudUnits)
			{
				item.setFilePath(new ShareUtils(this).getStorePathStr()+ File.separator+item.getObjid());
				//syncTask.download(item,null,false);
				//FileInfo0 item=new FileInfo0(finfo);
				if(!fileFilter.contains(item.getObjid()))
					continue;
				//已备份文件
				idList.add(item.getSysid());
				objList.add(item.getObjid());
				FileInfo0 tmpFileInfo0 = syncTask.GetLocalCopy(item);
				if (tmpFileInfo0 != null)
				{
					tmpFileMap.put(item.getSysid(), tmpFileInfo0);
				}
			}
		}

		//显示未备份文件

		if (filelistEntity != null) {
			if (bIsUbkList) {
				findViewById(R.id.rl_pic_ubk_layout).setVisibility(View.GONE);
				mTvCenter.setText("未备份照片");
			} else {
				if (filelistEntity.getLocallist() != null) {
					mTvNumber.setText(String.format("未备份照片%d张", filelistEntity.getLocallist().size()));
				}
			}

			if (idList != null && idList.size() > 0) {
				Map<Integer, Map<Integer, List<PicInfoBean>>> tmpYearMap = new HashMap<Integer, Map<Integer, List<PicInfoBean>>>();
				FileInfo0 info = null;
				for (int i = 0; i < idList.size(); i++) {
					info = tmpFileMap.get(idList.get(i));
					if (info == null)
					{
						info = bIsUbkList ? syncTask.queryLocalInfo(idList.get(i)) : syncTask.getUnitinfo(i);    /*getUnitinfo(id)*/;
						info.setFilePath(bIsUbkList ? info.getFilePath() : ThumUtil.getThumid(objList.get(i)));
					}

					int year = TimeUtils.getYearWithTimeMillis(info.getLastModified() * 1000L);
					int month = TimeUtils.getMonthWithTimeMillis(info.getLastModified() * 1000L);

					Map<Integer, List<PicInfoBean>> tmpMonthMap = new HashMap<Integer, List<PicInfoBean>>();
					if (tmpYearMap.get(year) != null) {
						tmpMonthMap = tmpYearMap.get(year);
					}

					PicInfoBean picInfoBean = new PicInfoBean();
					picInfoBean.setPicpath(info.getFilePath());
					picInfoBean.setMonth(month);

					if (tmpMonthMap.get(month) != null) {
						tmpMonthMap.get(month).add(picInfoBean);
					} else {
						List<PicInfoBean> monthPicInfoBeans = new ArrayList<PicInfoBean>();
						monthPicInfoBeans.add(picInfoBean);
						tmpMonthMap.put(month, monthPicInfoBeans);
						tmpYearMap.put(year, tmpMonthMap);
					}
				}

				if (tmpYearMap.size() > 0) {
					for (Map.Entry<Integer, Map<Integer, List<PicInfoBean>>> entryYear : tmpYearMap.entrySet()) {
						List<PicInfoBean> monthInfoBeanList = new ArrayList<PicInfoBean>();
						for (Map.Entry<Integer, List<PicInfoBean>> entryMonth : entryYear.getValue().entrySet()) {
							PicInfoBean monthInfoBean = new PicInfoBean();
							monthInfoBean.setMonth(entryMonth.getKey());
							monthInfoBean.setPicpath(entryMonth.getValue().get(0).getPicpath());
							monthInfoBean.setNumber(String.valueOf(entryMonth.getValue().size()));
							monthInfoBean.setPicUrl(R.drawable.pic_test1);
							monthInfoBeanList.add(monthInfoBean);
						}
						if (monthInfoBeanList.size() > 0) {
							PicBean picBean = new PicBean(String.valueOf(entryYear.getKey()), monthInfoBeanList);
							mPicList.add(picBean);
						}
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
