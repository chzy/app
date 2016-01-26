package com.chd.notepad.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.notepad.ui.db.FileDBmager;
import com.chd.notepad.ui.item.NoteItemtag;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;

import java.io.IOException;

public class NotepadCheckActivity extends Activity {

	private TextView contentText = null;
	private TextView timeText = null;
	private ImageView mIvLeft;
	private TextView mTvCenter;
	private final String TAG=this.getClass().getName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notepad_check);

		initTitle();
		contentText = (TextView)findViewById(R.id.checkContent);
		timeText = (TextView)findViewById(R.id.checkTime);
		
		Intent intent = getIntent();//获取启动该Activity的intent对象
		
		//String id = intent.getStringExtra("hashcode");
		//String time= intent.getStringExtra("time");
		//String content = intent.getStringExtra("content");
		//String fname=intent.getStringExtra("fname");
		//long t = Long.parseLong(time);
		NoteItemtag noteItemtag= (NoteItemtag) intent.getSerializableExtra("item");
		String path=new ShareUtils(this.getApplicationContext()).getStorePathStr();
		FileDBmager fileDBmager=new FileDBmager(this);
		String datetime = DateFormat.format("yyyy-MM-dd kk:mm:ss", noteItemtag.getStamp()*1000L).toString();
		String content= "";

		content = fileDBmager.readFile(noteItemtag.get_fname());

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
