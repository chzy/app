package com.chd.strongbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chd.base.UILActivity;
import com.chd.notepad.ui.activity.NotepadActivity;
import com.chd.other.ui.OtherActivity;
import com.chd.photo.ui.PicActivity;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.lockscreen.pattern.GuideGesturePasswordActivity;
import com.lockscreen.pattern.UnlockGesturePasswordActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * User: Liumj(liumengjie@365tang.cn)
 * Date: 2017-02-27
 * Time: 14:01
 * describe:
 */
public class StrongBoxActivity extends UILActivity {


	@BindView(R.id.iv_left)
	ImageView ivLeft;

	@BindView(R.id.tv_center)
	TextView tvTitle;
	@BindView(R.id.ll_one)
	LinearLayout llOne;
	@BindView(R.id.ll_two)
	LinearLayout llTwo;
	@BindView(R.id.ll_three)
	LinearLayout llThree;
	@BindView(R.id.ll_four)
	LinearLayout llFour;
	@BindView(R.id.ll_five)
	LinearLayout llFive;
	@BindView(R.id.ll_six)
	LinearLayout llSix;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_strongbox);
		ButterKnife.bind(this);
		tvTitle.setText("小心事");
		if (UILApplication.getInstance().getLockPatternUtils().savedPatternExists()) {
			if (!getIntent().getBooleanExtra("unlock", false)) {
				Intent i = new Intent(this, UnlockGesturePasswordActivity.class);
				startActivity(i);
				finish();
			}
		}else{
			Intent i = new Intent(this, GuideGesturePasswordActivity.class);
			startActivity(i);
			finish();
		}



	}


	@OnClick({R.id.iv_left,R.id.ll_one, R.id.ll_two, R.id.ll_three, R.id.ll_four, R.id.ll_five, R.id.ll_six})
	public void onClick(View view) {
		Intent intent=null;
		switch (view.getId()) {
			case R.id.iv_left:
				onBackPressed();
				break;
			case R.id.ll_one:
				//照片
				intent=new Intent(this, PicActivity.class);
				startActivity(intent);
				break;
			case R.id.ll_two:
				//视频
				break;
			case R.id.ll_three:
				//文档
				intent=new Intent(this, OtherActivity.class);
				startActivity(intent);
				break;
			case R.id.ll_four:
				//录音
				intent=new Intent(this, VoiceActivity.class);
				startActivity(intent);
				break;
			case R.id.ll_five:
				//记事本
				intent=new Intent(this, NotepadActivity.class);
				startActivity(intent);
				break;
			case R.id.ll_six:
				//密码管理器
				break;
		}
	}
}
