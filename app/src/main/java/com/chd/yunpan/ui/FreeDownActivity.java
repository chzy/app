package com.chd.yunpan.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chd.app.backend.AppInfo0;
import com.chd.app.backend.PakageInfoProvider;
import com.chd.yunpan.R;
import com.chd.yunpan.ui.adapter.FreeDownGridAdapter;
import com.chd.yunpan.ui.adapter.FreeDownListAdapter;

import java.util.ArrayList;
import java.util.List;

public class FreeDownActivity extends Activity implements OnClickListener
{

	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	
	private View mTabLayout;
	private TextView mTabLeft, mTabRight;
	private GridView mViewLeftGridView;
	private ListView mViewRightListView;
	
	private List<AppInfo0> mAppList = new ArrayList<AppInfo0>();
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			PakageInfoProvider pakageInfoProvider = new PakageInfoProvider(FreeDownActivity.this);
			mAppList.clear();
			mAppList.addAll(pakageInfoProvider.getAppInfo());

			List<AppInfo0> apps=pakageInfoProvider.getAppInfo();

			for (AppInfo0 app:apps)
			{
				if (app.isNeedUp())
					System.out.print(app.getAppName() + "need update");
				if (!app.isInstalled())
					System.out.print(app.getAppName() + "un loaded");
			}

			
			if (msg.what == 0)
			{
				mViewLeftGridView.setAdapter(new FreeDownGridAdapter(FreeDownActivity.this, mAppList));				
			}
			else
			{
				mViewRightListView.setAdapter(new FreeDownListAdapter(FreeDownActivity.this, mAppList));
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_freedown);
        
        initTitle();
		initResourceId();
		initListener();
		initData();
	}
	
	private void initData()
	{
		handler.sendEmptyMessage(0);
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mTvRight.setOnClickListener(this);
		
		mTabLeft.setOnClickListener(this);
		mTabRight.setOnClickListener(this);
		
	}

	private void initResourceId() {
        mTabLeft = (TextView) findViewById(R.id.freedown_tab_left);
        mTabRight = (TextView) findViewById(R.id.freedown_tab_right);
        
        mTabLayout = findViewById(R.id.freedown_tab_layout);
        mViewLeftGridView = (GridView) findViewById(R.id.freedown_left_view);
        mViewRightListView = (ListView) findViewById(R.id.freedown_right_view);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
		mTvRight = (TextView) findViewById(R.id.tv_right);

		mTvCenter.setText("免流量应用");
		mTvRight.setText("更新");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) 
		{
		case R.id.iv_left:
			finish();
			break;
		case R.id.tv_right:
		{
			
		}
			break;
		case R.id.freedown_tab_left:
		{
			mTabLayout.setBackgroundResource(R.drawable.freedown_hasdownload_bg);
			mViewLeftGridView.setVisibility(View.VISIBLE);
			//mViewRightListView.setVisibility(View.GONE);
			mTabRight.setTextColor(Color.rgb(248, 184, 45));
			mTabLeft.setTextColor(Color.rgb(255, 255, 255));
			
			handler.sendEmptyMessage(0);
		}
			break;
		case R.id.freedown_tab_right:
		{
			mTabLayout.setBackgroundResource(R.drawable.freedown_nodownload_bg);
			mViewLeftGridView.setVisibility(View.VISIBLE);
			//mViewRightListView.setVisibility(View.VISIBLE);
			mTabLeft.setTextColor(Color.rgb(248, 184, 45));
			mTabRight.setTextColor(Color.rgb(255, 255, 255));
			handler.sendEmptyMessage(0);
		}
			break;
		default:
			break;
		}
	}
	
}
