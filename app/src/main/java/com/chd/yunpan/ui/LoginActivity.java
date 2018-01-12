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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.TClient;
import com.chd.contacts.vcard.StringUtils;
import com.chd.proto.LoginResult;
import com.chd.proto.RetHead;
import com.chd.proto.VersionResult;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.net.ExecRunable;
import com.chd.yunpan.net.NetworkUtils;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.utils.AppUtils;
import com.chd.yunpan.utils.Logs;
import com.chd.yunpan.utils.TimeCount;
import com.chd.yunpan.utils.ToastUtils;
import com.chd.yunpan.utils.update.UpdateAppUtils;
import com.chd.yunpan.utils.update.VersionModel;
import com.chd.yunpan.view.circleimage.CircularProgressButton;
import com.mob.MobSDK;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


public class LoginActivity extends Activity implements OnClickListener {

    private CircularProgressButton btn_login = null;

    private EditText et_pwd;

    private EditText et_name;

    private EditText et_url;

    private TextView tv_forgot;
    private ProgressDialog dialog = null;

    private Button show;
    private boolean switcherState = false;
//    private ImageView mgb = null;

    private boolean f = true;
    private boolean isUnlock;
    private ShareUtils shareUtils;
    private Handler loginHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            System.out.println("发送之后：");

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
                    ToastUtils.toast(LoginActivity.this, msg.obj.toString());
                    break;
                case 3:
                    btn_login.setProgress(-1);
                    loginHandler.sendEmptyMessageDelayed(2, 2000);
                    ToastUtils.toast(LoginActivity.this, msg.obj.toString());
                    ll_smss_code.setVisibility(View.VISIBLE);
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
    private EditText et_Code;
    private CircularProgressButton btn_code;
    private TimeCount time;
    private LinearLayout ll_smss_code;

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
        initCode();
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

    private void initCode(){
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
                                Toast.makeText(LoginActivity.this, "提交验证码成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(LoginActivity.this, "获取验证码成功", Toast.LENGTH_SHORT).show();
                                time.start();
                            }
                        });


                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "支持国家成功", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
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

    String imei = "";

    private void setListener() {

        MobSDK.init(this, "22b36239ac552", "1d832cf5d4af820e48e9f6bd244dcf1c");
        time = new TimeCount(60 * 1000, 1000, btn_code);
        btn_code.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final String phone = et_name.getText().toString();
                dialog=new ProgressDialog(LoginActivity.this);
                dialog.setMessage("正在获取验证码");
                dialog.show();
                SMSSDK.getVerificationCode("86", phone);
            }
        });
        btn_login.setIndeterminateProgressMode(true);
        btn_login.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_name.getText().toString())) {
                    share(et_name.getId());
                    return;
                }
                if (TextUtils.isEmpty(et_pwd.getText().toString())) {
                    share(et_pwd.getId());
                    return;
                }
                if(ll_smss_code.getVisibility()==View.VISIBLE&&et_Code.getText().toString().length()==0){
                    share(et_Code.getId());
                    return;
                }
                if (!NetworkUtils.isNetworkAvailable(LoginActivity.this)) {
                    ToastUtils.toast(LoginActivity.this, "网络开小差了!!!");
                    return;
                }
                btn_login.setProgress(50);
                System.out.println("没进去");
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       System.out.println("跑进来了");
                       Message msg = new Message();
                       LoginResult entity = null;
                       try {
                           String pwd = et_pwd.getText().toString();
                           String username = et_name.getText().toString();
                           String code = et_Code.getText().toString();
                           TClient th = TClient.getinstance();
                           if (shareUtils.isAutoLogin()) {
                               username = shareUtils.getUsername();
                               pwd = shareUtils.getPwd();

                           }
                           if(ll_smss_code.getVisibility()==View.VISIBLE){
                               RetHead resetimie = th.Resetimie(imei, username, code);
                               if(resetimie.getRet().getValue()==0){
                                   entity = th.loginAuth(username, pwd, imei);
                                   if (entity.isSetToken()) {
                                       msg.what = 0;
                                       msg.obj = entity;
                                       //loginEntity 里面有用户容量,多少空间等属性. 需要显示在 Myspace里面
                                       shareUtils.setLoginEntity(entity);
                                   }else if(entity.getResult().getRet().getValue() == 10) {
                                       //IMEI号错误
                                       msg.what = 3;
                                       msg.obj = "已绑定其他手机，请输入验证码重新绑定";
                                   }else {
                                       msg.what = -1;
                                       msg.obj = "验证失败";
                                   }
                               }else{
                                   msg.what = -1;
                                   msg.obj = "登录异常,请稍候再试";
                               }
                           }else{
                               System.out.println("登录之前");
                               entity = th.loginAuth(username, pwd, imei);
                               if (entity.isSetToken()) {
                                   msg.what = 0;
                                   msg.obj = entity;
                                   //loginEntity 里面有用户容量,多少空间等属性. 需要显示在 Myspace里面
                                   shareUtils.setLoginEntity(entity);
                               }else if(entity.getResult().getRet().getValue() == 10) {
                                   //IMEI号错误
                                   msg.what = 3;
                                   msg.obj = "已绑定其他手机，请输入验证码重新绑定";
                               }else {
                                   msg.what = -1;
                                   msg.obj = "验证失败";
                               }
                           }
                       } catch (Exception e) {
                           msg.what = -1;
                           msg.obj = "登录异常,请稍候再试";
                       }
                       System.out.println("发送了么");
                       loginHandler.sendMessage(msg);
                   }
               }).start();

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

//        mgb.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switcherChange();
//            }
//        });
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
//
//    private void switcherChange() {
//        switcherState = !switcherState;
//        if (switcherState) {
//            mgb.setImageResource(R.drawable.check_on);
//        } else {
//            mgb.setImageResource(R.drawable.check_off);
//        }
//    }

    private void initViews() {
        btn_login = (CircularProgressButton) findViewById(R.id.log_btn_log);
        btn_code = (CircularProgressButton) findViewById(R.id.log_btn_code);
        et_name = (EditText) findViewById(R.id.log_name);
        ll_smss_code = (LinearLayout) findViewById(R.id.ll_smss_code);
        et_pwd = (EditText) findViewById(R.id.log_pwd);
        et_Code = (EditText) findViewById(R.id.login_et_code);
        tv_forgot = (TextView) findViewById(R.id.log_tv_forgrt);
        show = (Button) findViewById(R.id.log_btn_show);
        TextView ll_head_help = (TextView) findViewById(R.id.ll_head_help);
        TextView register = (TextView) findViewById(R.id.log_tv_register);

        register.setOnClickListener(this);
        ll_head_help.setOnClickListener(this);


        ShareUtils shareUtils = new ShareUtils(this);
        if (shareUtils.isAutoLogin()) {
            et_name.setText(shareUtils.getUsername());
            et_pwd.setText(shareUtils.getPwd());
//            switcherChange();
        }

    }

    public void share(int _id) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        findViewById(_id).startAnimation(shake);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
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
                entity = th.loginAuth(username, pwd, imei);

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
