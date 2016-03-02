package com.chd.yunpan.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.chd.yunpan.R;
import com.chd.yunpan.myclass.Album;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.ui.adapter.AlbumGridAdapter;
import com.chd.yunpan.utils.AlbumGridViewAdapterManager;

public class AutoPhotoBackupChooseAlbumAvtivity extends Activity implements
		OnClickListener {

	private View back = null;

	private GridView gv = null;

	private Button confirm = null;

	private AlbumGridAdapter adapter = null;

	private AlbumGridViewAdapterManager agva = null;

	private List<Album> albums = null;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = View
				.inflate(this, R.layout.lz_photo_backup_choose_album, null);
		this.setContentView(v);
//		this.overridePendingTransition(R.anim.activity_right_in,
//				R.anim.activity_left_out);
		initView();
	}

	private void initView() {
		back = this.findViewById(R.id.photoChooseAlbumBack);
		back.setOnClickListener(this);

		confirm = (Button) this.findViewById(R.id.photoChooseAlbumConfirm);
		confirm.setOnClickListener(this);

		gv = (GridView) this.findViewById(R.id.photoChooseAlbumList);
		agva = new AlbumGridViewAdapterManager(this);

		adapter = agva.getAdapter();
		adapter.setButton(confirm);
		gv.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.photoChooseAlbumBack:
			back2Front();
			break;
		case R.id.photoChooseAlbumConfirm:
			albums = adapter.getAlbums();
			saveChoice();
			//Toast.makeText(AutoPhotoBackupChooseAlbumAvtivity.this, "添加成功", 1).show();
			break;
		}

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
//			AutoPhotoBackupChooseAlbumAvtivity.this.stopService(new Intent(
//					AutoPhotoBackupChooseAlbumAvtivity.this,
//					UpLoadService.class));
//			AutoPhotoBackupChooseAlbumAvtivity.this.startService(new Intent(
//					AutoPhotoBackupChooseAlbumAvtivity.this,
//					UpLoadService.class));
//			back2Front();
		}
	};

	private void saveChoice() {
		
		List<Album> list2UpLoad = new ArrayList<Album>();
		if (albums == null)
			return;
		ShareUtils utils = new ShareUtils(this);
		utils.clearPath();
		for (Album a : albums) {
			if(a.isFlag()){
				utils.addPath(a.getFirstImagePath().substring(0, a.getFirstImagePath().lastIndexOf("/")));
			}
				
		
			
		}	
		Toast.makeText(this, "添加成功", 0).show(); 
		if(utils.getAutoPhotoBack()){
//			Intent i = new Intent(this,AutoBackupPhotoService.class);
//			stopService(i);
//			startService(i);
		}
		back2Front();
		
		//new Thread(new AddFileNeedUpLoad(list2UpLoad, this, handler)).start();

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			back2Front();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void back2Front() {
		this.finish();

	}
}
