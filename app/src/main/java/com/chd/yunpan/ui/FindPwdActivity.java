package com.chd.yunpan.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.TClient;
import com.chd.contacts.vcard.StringUtils;
import com.chd.proto.Errcode;
import com.chd.proto.RetHead;
import com.chd.yunpan.R;
import com.chd.yunpan.utils.TimeCount;
import com.chd.yunpan.view.circleimage.CircularProgressButton;
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class FindPwdActivity extends Activity implements View.OnClickListener {



	private ImageView iv_left;
	private TextView tv_title;
	private EditText mRegEdAccountEditText;
	private LinearLayout mRegAccountLinearLayout;
	private EditText mRegEdConfirmPwdEditText;
	private CircularProgressButton mBtnCodeCircularProgressButton;
	private LinearLayout mRegConfimPasswordLinearLayout;
	private EditText mRegEdPwdEditText;
	private LinearLayout mRegPasswordLinearLayout;
	private CircularProgressButton mBtnLogCircularProgressButton;
	private LinearLayout mLinearLayoutRegLinearLayout;
	private TimeCount time;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		this.setContentView(R.layout.lz_find_password);
		MobSDK.init(this, "22b36239ac552", "1d832cf5d4af820e48e9f6bd244dcf1c");

		mRegEdAccountEditText = (EditText) findViewById(R.id.reg_ed_account);
		mRegAccountLinearLayout = (LinearLayout) findViewById(R.id.reg_account);
		mRegEdConfirmPwdEditText = (EditText) findViewById(R.id.reg_ed_confirm_pwd);
		mBtnCodeCircularProgressButton = (CircularProgressButton) findViewById(R.id.log_btn_code);
		mRegConfimPasswordLinearLayout = (LinearLayout) findViewById(R.id.reg_confim_password);
		mRegEdPwdEditText = (EditText) findViewById(R.id.reg_ed_pwd);
		mRegPasswordLinearLayout = (LinearLayout) findViewById(R.id.reg_password);
		mBtnLogCircularProgressButton = (CircularProgressButton) findViewById(R.id.log_btn_log);
		mLinearLayoutRegLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutReg);

		iv_left= (ImageView) findViewById(R.id.iv_left);
		tv_title= (TextView) findViewById(R.id.tv_center);
		mBtnLogCircularProgressButton.setOnClickListener(this);
		mBtnCodeCircularProgressButton.setOnClickListener(this);
		tv_title.setText("找回密码");
		iv_left.setOnClickListener(this);


		time=new TimeCount(60*1000,1000,mBtnCodeCircularProgressButton);

		// 如果希望在读取通信录的时候提示用户，可以添加下面的代码，并且必须在其他代码调用之前，否则不起作用；如果没这个需求，可以不加这行代码
		SMSSDK.setAskPermisionOnReadContact(true);
		EventHandler eh = new EventHandler() {
			@Override
			public void afterEvent(int event, int result, final Object data) {
				Log.d("lmj", result + "");
				if (result == SMSSDK.RESULT_COMPLETE) {
					//回调完成
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
						//提交验证码成功
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(FindPwdActivity.this, "提交验证码成功", Toast.LENGTH_SHORT).show();
							}
						});

					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						//获取验证码成功
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(FindPwdActivity.this, "获取验证码成功", Toast.LENGTH_SHORT).show();
								time.start();
							}
						});


					} else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
						//返回支持发送验证码的国家列表
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(FindPwdActivity.this, "支持国家成功", Toast.LENGTH_SHORT).show();
							}
						});
					}
				} else {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								JSONObject obj=new JSONObject(data.toString());
								String msg=obj.getString("detail");
								Toast.makeText(FindPwdActivity.this, msg, Toast.LENGTH_SHORT).show();
							} catch (JSONException e) {

							}

						}
					});
				}
			}
		};
		SMSSDK.registerEventHandler(eh);

	}


	@Override
	public void onClick(View view) {
		int id=view.getId();
		switch (id){
			case R.id.log_btn_code:
				//验证码

				String phone = mRegEdAccountEditText.getText().toString();
				if(StringUtils.isNullOrEmpty(phone)||!(phone.length()==11)){
					Toast.makeText(FindPwdActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
					return ;
				}
				SMSSDK.getVerificationCode("86", phone);
				break;
			case R.id.iv_left:
				onBackPressed();
				break;
			case R.id.log_btn_log:
				//登陆
				final String name = mRegEdAccountEditText.getText().toString();
				final String pass1 = mRegEdPwdEditText.getText().toString();
				final String code = mRegEdConfirmPwdEditText.getText().toString();
				if(StringUtils.isNullOrEmpty(name)||!(name.length()==11)){
					Toast.makeText(FindPwdActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
					return ;
				}
				if(StringUtils.isNullOrEmpty(pass1)||pass1.length()<6||pass1.length()>18){
					Toast.makeText(FindPwdActivity.this, "请输入正确的密码格式", Toast.LENGTH_SHORT).show();
					return ;
				}
				if(StringUtils.isNullOrEmpty(code)||!(code.length()==4)){
					Toast.makeText(FindPwdActivity.this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
					return ;
				}

				final ProgressDialog dialog=new ProgressDialog(this);
				dialog.setMessage("正在加载");
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();

				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							final RetHead retHead = TClient.getinstance().Rsetpwd(name, pass1, code);

							if(Errcode.SUCCESS==retHead.getRet()){
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										dialog.dismiss();
										Toast.makeText(FindPwdActivity.this, "重置密码成功", Toast.LENGTH_SHORT).show();
										Intent intent=new Intent();
										intent.setClass(FindPwdActivity.this,LoginActivity.class);
										startActivity(intent);
										finish();

									}
								});
							}else{
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										dialog.dismiss();
										String msg=retHead.getMsg();
										Toast.makeText(FindPwdActivity.this, msg, Toast.LENGTH_SHORT).show();
									}
								});
							}
						} catch (Exception e) {
						}
					}
				}).start();
				break;
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		SMSSDK.unregisterAllEventHandler();

	}
}
