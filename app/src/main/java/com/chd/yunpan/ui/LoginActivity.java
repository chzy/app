package com.chd.yunpan.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.TClient;
import com.chd.contacts.vcard.StringUtils;
import com.chd.proto.LoginResult;
import com.chd.proto.VersionResult;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.net.ExecRunable;
import com.chd.yunpan.net.NetworkUtils;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.ui.dialog.UpdateDialog;
import com.chd.yunpan.utils.Logs;
import com.chd.yunpan.utils.ToastUtils;
import com.chd.yunpan.view.CircularProgressButton;


public class LoginActivity extends Activity implements OnClickListener {

    private CircularProgressButton btn_login = null;

    private EditText et_pwd;

    private EditText et_name;

    private EditText et_url;

    private TextView tv_forgot;
    private ProgressDialog dialog = null;

    private Button show;
    private boolean switcherState = false;
    private ImageView mgb = null;

    private boolean isLogining = false;
    private ShareUtils shareUtils;
    private TextView register;
    private boolean f = true;
    private boolean isUnlock;
    private boolean canLogin = true;
    private Handler loginHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (!canLogin) {
                return;
            }
            isLogining = false;
            switch (msg.what) {

                case 0://sucess
                    Logs.log(msg.obj.toString());

                    shareUtils.setLoginEntity((LoginResult) (msg.obj));
                    //shareUtils.setURL(et_url.getText().toString());
                    shareUtils.setAutoLogin(switcherState);
                    shareUtils.setUsername(et_name.getText().toString());
                    shareUtils.setPwd(et_pwd.getText().toString());
                    if(isUnlock){
                        UILApplication.getInstance().getLockPatternUtils().clearLock();
                    }
                    gotoMain();
                    break;
                case -1:
                    btn_login.setProgress(-1);
                    loginHandler.sendEmptyMessageDelayed(2, 2000);
                    Logs.log(msg.obj.toString());
                    ToastUtils.toast(LoginActivity.this, msg.obj.toString());
                    break;
                case 2:
                    btn_login.setProgress(0);
                    break;
            }
            dialog.dismiss();
        }


    };
    private TextView ll_head_help;
private  String verName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		/*StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
*/
        setContentView(R.layout.activity_login);
        verName=getVersion();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final VersionResult result = TClient.getinstance().CheckVer(verName);
                    if(result!=null){
                        Log.d("更新:",result.getVersion());
                    loginHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            new UpdateDialog(LoginActivity.this,result).show();
                        }
                    });
                    }

                } catch (Exception e) {
                    Log.e("liumj","更新异常");
                }
            }
        }).start();
        shareUtils = new ShareUtils(LoginActivity.this);




        initViews();
        setListener();
        dialog = new ProgressDialog(LoginActivity.this);

