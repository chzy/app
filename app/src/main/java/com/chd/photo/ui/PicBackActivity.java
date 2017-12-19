package com.chd.photo.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.UILActivity;
import com.chd.base.backend.SyncTask;
import com.chd.photo.adapter.PicAdapter;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.service.RPCchannel.upload.FileUploadInfo;
import com.chd.service.RPCchannel.upload.FileUploadManager;
import com.chd.service.RPCchannel.upload.UploadOptions;
import com.chd.service.RPCchannel.upload.listener.OnUploadListener;
import com.chd.service.RPCchannel.upload.progressaware.ProgressBarAware;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.utils.TimeUtils;
import com.chd.yunpan.utils.ToastUtils;
import com.chd.yunpan.view.SuperRefreshRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PicBackActivity extends UILActivity implements View.OnClickListener {

    private SuperRefreshRecyclerView mPicRecyclerView;
    private TextView mPicUploadTextView;
    private RelativeLayout mPicBottomRelativeLayout;
    private ArrayList<ArrayList<FileLocal>> localList = new ArrayList();
    private PicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unbackup);
        mPicRecyclerView = (SuperRefreshRecyclerView) findViewById(R.id.lv_pic);
        mPicUploadTextView = (TextView) findViewById(R.id.tv_pic_upload);
        mPicBottomRelativeLayout = (RelativeLayout) findViewById(R.id.rl_pic_bottom);
        TextView title = (TextView) findViewById(R.id.tv_center);
        ImageView iv_back = (ImageView) findViewById(R.id.iv_left);
        title.setText("选择未备份照片");
        iv_back.setOnClickListener(this);
        right = (TextView) findViewById(R.id.tv_right);
        right.setText("全选");
        right.setOnClickListener(this);
        mPicUploadTextView.setOnClickListener(this);
        adapter = new PicAdapter(PicBackActivity.this, localList, null, false, true);
        adapter.setbIsUbkList(true);
        mPicRecyclerView.setAdapter(adapter);
        mPicRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPicRecyclerView.setChangeScrollStateCallback(new SuperRefreshRecyclerView.ChangeScrollStateCallback() {
            @Override
            public void change(int c) {
                switch (c) {
                    case 2:
                        Glide.with(PicBackActivity.this).pauseRequests();

                        break;
                    case 0:
                        Glide.with(PicBackActivity.this).resumeRequests();

                        break;
                    case 1:
                        Glide.with(PicBackActivity.this).resumeRequests();
                        break;
                }
            }
        });
        onNewThreadRequest();
    }

    TextView right;


    private SyncTask syncTask;
    private FilelistEntity filelistEntity;

    private void onNewThreadRequest() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                localList.clear();
                filelistEntity = UILApplication.getFilelistEntity();
                if (syncTask == null)
                    syncTask = new SyncTask(PicBackActivity.this, FTYPE.PICTURE);
                //未备份文件 ==  backedlist . removeAll(localist);
                List<FileInfo> cloudUnits = syncTask.getCloudUnits(0, 10000);
                syncTask.analyPhotoUnits(cloudUnits, filelistEntity);
                List<FileLocal> localUnits = filelistEntity.getLocallist();
                if (localUnits != null && !localUnits.isEmpty()) {
                    ArrayList<FileLocal> local = new ArrayList<>();
                    int time = TimeUtils.getZeroTime(localUnits.get(0).getLastModified());
                    local.add(localUnits.get(0));
                    for (int i = 1; i < localUnits.size(); i++) {
                        FileLocal fileInfo = localUnits.get(i);
                        if (!fileInfo.bakuped) {
                            if (fileInfo.lastModified <= ((localList.size() + 1) * 3 * 24 * 3600 + time)) {
                                local.add(fileInfo);
                            } else {
                                Collections.reverse(local);
                                localList.add(local);
                                local = new ArrayList<>();
                                local.add(fileInfo);
                                time = TimeUtils.getZeroTime(fileInfo.getLastModified());
                            }
                        }
                    }
                    localList.add(local);
                    Collections.reverse(localList);
                    local = null;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });
        thread.start();
    }

    @Override
    public void onEventMainThread(Object obj) {
        super.onEventMainThread(obj);
        mPicUploadTextView.setText(String.format("备份（%d）", obj));
    }

    int count = 0;

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_left:
                onBackPressed();
                break;
            case R.id.tv_right:
                //全选
                if ("取消全选".equals(right.getText().toString())) {
                    ArrayList<String> lists = new ArrayList<>();
                    adapter.setSelectList(lists);
                    right.setText("全选");
                } else {
                    ArrayList<String> lists = new ArrayList<>();
                    for (int i = 0; i < localList.size(); i++) {
                        ArrayList<FileLocal> fileLocals = localList.get(i);
                        for (int j = 0; j < fileLocals.size(); j++) {
                            lists.add(i + " " + j);
                        }
                    }
                    adapter.setSelectList(lists);
                    right.setText("取消全选");
                }
                break;
            case R.id.tv_pic_upload:
                //上传
                final ArrayList<String> selectData = adapter.getSelectData();
                if (selectData.isEmpty()) {
                    ToastUtils.toast(this, "请选择上传文件");
                    return;
                }
                FileUploadManager manager = FileUploadManager.getInstance();
                boolean overwrite = true;
                boolean resume = true;
                UploadOptions options = new UploadOptions(overwrite, resume);
                final MaterialDialog.Builder builder = new MaterialDialog.Builder(PicBackActivity.this);
                builder.content("正在上传");
                builder.progress(true, 100);
                final MaterialDialog build = builder.build();
                build.show();
                count = 0;
                for (String s :
                        selectData) {
                    String[] split = s.split(" ");
                    int pos1 = Integer.parseInt(split[0]);
                    int pos2 = Integer.parseInt(split[1]);
                    final FileLocal local = localList.get(pos1).get(pos2);
                    local.setFtype(FTYPE.PICTURE);
                    manager.uploadFile(new ProgressBarAware(build), null, local, new OnUploadListener() {
                        @Override
                        public void onError(FileUploadInfo uploadData, int errorType, String msg) {
                            ToastUtils.toast(PicBackActivity.this, "上传失败");
                            build.dismiss();
                        }

                        @Override
                        public void onSuccess(FileUploadInfo uploadData, Object data) {
                            build.dismiss();
                            count++;
                            if (count == selectData.size()) {
                                ToastUtils.toast(PicBackActivity.this, "上传成功");
                                setResult(RESULT_OK);
                                onBackPressed();
                            }
                        }
                    }, options);
                }


                break;
        }
    }
}
