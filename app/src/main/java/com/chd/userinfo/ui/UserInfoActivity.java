package com.chd.userinfo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.userinfo.ui.entity.UserInfoFlag;
import com.chd.yunpan.R;

public class UserInfoActivity extends Activity implements OnClickListener
{
	private ImageView mIvLeft;
	private TextView mTvCenter;
	private View mViewHead, mViewName, mViewSex, mViewAge, mViewMobile, mViewPwd;
	private TextView mTextName, mTextSex, mTextAge, mTextMobile, mTextPwd;
	private ImageView mImgHead;
	private Button mBtnLogout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_userinfo);
		
		initTitle();
		initResourceId();
		initListener();
		initData();
	}
	
	private void initData()
	{
		mImgHead.setImageResource(R.drawable.userinfo_head_src);
		mTextName.setText("小艾");
		mTextSex.setText("女");
		mTextAge.setText("20");
		mTextMobile.setText("152****6666");
		mTextPwd.setText("******");
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mViewHead.setOnClickListener(this);
		mViewName.setOnClickListener(this);
		mViewSex.setOnClickListener(this);
		mViewAge.setOnClickListener(this);
		mViewMobile.setOnClickListener(this);
		mViewPwd.setOnClickListener(this);
		mBtnLogout.setOnClickListener(this);
	}

	private void initResourceId() {
		mViewHead = findViewById(R.id.userinfo_head_layout);
		mViewName = findViewById(R.id.userinfo_name_layout);
		mViewSex = findViewById(R.id.userinfo_sex_layout);
		mViewAge = findViewById(R.id.userinfo_age_layout);
		mViewMobile = findViewById(R.id.userinfo_mobile_layout);
		mViewPwd = findViewById(R.id.userinfo_pwd_layout);
		
		mImgHead = (ImageView) findViewById(R.id.userinfo_head_img);
		mTextName = (TextView) findViewById(R.id.userinfo_name_txt);
		mTextSex = (TextView) findViewById(R.id.userinfo_sex_txt);
		mTextAge = (TextView) findViewById(R.id.userinfo_age_txt);
		mTextMobile = (TextView) findViewById(R.id.userinfo_mobile_txt);
		mTextPwd = (TextView) findViewById(R.id.userinfo_pwd_txt);
		
		mBtnLogout = (Button) findViewById(R.id.userinfo_logout);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);

		mTvCenter.setText("个人信息");
	}

	@Override
	public void onClick(View v) 
	{
		Intent intent = new Intent(this, UserInfoEditActivity.class);
		int edit_type = -1;
		String edit_value = "";
		switch (v.getId()) 
		{
		case R.id.userinfo_head_layout:
		{
		}
			break;
		case R.id.userinfo_name_layout:
		{
			edit_type = UserInfoFlag.FLAG_EDIT_TYPE_NAME;
			edit_value = mTextName.getText().toString();
		}
			break;
		case R.id.userinfo_sex_layout:
		{
			edit_type = UserInfoFlag.FLAG_EDIT_TYPE_SEX;
			edit_value = mTextSex.getText().toString();	
		}
			break;
		case R.id.userinfo_age_layout:
		{
			edit_type = UserInfoFlag.FLAG_EDIT_TYPE_AGE;
			edit_value = mTextAge.getText().toString();
		}
			break;
		case R.id.userinfo_mobile_layout:
		{
			edit_type = UserInfoFlag.FLAG_EDIT_TYPE_MOBILE;
			edit_value = mTextMobile.getText().toString();	
		}
			break;
		case R.id.userinfo_pwd_layout:
		{
			edit_type = UserInfoFlag.FLAG_EDIT_TYPE_PWD;
			edit_value = mTextPwd.getText().toString();	
		}
			break;
		case R.id.userinfo_logout:
		{
			
		}
			return;
		default:
			break;
		}
		
		if (edit_type != -1)
		{
			intent.putExtra(UserInfoFlag.FLAG_EDIT_TYPE, edit_type);
			intent.putExtra(UserInfoFlag.FLAG_EDIT_VALUE, edit_value);
			startActivityForResult(intent, 1000);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && requestCode == 1000)
		{
			int edit_type = data.getIntExtra(UserInfoFlag.FLAG_EDIT_TYPE, -1);
			String edit_value = data.getStringExtra(UserInfoFlag.FLAG_EDIT_VALUE);
			if (edit_value == null)
			{
				edit_value = "";
			}
			switch (edit_type) 
			{
			case UserInfoFlag.FLAG_EDIT_TYPE_NAME:
			{
				mTextName.setText(edit_value);
			}
				break;
			case UserInfoFlag.FLAG_EDIT_TYPE_SEX:
			{
				mTextSex.setText(edit_value);
			}
				break;
			case UserInfoFlag.FLAG_EDIT_TYPE_AGE:
			{
				mTextAge.setText(edit_value);
			}
				break;
			case UserInfoFlag.FLAG_EDIT_TYPE_MOBILE:
			{
				mTextMobile.setText(edit_value);
			}
				break;
			case UserInfoFlag.FLAG_EDIT_TYPE_PWD:
			{
				mTextPwd.setText(edit_value);
			}
				break;
			default:
				break;
			}
		}
	}
	
}
