package com.chd.music.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.MediaMgr.utils.MFileFilter;
import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.backend.SyncTask;
import com.chd.music.adapter.MusicAdapter;
import com.chd.music.entity.MusicBean;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends Activity implements OnClickListener,OnItemClickListener {

	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	private TextView mTvNumber;
	private GridView mGvMusic;
	private View mViewNumber;
	
	private List<MusicBean> mMusicList = new ArrayList<MusicBean>();
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mGvMusic.setAdapter(new MusicAdapter(MusicActivity.this,
					mMusicList));
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_music);

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
				SyncTask syncTask =new SyncTask(MusicActivity.this, FTYPE.MUSIC);
				//未备份文件 ==  backedlist . removeAll(localist);

				final List<FileInfo0> cloudUnits=syncTask.getCloudUnits(0, 1000);
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

		SyncTask syncTask =new SyncTask(this, FTYPE.MUSIC);

		if (cloudUnits==null)
		{
			System.out.print("query remote failed");
		}
		FilelistEntity filelistEntity=syncTask.analyMusicUnits(cloudUnits);
		cloudUnits.clear();
		cloudUnits=null;
		List<FileLocal> fileLocals=filelistEntity.getLocallist();
		cloudUnits= filelistEntity.getBklist();
		//显示的时候过滤文件类型
		MFileFilter fileFilter=new MFileFilter();
		fileFilter.setCustomCategory(new String[]{"mp3"},true);

		for(FileInfo0 item:cloudUnits)
		{
			//FileInfo0 item=new FileInfo0(finfo);
			item.setFilePath(new ShareUtils(this).getMusicFile().getPath()+ File.separator+item.getObjid());
			if(!fileFilter.contains(item.getObjid()))
				continue;
			//已备份文件
			String path = item.getFilePath();
			String name = item.getFilename();
			int id = item.getSysid();
			MusicBean musicBean = new MusicBean(name, path);
			musicBean.setId(id);
			musicBean.setFileInfo0(item);
			if (syncTask.haveLocalCopy(item))
				musicBean.setFileInfo0(item);
			
			mMusicList.add(musicBean);
		}
		
		mTvNumber.setText(String.format("未备份音乐%d首", fileLocals.size()));
		
		handler.sendEmptyMessage(0);

	}

	private void initResourceId() {
		mTvNumber = (TextView) findViewById(R.id.tv_music_number);
		mGvMusic = (GridView) findViewById(R.id.gv_music);
		mViewNumber = findViewById(R.id.iv_music_num_layout);

		mTvNumber.setText("未备份音乐0首");
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mTvRight.setOnClickListener(this);
		mGvMusic.setOnItemClickListener(this);
		mViewNumber.setOnClickListener(this);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
		mTvRight = (TextView) findViewById(R.id.tv_right);

		mTvCenter.setText("音乐");
		mTvRight.setText("编辑");
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_left:
			finish();
			break;
		case R.id.tv_right: // 编辑
			// TODO
			break;
		case R.id.iv_music_num_layout:
			Intent intent = new Intent(this, MusicBackupActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(this, MusicDetailActivity.class);
		intent.putExtra("file", mMusicList.get(arg2));
		startActivity(intent);
	}
}
