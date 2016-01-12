package com.chd.notepad.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.yunpan.R;

public class NotepadCheckActivity extends Activity {

	private TextView contentText = null;
	private TextView timeText = null;
	private ImageView mIvLeft;
	private TextView mTvCenter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notepad_check);

		initTitle();
		contentText = (TextView)findViewById(R.id.checkContent);
		timeText = (TextView)findViewById(R.id.checkTime);
		
		Intent intent = getIntent();//获取启动该Activity的intent对象
		
		String id = intent.getStringExtra("hashcode");
		String time= intent.getStringExtra("time");
		String content = intent.getStringExtra("content");
		
		long t = Long.parseLong(time);
		
		String datetime = DateFormat.format("yyyy-MM-dd kk:mm:ss", t).toString();
		
		this.timeText.setText(datetime);
		this.contentText.setText(content);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);

		mTvCenter.setText("心事详情");
		mIvLeft.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
	}
	
}
