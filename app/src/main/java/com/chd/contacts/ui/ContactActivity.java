package com.chd.contacts.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.MediaMgr.utils.MediaFileUtil;
import com.chd.base.UILActivity;
import com.chd.base.backend.SyncTask;
import com.chd.contacts.vcard.VCardIO;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.service.RPCchannel.download.DownloadManager;
import com.chd.service.RPCchannel.download.FileDownloadTask;
import com.chd.service.RPCchannel.download.listener.OnDownloadingListener;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;

import java.io.File;
import java.util.List;

public class ContactActivity extends UILActivity implements OnClickListener {

    private ImageView mIvLeft;
    private TextView mTvCenter;
    private TextView mTvRight;
    private TextView mSmsNumber;
    private TextView mCloudNumber;
    private ImageView mIvSelect;
    private String contactPath;

    private List<FileInfo> cloudUnits;
    private SyncTask syncTask = null;


    private VCardIO vcarIO;
    private int netSize;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 998:
                    //本地通讯里数量
                    dismissWaitDialog();
                    dismissDialog();
                    int size = (Integer) msg.obj;
                    mSmsNumber.setText(size + "");
                    break;
                case 999:
                    //网络通讯录数量
                    dismissWaitDialog();
                    dismissDialog();
                    netSize = (Integer) msg.obj;
                    mCloudNumber.setText(netSize + "");
                    break;
                case 0:
                    vcarIO.getLocalSize(handler);
                    if (cloudUnits.isEmpty())
                        mCloudNumber.setText("尚未备份");
                    else {
                        new Thread() {
                            @Override
                            public void run() {
                                FileInfo0 info0 = new FileInfo0(cloudUnits.get(0));
                                vcarIO.getNetSize(info0.getObjid(), handler);
                            }
                        }.start();

                    }
                    break;
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact);
        contactPath = new ShareUtils(this).getContactFile().getPath() + "/backup.vcf";

        vcarIO = new VCardIO(this);

        initTitle();
        initResourceId();
        initListener();
        newRequest();
    }

    private void newRequest() {


        if (syncTask == null)
            syncTask = new SyncTask(this, FTYPE.ADDRESS);
        showWaitDialog();
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        //vcarIO.download(contactPath, null);
                        if (cloudUnits == null || cloudUnits.isEmpty())
                            // 0-100 分批取文件
                            cloudUnits = syncTask.getCloudUnits(0, 10); //10个备份
                        handler.sendEmptyMessage(0);
                    }
                }
        ).start();
    }


    private void initResourceId() {
        mSmsNumber = (TextView) findViewById(R.id.tv_sms_number);
        mCloudNumber = (TextView) findViewById(R.id.tv_cloud_number);
        mIvSelect = (ImageView) findViewById(R.id.iv_select);
    }

    private void initListener() {
        mIvLeft.setOnClickListener(this);
        mTvRight.setOnClickListener(this);
        mIvSelect.setOnClickListener(this);
    }

    private void initTitle() {
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mTvCenter = (TextView) findViewById(R.id.tv_center);
        mTvRight = (TextView) findViewById(R.id.tv_right);

        mTvCenter.setText("联系人备份");
        mTvRight.setText("一键恢复");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_right: // 一键恢复
                // TODO
                if (vcarIO != null) {
                    // 更新进度
                    FileInfo0 info = new FileInfo0();
                    info.setObjid(MediaFileUtil.getNameFromFilepath(contactPath));
                    info.setFilePath(contactPath);
                    info.setFtype(FTYPE.ADDRESS);

                    DownloadManager manager = DownloadManager.getInstance(this);
                    manager.downloadFile(info, new File(contactPath), new OnDownloadingListener() {
                        @Override
                        public void onDownloadFailed(FileDownloadTask task, int errorType, String msg) {
                            Toast.makeText(ContactActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onDownloadSucc(FileDownloadTask task, File outFile) {
                            new Thread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            setParMessage("正在导入,请稍候...");
                                            showDialog("正在导入,请稍候...");
                                            vcarIO.doImport(handler, contactPath, false,
                                                    ContactActivity.this, netSize);
                                        }
                                    }
                            ).start();

                        }
                    });

                }

                break;
            case R.id.iv_select: // 一键备份
                // TODO
                if (vcarIO != null) {
                    // 更新进度
                    setParMessage("正在上传,请稍候...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            vcarIO.doExport(contactPath, ContactActivity.this,handler);
                        }
                    }).start();

                }

                break;
        }
    }


}
