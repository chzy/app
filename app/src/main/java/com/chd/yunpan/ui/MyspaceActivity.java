package com.chd.yunpan.ui;

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

import com.chd.contacts.ui.Contacts;
import com.chd.music.ui.MusicActivity;
import com.chd.notepad.ui.activity.NotepadActivity;
import com.chd.other.ui.OtherActivity;
import com.chd.photo.ui.PicActivity;
import com.chd.smsbackup.ui.SmsActivity;
import com.chd.yunpan.R;
import com.chd.yunpan.ui.adapter.MenuGridAdapter;
import com.chd.yunpan.ui.entity.MySpaceBean;

import java.util.ArrayList;
import java.util.List;

//import org.achartengine.GraphicalView;

/**
 * Created by lxp1 on 2015/10/23.
 */
public class MyspaceActivity extends Activity implements OnClickListener, OnItemClickListener
{

	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	private TextView mTvSpaceNumber;
	private GridView mGvSpace;
	
    List<MySpaceBean> meumList = new ArrayList<MySpaceBean>();
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mGvSpace.setAdapter(new MenuGridAdapter(MyspaceActivity.this, meumList));
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myspace_grid);
        
        initTitle();
		initResourceId();
		initListener();
		initData();
    }

	private void initData() {
		//模拟数据
        MySpaceBean mySpaceBean0 = new MySpaceBean("照片", R.drawable.myspace_grid_photo, PicActivity.class);
        MySpaceBean mySpaceBean1 = new MySpaceBean("音乐", R.drawable.myspace_grid_music, MusicActivity.class);
        MySpaceBean mySpaceBean2 = new MySpaceBean("小心事", R.drawable.myspace_grid_notepad, NotepadActivity.class);
        //MySpaceBean mySpaceBean3 = new MySpaceBean("联系人", R.drawable.myspace_grid_contact, ContactActivity.class);
		MySpaceBean mySpaceBean3 = new MySpaceBean("联系人", R.drawable.myspace_grid_contact, Contacts.class);
        MySpaceBean mySpaceBean4 = new MySpaceBean("短信", R.drawable.myspace_grid_message, SmsActivity.class);
		//MySpaceBean mySpaceBean4 = new MySpaceBean("短信", R.drawable.myspace_grid_message, Contacts.class);
        MySpaceBean mySpaceBean5 = new MySpaceBean("其他", R.drawable.myspace_grid_other, OtherActivity.class);
		
        meumList.add(mySpaceBean0);
        meumList.add(mySpaceBean1);
        meumList.add(mySpaceBean2);
        meumList.add(mySpaceBean3);
        meumList.add(mySpaceBean4);
        meumList.add(mySpaceBean5);
        
		handler.sendEmptyMessage(0);
		
		mTvSpaceNumber.setText("16G");
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mTvRight.setOnClickListener(this);
		mTvSpaceNumber.setOnClickListener(this);
        mGvSpace.setOnItemClickListener(this);
	}

	private void initResourceId() {
        mGvSpace = (GridView) findViewById(R.id.myspace_gridview);
		mTvSpaceNumber = (TextView) findViewById(R.id.myspace_space_textview);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
		mTvRight = (TextView) findViewById(R.id.tv_right);

		mTvCenter.setText("私属空间");
		mTvRight.setText("设置");
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent pageintent = new Intent();
        pageintent.setClass(this, meumList.get(arg2).getCls());
        pageintent.putExtra("callpage",arg2);
        startActivity(pageintent);
        //Toast用于向用户显示一些帮助/提示
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_left:
			finish();
			break;
		case R.id.tv_right:
			break;
		case R.id.myspace_space_textview:
			break;
		}
	}

}

