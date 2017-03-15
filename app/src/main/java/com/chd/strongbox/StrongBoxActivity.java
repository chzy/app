package com.chd.strongbox;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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


	private static final int REQUEST_CODE_PERMISSION_CONTACTS = 100;
	private static final int REQUEST_CODE_PERMISSION_SMS = 101;

	private static final int REQUEST_CODE_SETTING = 300;

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
				intent=new Intent(this, VideoListActivity.class);
				startActivity(intent);
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
				//音乐
				intent=new Intent(this, MusicActivity.class);
				startActivity(intent);
				break;
			case R.id.ll_seven:
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
						.send();
				break;
			case R.id.ll_eight:
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
						.send();
				break;
			case R.id.ll_nine:
				ToastUtils.toast(this,"敬请期待");
				break;
		}
	}


	private PermissionListener listener = new PermissionListener() {
		@Override
		public void onSucceed(int requestCode, List<String> grantedPermissions) {
			// 权限申请成功回调。
			if(requestCode == 100) {
				// TODO 相应代码。 联系人
				Intent intent=new Intent(StrongBoxActivity.this, ContactActivity.class);
				startActivity(intent);
			} else if(requestCode == 101) {
				// TODO 相应代码。
				Intent pageintent = new Intent();
				pageintent.setClass(StrongBoxActivity.this, SmsBackActivity.class);
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
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		// 只需要调用这一句，其它的交给AndPermission吧，最后一个参数是PermissionListener。
		AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
	}
}
