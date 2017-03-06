package com.chd.yunpan.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.payfor.ui.OpenSpaceActivity;
import com.chd.proto.LoginResult;
import com.chd.userinfo.ui.UserInfoActivity;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.ui.progressbar.McircleProgressBar;
import com.chd.yunpan.utils.TimeAndSizeUtil;
import com.chd.yunpan.utils.ToastUtils;
import com.chd.yunpan.view.circleimage.CircularProgressButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class netdiskActivity extends Activity implements OnClickListener {

	private CircularProgressButton btn_login = null;

	private TextView mTextTitle;// 套餐容量
	private ImageView mImgCurves;

	private View mViewSpace;
	private McircleProgressBar mProSpace;
	private TextView mTextUserSpace;

	private View mViewFreeapp;
	private McircleProgressBar mProFreeapp;
	private TextView mTextFreeapp;

	private TextView mTextRemainder;

	private View mViewMenu0;
	private View mViewMenu1;
	private View mViewMenu2;
	private View mViewMenu3;

	private LoginResult entity;
	private boolean isLogining=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.netdisk_main);

		initResourceId();
		initListener();
		Dexter.withActivity(this)
				.withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
				.withListener(new PermissionListener() {
					@Override
					public void onPermissionGranted(PermissionGrantedResponse response) {
						//权限授予
					}

					@Override
					public void onPermissionDenied(PermissionDeniedResponse response) {
						//权限拒绝

					}

					@Override
					public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
						token.continuePermissionRequest();
					}
				})
				.check();

	}
	private String spaceStr;

	private void initData() {
		long flow = entity.getFlow();//免费流量
		long uflow = entity.getUflow();//已使用流量
		long space = entity.getSpace();//用户空间bite
		long uspace = entity.getUspace();//用户已经使用的空间bite
		int spacePro=0;
		try{
		spacePro = (int) (uspace*100l/space);
		}catch (Exception e){

		}
		spaceStr = TimeAndSizeUtil.getSize((space - uspace) + "");
		mProSpace.setTxt(spaceStr );
		mProSpace.setProgress(spacePro);
		mTextUserSpace.setText(String.format( "%d%%", spacePro));

		int freePro=uflow==00?0 : (int) (uflow*100/flow);
		String freeStr = TimeAndSizeUtil.getSize((flow - uflow) + "");
		mProFreeapp.setProgress(freePro);
		mProFreeapp.setTxt(freeStr );
		mTextFreeapp.setText(String.format("%d%%", freePro));
		String spaceS=TimeAndSizeUtil.getSize(space+"");
		String flowS=TimeAndSizeUtil.getSize(flow+"");
		mTextTitle.setText(String.format(spaceS+"空间"));

//		mTextRemainder.setText(String.format("本月剩余"+freeStr));
	}

	private void initListener() {
		mViewSpace.setOnClickListener(this);
		mViewFreeapp.setOnClickListener(this);
		mViewMenu0.setOnClickListener(this);
		mViewMenu1.setOnClickListener(this);
		mViewMenu2.setOnClickListener(this);
		mViewMenu3.setOnClickListener(this);
	}

	private void initResourceId() {
		mTextTitle = (TextView) findViewById(R.id.netdisk_title);
		mImgCurves = (ImageView) findViewById(R.id.netdisk_curves);

		mViewSpace = findViewById(R.id.netdisk_space_layout);
		mProSpace = (McircleProgressBar) findViewById(R.id.netdisk_myspacegbar);
		mProSpace.setlinecolor(Color.rgb(143, 111, 242));
		mTextUserSpace = (TextView) findViewById(R.id.netdisk_txtspace_usage);

		mViewFreeapp = findViewById(R.id.netdisk_freedown_layout);
		mProFreeapp = (McircleProgressBar) findViewById(R.id.netdisk_free3gapp);
		mProFreeapp.setlinecolor(Color.rgb(247, 117, 89));
		mTextFreeapp = (TextView) findViewById(R.id.netdisk_fee_apptitle);

//		mTextRemainder = (TextView) findViewById(R.id.netdisk_remainder);

		mViewMenu0 = findViewById(R.id.netdisk_menu0);
		mViewMenu1 = findViewById(R.id.netdisk_menu1);
		mViewMenu2 = findViewById(R.id.netdisk_menu2);
		mViewMenu3 = findViewById(R.id.netdisk_menu3);
	}

	@Override
	public void onClick(View v)
	{


		switch (v.getId())
		{
		case R.id.netdisk_menu0:
		{
			Intent i = new Intent(getApplicationContext(),OpenSpaceActivity.class);
			startActivity(i);
		}
			break;
		case R.id.netdisk_menu1:
		{
			Intent i = new Intent(netdiskActivity.this, CRBTActivity.class);
			startActivity(i);
		}
			break;
		case R.id.netdisk_menu2:
		{
			Intent i = new Intent(netdiskActivity.this, SettingActivity.class);
			startActivity(i);
		}
			break;
		case R.id.netdisk_menu3:
		{
			Intent i = new Intent(netdiskActivity.this, UserInfoActivity.class);
			startActivity(i);
		}
			break;
		case R.id.netdisk_space_layout:
		{
			Intent i = new Intent(netdiskActivity.this, MyspaceActivity.class);
			i.putExtra("space",spaceStr);
			startActivity(i);
		}
			break;
		case R.id.netdisk_freedown_layout:
		{
			Intent i = new Intent(netdiskActivity.this, FreeDownActivity.class);
			startActivity(i);
		}
			break;
		default:
			break;
		}
	}

	private  void bgtask()
	{
		ShareUtils shareUtils = new ShareUtils(this);
		entity = shareUtils.getLoginEntity();
		if (entity==null)
		{
			Toast.makeText(this,"用户信息错误,请重新登录",Toast.LENGTH_LONG);
			Intent i = new Intent(this,LoginActivity.class);
			startActivity(i);
		}
	}

	@Override
	protected void  onResume()
	{
		super.onResume();
		bgtask();
		initData();
	}

	private boolean canLogin = true;

	@Override
	protected void onPause() {
		canLogin = false;
		super.onPause();
	}

	private long exitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK&&isLogining){
			if(!canLogin){

			}else {
				isLogining = false;
				canLogin = false;
				btn_login.setProgress(0);
				return false;
			}
		}
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if(System.currentTimeMillis() - exitTime > 2000)
			{
				ToastUtils.toast(this, "再按一次退出程序");
				exitTime = System.currentTimeMillis();
			}
			else
			{
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
