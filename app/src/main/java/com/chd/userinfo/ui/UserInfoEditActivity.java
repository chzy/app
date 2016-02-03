package com.chd.userinfo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.userinfo.ui.entity.UserInfoFlag;
import com.chd.yunpan.R;
import com.chd.yunpan.utils.ToastUtils;

import java.util.Random;

public class UserInfoEditActivity extends Activity implements OnClickListener
{
	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	
	private View mViewNewMobile, mViewNewPwd, mViewNewSex, mViewOther;
	
	private TextView mTextOldMobile;
	private EditText mEditNewMobile, mEditVCode;
	private Button mBtnVCode;
	
	private EditText mEditOldPwd;
	private EditText mEditNewPwd, mEditNewPwd2;
	
	private View mViewSexMan, mViewSexWomen;
	private ImageView mImgSexMan, mImgSexWomen;
	
	private TextView mTextOtherLable;
	private EditText mEditOtherValue;
	
	private int nEditType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_userinfo_edit);
		
		initTitle();
		initResourceId();
		initListener();
		initData();
	}
	
	private void initData()
	{
		Intent intent = getIntent();
		nEditType = intent.getIntExtra(UserInfoFlag.FLAG_EDIT_TYPE, -1);
		if (nEditType == -1)
		{
			return;
		}
		
		String sEditValue = intent.getStringExtra(UserInfoFlag.FLAG_EDIT_VALUE);
		if (sEditValue == null)
		{
			return;
		}
		
		switch (nEditType) 
		{
		case UserInfoFlag.FLAG_EDIT_TYPE_NAME:
		{
			mViewOther.setVisibility(View.VISIBLE);
			mTextOtherLable.setText("名称");
			mEditOtherValue.setText(sEditValue);
		}
			break;
		case UserInfoFlag.FLAG_EDIT_TYPE_SEX:
		{
			mViewNewSex.setVisibility(View.VISIBLE);
			mViewNewSex.setTag(sEditValue);
			if (sEditValue.contains("男"))
			{
				mImgSexMan.setImageResource(R.drawable.userinfoedit_check);
				mImgSexWomen.setImageDrawable(null);
			}
			else
			{
				mImgSexMan.setImageDrawable(null);
				mImgSexWomen.setImageResource(R.drawable.userinfoedit_check);
			}
		}
			break;
		case UserInfoFlag.FLAG_EDIT_TYPE_AGE:
		{
			mViewOther.setVisibility(View.VISIBLE);
			mTextOtherLable.setText("年龄");
			mEditOtherValue.setInputType(InputType.TYPE_CLASS_NUMBER);
			mEditOtherValue.setText(sEditValue);
		}
			break;
		case UserInfoFlag.FLAG_EDIT_TYPE_MOBILE:
		{
			mViewNewMobile.setVisibility(View.VISIBLE);
			mTextOldMobile.setText(sEditValue);
		}
			break;
		case UserInfoFlag.FLAG_EDIT_TYPE_PWD:
		{
			mViewNewPwd.setVisibility(View.VISIBLE);
		}
			break;
		default:
			break;
		}
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mTvRight.setOnClickListener(this);
		
		mBtnVCode.setOnClickListener(this);
		
		mViewSexMan.setOnClickListener(this);
		mViewSexWomen.setOnClickListener(this);
	}

	private void initResourceId() {
		mViewNewMobile = findViewById(R.id.userinfoedit_newmobile_view);
		mViewNewPwd = findViewById(R.id.userinfoedit_pwd_view);
		mViewNewSex = findViewById(R.id.userinfoedit_sex_view);
		mViewOther = findViewById(R.id.userinfoedit_other_view);
		
		mTextOldMobile = (TextView) findViewById(R.id.userinfoedit_mobile_txt);
		mEditNewMobile = (EditText) findViewById(R.id.userinfoedit_newmobile_txt);
		mEditVCode = (EditText) findViewById(R.id.userinfoedit_vcode_txt);
		mBtnVCode = (Button) findViewById(R.id.userinfoedit_vcode_btn);
		
		mEditOldPwd = (EditText) findViewById(R.id.userinfoedit_pwd_txt);
		mEditNewPwd = (EditText) findViewById(R.id.userinfoedit_newpwd_txt);
		mEditNewPwd2 = (EditText) findViewById(R.id.userinfoedit_newpwd2_txt);
		
		mViewSexMan = findViewById(R.id.userinfoedit_man_layout);
		mViewSexWomen = findViewById(R.id.userinfoedit_women_layout);
		mImgSexMan = (ImageView) findViewById(R.id.userinfoedit_man_img);
		mImgSexWomen = (ImageView) findViewById(R.id.userinfoedit_women_img);
		
		mTextOtherLable = (TextView) findViewById(R.id.userinfoedit_other_lable);
		mEditOtherValue = (EditText) findViewById(R.id.userinfoedit_other_txt);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
		mTvRight = (TextView) findViewById(R.id.tv_right);

		mTvCenter.setText("修改个人信息");
		mTvRight.setText("保存");
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
		case R.id.tv_right:
		{
			onSaveUserInfo();
		}
			break;
		case R.id.userinfoedit_vcode_btn:
		{
			Random random = new Random();
			int nCode = random.nextInt(9999 - 1000 + 1) + 1000;
			mEditVCode.setText(String.format("%d", nCode));
		}
			break;
		case R.id.userinfoedit_man_layout:
		{
			mImgSexMan.setImageResource(R.drawable.userinfoedit_check);
			mImgSexWomen.setImageDrawable(null);
			mViewNewSex.setTag("男");
		}
			break;
		case R.id.userinfoedit_women_layout:
		{
			mImgSexMan.setImageDrawable(null);
			mImgSexWomen.setImageResource(R.drawable.userinfoedit_check);
			mViewNewSex.setTag("女");
		}
			break;
		default:
			break;
		}
	}
	
	private void onSaveUserInfo()
	{
		Intent intent = new Intent();
		if (nEditType == -1)
		{
			return;
		}
		
		String vEditValue = "";
		
		switch (nEditType) 
		{
		case UserInfoFlag.FLAG_EDIT_TYPE_NAME:
		{
			vEditValue = mEditOtherValue.getText().toString();
		}
			break;
		case UserInfoFlag.FLAG_EDIT_TYPE_SEX:
		{
			vEditValue = (String) mViewNewSex.getTag();
		}
			break;
		case UserInfoFlag.FLAG_EDIT_TYPE_AGE:
		{
			vEditValue = mEditOtherValue.getText().toString();
		}
			break;
		case UserInfoFlag.FLAG_EDIT_TYPE_MOBILE:
		{
			vEditValue = mEditNewMobile.getText().toString();
		}
			break;
		case UserInfoFlag.FLAG_EDIT_TYPE_PWD:
		{
			String sNewPwd = mEditNewPwd.getText().toString();
			String sNewPwd2 = mEditNewPwd2.getText().toString();
			if (!sNewPwd.equals(sNewPwd2))
			{
				ToastUtils.toast(this, "两次密码输入不一致，请重新输入");
				return;
			}
			vEditValue = mEditNewPwd.getText().toString();
			intent.putExtra("oldPass",mEditOldPwd.getText().toString());
		}
			break;
		default:
			break;
		}
		

		intent.putExtra(UserInfoFlag.FLAG_EDIT_TYPE, nEditType);
		intent.putExtra(UserInfoFlag.FLAG_EDIT_VALUE, vEditValue);
		setResult(1000, intent);
		finish();
	}
	
}
