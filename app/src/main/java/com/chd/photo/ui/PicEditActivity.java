package com.chd.photo.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chd.MediaMgr.utils.MFileFilter;
import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.Ui.ActiveProcess;
import com.chd.base.backend.SyncTask;
import com.chd.photo.adapter.PicEditAdapter;
import com.chd.photo.entity.PicEditBean;
import com.chd.photo.entity.PicEditItemBean;
import com.chd.photo.entity.ThumUtil;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PicEditActivity extends ActiveProcess implements OnClickListener
{
	
	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	private ListView mLvPic;
	private View mLvPicEditDel;
	private View mLvPicEditUpDown;
	private View mLvPicEditView;

	private int month, year;
	private List<PicEditBean> mPicList = new ArrayList<PicEditBean>();
	private PicEditAdapter picEditAdapter;
	private boolean bIsUbkList;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (picEditAdapter == null)
			{
				picEditAdapter = new PicEditAdapter(PicEditActivity.this, mPicList);
				mLvPic.setAdapter(picEditAdapter);
			}
			else
			{
				picEditAdapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_pic_edit);
		
		// 模拟数据
		bIsUbkList = getIntent().getBooleanExtra("ubklist", false);
		month = getIntent().getIntExtra("month", -1);
		String sYear = getIntent().getStringExtra("year");
		if (sYear != null && sYear.length() > 0)
		{
			year = Integer.valueOf(sYear);
		}
		
		initTitle();
		initResourceId();
		initListener();
		onNewThreadRequest();
	}
	
	private void onNewThreadRequest()
	{

		Thread thread = new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				SyncTask syncTask =new SyncTask(PicEditActivity.this, FTYPE.PICTURE);
				//未备份文件 ==  backedlist . removeAll(localist);

				final List<FileInfo0> cloudUnits=syncTask.getCloudUnits(0, bIsUbkList ? 10 : 1000);
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
		SyncTask syncTask =new SyncTask(this, FTYPE.PICTURE);
		
		if (cloudUnits==null)
		{
			System.out.print("query remote failed");
		}

		FilelistEntity filelistEntity=syncTask.analyPhotoUnits(cloudUnits);
		cloudUnits.clear();
		cloudUnits=null;
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
		List<FileLocal> localList= syncTask.getLocalUnits(0, 50);
		Map<Integer, FileInfo0> tmpFileMap = new HashMap<Integer, FileInfo0>();
		if (bIsUbkList)
		{
			for (FileLocal fileLocal : localList)
			{
				idList.add(fileLocal.sysid);
			}
		}
		else
		{
			for(FileInfo0 item:cloudUnits)
			{
				//FileInfo0 item=new FileInfo0(finfo);
				if(!fileFilter.contains(item.getObjid()))
					continue;
				idList.add(item.getSysid());
				objList.add(item.getObjid());
				FileInfo0 tmpFileInfo0 = syncTask.GetLocalCopy(item);
				if (tmpFileInfo0 != null)
				{
					tmpFileMap.put(item.getSysid(), tmpFileInfo0);
				}
			}
		}
		
		
		if (idList.size() > 0)
		{
			Map<String, List<PicEditItemBean>> tmpDayMap = new HashMap<String, List<PicEditItemBean>>();
			
			for (int i = 0 ; i < idList.size() ; i ++)
			{
				FileInfo0 info = tmpFileMap.get(idList.get(i));
				if (info == null)
				{
					info = bIsUbkList ? syncTask.queryLocalInfo(idList.get(i)) : syncTask.getUnitinfo(i);
					info.setFilePath(bIsUbkList ? info.getFilePath() : ThumUtil.getThumid(objList.get(i)));
				}
				int tmpYear = TimeUtils.getYearWithTimeMillis(info.getLastModified() * 1000L);
				int tmpMonth = TimeUtils.getMonthWithTimeMillis(info.getLastModified() * 1000L);
				String tmpDay = TimeUtils.getTime(info.getLastModified(), new SimpleDateFormat("MM月dd日"));
				
				if (tmpYear == this.year && tmpMonth == this.month)
				{
					PicEditItemBean picInfoBean = new PicEditItemBean();
					picInfoBean.setPicpath(info.getFilePath());
					picInfoBean.setPicid(info.getSysid());
					picInfoBean.setPicUrl(R.drawable.pic_test1);
					picInfoBean.setSelect(false);
					picInfoBean.setbIsUbkList(bIsUbkList);
					picInfoBean.setFileInfo0(info);
					
					if (tmpDayMap.get(tmpDay) != null)
					{
						tmpDayMap.get(tmpDay).add(picInfoBean);
					}
					else
					{
						List<PicEditItemBean> dayItemBeans = new ArrayList<PicEditItemBean>();
						dayItemBeans.add(picInfoBean);
						tmpDayMap.put(tmpDay, dayItemBeans);
					}
				}
			}
			
			if (tmpDayMap.size() > 0)
			{
				for (Map.Entry<String, List<PicEditItemBean>> entryDay : tmpDayMap.entrySet())
				{
					PicEditBean picBean = new PicEditBean(String.valueOf(entryDay.getKey()), entryDay.getValue());
					picBean.setbIsUbkList(bIsUbkList);
					mPicList.add(picBean);
				}
			}
		}

		handler.sendEmptyMessage(0);
	}
	
	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mTvRight.setOnClickListener(this);
		mTvRight.setTag(false);
		mLvPicEditDel.setOnClickListener(this);
		mLvPicEditUpDown.setOnClickListener(this);
	}
	
	private void initResourceId() {
		mLvPic = (ListView) findViewById(R.id.lv_pic_edit);
		mLvPicEditDel = findViewById(R.id.lv_pic_edit_del);
		mLvPicEditView = findViewById(R.id.lv_pic_edit_layout);
		mLvPicEditUpDown = findViewById(R.id.lv_pic_edit_updown);
		
		TextView textView = (TextView) findViewById(R.id.lv_pic_edit_updown_text);
		if (bIsUbkList)
		{
			textView.setText("上传");
		}
		else
		{
			textView.setText("下载");
		}
	}
	
	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
		mTvRight = (TextView) findViewById(R.id.tv_right);

		mTvCenter.setText("照片");
		mTvRight.setText("编辑");
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
		case R.id.iv_left:
			finish();
			break;
		case R.id.tv_right:
			boolean bTag = (Boolean) mTvRight.getTag();
			mLvPicEditView.setVisibility(bTag ? View.GONE : View.VISIBLE);
			mTvRight.setText(bTag ? "编辑" : "取消");
			mTvRight.setTag(!bTag);
			for (PicEditBean picEditBean : mPicList)
			{
				picEditBean.setEdit(!bTag);
				picEditBean.setSelect(false);
				for (PicEditItemBean picEditItemBean : picEditBean.getList())
				{
					picEditItemBean.setEdit(!bTag);
					picEditBean.setSelect(false);
				}
			}
			handler.sendEmptyMessage(0);
			break;
		case R.id.lv_pic_edit_del:
		{
		}
			break;
		case R.id.lv_pic_edit_updown:
		{
			for (PicEditBean picEditBean : mPicList)
			{
				/*if (!picEditBean.isSelect())
				{
					continue;
				}*/
				
				for (PicEditItemBean picEditItemBean : picEditBean.getList())
				{
					if (!picEditItemBean.isSelect())
					{
						continue;
					}
					
					final SyncTask syncTask =new SyncTask(this, FTYPE.PICTURE);
					FileInfo fileInfo = syncTask.queryLocalInfo(picEditItemBean.getPicid());
					final FileInfo0 fileInfo0 = new FileInfo0(fileInfo);
					fileInfo0.setFilePath(ThumUtil.splitFileName(picEditItemBean.getPicpath()));
					fileInfo0.setFilesize(fileInfo.getFilesize());
					Thread thread = new Thread(new Runnable() 
					{
						@Override
						public void run() 
						{
							if (bIsUbkList)
							{
								syncTask.upload(fileInfo0, null, false);	
							}
							else
							{
								syncTask.download(fileInfo0, null, false);
							}		
						}
					});
					thread.start();
				}
			}
		}
			break;
		default:
			break;
		}
	}
}
