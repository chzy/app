package com.chd.strongbox;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.UILActivity;
import com.chd.base.backend.SyncTask;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.record.AndroidAudioRecorder;
import com.chd.record.model.AudioChannel;
import com.chd.record.model.AudioSampleRate;
import com.chd.record.model.AudioSource;
import com.chd.service.RPCchannel.upload.FileUploadInfo;
import com.chd.service.RPCchannel.upload.FileUploadManager;
import com.chd.service.RPCchannel.upload.UploadOptions;
import com.chd.service.RPCchannel.upload.listener.OnUploadListener;
import com.chd.service.RPCchannel.upload.progressaware.ProgressBarAware;
import com.chd.strongbox.adapter.VoiceAdapter;
import com.chd.strongbox.domain.VoiceEntity;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.utils.TimeUtils;
import com.chd.yunpan.utils.ToastUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * User: Liumj(liumengjie@365tang.cn)
 * Date: 2017-03-03
 * Time: 13:41
 * describe:
 */
public class VoiceActivity extends UILActivity {


    @BindView(R.id.rv_voice_content)
    RecyclerView rvVoiceContent;
    @BindView(R.id.tv_voice_time)
    TextView tvVoiceTime;
    @BindView(R.id.iv_voice_status)
    ImageView ivVoiceStatus;

    @BindView(R.id.iv_left)
    ImageView ivLeft;

    @BindView(R.id.tv_center)
    TextView tvCenter;


    private String recordPath = "";
    VoiceAdapter adapter = null;
    private List<VoiceEntity> entities;
    private FilelistEntity filelistEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        ButterKnife.bind(this);
        tvCenter.setText("录音");
        entities = new ArrayList<>();
        adapter = new VoiceAdapter(entities);

