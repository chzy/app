package com.chd.yunpan.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chd.app.backend.AppInfo0;
import com.chd.app.backend.PakageInfoProvider;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.ui.adapter.FreeDownGridAdapter;
import com.chd.yunpan.ui.adapter.FreeDownListAdapter;
import com.chd.yunpan.utils.AppUtils;
import com.chd.yunpan.utils.update.UpdateAppUtils;
import com.chd.yunpan.utils.update.VersionModel;

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

	private boolean isDown=true;
	private List<AppInfo0> mAppList = new ArrayList<AppInfo0>();

	private PakageInfoProvider appProvider;
	private FreeDownListAdapter listAdapter;
	private FreeDownGridAdapter gridAdapter;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mAppList.clear();
			if(isDown){
				mAppList.addAll(appProvider.getDownApps());
				gridAdapter.notifyDataSetChanged();
			}else{
				mAppList.addAll(appProvider.getUnDownApps());
				listAdapter.notifyDataSetChanged();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_freedown);
		appProvider=new PakageInfoProvider(this);
		listAdapter=new FreeDownListAdapter(FreeDownActivity.this, mAppList);
		gridAdapter=new FreeDownGridAdapter(FreeDownActivity.this, mAppList);
		initTitle();
		initResourceId();
		mViewLeftGridView.setAdapter(gridAdapter);
		mViewRightListView.setAdapter(listAdapter);
		initListener();
		initData();

		mViewLeftGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				AppInfo0 appInfo0 = mAppList.get(i);
				String packageName = appInfo0.getPackageName();
				if(AppUtils.isAppInstallen(FreeDownActivity.this,packageName)){
					ShareUtils shareUtils = new ShareUtils(FreeDownActivity.this);
					Bundle bundle=new Bundle();
					bundle.putString("phone",shareUtils.getUsername());
					bundle.putString("pwd",shareUtils.getPwd());
					AppUtils.openApp(FreeDownActivity.this,packageName,bundle);

				}else{
					VersionModel vm=new VersionModel();
					vm.url=appInfo0.getUrl();
					UpdateAppUtils.launch(FreeDownActivity.this,vm);
				}
			}
		});

	}
	
	private void initData()
	{

		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					boolean b = appProvider.queryRemoteApps();
					if(b){

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								mTabLeft.performClick();
							}
						});
					}
				}
			}).start();


		} catch (Exception e) {
			Log.e("lmj","数据异常");
		}
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
//		mTvRight.setText("更新");
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
			isDown=true;
			mTabLayout.setBackgroundResource(R.drawable.freedown_hasdownload_bg);
			mViewLeftGridView.setVisibility(View.VISIBLE);
			mViewRightListView.setVisibility(View.GONE);
			mTabRight.setTextColor(Color.rgb(248, 184, 45));
			mTabLeft.setTextColor(Color.rgb(255, 255, 255));
			handler.sendEmptyMessage(0);
		}
			break;
		case R.id.freedown_tab_right:
		{
			isDown=false;
			mTabLayout.setBackgroundResource(R.drawable.freedown_nodownload_bg);
			mViewLeftGridView.setVisibility(View.GONE);
			mViewRightListView.setVisibility(View.VISIBLE);
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
