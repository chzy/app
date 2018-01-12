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
import com.google.gson.Gson;
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * @description
 * @FileName: com.chd.yunpan.ui.RegisterActivity
 * @author: liumj
 * @date:2016-01-28 18:36
 * OS:Mac 10.10
 * Developer Kits:AndroidStudio 1.5
 */

public class RegisterActivity extends Activity implements View.OnClickListener {


	private EditText mEdAccountEditText;
	private LinearLayout mAccountLinearLayout;
	private EditText mEdPwdEditText;
	private LinearLayout mPasswordLinearLayout;
	private EditText mEdConfirmPwdEditText;
	private LinearLayout mConfimPasswordLinearLayout;
	private CircularProgressButton mBtnLogCircularProgressButton;
	private LinearLayout mLinearLayoutRegLinearLayout;

	/**
	 * 用户名
	 */
	private String pass;
	/**
	 * 密码
	 */
	private String name;
	private ImageView mIvLeft;
	private TextView mTvTitle;
	private CircularProgressButton mBtnCodeCircularProgressButton;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		MobSDK.init(this, "22b36239ac552", "1d832cf5d4af820e48e9f6bd244dcf1c");


		initView();
		initListener();
	}

	private TimeCount time;

	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mBtnLogCircularProgressButton.setOnClickListener(this);
		mBtnCodeCircularProgressButton.setOnClickListener(this);
	}

	private void initView() {
		mEdAccountEditText = (EditText) findViewById(R.id.reg_ed_account);
		mAccountLinearLayout = (LinearLayout) findViewById(R.id.reg_account);
		mEdPwdEditText = (EditText) findViewById(R.id.reg_ed_pwd);
		mPasswordLinearLayout = (LinearLayout) findViewById(R.id.reg_password);
		mEdConfirmPwdEditText = (EditText) findViewById(R.id.reg_ed_confirm_pwd);
		mConfimPasswordLinearLayout = (LinearLayout) findViewById(R.id.reg_confim_password);
		mBtnLogCircularProgressButton = (CircularProgressButton) findViewById(R.id.log_btn_log);
		mBtnCodeCircularProgressButton = (CircularProgressButton) findViewById(R.id.log_btn_code);
		mLinearLayoutRegLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutReg);
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvTitle = (TextView) findViewById(R.id.tv_center);
		mLinearLayoutRegLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutReg);
		mLinearLayoutRegLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutReg);

		mTvTitle.setText("注册");
		time = new TimeCount(60 * 1000, 1000, mBtnCodeCircularProgressButton);

		EventHandler eh = new EventHandler() {


			@Override
			public void afterEvent(int event, int result, final Object data) {
				Log.d("lmj", result + "");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(dialog!=null&&dialog.isShowing()){
							dialog.dismiss();
						}
					}
				});
				if (result == SMSSDK.RESULT_COMPLETE) {
					//回调完成
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
						//提交验证码成功
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(RegisterActivity.this, "提交验证码成功", Toast.LENGTH_SHORT).show();
							}
						});

					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						//获取验证码成功
						runOnUiThread(new Runnable() {
							@Override
							public void run() {

								Toast.makeText(RegisterActivity.this, "获取验证码成功", Toast.LENGTH_SHORT).show();
								time.start();
							}
						});


					} else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
						//返回支持发送验证码的国家列表
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(RegisterActivity.this, "支持国家成功", Toast.LENGTH_SHORT).show();
							}
						});


					}
				} else {
//					Log.d("liumj",((Throwable)data).getLocalizedMessage());
//					Log.d("liumj",((Throwable)data).getMessage());
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								JSONObject obj = new JSONObject(((Throwable)data).getLocalizedMessage());
								String msg = obj.getString("detail");
								Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
							} catch (JSONException e) {

							}

						}
					});
//					((Throwable) data).printStackTrace();
				}
			}
		};
		SMSSDK.registerEventHandler(eh);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		SMSSDK.unregisterAllEventHandler();
	}

	ProgressDialog dialog;

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.log_btn_code:
				//验证码

//				if (!NetUtils.isGPRS(this)) {
//					Toast.makeText(RegisterActivity.this, "请切换为流量方式", Toast.LENGTH_SHORT).show();
//					return;
//				}
				final String phone = mEdAccountEditText.getText().toString();
				if(phone.length()!=11){
					Toast.makeText(RegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
					return ;
				}
				dialog=new ProgressDialog(this);
				dialog.setMessage("正在获取验证码");
				dialog.show();
				SMSSDK.getVerificationCode("86", phone);
				break;

			case R.id.iv_left:
				//退出
				onBackPressed();
				break;
			case R.id.log_btn_log:
				//登陆
				final String name = mEdAccountEditText.getText().toString();
				final String pass1 = mEdPwdEditText.getText().toString();
				final String code = mEdConfirmPwdEditText.getText().toString();
				if (StringUtils.isNullOrEmpty(name) || !(name.length() == 11)) {
					Toast.makeText(RegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
					return;
				}
				if (StringUtils.isNullOrEmpty(pass1) || pass1.length() < 6 || pass1.length() > 18) {
					Toast.makeText(RegisterActivity.this, "请输入正确的密码格式", Toast.LENGTH_SHORT).show();
					return;
				}
				if (StringUtils.isNullOrEmpty(code) || !(code.length() == 4)) {
					Toast.makeText(RegisterActivity.this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
					return;
				}
				dialog = new ProgressDialog(this);
				dialog.setMessage("正在加载");
				dialog.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							final RetHead retHead = TClient.getinstance().RegistUser(name, pass1, code);
							Log.d("liumj",new Gson().toJson(retHead));
							if (Errcode.SUCCESS == retHead.getRet()) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										dialog.dismiss();
										Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
										Intent intent = new Intent();
										intent.setClass(RegisterActivity.this, LoginActivity.class);
										intent.putExtra("phone", name);
										intent.putExtra("isReg", true);
										intent.putExtra("pass", pass1);
										startActivity(intent);
										finish();
									}
								});
							} else {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										dialog.dismiss();
										String msg = retHead.getMsg();
										Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
									}
								});
							}
						} catch (Exception e) {
							e.printStackTrace();
							Log.e("register ", "注册调用失败 " + e.getMessage());
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									dialog.dismiss();
									Toast.makeText(RegisterActivity.this, "开小差了，请重试", Toast.LENGTH_SHORT).show();
								}
							});

						}
					}
				}).start();
				break;
		}
	}

}
