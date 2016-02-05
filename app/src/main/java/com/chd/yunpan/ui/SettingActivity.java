package com.chd.yunpan.ui;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.chd.TClient;
import com.chd.yunpan.R;
import com.chd.yunpan.ui.dialog.DialogPopupFromBottom2;
import com.chd.yunpan.ui.dialog.DialogPopupFromBottom2.OnConfirmListener;
import com.chd.yunpan.utils.TimeAndSizeUtil;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class SettingActivity extends Activity implements OnClickListener {
	private ImageView mIvLeft;
	private TextView mTvCenter;
	
	private ImageView mImgIsWifi;
	private View vHelp;
	
	private View clear = null;

	private ViewGroup v = null;

	private View about = null;

	private View update = null;

	private String appurl=null;

	private TextView settingROM;

	public Handler checkUpdateHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case -1:
				//获取更新失败
				Toast.makeText(SettingActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
				break;
			case 0:
				createUpdateDialog();
				break;
			case 1:

				break;

			default:
				break;
			}

		}

	};

	private File file;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		v = (ViewGroup) View.inflate(this, R.layout.setting_layout, null);
		this.setContentView(v);
		file = StorageUtils.getCacheDirectory(SettingActivity.this);
		initView();

	}

	private void initView() {
		
		mImgIsWifi = (ImageView) findViewById(R.id.setting_iswifi);
		mImgIsWifi.setOnClickListener(this);
		mImgIsWifi.setImageResource(R.drawable.setting_iswifi_on);
		mImgIsWifi.setTag(true);
		
		vHelp = findViewById(R.id.setting_help);
		vHelp.setOnClickListener(this);
		
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
		mTvCenter.setText("设置");
		mIvLeft.setOnClickListener(this);
		
		clear = this.findViewById(R.id.settingClearROM);
		clear.setOnClickListener(this);

		about = this.findViewById(R.id.settingAbout);
		about.setOnClickListener(this);

		update = this.findViewById(R.id.settingCheckUpdate);
		update.setOnClickListener(this);

		settingROM = (TextView) findViewById(R.id.settingROM);
		size = 0;
		getFileSize(file);
		settingROM.setText(TimeAndSizeUtil.getSize(size + ""));
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.iv_left:
		{
			finish();
		}
			break;
		case R.id.settingAbout:
			Intent aboutIntent = new Intent(this, HelpActivity.class);
			startActivity(aboutIntent);
			break;

		case R.id.settingBack:
			finish();
			break;
		case R.id.settingClearROM:
			createDialog();
			break;

		case R.id.settingCheckUpdate:
			//TODO 更新提示
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						TClient.getinstance().CheckVer("4.0.0");
					} catch (Exception e) {
						checkUpdateHandler.sendEmptyMessage(-1);
					}
				}
			}).start();


			if (appurl==null)
				Toast.makeText(this, "当前是最新版本", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(this, "检测到新版本", Toast.LENGTH_SHORT).show();
			// 更新检查
			new Thread() {
				public void run() {
					SystemClock.sleep(4000);
				//checkUpdateHandler.sendEmptyMessage(0);
				}

			}.start();
			break;
		case R.id.setting_iswifi:
		{
			boolean bol = (Boolean) v.getTag();
			mImgIsWifi.setImageResource(bol ? R.drawable.setting_iswifi_off : R.drawable.setting_iswifi_on);
			mImgIsWifi.setTag(!bol);
		}
			break;
		case R.id.setting_help:
		{
		//TODO 帮助


			
		}
			break;
		default:
			break;
		}

	}


	private void createUpdateDialog() {
		if (SettingActivity.this != null) {
			final Dialog d = new Dialog(this);
			d.setContentView(R.layout.lz_update);
			Button b = (Button) d.findViewById(R.id.settingUpdate0Button);

			Window dialogWindow = d.getWindow();
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			dialogWindow.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			lp.width = LayoutParams.MATCH_PARENT;
			lp.height = LayoutParams.WRAP_CONTENT;
			lp.x = 0;
			dialogWindow
					.setBackgroundDrawableResource(R.drawable.background_dialog);
			b.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					d.dismiss();
				}
			});
			d.show();
		}

	}

	private void createDialog() {

		DialogPopupFromBottom2 bottom = new DialogPopupFromBottom2(this);
		bottom.setOnConfirmListener(new OnConfirmListener() {

			@Override
			public void confirm() {

				clearCache(file);
				size = 0;
				getFileSize(file);
				settingROM.setText(TimeAndSizeUtil.getSize(size + ""));
				Toast.makeText(SettingActivity.this, "清理完成", 0).show();
			}
		});
		bottom.showMyDialog();

	}

	private long size;

	public void clearCache(File f) {
		if (f.isDirectory()) {
			File[] fs = f.listFiles();
			for (int i = 0; i < fs.length; i++) {
				clearCache(fs[i]);
			}
		} else {
			f.delete();
		}
	}

	public void getFileSize(File f) {
		if (f.isDirectory()) {
			File[] fs = f.listFiles();
			for (int i = 0; i < fs.length; i++) {
				getFileSize(fs[i]);
			}
		} else {
			size += f.length();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		}

		return super.onKeyDown(keyCode, event);
	}

}
