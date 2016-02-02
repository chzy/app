package com.chd.contacts.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.base.Ui.ActiveProcess;
import com.chd.base.backend.SyncTask;
import com.chd.contacts.entity.ContactBean;
import com.chd.contacts.vcard.VCardIO;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends ActiveProcess implements OnClickListener{

    private ImageView mIvLeft;
    private TextView mTvCenter;
    private TextView mTvRight;
    private TextView mSmsNumber;
    private TextView mCloudNumber;
    private ImageView mIvSelect;
    private String contactPath;

    private  List<FileInfo0> cloudUnits;
    private SyncTask syncTask=null;


    private List<ContactBean> mContactList = new ArrayList<ContactBean>();
    private VCardIO vcarIO;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 998:
                    //本地通讯里数量
                    int size = (Integer) msg.obj;
                    mSmsNumber.setText(size + "");
                    break;
                case 999:
                    //网络通讯录数量
                    int length = (Integer) msg.obj;
                    mCloudNumber.setText(length + "");
                    break;
                case 0:
                    vcarIO.getLocalSize(handler);
                    if (cloudUnits.isEmpty())
                        mCloudNumber.setText("尚未备份");
                    else
                        vcarIO.getNetSize(cloudUnits.get(0).getObjid(), handler);
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


        if (syncTask==null)
            syncTask=new SyncTask(this,FTYPE.ADDRESS);

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        //vcarIO.download(contactPath, null);
                        if (cloudUnits==null || cloudUnits.isEmpty())
                            // 0-100 分批取文件
                            cloudUnits=syncTask.getCloudUnits(0, 10); //10个备份
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
                    setParMessage("正在导入,请稍候...");
                    updateProgress(0);
                    vcarIO.doImport(contactPath, false,
                            ContactActivity.this);
                }

                break;
            case R.id.iv_select: // 一键备份
                // TODO
                if (vcarIO != null) {
                    // 更新进度
                    setParMessage("正在上传,请稍候...");
                    updateProgress(0);
                    vcarIO.doExport(contactPath, ContactActivity.this);
                }

                break;
        }
    }


}
