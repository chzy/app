package com.chd.yunpan.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.TClient;
import com.chd.proto.Errcode;
import com.chd.proto.RetHead;
import com.chd.yunpan.R;
import com.chd.yunpan.view.CircularProgressButton;

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
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initListener();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
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
    time=new TimeCount(60*1000,1000,mBtnCodeCircularProgressButton);

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject obj=new JSONObject(data.toString());
                                String msg=obj.getString("detail");
                                Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {

                            }

                        }
                    });

                    ((Throwable) data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh);
    }



    class TimeCount extends CountDownTimer {
        private Button btn;
        public TimeCount(long millisInFuture, long countDownInterval,Button v) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
            this.btn=v;
        }
        @Override
        public void onFinish() {//计时完毕时触发
            btn.setText("重新验证");
            btn.setClickable(true);
        }
        @Override
        public void onTick(long millisUntilFinished){//计时过程显示
            btn.setClickable(false);
            btn.setText(millisUntilFinished /1000+"秒");
        }
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();

        SMSSDK.unregisterAllEventHandler();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.log_btn_code:
                //验证码
                String phone = mEdAccountEditText.getText().toString();
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
                final ProgressDialog dialog=new ProgressDialog(this);
                dialog.setMessage("正在加载");
                dialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final RetHead retHead = TClient.getinstance().RegistUser(name, pass1, code);

                            if(Errcode.SUCCESS==retHead.getRet()){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent();
                                        intent.setClass(RegisterActivity.this,LoginActivity.class);
                                        intent.putExtra("phone",name);
                                        intent.putExtra("isReg",true);
                                        intent.putExtra("pass",pass1);
                                        startActivity(intent);

                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        String msg=retHead.getMsg();
                                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                        }
                    }
                }).start();



//                if(StringUtils.isNullOrEmpty(name)){
//                    ToastUtils.toast(this,"请输入用户名");
//                    return ;
//                }
//                boolean flag = StringUtils.isConfirmPass(this, pass1, pass2);
//
//               if(flag){
//
//
//
//
//
//
//
//
//
//
//               }else{
//                   return ;
//               }


                break;
        }
    }

}
