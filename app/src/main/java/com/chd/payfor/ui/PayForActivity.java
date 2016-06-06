package com.chd.payfor.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.TClient;
import com.chd.payfor.entity.PayForFlag;
import com.chd.payfor.ui.dialog.PayForOpenSpaceDlg;
import com.chd.payfor.ui.dialog.PayForOpenSpaceDlg.OnConfirmListener;
import com.chd.proto.Errcode;
import com.chd.proto.RetHead;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;

public class PayForActivity extends Activity implements OnClickListener, OnConfirmListener
{
	private ImageView mIvLeft;
	private TextView mTvCenter;
	
	private TextView mEditMobile;
	private TextView mTextMoney;
	private Button mBtnPayFor;
	
	private String strMoney;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_openspace_payfor);
		
		initTitle();
		initResourceId();
		initListener();
		initData();
	}
	
	private void initData()
	{
		strMoney = getIntent().getStringExtra(PayForFlag.FLAG_PAY_TYPE);
		mTextMoney.setText(strMoney);
		
		ShareUtils shareUtils = new ShareUtils(this);
		mEditMobile.setText(shareUtils.getUsername());
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mBtnPayFor.setOnClickListener(this);
	}

	private void initResourceId() {
		mEditMobile = (TextView) findViewById(R.id.openspace_payfor_mobile_edit);
		mTextMoney = (TextView) findViewById(R.id.openspace_payfor_money_txt);
		mBtnPayFor = (Button) findViewById(R.id.openspace_payfor_btn);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);

		mTvCenter.setText("确认支付");
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
		case R.id.iv_left:
			finish();
			break;
		case R.id.openspace_payfor_btn:
		{
			PayForOpenSpaceDlg payForOpenSpaceDlg = new PayForOpenSpaceDlg(this);
			payForOpenSpaceDlg.setOnConfirmListener(new PayForOpenSpaceDlg.OnConfirmListener() 
			{
				
				@Override
				public void confirm() 
				{
					PayForOpenSpaceDlg payForOpenSpaceDlg = new PayForOpenSpaceDlg(PayForActivity.this);
					payForOpenSpaceDlg.setOnConfirmListener(PayForActivity.this);
					payForOpenSpaceDlg.showMyDialog(String.format("请再次确认订购“沃”空间业务，资费%s", strMoney));
				}
				
				@Override
				public void cancel() 
				{
					
				}
				
			});
			payForOpenSpaceDlg.showMyDialog(String.format("您即将订购“沃空间”业务，订购成功后即可享受会员空间和定向流量，资费%s，是否确认订购？", strMoney));
		}
			break;
		default:
			break;
		}
	}

	@Override
	public void confirm() 
	{
		final ProgressDialog dialog=new ProgressDialog(this);
		dialog.setMessage("正在加载");
		dialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final RetHead retHead = TClient.getinstance().OrderByVac(true);

					if(Errcode.SUCCESS==retHead.getRet()){
						//成功
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dialog.dismiss();
								Intent intent = new Intent(PayForActivity.this, PayForResultActivity.class);
								intent.putExtra(PayForFlag.FLAG_PAY_VALUE, getIntent().getStringExtra(PayForFlag.FLAG_PAY_VALUE));
								intent.putExtra(PayForFlag.FLAG_PAY_RESULT, PayForFlag.FLAG_PAY_SUCCESS);
								startActivityForResult(intent, 1000);
							}
						});


					}else{
						//失败
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dialog.dismiss();
								String msg=retHead.getMsg();
								if("User Mobile Is Not Xinjiang Number!".equals(msg)){
									Toast.makeText(PayForActivity.this, "仅限新疆联通地区订购", Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(PayForActivity.this, msg, Toast.LENGTH_SHORT).show();
								}
								Intent intent = new Intent(PayForActivity.this, PayForResultActivity.class);
								intent.putExtra(PayForFlag.FLAG_PAY_VALUE, getIntent().getStringExtra(PayForFlag.FLAG_PAY_VALUE));
								intent.putExtra(PayForFlag.FLAG_PAY_RESULT, PayForFlag.FLAG_PAY_FAILED);
								startActivityForResult(intent, 1000);
							}
						});
					}

				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dialog.dismiss();
							Toast.makeText(PayForActivity.this, "开小差了，请重试", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();


	}

	@Override
	public void cancel() 
	{
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && requestCode == 1000)
		{
			setResult(resultCode, data);
			finish();
		}
	}
	
}
