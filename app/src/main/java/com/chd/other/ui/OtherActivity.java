package com.chd.other.ui;

import android.app.Activity;
import android.graphics.Color;
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
import com.chd.base.backend.SyncTask;
import com.chd.other.adapter.OtherListAdapter;
import com.chd.other.entity.FileInfoL;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;

import java.util.ArrayList;
import java.util.List;

public class OtherActivity extends Activity implements OnClickListener
{
	
	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	
	private int nRgbColorNor = Color.rgb(248, 184, 45);
	private int nRgbColorSel = Color.rgb(255, 255, 255);
	
	private TextView mTabAll, mTabDOC, mTabXLS, mTabPPT, mTabPDF;
	private ListView mListView;
	
	private List<FileInfo> mFileInfoList = new ArrayList<FileInfo>();
	private String filetype = "";

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			List<FileInfo> tmpFileInfo = new ArrayList<FileInfo>();
			
			if (!filetype.equals(""))
			{
				for (FileInfo fileInfo : mFileInfoList)
				{
					if (fileInfo.getFtype().equals(filetype))
					{
						tmpFileInfo.add(fileInfo);
					}
				}
			}
			else
			{
				tmpFileInfo.addAll(mFileInfoList);
			}
			mListView.setAdapter(new OtherListAdapter(OtherActivity.this, tmpFileInfo));
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		/*StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
*/
		setContentView(R.layout.activity_other);
        
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
				SyncTask syncTask =new SyncTask(OtherActivity.this, FTYPE.NORMAL);
				//未备份文件 ==  backedlist . removeAll(localist);

