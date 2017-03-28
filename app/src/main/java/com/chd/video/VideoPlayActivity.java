package com.chd.video;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.chd.base.UILActivity;
import com.chd.yunpan.R;
import com.chd.yunpan.view.ActionSheetDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoPlayActivity extends UILActivity implements MediaPlayer.OnPreparedListener {


	@BindView(R.id.iv_left)
	ImageView ivLeft;
	@BindView(R.id.tv_center)
	TextView tvCenter;
	@BindView(R.id.tv_right)
	TextView tvRight;
	@BindView(R.id.video_view)
	FullScreenVideoView videoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_play);
		ButterKnife.bind(this);
		tvCenter.setText("视频播放");
		tvRight.setText("编辑");
		String url = getIntent().getStringExtra("url");

		videoView.setOnPreparedListener(this);
		//For now we just picked an arbitrary item to play.  More can be found at
		//https://archive.org/details/more_animation
//		videoView.setVideoURI(Uri.parse("https://archive.org/download/Popeye_forPresident/Popeye_forPresident_512kb.mp4"));
		videoView.setVideoURI(Uri.parse(url));
		videoView.setMediaController(new MediaController(this));
	}


	@OnClick({R.id.iv_left,R.id.tv_right})
	public void onClick(View v){
		switch (v.getId()){
			case R.id.iv_left:
				onBackPressed();
				break;
			case R.id.tv_right:
				//添加图片
				//从本地添加，视频拍照
				editVideo();
				break;
		}


	}

	private void editVideo(){
		new ActionSheetDialog(this).builder().addSheetItem("下载视频", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
                    //下载视频操作
			}
		}).addSheetItem("删除视频", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
                //删除视频操作

			}
		}).setCanceledOnTouchOutside(true).setCancelable(true).show();
	}



	@Override
	public void onPrepared(MediaPlayer mediaPlayer) {
//		mediaPlayer.start();
	}


}
