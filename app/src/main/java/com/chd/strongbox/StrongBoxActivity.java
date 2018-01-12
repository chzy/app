package com.chd.strongbox;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chd.base.UILActivity;
import com.chd.contacts.ui.ContactActivity;
import com.chd.music.ui.MusicActivity;
import com.chd.notepad.ui.activity.NotepadActivity;
import com.chd.other.ui.OtherActivity;
import com.chd.photo.ui.PicActivity;
import com.chd.smsbackup.ui.SmsBackActivity;
import com.chd.video.VideoListActivity;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.utils.ToastUtils;
import com.lockscreen.pattern.GuideGesturePasswordActivity;
import com.lockscreen.pattern.UnlockGesturePasswordActivity;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;


/**
 * User: Liumj(liumengjie@365tang.cn)
 * Date: 2017-02-27
 * Time: 14:01
 * describe:
 */
public class StrongBoxActivity extends UILActivity implements View.OnClickListener {


	ImageView ivLeft;

	TextView tvTitle;
	LinearLayout llOne;
	LinearLayout llTwo;
	LinearLayout llThree;
	LinearLayout llFour;
	LinearLayout llFive;
	LinearLayout llSix;
	LinearLayout llSeven;
	LinearLayout llEight;
	LinearLayout llNine;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_strongbox);
		initView();

		tvTitle.setText("小心事");
		if (UILApplication.getInstance().getLockPatternUtils().savedPatternExists()) {
			if (!getIntent().getBooleanExtra("unlock", false)) {
				Intent i = new Intent(this, UnlockGesturePasswordActivity.class);
				startActivity(i);
				finish();
			}
		} else {
			Intent i = new Intent(this, GuideGesturePasswordActivity.class);
			startActivity(i);
			finish();
		}


	}

	private void initView() {
		ivLeft= (ImageView) findViewById(R.id.iv_left);
		tvTitle= (TextView) findViewById(R.id.tv_center);
		llOne= (LinearLayout) findViewById(R.id.ll_one);
		llTwo= (LinearLayout) findViewById(R.id.ll_two);
		llThree= (LinearLayout) findViewById(R.id.ll_three);
		llFour= (LinearLayout) findViewById(R.id.ll_four);
		llFive= (LinearLayout) findViewById(R.id.ll_five);
		llSix= (LinearLayout) findViewById(R.id.ll_six);
		llSeven= (LinearLayout) findViewById(R.id.ll_seven);
		llEight= (LinearLayout) findViewById(R.id.ll_eight);
		llNine= (LinearLayout) findViewById(R.id.ll_nine);


		ivLeft.setOnClickListener(this);
		llOne.setOnClickListener(this);
		llTwo.setOnClickListener(this);
		llThree.setOnClickListener(this);
		llFour.setOnClickListener(this);
		llFive.setOnClickListener(this);
		llSix.setOnClickListener(this);
		llSeven.setOnClickListener(this);
		llEight.setOnClickListener(this);
		llNine.setOnClickListener(this);
	}


	private static final int REQUEST_CODE_PERMISSION_CONTACTS = 100;
	private static final int REQUEST_CODE_PERMISSION_SMS = 101;
	private static final int REQUEST_CODE_PERMISSION_SD = 102;
	private static final int REQUEST_CODE_SETTING = 300;
	Class cls;

	@Override
	public void onClick(View view) {
		Intent intent = null;
		switch (view.getId()) {
			case R.id.iv_left:
				onBackPressed();
				break;
			case R.id.ll_one:
				//照片
				cls = PicActivity.class;
				AndPermission.with(this)
						.requestCode(REQUEST_CODE_PERMISSION_SD)
						.permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_CONTACTS)
						// rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
						.rationale(new RationaleListener() {
							@Override
							public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
							}
						})
						.callback(listener)
						.start();

				break;
			case R.id.ll_two:
				//视频
				cls = VideoListActivity.class;
				AndPermission.with(this)
						.requestCode(REQUEST_CODE_PERMISSION_SD)
						.permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_CONTACTS)
						// rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
						.rationale(new RationaleListener() {
							@Override
							public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
							}
						})
						.callback(listener)
						.start();

				break;
			case R.id.ll_three:
				//录音
				cls = VoiceActivity.class;
				AndPermission.with(this)
						.requestCode(REQUEST_CODE_PERMISSION_SD)
						.permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_CONTACTS)
						// rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
						.rationale(new RationaleListener() {
							@Override
							public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
							}
						})
						.callback(listener)
						.start();

				break;
			case R.id.ll_four:
				//音乐
				cls = MusicActivity.class;
				AndPermission.with(this)
						.requestCode(REQUEST_CODE_PERMISSION_SD)
						.permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_CONTACTS)
						// rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
						.rationale(new RationaleListener() {
							@Override
							public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
							}
						})
						.callback(listener)
						.start();

				break;
			case R.id.ll_five:
				//短信
				AndPermission.with(this)
						.requestCode(REQUEST_CODE_PERMISSION_SMS)
						.permission(Manifest.permission.WRITE_CONTACTS)
						// rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
						.rationale(new RationaleListener() {
							@Override
							public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
							}
						})
						.callback(listener)
						.start();

				break;
			case R.id.ll_six:
				//通讯录
				// 申请单个权限。联系人
				AndPermission.with(this)
						.requestCode(REQUEST_CODE_PERMISSION_CONTACTS)
						.permission(Manifest.permission.WRITE_CONTACTS)
						// rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
						.rationale(new RationaleListener() {
							@Override
							public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
							}
						})
						.callback(listener)
						.start();

				break;
			case R.id.ll_seven:
				//文档
				cls = OtherActivity.class;
				AndPermission.with(this)
						.requestCode(REQUEST_CODE_PERMISSION_SD)
						.permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_CONTACTS)
						// rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
						.rationale(new RationaleListener() {
							@Override
							public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
							}
						})
						.callback(listener)
						.start();
				break;
			case R.id.ll_eight:
				//记事本
				cls = NotepadActivity.class;
				AndPermission.with(this)
						.requestCode(REQUEST_CODE_PERMISSION_SD)
						.permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_CONTACTS)
						// rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
						.rationale(new RationaleListener() {
							@Override
							public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
							}
						})
						.callback(listener)
						.start();
				break;
			case R.id.ll_nine:
				ToastUtils.toast(this, "敬请期待");
				break;
		}
	}


	private PermissionListener listener = new PermissionListener() {
		@Override
		public void onSucceed(int requestCode, List<String> grantedPermissions) {
			// 权限申请成功回调。
			if (requestCode == 100) {
				// TODO 相应代码。 联系人
				Intent intent = new Intent(StrongBoxActivity.this, ContactActivity.class);
				startActivity(intent);
			} else if (requestCode == 101) {
				// TODO 相应代码。
				Intent pageintent = new Intent();
				pageintent.setClass(StrongBoxActivity.this, SmsBackActivity.class);
				startActivity(pageintent);
			} else if (requestCode == 102) {
				//相应代码
				Intent pageintent = new Intent();
				pageintent.setClass(StrongBoxActivity.this, cls);
				startActivity(pageintent);
			}
		}

		@Override
		public void onFailed(int requestCode, List<String> deniedPermissions) {
			// 权限申请失败回调。

			// 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
			if (AndPermission.hasAlwaysDeniedPermission(StrongBoxActivity.this, deniedPermissions)) {
				// 第一种：用默认的提示语。
				AndPermission.defaultSettingDialog(StrongBoxActivity.this, REQUEST_CODE_SETTING).show();

				// 第二种：用自定义的提示语。
				// AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING)
				// .setTitle("权限申请失败")
				// .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
				// .setPositiveButton("好，去设置")
				// .show();

				// 第三种：自定义dialog样式。
				// SettingService settingService =
				//    AndPermission.defineSettingDialog(this, REQUEST_CODE_SETTING);
				// 你的dialog点击了确定调用：
				// settingService.execute();
				// 你的dialog点击了取消调用：
				// settingService.cancel();
			}
		}
	};

}