				final List<FileInfo0> cloudUnits=syncTask.getCloudUnits(0, 100);
				runOnUiThread(new Runnable() {
					public void run() {
						initData(cloudUnits);
					}
				});
			}
		});
		thread.start();
	}
	
	private void initData(List<FileInfo0> cloudUnits)
	{
		/*FileInfo fileInfo0 = new FileInfo(R.drawable.other_icon_doc, "行程清单0.doc", TimeUtils.getCurrentTimeInString(), "1024M", FileInfoL.FILE_TYPE_DOC);
		mFileInfoList.add(fileInfo13);
		
		filetype = FileInfo.FILE_TYPE_ALL;*/

		SyncTask syncTask =new SyncTask(this, FTYPE.NORMAL);

		if (cloudUnits==null)
		{
			System.out.print("query remote failed");
		}

		FilelistEntity filelistEntity=syncTask.analyUnits(cloudUnits);
		//cloudUnits  是网存的文件
		cloudUnits.clear();
		cloudUnits=null;
		List<FileLocal> fileLocals=filelistEntity.getLocallist();
		//显示的时候过滤文件类型
		MFileFilter fileFilter=new MFileFilter();
		fileFilter.setCustomCategory(new String[]{FileInfoL.FILE_TYPE_DOC,FileInfoL.FILE_TYPE_PDF,FileInfoL.FILE_TYPE_PPT, FileInfoL.FILE_TYPE_XLS},true);

		/*for(FileInfo0 item:cloudUnits)
		{
			//FileInfo0 item=new FileInfo0(finfo);
			if(!fileFilter.contains(item.getObjid()))
				continue;
			//已备份文件
			if (syncTask.haveLocalCopy(item))
			{
				String path=item.getFilePath();
			}
			else
			{
				String savepath= new ShareUtils(this).getStorePathStr()+item.getFilename();
				item.setFilePath(savepath);
				//param1  object ,param2 progressBar, param 3  beeque
				syncTask.download(item,null,false);
			}
		}*/
		for (FileLocal fileLocal : fileLocals)
		{
			if (fileLocal.bakuped)
				continue;
			
			FileInfo0 fileInfo0 = syncTask.queryLocalInfo(fileLocal.sysid);
			if (fileInfo0 == null)
			{
				continue;
			}
			
			if (fileFilter.contains(fileInfo0.getFilePath()))
			{
				FileInfo fileInfo = new FileInfo(fileInfo0.getObjid(), fileInfo0.getFilesize(), fileInfo0.getFtype(), fileInfo0.getLastModified());
				mFileInfoList.add(fileInfo);
			}
		}

		handler.sendEmptyMessage(0);
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mTvRight.setOnClickListener(this);
		
		mTabAll.setOnClickListener(this);
		mTabDOC.setOnClickListener(this);
		mTabPDF.setOnClickListener(this);
		mTabPPT.setOnClickListener(this);
		mTabXLS.setOnClickListener(this);
	}

	private void initResourceId() {
		mTabAll = (TextView) findViewById(R.id.other_tab_all);
		mTabDOC = (TextView) findViewById(R.id.other_tab_doc);
		mTabPDF = (TextView) findViewById(R.id.other_tab_pdf);
		mTabPPT = (TextView) findViewById(R.id.other_tab_ppt);
		mTabXLS = (TextView) findViewById(R.id.other_tab_xls);
		
		mListView = (ListView) findViewById(R.id.other_listview);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
		mTvRight = (TextView) findViewById(R.id.tv_right);

		mTvCenter.setText("免流量应用");
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
		case R.id.iv_left:
		{
			finish();
		}
			break;
		case R.id.other_tab_all:
		{
			mTabAll.setBackgroundResource(R.drawable.other_tab_left_checked);
			mTabDOC.setBackgroundResource(R.drawable.other_tab_center_normal);
			mTabPPT.setBackgroundResource(R.drawable.other_tab_center_normal);
			mTabXLS.setBackgroundResource(R.drawable.other_tab_center_normal);
			mTabPDF.setBackgroundResource(R.drawable.other_tab_right_normal);
			
			mTabAll.setTextColor(nRgbColorSel);
			mTabDOC.setTextColor(nRgbColorNor);
			mTabPPT.setTextColor(nRgbColorNor);
			mTabXLS.setTextColor(nRgbColorNor);
			mTabPDF.setTextColor(nRgbColorNor);
			
			//filetype = FileInfo.FILE_TYPE_ALL;
			handler.sendEmptyMessage(0);
		}
			break;
		case R.id.other_tab_doc:
		{
			mTabAll.setBackgroundResource(R.drawable.other_tab_left_normal);
			mTabDOC.setBackgroundResource(R.drawable.other_tab_center_checked);
			mTabPPT.setBackgroundResource(R.drawable.other_tab_center_normal);
			mTabXLS.setBackgroundResource(R.drawable.other_tab_center_normal);
			mTabPDF.setBackgroundResource(R.drawable.other_tab_right_normal);
			
			mTabAll.setTextColor(nRgbColorNor);
			mTabDOC.setTextColor(nRgbColorSel);
			mTabPPT.setTextColor(nRgbColorNor);
			mTabXLS.setTextColor(nRgbColorNor);
			mTabPDF.setTextColor(nRgbColorNor);
			
			//filetype = FileInfo.FILE_TYPE_DOC;
			handler.sendEmptyMessage(0);
		}
			break;
		case R.id.other_tab_pdf:
		{
			mTabAll.setBackgroundResource(R.drawable.other_tab_left_normal);
			mTabDOC.setBackgroundResource(R.drawable.other_tab_center_normal);
			mTabPPT.setBackgroundResource(R.drawable.other_tab_center_normal);
			mTabXLS.setBackgroundResource(R.drawable.other_tab_center_normal);
			mTabPDF.setBackgroundResource(R.drawable.other_tab_right_checked);
			
			mTabAll.setTextColor(nRgbColorNor);
			mTabDOC.setTextColor(nRgbColorNor);
			mTabPPT.setTextColor(nRgbColorNor);
			mTabXLS.setTextColor(nRgbColorNor);
			mTabPDF.setTextColor(nRgbColorSel);
			
			//filetype = FileInfo.FILE_TYPE_PDF;
			handler.sendEmptyMessage(0);
		}
			break;
		case R.id.other_tab_ppt:
		{
			mTabAll.setBackgroundResource(R.drawable.other_tab_left_normal);
			mTabDOC.setBackgroundResource(R.drawable.other_tab_center_normal);
			mTabPPT.setBackgroundResource(R.drawable.other_tab_center_checked);
			mTabXLS.setBackgroundResource(R.drawable.other_tab_center_normal);
			mTabPDF.setBackgroundResource(R.drawable.other_tab_right_normal);
			
			mTabAll.setTextColor(nRgbColorNor);
			mTabDOC.setTextColor(nRgbColorNor);
			mTabPPT.setTextColor(nRgbColorSel);
			mTabXLS.setTextColor(nRgbColorNor);
			mTabPDF.setTextColor(nRgbColorNor);
			
			//filetype = FileInfo.FILE_TYPE_PPT;
			handler.sendEmptyMessage(0);
		}
			break;
		case R.id.other_tab_xls:
		{
			mTabAll.setBackgroundResource(R.drawable.other_tab_left_normal);
			mTabDOC.setBackgroundResource(R.drawable.other_tab_center_normal);
			mTabPPT.setBackgroundResource(R.drawable.other_tab_center_normal);
			mTabXLS.setBackgroundResource(R.drawable.other_tab_center_checked);
			mTabPDF.setBackgroundResource(R.drawable.other_tab_right_normal);
			
			mTabAll.setTextColor(nRgbColorNor);
			mTabDOC.setTextColor(nRgbColorNor);
			mTabPPT.setTextColor(nRgbColorNor);
			mTabXLS.setTextColor(nRgbColorSel);
			mTabPDF.setTextColor(nRgbColorNor);
			
			//filetype = FileInfo.FILE_TYPE_XLS;
			handler.sendEmptyMessage(0);
		}
			break;
		default:
			break;
		}
	}
	
}
