package com.chd.yunpan.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.base.UILActivity;
import com.chd.contacts.ui.ContactActivity;
import com.chd.music.ui.MusicActivity;
import com.chd.other.ui.OtherActivity;
import com.chd.photo.ui.PicActivity;
import com.chd.smsbackup.ui.SmsBackActivity;
import com.chd.strongbox.StrongBoxActivity;
import com.chd.yunpan.R;
import com.chd.yunpan.ui.adapter.MenuGridAdapter;
import com.chd.yunpan.ui.entity.MySpaceBean;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.ArrayList;
import java.util.List;

//import org.achartengine.GraphicalView;

/**
 * Created by lxp1 on 2015/10/23.
 */
public class MyspaceActivity extends UILActivity implements OnClickListener, OnItemClickListener {

	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	private TextView mTvSpaceNumber;
	private GridView mGvSpace;

	private String space;
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
		space = getIntent().getStringExtra("space");

		initTitle();
		initResourceId();
		initListener();
		initData();
	}

	private void initData() {
		//模拟数据
		MySpaceBean mySpaceBean2 = new MySpaceBean("小心事", R.drawable.myspace_grid_notepad, StrongBoxActivity.class);
		MySpaceBean mySpaceBean0 = new MySpaceBean("照片", R.drawable.myspace_grid_photo, PicActivity.class);
		MySpaceBean mySpaceBean1 = new MySpaceBean("音乐", R.drawable.myspace_grid_music, MusicActivity.class);

		MySpaceBean mySpaceBean3 = new MySpaceBean("联系人", R.drawable.myspace_grid_contact, ContactActivity.class);
		MySpaceBean mySpaceBean4 = new MySpaceBean("短信", R.drawable.myspace_grid_message, SmsBackActivity.class);
		MySpaceBean mySpaceBean5 = new MySpaceBean("其他", R.drawable.myspace_grid_other, OtherActivity.class);

		meumList.add(mySpaceBean0);
		meumList.add(mySpaceBean1);
		meumList.add(mySpaceBean2);
		meumList.add(mySpaceBean3);
		meumList.add(mySpaceBean4);
		meumList.add(mySpaceBean5);

		handler.sendEmptyMessage(0);

		mTvSpaceNumber.setText(space);
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
//		mTvRight.setText("设置");
	}

	int clickPos=-1;
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
		clickPos=arg2;
		if (arg2 == 3) {
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
		} else if (arg2 == 4) {
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

		}else{
			Intent pageintent = new Intent();
			pageintent.setClass(this, meumList.get(arg2).getCls());
			pageintent.putExtra("callpage", arg2);
			startActivity(pageintent);
			//Toast用于向用户显示一些帮助/提示
		}


	}

	private static final int REQUEST_CODE_PERMISSION_CONTACTS = 100;
	private static final int REQUEST_CODE_PERMISSION_SMS = 101;

	private static final int REQUEST_CODE_SETTING = 300;

	private PermissionListener listener = new PermissionListener() {
		@Override
		public void onSucceed(int requestCode, List<String> grantedPermissions) {
			// 权限申请成功回调。
			if(requestCode == 100) {
				// TODO 相应代码。 联系人
				Intent pageintent = new Intent();
				pageintent.setClass(MyspaceActivity.this, meumList.get(clickPos).getCls());
				pageintent.putExtra("callpage", clickPos);
				startActivity(pageintent);
			} else if(requestCode == 101) {
				// TODO 相应代码。
				Intent pageintent = new Intent();
				pageintent.setClass(MyspaceActivity.this, meumList.get(clickPos).getCls());
				pageintent.putExtra("callpage", clickPos);
				startActivity(pageintent);
			}
		}

		@Override
		public void onFailed(int requestCode, List<String> deniedPermissions) {
			// 权限申请失败回调。

			// 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
			if (AndPermission.hasAlwaysDeniedPermission(MyspaceActivity.this, deniedPermissions)) {
				// 第一种：用默认的提示语。
				AndPermission.defaultSettingDialog(MyspaceActivity.this, REQUEST_CODE_SETTING).show();

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

