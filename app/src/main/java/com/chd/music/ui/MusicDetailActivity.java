package com.chd.music.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.base.backend.SyncTask;
import com.chd.photo.adapter.RoundImageView;
import com.chd.photo.entity.ThumUtil;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

public class MusicDetailActivity extends Activity implements OnClickListener
{
	
	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	
	private TextView mTvTotalTime;
	private TextView mTvMusicName;
	private TextView mTvMusicDestrip;
	
	private ImageView mBtnDownload;
	private ImageView mBtnPlay;
	private ImageView mBtnDelete;
	private RoundImageView mRoundImageView;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	
	private SyncTask syncTask;
	FileInfo0 fileInfo0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_music_detail);
		
		initTitle();
		initResourceId();
		initListener();

		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.pic_test1)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(20))
		.extraForDownloader(new ShareUtils(this).getStorePathStr())  //增加保存路径
		.build();
		
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
				SyncTask syncTask =new SyncTask(MusicDetailActivity.this, FTYPE.MUSIC);
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
		//模拟数据
		int sysid = getIntent().getIntExtra("sysid", -1);
		String objId = getIntent().getStringExtra("objId");
		if (sysid < 0)
		{
			return;
		}
		
		syncTask = new SyncTask(this, FTYPE.MUSIC);
		
		syncTask.analyMusicUnits(cloudUnits);
//		fileInfo0 = syncTask.getUnitinfo(objId);
		if (fileInfo0 == null)
		{
			return;
		}
		mTvMusicName.setText(fileInfo0.getFilename());
		String url = ThumUtil.getThumid(fileInfo0.getObjid());
		if (url != null)
		{
			/*if (!syncTask.haveLocalCopy(fileInfo0))
			{
				//url = ThumUtil.getThumid(fileInfo0.getObjid()); //缩略图地址
			}*/
			imageLoader.displayImage(url, mRoundImageView,
					options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							/*vh.progressBar.setProgress(0);
							vh.progressBar.setVisibility(View.VISIBLE);*/
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							/*vh.progressBar.setVisibility(View.GONE);*/
						}

						@Override
						public void onLoadingComplete(String imageUri, View view,
								Bitmap loadedImage) {
							/*vh.progressBar.setVisibility(View.GONE);*/
						}
					}, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String imageUri, View view,
								int current, int total) {
							/*vh.progressBar.setProgress(Math.round(100.0f * current
									/ total));*/
						}
					});
		}
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mBtnDownload.setOnClickListener(this);
		mBtnPlay.setOnClickListener(this);
		mBtnDelete.setOnClickListener(this);
	}

	private void initResourceId() {
		mBtnDownload = (ImageView) findViewById(R.id.music_detail_download);
		mBtnPlay = (ImageView) findViewById(R.id.music_detail_play);
		mBtnDelete = (ImageView) findViewById(R.id.music_detail_delete);
		
		mTvTotalTime = (TextView) findViewById(R.id.music_detail_totaltime);
		mTvMusicName = (TextView) findViewById(R.id.music_detail_musicname);
		mTvMusicDestrip = (TextView) findViewById(R.id.music_detail_musicdestrip);
		mRoundImageView = (RoundImageView) findViewById(R.id.music_detail_pic);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
		mTvRight = (TextView) findViewById(R.id.tv_right);

		mTvCenter.setText("音乐");
		mTvRight.setText("取消");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_right:
		case R.id.iv_left:
			finish();
			break;
		case R.id.music_detail_download:
			if (syncTask != null && fileInfo0 != null)
			{
				syncTask.download(fileInfo0, null, false);
			}
			break;
		case R.id.music_detail_play:
			break;
		case R.id.music_detail_delete:
			if (syncTask != null && fileInfo0 != null)
			{
				syncTask.DelRemoteObj(fileInfo0);
			}
			break;
		}
	}
	
	
}