        recordPath = new ShareUtils(this).getRecordFile().getPath();
        rvVoiceContent.setLayoutManager(new LinearLayoutManager(this));
        rvVoiceContent.setAdapter(adapter);
        rvVoiceContent.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.parseColor("#d5d5d5"))
                        .size(1)
                        .build());
        rvVoiceContent.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                //点击事件
                Log.d("liumj", "执行了几次");
                pos = position;
                VoiceEntity voiceEntity = entities.get(position);
                title = voiceEntity.getTitle();
                filePath = entities.get(position).getFilePath();
                isNew = false;
                AndPermission.with(VoiceActivity.this)
                        .requestCode(REQUEST_CODE_PERMISSION_RECORD)
                        .permission(Manifest.permission.RECORD_AUDIO)
                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
                        .rationale(new RationaleListener() {
                            @Override
                            public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                            }
                        })
                        .send();
            }
        });
        onNewThreadRequest();
    }

    private void onNewThreadRequest() {
        showWaitDialog();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                filelistEntity = UILApplication.getFilelistEntity();
                SyncTask syncTask = new SyncTask(VoiceActivity.this, FTYPE.RECORD);
                //未备份文件 ==  backedlist . removeAll(localist);
                final List<FileInfo> cloudUnits = syncTask.getCloudUnits(0, 10000);
                if (cloudUnits == null) {
                    System.out.print("query cloudUnits remote failed");
                    return;
                }
                syncTask.analyRecordUnits(cloudUnits, filelistEntity);
                Log.d("liumj", "云端数量：" + cloudUnits.size());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissWaitDialog();
                    }
                });
            }
        });
        thread.start();
    }

    int pos = -1;

    long time = 0L;
    String filePath = "";
    String title = "";
    private boolean isNew;

    private final static int REQUEST_CODE_PERMISSION_RECORD = 100;

    @OnClick({R.id.iv_left, R.id.iv_voice_status})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                onBackPressed();
                break;
            case R.id.iv_voice_status:
                time = System.currentTimeMillis();
                filePath = recordPath + "/" + time + ".wav";
                isNew = true;
                AndPermission.with(this)
                        .requestCode(REQUEST_CODE_PERMISSION_RECORD)
                        .permission(Manifest.permission.RECORD_AUDIO)
                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
                        .rationale(new RationaleListener() {
                            @Override
                            public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                            }
                        })
                        .send();
                break;
        }
    }

    private static final int REQUEST_CODE_SETTING = 300;
    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            if (requestCode == REQUEST_CODE_PERMISSION_RECORD) {
                // TODO 相应代码。
                if (isNew) {
                    AndroidAudioRecorder.with(VoiceActivity.this)
                            // Required
                            .setFilePath(filePath)
                            .setTitle("新录音" + (entities.size() + 1))
                            .setColor(Color.parseColor("#f8b82d"))
                            .setRequestCode(0)
                            // Optional
                            .setSource(AudioSource.MIC)
                            .setChannel(AudioChannel.STEREO)
                            .setSampleRate(AudioSampleRate.HZ_48000)
                            .setAutoStart(true)
                            .setKeepDisplayOn(true)
                            // Start recording
                            .record();
                } else {
                    AndroidAudioRecorder.with(VoiceActivity.this)
                            // Required
                            .setFilePath(filePath)
                            .setTitle(title)
                            .setColor(Color.parseColor("#f8b82d"))
                            .setRequestCode(1)
                            // Optional
                            .setSource(AudioSource.MIC)
                            .setChannel(AudioChannel.STEREO)
                            .setSampleRate(AudioSampleRate.HZ_48000)
                            .setAutoStart(true)
                            .setExist(true)
                            .setKeepDisplayOn(true)
                            // Start recording
                            .record();
                }
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(VoiceActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(VoiceActivity.this, REQUEST_CODE_SETTING).show();

                // 第二种：用自定义的提示语。
                // AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING)
                // .setTitle("权限申请失败")
                // .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                // .setPositiveButton("好，去设置")
                // .show();

                // 第三种：自定义dialog样式。
                // SettingService settingService =
                //    AndPermission.defineSettingDialog(this, REQUEST_CODE_SETTING);
                // 你的dialog点击了确定调用：
                // settingService.execute();
                // 你的dialog点击了取消调用：
                // settingService.cancel();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 只需要调用这一句，其它的交给AndPermission吧，最后一个参数是PermissionListener。
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (requestCode == 0) {
                //添加时候
                // Great! User has recorded and saved the audio file
                boolean delete = data.getBooleanExtra("delete", false);
                if (delete) {
                    //删除
                    File f=new File(filePath);
                    if(f.exists()){
                        f.delete();
                    }

                } else {
                    String time = TimeUtils.getTime(this.time, "yyyy-MM-dd HH:mm");
                    String title = data.getStringExtra("title");
                    FileLocal fileLocal = new FileLocal();
                    File oldFile = new File(filePath);
                    File newFile=null;
                    if (!oldFile.getName().equals(title + ".wav")) {
                        newFile = new File(recordPath + "/" + title + ".wav");
                        oldFile.renameTo(newFile);
                    }
                    Log.d("liumj",newFile.getPath());
                    int pathid = UILApplication.getFilelistEntity().addFilePath(newFile.getParent());
                    fileLocal.setPathid(pathid);
                    fileLocal.setFtype(FTYPE.RECORD);
                    fileLocal.setObjid(newFile.getName());
                    HashMap<String, String> param = new HashMap<>();
                    param.put("time", time);
                    param.put("title", title);
                    param.put("duration", data.getStringExtra("duration"));
                    UploadOptions options = new UploadOptions(true, true);
                    FileUploadManager manager = FileUploadManager.getInstance();
                    MaterialDialog.Builder builder=new MaterialDialog.Builder(VoiceActivity.this);
                    builder.content("正在上传：0%");
                    builder.progress(false,100);
                    final MaterialDialog build = builder.build();
                    build.show();
                    manager.uploadFile(new ProgressBarAware(build), param, fileLocal, new OnUploadListener() {
                        @Override
                        public void onError(FileUploadInfo uploadData, int errorType, String msg) {
                            build.dismiss();
                            ToastUtils.toast(VoiceActivity.this, "上传失败");
                        }

                        @Override
                        public void onSuccess(FileUploadInfo uploadData, Object data) {
                            //上传成功
                            build.dismiss();
                            ToastUtils.toast(VoiceActivity.this, "上传成功");
                        }
                    }, options);


                }

            } else if (requestCode == 1) {
                //点击已添加的进入
                boolean delete = data.getBooleanExtra("delete", false);
                if (delete) {
                    //删除
                    entities.remove(pos);
                } else {
                    VoiceEntity entity = entities.get(pos);
                    entity.setTitle(data.getStringExtra("title"));
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}
