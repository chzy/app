package com.chd.payfor.ui;

import com.chd.payfor.entity.PayForFlag;
import com.chd.yunpan.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OpenSpaceActivity extends Activity implements OnClickListener
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_openspace);
		
		initTitle();
		initResourceId();
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
			Intent intent = new Intent(this, PayForActivity.class);
			intent.putExtra(PayForFlag.FLAG_PAY_TYPE, mTextSixPrice.getText().toString());
			intent.putExtra(PayForFlag.FLAG_PAY_VALUE, mTextSixValue.getText().toString());
			startActivityForResult(intent, 1000);
		}
			break;
		case R.id.openspace_ten_btn:
		{
			Intent intent = new Intent(this, PayForActivity.class);
			intent.putExtra(PayForFlag.FLAG_PAY_TYPE, mTextTenPrice.getText().toString());
			intent.putExtra(PayForFlag.FLAG_PAY_VALUE, mTextTenValue.getText().toString());
			startActivityForResult(intent, 1000);
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
	
}
