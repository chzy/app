package com.chd.payfor.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.TClient;
import com.chd.payfor.entity.PayForFlag;
import com.chd.payfor.ui.dialog.PayForOpenSpaceDlg;
import com.chd.proto.Errcode;
import com.chd.proto.LoginResult;
import com.chd.proto.RetHead;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.ui.LoginActivity;

public class OpenSpaceActivity extends Activity implements OnClickListener,PayForOpenSpaceDlg.OnConfirmListener
{
	private ImageView mIvLeft;
	private TextView mTvCenter;
	
	private TextView mTextSixOpen;
	private TextView mTextSixValue;
	private TextView mTextSixPrice;
	private TextView mTextTenOpen;
	private TextView mTextTenValue;
	private TextView mTextTenPrice;
	
	private Button mBtnSixOpen;
	private Button mBtnTenOpen;
	private LinearLayout mSixLayout;
	private LinearLayout mNineLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_openspace);
		
		initTitle();
		initResourceId();

		ShareUtils sareUtils = new ShareUtils(this);
		LoginResult loginEntity = sareUtils.getLoginEntity();
		if(loginEntity.getSpace()>(60*1024*1024*1024l)&&loginEntity.getSpace()<70l*1024*1024*1024){
			mSixLayout.setVisibility(View.VISIBLE);
			mNineLayout.setVisibility(View.GONE);
		}
		if((120l * 1024 * 1024 * 1024) < loginEntity.getSpace()){
			mBtnTenOpen.setText("退订");
		}


		initListener();
		initData();
	}
	
	private void initData()
	{
		
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mBtnSixOpen.setOnClickListener(this);
		mBtnTenOpen.setOnClickListener(this);
	}

	private void initResourceId() {
		mBtnSixOpen = (Button) findViewById(R.id.openspace_six_btn);
		mBtnTenOpen = (Button) findViewById(R.id.openspace_ten_btn);
		mSixLayout = (LinearLayout) findViewById(R.id.openspace_six_layout);
		mNineLayout = (LinearLayout) findViewById(R.id.openspace_ten_layout);

		mTextSixOpen = (TextView) findViewById(R.id.openspace_six_text);
		mTextTenOpen = (TextView) findViewById(R.id.openspace_ten_text);
		mTextSixValue = (TextView) findViewById(R.id.openspace_six_value);
		mTextTenValue = (TextView) findViewById(R.id.openspace_ten_value);
		mTextSixPrice = (TextView) findViewById(R.id.openspace_six_price);
		mTextTenPrice = (TextView) findViewById(R.id.openspace_ten_price);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);

		mTvCenter.setText("开通空间");
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
		case R.id.iv_left:
			finish();
			break;
		case R.id.openspace_six_btn:
		{

			Uri smsToUri = Uri.parse("smsto:106558898");

			Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

			intent.putExtra("sms_body", "TDZC");

			startActivity(intent);
		}
			break;
		case R.id.openspace_ten_btn:
		{
			if("退订".equals(mBtnTenOpen.getText().toString())){
				PayForOpenSpaceDlg payForOpenSpaceDlg = new PayForOpenSpaceDlg(this);
				payForOpenSpaceDlg.setOnConfirmListener(new PayForOpenSpaceDlg.OnConfirmListener()
				{

					@Override
					public void confirm()
					{
						PayForOpenSpaceDlg payForOpenSpaceDlg = new PayForOpenSpaceDlg(OpenSpaceActivity.this);
						payForOpenSpaceDlg.setOnConfirmListener(OpenSpaceActivity.this);
						payForOpenSpaceDlg.showMyDialog(String.format("请再次确认退订“沃”空间业务，资费%s", "10元"));
					}

					@Override
					public void cancel()
					{

					}

				});
				payForOpenSpaceDlg.showMyDialog(String.format("您即将退订“沃空间”业务，成功后即不再享会员空间和定向流量，资费%s，是否确认退订？", "10元"));
			}else{
				Intent intent = new Intent(this, PayForActivity.class);
				intent.putExtra(PayForFlag.FLAG_PAY_TYPE, mTextTenPrice.getText().toString());
				intent.putExtra(PayForFlag.FLAG_PAY_VALUE, mTextTenValue.getText().toString());
				startActivityForResult(intent, 1000);
			}
		}
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && requestCode == 1000)
		{
			finish();
		}
	}

	@Override
	public void confirm() {
		final ProgressDialog dialog=new ProgressDialog(this);
		dialog.setMessage("正在加载");
		dialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final RetHead retHead = TClient.getinstance().OrderByVac(false);
					if(Errcode.SUCCESS==retHead.getRet()){
						//成功
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dialog.dismiss();
								Toast.makeText(OpenSpaceActivity.this, "退订成功", Toast.LENGTH_SHORT).show();
								ShareUtils shareUtils = new ShareUtils(OpenSpaceActivity.this);
								shareUtils.setAutoLogin(false);
								Intent intent=new Intent(OpenSpaceActivity.this, LoginActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
								finish();
								}
						});
					}else{
						//失败
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								String msg=retHead.getMsg();
								Toast.makeText(OpenSpaceActivity.this, msg, Toast.LENGTH_SHORT).show();
							}
						});
					}

				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dialog.dismiss();
							Toast.makeText(OpenSpaceActivity.this, "开小差了，请重试", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();




	}

	@Override
	public void cancel() {

	}
}
