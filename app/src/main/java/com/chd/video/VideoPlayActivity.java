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
		tvRight.setText("删除");
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
				//删除
				break;
		}


	}



	@Override
	public void onPrepared(MediaPlayer mediaPlayer) {
//		mediaPlayer.start();
	}


}
