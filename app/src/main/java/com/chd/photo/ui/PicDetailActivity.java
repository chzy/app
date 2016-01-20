package com.chd.photo.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.MediaMgr.utils.MediaFileUtil;
import com.chd.base.backend.SyncTask;
import com.chd.contacts.vcard.StringUtils;
import com.chd.photo.entity.PicEditItemBean;
import com.chd.photo.entity.ThumUtil;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.utils.ToastUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

public class PicDetailActivity extends Activity implements OnClickListener
{

	private ImageView mIvLeft;
	private TextView mTvCenter;
	private ImageView mImgView;
	private Button mBtnSaveLocal;
	private Button mBtnCancel;
	private Button mBtnDelete;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	private boolean bIsUbkList;
	
	private SyncTask syncTask;
	private FileInfo0 fileInfo0;
	private final String TAG=this.getClass().getName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_pic_detail);
		
		bIsUbkList = getIntent().getBooleanExtra("ubklist", false);
		if (bIsUbkList)
		{
			findViewById(R.id.pic_detail_btm_layout).setVisibility(View.GONE);
		}

		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.pic_test1)
		.cacheInMemory(false)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.extraForDownloader(new ShareUtils(this).getStorePathStr())  //增加保存路径
		.build();
		
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
				syncTask =new SyncTask(PicDetailActivity.this, FTYPE.PICTURE);
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
	
	private void initData(List<FileInfo0> cloudUnits){
		/*int nPicId = getIntent().getIntExtra("picid", -1);
		if (nPicId < 0)
		{
			return;
		}*/
		if (syncTask == null)
			syncTask=new SyncTask(this, FTYPE.PICTURE);
		/*syncTask.analyPhotoUnits(cloudUnits);
		cloudUnits.clear();
		cloudUnits = null;
		fileInfo0 = (FileInfo0) getIntent().getSerializableExtra("fileinfo0");
		if (fileInfo0 == null)
		{
			fileInfo0 = bIsUbkList ? syncTask.queryLocalInfo(nPicId) : syncTask.getUnitinfo(nPicId);
		}
		String url = ThumUtil.getThumid(fileInfo0.getObjid());
		String filepath = getIntent().getStringExtra("filepath");*/
		PicEditItemBean editItemBean= (PicEditItemBean) getIntent().getSerializableExtra("bean");
		String url =editItemBean.getUrl();
		int idx=url.indexOf("://");
		if (idx<0) {
			Log.e(TAG, "error file path fail!!!!");
			return;
					}
		fileInfo0=new FileInfo0();
		idx+=3;
		String uri=editItemBean.getUrl().substring(idx);
		if (editItemBean.isbIsUbkList()) {
			fileInfo0.setFilePath(uri);
			fileInfo0.setObjid(MediaFileUtil.getFnameformPath(uri));
		}
		else
			fileInfo0.setObjid(uri);

		/*if(!StringUtils.isNullOrEmpty(filepath)) {
			url = filepath;
		}*/
		if (url != null)
		{
			/*if (!syncTask.haveLocalCopy(fileInfo0))
			{
				//url = ThumUtil.getThumid(fileInfo0.getObjid()); //缩略图地址
			}*/
			imageLoader.displayImage(url, mImgView,
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
		/*else
		{
			mImgView.setImageResource(R.drawable.pic_test1);
		}*/
	}
	
	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		mBtnDelete.setOnClickListener(this);
		mBtnSaveLocal.setOnClickListener(this);
	}
	
	private void initResourceId() {
		mImgView = (ImageView) findViewById(R.id.pic_detail_img);
		mBtnSaveLocal = (Button) findViewById(R.id.pic_detail_savelocal);
		mBtnDelete = (Button) findViewById(R.id.pic_detail_delete);
		mBtnCancel = (Button) findViewById(R.id.pic_detail_cancel);
	}
	
	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
		//mTvRight = (TextView) findViewById(R.id.tv_right);

		mTvCenter.setText("照片");
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
		case R.id.iv_left:
		case R.id.pic_detail_cancel:
		{
			finish();
		}
			break;
		case R.id.pic_detail_delete:
		{
			Thread thread = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					if (fileInfo0 != null)
					{
						final boolean bSucc = syncTask.DelRemoteObj(fileInfo0);
						runOnUiThread(new Runnable() 
						{
							@Override
							public void run() {
								String strData = bSucc ? "删除成功" : "删除失败";
								ToastUtils.toast(PicDetailActivity.this, strData);		
							}
						});
					}
				}
			});
			thread.start();
		}
			break;
		case R.id.pic_detail_savelocal:
		{
			if (fileInfo0 != null && fileInfo0.getFilePath() != null)
			{
				if (syncTask != null)
				{
					syncTask.download(fileInfo0, null, false);
				}
			}
			
			ToastUtils.toast(this, "保存成功!");
		}
			break;

		default:
			break;
		}
	}
}