//        if (UILApplication.getInstance().getLockPatternUtils().savedPatternExists()) {
//            if (!getIntent().getBooleanExtra("unlock", false)) {
//                Intent i = new Intent(this, UnlockGesturePasswordActivity.class);
//                startActivity(i);
//                finish();
//            }
//        }
        Intent intent = getIntent();
        isUnlock=intent.getBooleanExtra("Unlock",false);

        if (intent != null && intent.getBooleanExtra("isReg", false)) {
            et_name.setText(intent.getStringExtra("phone"));
            et_pwd.setText(intent.getStringExtra("pass"));
            ExecRunable.execRun(new LoginThread());
        } else {
            if (!StringUtils.isNullOrEmpty(shareUtils.getUsername())) {
                et_name.setText(shareUtils.getUsername());
                et_pwd.setText(shareUtils.getPwd());
            }

            if (shareUtils.isAutoLogin()) {
                //gotoMain();
                if (shareUtils.getUsername().length() > 3 && shareUtils.getPwd().length() > 2) {
                    //isLogining=true;
                    ExecRunable.execRun(new LoginThread());
                }
            }
        }


    }

    private void setListener() {

        btn_login.setIndeterminateProgressMode(true);
        btn_login.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                canLogin = true;
                if (isLogining) {
                    return;
                }

                if (TextUtils.isEmpty(et_name.getText().toString())) {
                    share(et_name.getId());
                    return;
                }
                if (TextUtils.isEmpty(et_pwd.getText().toString())) {
                    share(et_pwd.getId());
                    return;
                }

                if (!NetworkUtils.isNetworkAvailable(LoginActivity.this)) {
                    ToastUtils.toast(LoginActivity.this, "网络开小差了!!!");
                    return;
                }


                btn_login.setProgress(50);
//				dialog.show();
                isLogining = true;
                //ExecRunable.execRun(new LoginThread());
                ExecRunable.execRun(new Thread() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        LoginResult entity = null;
                        try {
                            //new TClient();
                            String pwd = et_pwd.getText().toString();
                            String username = et_name.getText().toString();
                            TClient th = TClient.getinstance();
                            //TClient th=new TClient(false);
                            //th.addurl(new String[]{"http://221.7.13.207:8080/chdserver.php"});
                            if (shareUtils.isAutoLogin()) {
                                username = shareUtils.getUsername();
                                pwd = shareUtils.getPwd();

                            }
                            entity = th.loginAuth(username, pwd, 1);


                            //获取登录成功后的状态数据

//								LoginEntity entity = new LoginEntity();
//								entity.setName("account_name");
//								entity.setId("1000001");
//								entity.setToken("token");
//								entity.setSpace(loginResult.getSpace());
//								entity.setUspace(loginResult.getUspace());
//								entity.setFlow(1);
//								entity.setUflow(1);
//								entity.setState(true);
                            //LoginEntity entity = new  LoginParse().parse(result);
                            if (entity.isSetToken()) {
                                msg.what = 0;
                                msg.obj = entity;

                                //loginEntity 里面有用户容量,多少空间等属性. 需要显示在 Myspace里面
                                shareUtils.setLoginEntity(entity);


                            } else {
                                msg.what = -1;
                                msg.obj = "验证失败";
                            }

                        } catch (Exception e) {
                            msg.what = -1;
                            msg.obj = "登录异常,请稍候再试";
                        }

                        if (canLogin)
                            loginHandler.sendMessage(msg);

                    }
                });

            }
        });


        tv_forgot.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, FindPwdActivity.class);
                startActivity(i);
            }
        });
        show.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (f) {
                    show.setText(R.string.et_password_hiden);
                    show.setBackgroundResource(R.drawable.qihoo_accounts_btn_show_pressed);
                    et_pwd.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
                    et_pwd.setSelection(et_pwd.getText().toString().length());
                    f = false;
                } else {
                    show.setText(R.string.et_password_show);
                    show.setBackgroundResource(R.drawable.qihoo_accounts_btn_show_normal);
                    String s = et_pwd.getText().toString();
                    et_pwd.setSelection(s.length());
                    et_pwd.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
                    et_pwd.setSelection(et_pwd.getText().toString().length());
                    et_pwd.postInvalidate();
                    f = true;
                }
            }
        });

        mgb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switcherChange();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        Intent startIntent = new Intent();
        switch (id) {
            case R.id.log_tv_register:
                //登陆注册
                startIntent.setClass(LoginActivity.this, RegisterActivity.class);
                break;
            case R.id.ll_head_help:
                //TODO 帮助
                startIntent.setClass(LoginActivity.this, AboutActivity.class);
                break;
        }
        startActivity(startIntent);
    }

    public void gotoMain() {
        //Intent i = new Intent(this,MainFragmentActivity.class);
        Intent i = new Intent(this, netdiskActivity.class);
        startActivity(i);
        finish();
    }

    private void switcherChange() {
        switcherState = !switcherState;
        if (switcherState) {
            mgb.setImageResource(R.drawable.check_on);
        } else {
            mgb.setImageResource(R.drawable.check_off);
        }
    }

    private void initViews() {
        btn_login = (CircularProgressButton) findViewById(R.id.log_btn_log);
        et_name = (EditText) findViewById(R.id.log_name);
        et_pwd = (EditText) findViewById(R.id.log_pwd);
        tv_forgot = (TextView) findViewById(R.id.log_tv_forgrt);
        mgb = (ImageView) this.findViewById(R.id.tv_forget_password);
        show = (Button) findViewById(R.id.log_btn_show);
        ll_head_help = (TextView) findViewById(R.id.ll_head_help);
        register = (TextView) findViewById(R.id.log_tv_register);

        register.setOnClickListener(this);
        ll_head_help.setOnClickListener(this);


        ShareUtils shareUtils = new ShareUtils(this);
        if (shareUtils.isAutoLogin()) {
            et_name.setText(shareUtils.getUsername());
            et_pwd.setText(shareUtils.getPwd());
            switcherChange();
        }

    }

    public void share(int _id) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        findViewById(_id).startAnimation(shake);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        canLogin = false;
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && isLogining) {
            if (canLogin == false) {
            } else {
                isLogining = false;
                canLogin = false;
                btn_login.setProgress(0);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class LoginThread extends Thread {
        @Override
        public void run() {
            Message msg = new Message();
            LoginResult entity = null;
            try {
                String username = et_pwd.getText().toString();
                String pwd = et_name.getText().toString();
                TClient th = TClient.getinstance();
                if (shareUtils.isAutoLogin()) {
                    username = shareUtils.getUsername();
                    pwd = shareUtils.getPwd();
                }
                entity = th.loginAuth(username, pwd, 1);

                if (entity.isSetToken()) {
                    msg.what = 0;
                    msg.obj = entity;
                } else {
                    msg.what = -1;
                    msg.obj = "验证失败";
                }

            } catch (Exception e) {
                msg.what = -1;
                msg.obj = "登录异常,请稍候再试";
            }

            if (canLogin)
                loginHandler.sendMessage(msg);

        }

    }


    /**
     2  * 获取版本号
     3  * @return 当前应用的版本号
     4  */
     public String getVersion() {
         try {
                     PackageManager manager = this.getPackageManager();
                     PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
                     String version = info.versionName;
                     return version;
                 } catch (Exception e) {
                     e.printStackTrace();
                     return "2.1.1";
                 }
        }

}
