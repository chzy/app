package com.chd.notepad.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.notepad.ui.adapter.NineAdapter;
import com.chd.notepad.ui.db.FileDBmager;
import com.chd.notepad.ui.item.NoteItem;
import com.chd.notepad.ui.item.NoteItemtag;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.view.NineGridlayout;
import com.google.gson.Gson;

import java.util.ArrayList;

public class NotepadCheckActivity extends Activity {

	private TextView contentText = null;
	private TextView timeText = null;
	private ImageView mIvLeft;
	private TextView mTvCenter;
	Gson gson;

	private final String TAG=this.getClass().getName();
	private NineGridlayout nineGrid;
	private ArrayList<String> eatPath;
	private Context mContext;
	private int FOOD_IMAGE=0xAF;
	private int DELFOODIMG=0xAE;
	private NineAdapter adapter;
	private TextView checkTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notepad_check);

		gson=new Gson();
		mContext=this;
		initTitle();
		contentText = (TextView)findViewById(R.id.checkContent);
		checkTitle = (TextView)findViewById(R.id.checkTitle);
		timeText = (TextView)findViewById(R.id.checkTime);
		nineGrid = (NineGridlayout) findViewById(R.id.editNineGrid);



		nineGrid.setOnItemClickListerner(new NineGridlayout.OnItemClickListerner() {
			@Override
			public void onItemClick(View view, int position) {

					Intent intent = new Intent(mContext, PhotoBrowseActivity.class);
					intent.putStringArrayListExtra("PhotoPath", eatPath);
				intent.putExtra("PhotoPosition", position);
					startActivityForResult(intent, DELFOODIMG);
			}
		});



		Intent intent = getIntent();//获取启动该Activity的intent对象
		
		//String id = intent.getStringExtra("hashcode");
		//String time= intent.getStringExtra("time");
		//String content = intent.getStringExtra("content");
		//String fname=intent.getStringExtra("fname");
		//long t = Long.parseLong(time);
		NoteItemtag noteItemtag= (NoteItemtag) intent.getSerializableExtra("item");
		String path=new ShareUtils(this.getApplicationContext()).getStorePathStr();
		FileDBmager fileDBmager=new FileDBmager(this);
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeInMillis();
		String datetime = DateFormat.format("yyyy-MM-dd HH:mm:ss", noteItemtag.getStamp()*1000L).toString();
		String content= "";
		content = fileDBmager.readFile(noteItemtag.get_fname());
		try {
			NoteItem noteItem = gson.fromJson(content, NoteItem.class);
			this.contentText.setText(noteItem.getContent());
			this.checkTitle.setText(noteItem.getTitle());
			eatPath=noteItem.getPicList();
			if(eatPath!=null&&eatPath.size()>0){
				adapter=new NineAdapter(this,eatPath);
				nineGrid.setAdapter(adapter);
			}else{
				nineGrid.setVisibility(View.GONE);
			}


		}catch (Exception e){
			this.contentText.setText(content);
		}



		this.timeText.setText(datetime);

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
