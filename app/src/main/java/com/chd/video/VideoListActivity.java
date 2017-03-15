package com.chd.video;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.base.UILActivity;
import com.chd.yunpan.R;
import com.gturedi.views.StatefulLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * User: Liumj(liumengjie@365tang.cn)
 * Date: 2017-03-06
 * Time: 10:11
 * describe:
 */
public class VideoListActivity extends UILActivity {


	private static final int RECORD_VIDEO = 1000;
	@BindView(R.id.iv_left)
	ImageView ivLeft;
	@BindView(R.id.tv_center)
	TextView tvCenter;
	@BindView(R.id.rv_video_list_content)
	RecyclerView rvVideoListContent;
	@BindView(R.id.sl_video_list_layout)
	StatefulLayout slVideoListLayout;
	@BindView(R.id.iv_video_list_add)
	ImageView ivVideoListAdd;
	@BindView(R.id.iv_video_list_start)
	ImageView ivVideoListStart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_list);
		ButterKnife.bind(this);
		File f=new File(
				getCacheDir()+"/video");
		if(!f.exists()){
			f.mkdir();
		}


	}


	@OnClick({R.id.iv_left,R.id.iv_video_list_add,R.id.iv_video_list_start})
	public void onClick(View v){
		switch (v.getId()){
			case R.id.iv_left:
				//退出
				onBackPressed();
				break;
			case R.id.iv_video_list_add:
				//从本地添加
				break;
			case R.id.iv_video_list_start:
				//视频拍照
				startrecord();
				break;
		}

	}

	/**录制视频**/
	public void startrecord() {
		Intent mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		mIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);
		startActivityForResult(mIntent, RECORD_VIDEO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
			case RECORD_VIDEO:
				// 录制视频完成
				try {
					AssetFileDescriptor videoAsset = getContentResolver()
							.openAssetFileDescriptor(data.getData(), "r");
					FileInputStream fis = videoAsset.createInputStream();

					File tmpFile = new File(
							getCacheDir()+"/video",
							"recordvideo.mp4");

					FileOutputStream fos = new FileOutputStream(tmpFile);
					byte[] buf = new byte[1024];
					int len;
					while ((len = fis.read(buf)) > 0) {
						fos.write(buf, 0, len);
					}
					fis.close();
					fos.close();
					// 文件写完之后删除/sdcard/dcim/CAMERA/XXX.MP4
					deleteDefaultFile(data.getData());
					Intent intent=new Intent(VideoListActivity.this,VideoPlayActivity.class);
					intent.putExtra("url",tmpFile.getPath());
					intent.putExtra("bitmap",bitmap);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
		}
	}

	private Bitmap bitmap=null;

	// 删除在/sdcard/dcim/Camera/默认生成的文件
	private void deleteDefaultFile(Uri uri) {
		String fileName = null;
		if (uri != null) {
			// content
			Log.d("Scheme", uri.getScheme().toString());
			if (uri.getScheme().toString().equals("content")) {
				Cursor cursor = this.getContentResolver().query(uri, null,
						null, null, null);
				if (cursor.moveToNext()) {
					int columnIndex = cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
					fileName = cursor.getString(columnIndex);
					//获取缩略图id
					int id = cursor.getInt(cursor
							.getColumnIndex(MediaStore.Video.VideoColumns._ID));
					//获取缩略图
					bitmap = MediaStore.Video.Thumbnails.getThumbnail(
							getContentResolver(), id, MediaStore.Video.Thumbnails.MICRO_KIND,
							null);
					Log.d("fileName", fileName);
//					if (!fileName.startsWith("/mnt")) {
//						fileName = "/mnt" + fileName;
//					}
//					Log.d("fileName", fileName);
				}
			}
		}
		// 删除文件
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
			Log.d("delete", "删除成功");
		}
	}
}
