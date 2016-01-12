package com.chd.yunpan.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.ui.dialog.DialogPopupFromBottom3;

public class AutoPhotoBackupsActivity extends Activity implements
		OnClickListener {

	private boolean openState = false;

	private ImageView disPlayPhoto = null;

	private Button open = null;

	private View chooseAlbum = null;

	private View close = null;

	private ImageView switcher = null;

	private View back = null;

	private View opened = null;

	private boolean switcherState = false;

	private ImageView image2 = null;
	
	private ShareUtils  utils;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.lz_photo_backups);
		utils = new ShareUtils(this);
		switcherState = utils.getAutoPhotoBack();

		initView();
	}

	private void initView() {

		disPlayPhoto = (ImageView) this.findViewById(R.id.photoBackupImage);

		image2 = (ImageView) this.findViewById(R.id.photoBackupImage2);

		close = this.findViewById(R.id.photoBackupClose);
		close.setOnClickListener(this);

		open = (Button) this.findViewById(R.id.photoBackupOpen);
		open.setOnClickListener(this);

		chooseAlbum = this.findViewById(R.id.photoBackupChooseAlbum);
		chooseAlbum.setOnClickListener(this);

		switcher = (ImageView) this
				.findViewById(R.id.photoBackupsVideoSwitcher);
		switcher.setOnClickListener(this);

		opened = this.findViewById(R.id.photoBackupOpened);

		back = this.findViewById(R.id.photoBackupBack);
		back.setOnClickListener(this);

		setAvtivity();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.photoBackupOpen:
			utils.setAutoPhotoBack(true);
			setAvtivity();
			break;
		case R.id.photoBackupChooseAlbum:
			Intent chooseIntent = new Intent(this,
					AutoPhotoBackupChooseAlbumAvtivity.class);
			startActivity(chooseIntent);
			break;
		case R.id.photoBackupsVideoSwitcher:
			switcherChange();
			break;
		case R.id.photoBackupBack:
			back2Main();
			break;
		case R.id.photoBackupClose:

			DialogPopupFromBottom3 bottom = new DialogPopupFromBottom3(this);
			bottom.setOnConfirmListener(new DialogPopupFromBottom3.OnConfirmListener() {

				public void confirm() {

					ShareUtils utils = new ShareUtils(AutoPhotoBackupsActivity.this);
					utils.setAutoPhotoBack(false);
					utils.clearPath();
					setAvtivity();
					back2Main();
				}
			});
			bottom.showMyDialog();
			break;
		}
	}

	private void back2Main() {
//		Intent mainIntent = new Intent(this, MainActivity.class);
//		mainIntent.putExtra("currentItem", 2);
//		this.startActivity(mainIntent);
		this.finish();

	}

	private void setAvtivity() {
		ShareUtils utils = new ShareUtils(this);
		openState = utils.getAutoPhotoBack();
		if (openState) {

			//switcherState = SharePreUtil.getAutoVideoBackup(this);

			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.f_music);

			disPlayPhoto.setImageBitmap(bitmap);

			image2.setVisibility(View.VISIBLE);

			opened.setVisibility(View.VISIBLE);
			open.setVisibility(View.GONE);
			if (switcherState) {
				switcher.setImageResource(R.drawable.ab_on);
			} else {
				switcher.setImageResource(R.drawable.ab_off);
			}
		} else {

			disPlayPhoto.setImageResource(R.drawable.auto_photo_zone);

			image2.setVisibility(View.INVISIBLE);
			open.setVisibility(View.VISIBLE);
			opened.setVisibility(View.GONE);
		}
	}

	private void switcherChange() {
		switcherState = !switcherState;

		if (switcherState) {
			switcher.setImageResource(R.drawable.ab_on);
		} else {
			switcher.setImageResource(R.drawable.ab_off);
		}

	//	SharePreUtil.saveAutoVideoBackup(this, switcherState);
		

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode==KeyEvent.KEYCODE_BACK){
			back2Main();
		}
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

}
