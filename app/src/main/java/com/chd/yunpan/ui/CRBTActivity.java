package com.chd.yunpan.ui;

import com.chd.yunpan.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class CRBTActivity extends Activity implements OnClickListener
{
	private ImageView mIvLeft;
	private TextView mTvCenter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_crbt);
		
		initTitle();
		initListener();
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);

		mTvCenter.setText("精彩炫铃");
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId())
		{
		case R.id.iv_left:
		{
			finish();
		}
			break;
		default:
			break;
		}
	}
	
}
