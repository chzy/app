package com.chd.music.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.UILActivity;
import com.chd.base.Ui.DownListActivity;
import com.chd.base.backend.SyncTask;
import com.chd.music.adapter.MusicBackupAdapter;
import com.chd.music.backend.MediaUtil;
import com.chd.music.entity.MusicBackupBean;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.service.RPCchannel.upload.FileUploadInfo;
import com.chd.service.RPCchannel.upload.FileUploadManager;
import com.chd.service.RPCchannel.upload.UploadOptions;
import com.chd.service.RPCchannel.upload.listener.OnUploadListener;
import com.chd.service.RPCchannel.upload.progressaware.ProgressBarAware;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicBackupActivity extends UILActivity implements OnClickListener, OnItemClickListener {

    private ImageView mIvLeft;
    private TextView mTvCenter;
    private TextView mTvRight;
    private TextView mTvNumber;
    private Button mBtnBackup;
    private ListView mGvMusic;
    private FilelistEntity filelistEntity;

    private List<MusicBackupBean> mMusicBackupList = new ArrayList<MusicBackupBean>();

    private MusicBackupAdapter musicBackupAdapter;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            try {
                if (msg.what == 998) {
                    //多文件上传
                    processMsg(msg);
                } else if (msg.what == 997) {
                    //多文件下载
                } else if (msg.what == 996) {
                    processMsg(msg);
                } else {
                    dismissDialog();
                    dismissWaitDialog();
                     musicBackupAdapter = new MusicBackupAdapter(MusicBackupActivity.this, mMusicBackupList);
                    mGvMusic.setAdapter(musicBackupAdapter);
                    mTvNumber.setText(String.format("共：%d首", mMusicBackupList.size()));
                }
            } catch (Exception e) {
                Log.e(getClass().getName(), "退出页面空指针");
            }

        }
    };
    private int count;

    private void processMsg(Message msg) {
        //删除
        ArrayList<Integer> posList = (ArrayList<Integer>) msg.obj;
        if (posList.size() == 0) {
            if (msg.what == 998)
                toastMain("上传成功");
            else if (msg.what == 996) {
                toastMain("删除成功");
            }
        } else {
            if (msg.what == 998)
                toastMain("上传失败");
            else if (msg.what == 996) {
                toastMain("删除失败");
            }
        }
        if (posList.size() != uploadList.size()) {
            int i = 0;
            for (Integer index :
                    posList) {
                int pos = index - i;
                uploadList.remove(pos);
                i++;
            }
            mMusicBackupList.removeAll(uploadList);
            setResult(RESULT_OK);
            handler.sendEmptyMessage(0);
        }
    }


    private Button mBtnDown;

    private SyncTask syncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_music_backup);
        filelistEntity=UILApplication.getFilelistEntity();
        syncTask = new SyncTask(this, FTYPE.MUSIC);
        initTitle();
        initResourceId();
        initListener();
        filelistEntity=UILApplication.getFilelistEntity();



       // ArrayList<FileLocal> fileLocals = (ArrayList<FileLocal>) getIntent().getSerializableExtra("locallist");
        initData();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private boolean isUpdate = false;


    @Override
    protected void onPause() {
        super.onPause();

    }


    private void initData() {
        showWaitDialog();
        new Thread() {
            FileInfo0 item;
            @Override
            public void run() {

                for (Integer idx : filelistEntity.getUnbak_idx_lst()) {
                    item=filelistEntity.getLocalFileByIdx(idx);
                   // String name = fileLocal.getObjid();
                    //String path = UILApplication.getFilelistEntity().getDirPath(fileLocal.getPathid());
                    MusicBackupBean musicBackupBean = new MusicBackupBean(item, false);
                    musicBackupBean.getFilePath();
                    String albumArt = MediaUtil.getAlbumArt(MusicBackupActivity.this, musicBackupBean.getFileInfo0().getAbsFilePath());
                    musicBackupBean.albumArt=albumArt;
                    mMusicBackupList.add(musicBackupBean);

                }
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void initResourceId() {
        mTvNumber = (TextView) findViewById(R.id.gv_music_backup_num);
        mGvMusic = (ListView) findViewById(R.id.gv_music);
        mBtnBackup = (Button) findViewById(R.id.gv_music_backup);
        mBtnDown = (Button) findViewById(R.id.music_btn_down);
    }

    private void initListener() {
        mIvLeft.setOnClickListener(this);
        mTvRight.setOnClickListener(this);
        mGvMusic.setOnItemClickListener(this);
        mBtnBackup.setOnClickListener(this);
        mBtnDown.setOnClickListener(this);
    }

    private void initTitle() {
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mTvCenter = (TextView) findViewById(R.id.tv_center);
        mTvRight = (TextView) findViewById(R.id.tv_right);

        mTvCenter.setText("音乐");
        mTvRight.setText("全选");
        mTvRight.setTag(false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.music_btn_down:
                //任务列表页面
                Intent intent = new Intent(MusicBackupActivity.this, DownListActivity.class);
                startActivity(intent);
                break;

            case R.id.iv_left:
                if (isUpdate) {
                    setResult(RESULT_OK);
                }
                finish();
                break;
            case R.id.tv_right: //全选
                boolean bSel = (Boolean) v.getTag();
                for (MusicBackupBean musicBackupBean : mMusicBackupList) {
                    musicBackupBean.setSelect(!bSel);
                }
                handler.sendEmptyMessage(0);
                v.setTag(!bSel);
                mTvRight.setText(bSel ? "全选" : "取消");
                break;
            case R.id.gv_music_backup:
                goBackUpMusic();
                break;
        }
    }

    private List<MusicBackupBean> uploadList = new ArrayList<>();

    private void goBackUpMusic() {
        if (mMusicBackupList.size() <= 0) {
            Toast.makeText(MusicBackupActivity.this, "请选择需要上传的文件", Toast.LENGTH_SHORT).show();
            return;
        }
        final List<FileInfo0> info0s = new ArrayList<>();
        uploadList.clear();
        for (final MusicBackupBean musicBackupBean : mMusicBackupList) {
            if (musicBackupBean.isSelect()) {
                uploadList.add(musicBackupBean);
                FileInfo0 fileInfo0=musicBackupBean.getFileInfo0();
                info0s.add(fileInfo0);
            }
        }
        FileUploadManager manager = FileUploadManager.getInstance();
        boolean overwrite = true;
        boolean resume = true;
        UploadOptions options = new UploadOptions(overwrite, resume);
        final MaterialDialog.Builder builder = new MaterialDialog.Builder(MusicBackupActivity.this);
        builder.content("正在上传");
        builder.progress(true, 100);
        final MaterialDialog build = builder.build();
        build.show();
        count = 0;
        for (FileInfo0 local : info0s) {
            local.setFtype(FTYPE.MUSIC);
            manager.uploadFile(new ProgressBarAware(build), null, local, new OnUploadListener() {
                @Override
                public void onError(FileUploadInfo uploadData, int errorType, String msg) {
                    ToastUtils.toast(MusicBackupActivity.this, "上传失败");
                    build.dismiss();
                }

                @Override
                public void onSuccess(FileUploadInfo uploadData, Object data) {
                    build.dismiss();
                    count++;
                    if (count == info0s.size()) {
                        ToastUtils.toast(MusicBackupActivity.this, "上传成功");
                        setResult(RESULT_OK);
                        onBackPressed();
                    }
                }
            }, options);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        MusicBackupBean musicBackupBean = mMusicBackupList.get(arg2);
        if (!musicBackupBean.isSelect()) {
            musicBackupBean.setSelect(true);
        } else {
            musicBackupBean.setSelect(false);
        }
        musicBackupAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isUpdate) {
                setResult(RESULT_OK);
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
