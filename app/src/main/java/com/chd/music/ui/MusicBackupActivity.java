package com.chd.music.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.MessageEvent;
import com.chd.base.Ui.ActiveProcess;
import com.chd.base.Ui.DownListActivity;
import com.chd.base.backend.SyncTask;
import com.chd.music.adapter.MusicBackupAdapter;
import com.chd.music.entity.MusicBackupBean;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MusicBackupActivity extends ActiveProcess implements OnClickListener, OnItemClickListener
{

	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	private TextView mTvNumber;
	private Button mBtnBackup;
	private ListView mGvMusic;
	
	private List<MusicBackupBean> mMusicBackupList = new ArrayList<MusicBackupBean>();
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dismissDialog();
			mGvMusic.setAdapter(new MusicBackupAdapter(MusicBackupActivity.this, mMusicBackupList));
			mTvNumber.setText(String.format("共：%d首", mMusicBackupList.size()));
		}
	};
	private Button mBtnDown;

	private SyncTask syncTask;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_music_backup);
		EventBus.getDefault().register(this);

syncTask=new SyncTask(this,FTYPE.MUSIC);
		initTitle();
		initResourceId();
		initListener();


		ArrayList<FileLocal> fileLocals= (ArrayList<FileLocal>) getIntent().getSerializableExtra("locallist");

		initData(fileLocals);


		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		//client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	private boolean isUpdate=false;

	@Subscribe
	public void onEventMainThread(MessageEvent event) {
		if(event.type==FTYPE.MUSIC){
			isUpdate=true;
		mMusicBackupList.remove(uploadBean);
			handler.sendEmptyMessage(0);
		}
	}


	@Override
	protected void onPause() {
		super.onPause();

	}

	private void initData(ArrayList<FileLocal> fileLocals) {


		for (FileLocal fileLocal : fileLocals)
		{
			if (fileLocal.bakuped)
				continue;
			String name = fileLocal.fname;
			FileInfo0 fileInfo0 = syncTask.queryLocalInfo(fileLocal.sysid);
			if (fileInfo0 == null )
			{
				continue;
			}
			
			MusicBackupBean musicBackupBean = new MusicBackupBean(name, fileInfo0.getFilePath(), false);
			musicBackupBean.setFileInfo0(fileInfo0);
			mMusicBackupList.add(musicBackupBean);
		}
		
		handler.sendEmptyMessage(0);
	}

	private void initResourceId() {
		mTvNumber = (TextView) findViewById(R.id.gv_music_backup_num);
		mGvMusic = (ListView) findViewById(R.id.gv_music);
		mBtnBackup = (Button) findViewById(R.id.gv_music_backup);
		mBtnDown = (Button) findViewById(R.id.music_btn_down);
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mTvRight.setOnClickListener(this);
		mGvMusic.setOnItemClickListener(this);
		mBtnBackup.setOnClickListener(this);
		mBtnDown.setOnClickListener(this);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
		mTvRight = (TextView) findViewById(R.id.tv_right);

		mTvCenter.setText("音乐");
		mTvRight.setText("全选");
		mTvRight.setVisibility(View.GONE);
		mTvRight.setTag(false);
	}
	private MusicBackupBean uploadBean;

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) {
			case R.id.music_btn_down:
				//任务列表页面
				Intent intent=new Intent(MusicBackupActivity.this, DownListActivity.class);
				startActivity(intent);
				break;

		case R.id.iv_left:
				if(isUpdate){
					setResult(RESULT_OK);
				}
			finish();
			break;
		case R.id.tv_right: //全选
			boolean bSel = (Boolean) v.getTag();
			for (MusicBackupBean musicBackupBean : mMusicBackupList)
			{
				musicBackupBean.setSelect(!bSel);
			}
			handler.sendEmptyMessage(0);
			v.setTag(!bSel);
			mTvRight.setText(bSel ? "全选" : "取消");
			break;
		case R.id.gv_music_backup:
			goBackUpMusic();
			break;
		}
	}
	
	private void goBackUpMusic()
	{
		if(mMusicBackupList.size()<=0){
			Toast.makeText(MusicBackupActivity.this, "请选择需要上传的文件", Toast.LENGTH_SHORT).show();
			return;
		}
		for (final MusicBackupBean musicBackupBean : mMusicBackupList)
		{
			if (musicBackupBean.isSelect())
			{
				new Thread(new Runnable(){
					@Override
					public void run() {
						uploadBean=musicBackupBean;
						SyncTask syncTask = new SyncTask(MusicBackupActivity.this, FTYPE.MUSIC);
						syncTask.upload(musicBackupBean.getFileInfo0(), MusicBackupActivity.this, false);

					}
				}).start();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		boolean isSel=false;
		MusicBackupBean musicBackupBean = mMusicBackupList.get(arg2);
		if(!musicBackupBean.isSelect()){
			isSel=true;
		}
		for (MusicBackupBean bean:
			 mMusicBackupList) {
			bean.setSelect(false);
		}
		if(isSel) {
			musicBackupBean.setSelect(true);
		}

		handler.sendEmptyMessage(0);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(isUpdate){
				setResult(RESULT_OK);
			}
		}

		return super.onKeyDown(keyCode, event);
	}
}
