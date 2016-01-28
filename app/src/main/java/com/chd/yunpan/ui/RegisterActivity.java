package com.chd.yunpan.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chd.contacts.vcard.StringUtils;
import com.chd.yunpan.R;
import com.chd.yunpan.utils.ToastUtils;
import com.chd.yunpan.view.CircularProgressButton;

/**
 * @description
 * @FileName: com.chd.yunpan.ui.RegisterActivity
 * @author: liumj
 * @date:2016-01-28 18:36
 * OS:Mac 10.10
 * Developer Kits:AndroidStudio 1.5
 */

public class RegisterActivity extends Activity implements View.OnClickListener {


    private TextView mTitleTextView;
    private EditText mEdAccountEditText;
    private LinearLayout mAccountLinearLayout;
    private EditText mEdPwdEditText;
    private LinearLayout mPasswordLinearLayout;
    private EditText mEdConfirmPwdEditText;
    private LinearLayout mConfimPasswordLinearLayout;
    private CircularProgressButton mBtnLogCircularProgressButton;
    private LinearLayout mLinearLayoutRegLinearLayout;

    /**用户名*/
    private String pass;
    /**密码*/
    private String name;
    private ImageView mIvLeft;
    private TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initListener();


    }

    private void initListener() {
        mIvLeft.setOnClickListener(this);
        mBtnLogCircularProgressButton.setOnClickListener(this);
    }

    private void initView() {
        mTitleTextView = (TextView) findViewById(R.id.reg_title);
        mEdAccountEditText = (EditText) findViewById(R.id.reg_ed_account);
        mAccountLinearLayout = (LinearLayout) findViewById(R.id.reg_account);
        mEdPwdEditText = (EditText) findViewById(R.id.reg_ed_pwd);
        mPasswordLinearLayout = (LinearLayout) findViewById(R.id.reg_password);
        mEdConfirmPwdEditText = (EditText) findViewById(R.id.reg_ed_confirm_pwd);
        mConfimPasswordLinearLayout = (LinearLayout) findViewById(R.id.reg_confim_password);
        mBtnLogCircularProgressButton = (CircularProgressButton) findViewById(R.id.log_btn_log);
        mLinearLayoutRegLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutReg);
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mTvTitle= (TextView) findViewById(R.id.tv_title);
        mLinearLayoutRegLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutReg);
        mLinearLayoutRegLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutReg);

        mTvTitle.setText("注册");
    }


    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.iv_left:
                //退出
                onBackPressed();
                break;
            case R.id.log_btn_log:
                //登陆
                String name=mEdAccountEditText.getText().toString();
                String pass1=mEdPwdEditText.getText().toString();
                String pass2=mEdConfirmPwdEditText.getText().toString();

                if(StringUtils.isNullOrEmpty(name)){
                    ToastUtils.toast(this,"请输入用户名");
                    return ;
                }
                boolean flag = StringUtils.isConfirmPass(this, pass1, pass2);

               if(flag){








               }else{
                   return ;
               }







                break;
        }
    }
}
