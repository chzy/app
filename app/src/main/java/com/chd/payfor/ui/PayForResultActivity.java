package com.chd.payfor.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.payfor.entity.PayForFlag;
import com.chd.yunpan.R;

public class PayForResultActivity extends Activity implements OnClickListener
{

	private ImageView mIvLeft;
	private TextView mTvCenter;
	
	private TextView mTextResult;
	private Button mBtnResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_openspace_payforresult);
		
		initTitle();
		initResourceId();
		initListener();
		initData();
	}
	
	private void initData()
	{
		String status = getIntent().getStringExtra(PayForFlag.FLAG_PAY_RESULT);
		String strValue = getIntent().getStringExtra(PayForFlag.FLAG_PAY_VALUE);
		if (PayForFlag.FLAG_PAY_SUCCESS.equals(status))
		{
			mTextResult.setText(String.format("尊敬的用户：您已经成功开通沃空间，即刻享有%s，现在可以返回首页查看自己的流量，感谢你的使用。", strValue));
			mBtnResult.setText("返回首页");
			mBtnResult.setTag(true);
		}
		else
		{
			mTextResult.setText("对不起，支付失败，请重新支付。");
			mBtnResult.setText("返回上级");
			mBtnResult.setTag(false);
		}
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mBtnResult.setOnClickListener(this);
	}

	private void initResourceId() {
		mTextResult = (TextView) findViewById(R.id.openspace_payfor_result_txt);
		mBtnResult = (Button) findViewById(R.id.openspace_payfor_result_btn);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);

		mTvCenter.setText("支付结果");
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
		case R.id.iv_left:
			finish();
			break;
		case R.id.openspace_payfor_result_btn:
		{
			boolean bSucc = (Boolean) v.getTag();
			if (bSucc)
			{
				Intent intent = new Intent();
				setResult(1000, intent);
			}
			
			finish();
		}
			break;
		default:
			break;
		}
	}
	
}
