package com.chd.yunpan.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
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
import com.chd.yunpan.utils.AppUtils;
import com.chd.yunpan.utils.Logs;
import com.chd.yunpan.utils.ToastUtils;
import com.chd.yunpan.utils.update.UpdateAppUtils;
import com.chd.yunpan.utils.update.VersionModel;
import com.chd.yunpan.view.circleimage.CircularProgressButton;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;


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
    private boolean f = true;
    private boolean isUnlock;
    private boolean canLogin = true;
    private ShareUtils shareUtils;
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
                    if (isUnlock) {
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
    private String verName;
    private static final int REQUEST_CODE_PERMISSION_SD = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        verName = getVersion();
        shareUtils = new ShareUtils(this);
        AndPermission.with(this)
                .requestCode(REQUEST_CODE_PERMISSION_SD)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_PHONE_STATE)
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                    }
                }).callback(new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                imei = AppUtils.getIMEI(LoginActivity.this);
            }

            @Override
            public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {

            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final VersionResult result = TClient.getinstance().CheckVer(verName);
                    if (result != null) {
                        Log.d("更新:", result.getVersion());
                        String old_ver = verName.replace(".", "");
                        final String new_ver=result.getVersion().replace(".","");
                        int i = Integer.parseInt(old_ver);
                        int i1 = Integer.parseInt(new_ver);
                        if(i<i1){
                            loginHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    VersionModel vm=new VersionModel();
                                    vm.versionName=new_ver;
                                    vm.desc=result.getWhatsnew();
                                    vm.url=result.getUrl();
                                    vm.forced=1;
                                    UpdateAppUtils.launch(LoginActivity.this,vm);
                                }
                            });
                        }
                    }

                } catch (Exception e) {
                    Log.e("liumj", "更新异常");
                }
            }
        }).start();
        initViews();
        setListener();
        dialog = new ProgressDialog(LoginActivity.this);

        Intent intent = getIntent();
        isUnlock = intent.getBooleanExtra("Unlock", false);

        if (intent.getBooleanExtra("isReg", false)) {
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

    String imei = "";

    private void setListener() {

        btn_login.setIndeterminateProgressMode(true);
        btn_login.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
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
                            if (shareUtils.isAutoLogin()) {
                                username = shareUtils.getUsername();
                                pwd = shareUtils.getPwd();

                            }
                            entity = th.loginAuth(username, pwd, imei.hashCode());
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
                startIntent.setClass(LoginActivity.this, HelpActivity.class);
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
        TextView ll_head_help = (TextView) findViewById(R.id.ll_head_help);
        TextView register = (TextView) findViewById(R.id.log_tv_register);

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
            if (!canLogin) {
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
                String username = et_name.getText().toString();
                String pwd = et_pwd.getText().toString();
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
     * 2  * 获取版本号
     * 3  * @return 当前应用的版本号
     * 4
     */
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
