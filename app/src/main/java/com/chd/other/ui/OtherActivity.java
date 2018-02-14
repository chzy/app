package com.chd.other.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.UILActivity;
import com.chd.base.Ui.DownListActivity;
import com.chd.base.backend.SyncTask;
import com.chd.contacts.vcard.StringUtils;
import com.chd.listener.DataCallBack;
import com.chd.other.adapter.OtherListAdapter;
import com.chd.other.entity.FileInfoL;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.service.RPCchannel.download.DownloadManager;
import com.chd.service.RPCchannel.download.FileDownloadTask;
import com.chd.service.RPCchannel.download.listener.OnDownloadProgressListener;
import com.chd.service.RPCchannel.download.listener.OnDownloadingListener;
import com.chd.service.RPCchannel.upload.FileUploadInfo;
import com.chd.service.RPCchannel.upload.FileUploadManager;
import com.chd.service.RPCchannel.upload.UploadOptions;
import com.chd.service.RPCchannel.upload.listener.OnUploadListener;
import com.chd.service.RPCchannel.upload.progressaware.ProgressBarAware;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OtherActivity extends UILActivity implements OnClickListener {

    String path;
    final  String TAG="OtherActivity";
    ArrayList<FileInfo> checkList;
    private ImageView mIvLeft;
    private TextView mTvCenter;
    private TextView mTvRight;
    private int nRgbColorNor = Color.rgb(248, 184, 45);
    private int nRgbColorSel = Color.rgb(255, 255, 255);
    private TextView mTabAll, mTabDOC, mTabXLS, mTabPPT, mTabPDF;
    private ListView mListView;
    private List<FileInfo0> mFileInfoList = new LinkedList<>();
    private List<FileInfo0> mFileLocalList = new LinkedList<>();
    private String filetype = "";
    private boolean isLocal = false;
    private OtherListAdapter adapter;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 998:
                    ToastUtils.toast(OtherActivity.this,"执行成功");
                    dismissWaitDialog();
                    dismissDialog();
                    onNewThreadRequest();
                    break;
                default:
                    //tmpFileInfo.clear();
                   // tmpFileInfo=mFileInfoList;
                    List list=filelistEntity.getLocallist();
                    if (list.size()<1)
                        return;
                    if (isLocal) {
                        adapter.setList(filelistEntity.getLocallist());
                        adapter.setShowUnbakedfile(true);
                        if (!StringUtils.isNullOrEmpty(filetype) && !FileInfoL.FILE_TYPE_ALL.contains(filetype)) {
                            /*for (FileInfo0 fileInfo : mFileLocalList) {
                                if (fileInfo.getObjid().contains(filetype)) {
                                    tmpFileInfo.add(fileInfo);
                                }
                            }*/


                            adapter.setShowfiletype(filetype);
                        } else {
                            //tmpFileInfo.addAll(mFileLocalList);
                            adapter.setShowfiletype(null);
                           // adapter.setShowUnbakedfile(false);
                        }
                    } else {
                        adapter.setList(filelistEntity.getBklist());
                        adapter.setShowUnbakedfile(false);
                        if (!StringUtils.isNullOrEmpty(filetype) && !FileInfoL.FILE_TYPE_ALL.contains(filetype)) {
                           /* for (FileInfo0 fileInfo : mFileInfoList) {
                                if (fileInfo.getObjid().contains(filetype)) {
                                    tmpFileInfo.add(fileInfo);
                                }
                            }*/
                           adapter.setShowfiletype(filetype);

                        } else {
                           // tmpFileInfo.addAll(mFileInfoList);
                            adapter.setShowfiletype("");
                        }
                    }
                    adapter.notifyDataSetChanged();
                    break;
            }

        }
    };

    private RelativeLayout mRlOther;
    private Button mEditDownButton;
    private Button mEditDelButton;
    private RelativeLayout mEditRlRelativeLayout;
    private Button mEditCancelButton;
    private SyncTask syncTask;
    private TextView mTvNumber;
    private Button mBtnDown;
    private int count;
    //private ArrayList<FileLocal> mFileLocalLists = new ArrayList<>();
    private FilelistEntity filelistEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


		/*StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
*/
        setContentView(R.layout.activity_other);

        UILApplication.ClearFileEntity();
        initTitle();
        initResourceId();
        initListener();
        filelistEntity = UILApplication.getFilelistEntity();
        syncTask = new SyncTask(OtherActivity.this, FTYPE.NORMAL);
        path = new ShareUtils(this).getOtherFile().getPath();
        adapter = new OtherListAdapter(OtherActivity.this, null);
        mListView.setAdapter(adapter);
        onNewThreadRequest();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void onNewThreadRequest() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                //未备份文件 ==  backedlist . removeAll(localist);

                final List<FileInfo> cloudUnits = syncTask.getCloudUnits(0, 10000);
                filelistEntity.setBklist(cloudUnits);
                runOnUiThread(new Runnable() {
                    public void run() {
                        initData(cloudUnits);
                    }
                });
            }
        });
        thread.start();
    }



    private void initData(final List<FileInfo> cloudUnits) {

        if (cloudUnits == null) {
            System.out.print("query remote failed");
        }


        // 找到10个以后 先返回, 剩下的 在线程里面继续找
        syncTask.dbManager.GetLocalFiles0( new String[]{"pdf", "xls", "doc", "docx"}, true, filelistEntity, new DataCallBack() {
            @Override
            /*
            * @count 当前list的最后下标
            * */
            public void success(List<FileInfo0> datas, int offset,int count) {
                //接收到的数据
                syncTask.dbManager.anlayLocalUnits(cloudUnits, filelistEntity,offset,count);
                refreshData(filelistEntity.getUnbak_idx_lst().size());
            }
        });
       // Unbak_idx_lst.clear();
       // syncTask.analyOtherUnits0(cloudUnits, filelistEntity, pos);
    }



    private void refreshData(int count){

      /*  if (datas != null) {
            //for (FileInfo f : datas)
            int i=Math.max(0,end-10);
            FileInfo0 item=null;
            for (; i<end;i++)
            {
                item=datas.get(i);
                if (item==null)
                    continue;
                if (!item.backuped) {
                    Unbak_idx_lst.add(i);
                }
              */
        //count;
      /*TODO
      * */
      // 问题:  数量显示未更新,只有全部文件取完以后 才显示出个数
        mTvNumber.setText("未备份文件" + count + "个");
        handler.sendEmptyMessage(0);
    }

    private void initListener() {
        mIvLeft.setOnClickListener(this);
        mTvRight.setOnClickListener(this);

        mTabAll.setOnClickListener(this);
        mTabDOC.setOnClickListener(this);
        mTabPDF.setOnClickListener(this);
        mTabPPT.setOnClickListener(this);
        mTabXLS.setOnClickListener(this);

        mEditCancelButton.setOnClickListener(this);
        mEditDelButton.setOnClickListener(this);
        mEditDownButton.setOnClickListener(this);
        mBtnDown.setOnClickListener(this);
        mRlOther.setOnClickListener(this);
    }

    private void initResourceId() {
        mTabAll = (TextView) findViewById(R.id.other_tab_all);
        mTabDOC = (TextView) findViewById(R.id.other_tab_doc);
        mTabPDF = (TextView) findViewById(R.id.other_tab_pdf);
        mTabPPT = (TextView) findViewById(R.id.other_tab_ppt);
        mTabXLS = (TextView) findViewById(R.id.other_tab_xls);
        mEditDownButton = (Button) findViewById(R.id.other_edit_down);
        mEditDelButton = (Button) findViewById(R.id.other_edit_del);
        mEditCancelButton = (Button) findViewById(R.id.other_edit_cancel);
        mEditRlRelativeLayout = (RelativeLayout) findViewById(R.id.other_edit_rl);
        mRlOther = (RelativeLayout) findViewById(R.id.rl_other_ubk_layout);
        mTvNumber = (TextView) findViewById(R.id.tv_other_number);
        mBtnDown = (Button) findViewById(R.id.other_btn_down);
        mListView = (ListView) findViewById(R.id.other_listview);
    }

    private void initTitle() {
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mTvCenter = (TextView) findViewById(R.id.tv_center);
        mTvRight = (TextView) findViewById(R.id.tv_right);


        mTvCenter.setText("其他");
        mTvRight.setText("编辑");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.other_btn_down:
                //任务列表
                Intent intent = new Intent(OtherActivity.this, DownListActivity.class);
                startActivity(intent);

                break;

            case R.id.rl_other_ubk_layout:
                //未备份文件
                isLocal = true;
                mEditDownButton.setText("上传");
                mRlOther.setVisibility(View.GONE);
                handler.sendEmptyMessage(0);
                break;

            case R.id.other_edit_cancel:
                //取消
                adapter.showCB(false);
                adapter.notifyDataSetChanged();
                mTvRight.setText("编辑");
                mEditRlRelativeLayout.setVisibility(View.GONE);
                break;
            case R.id.other_edit_del:
                //删除
                checkList = adapter.getCheckList();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (final FileInfo i :
                                checkList) {
                            if (syncTask != null && i != null) {
                                boolean delS = syncTask.DelRemoteObj(i);
                                if (delS) {
                                    handler.post(new Runnable() {
                                        @Override

                                        public void run() {
                                            Toast.makeText(OtherActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                            //tmpFileInfo.remove(i);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(OtherActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        }
                    }
                }
                ).start();

                break;
            case R.id.other_edit_down:
                checkList = adapter.getCheckList();

                if (isLocal) {
                    //上传
                    if (checkList.isEmpty()) {
                        ToastUtils.toast(this, "请选择上传文件");
                        return;
                    }
                    upload(checkList);
                } else {
                    //下载
                    if (checkList.isEmpty()) {
                        ToastUtils.toast(this, "请选择下载文件");
                        return;
                    }
                    download(checkList);
                }

                break;
            case R.id.tv_right:
                if ("编辑".equals(mTvRight.getText().toString())) {
                    adapter.showCB(true);
                    adapter.notifyDataSetChanged();
                    mTvRight.setText("取消");
                    mEditRlRelativeLayout.setVisibility(View.VISIBLE);
                } else {
                    adapter.showCB(false);
                    adapter.notifyDataSetChanged();
                    mTvRight.setText("编辑");
                    mEditRlRelativeLayout.setVisibility(View.GONE);
                }


                break;
            case R.id.iv_left: {
                finish();
            }
            break;
            case R.id.other_tab_all: {
                mTabAll.setBackgroundResource(R.drawable.other_tab_left_checked);
                mTabDOC.setBackgroundResource(R.drawable.other_tab_center_normal);
                mTabPPT.setBackgroundResource(R.drawable.other_tab_center_normal);
                mTabXLS.setBackgroundResource(R.drawable.other_tab_center_normal);
                mTabPDF.setBackgroundResource(R.drawable.other_tab_right_normal);

                mTabAll.setTextColor(nRgbColorSel);
                mTabDOC.setTextColor(nRgbColorNor);
                mTabPPT.setTextColor(nRgbColorNor);
                mTabXLS.setTextColor(nRgbColorNor);
                mTabPDF.setTextColor(nRgbColorNor);

                filetype = FileInfoL.FILE_TYPE_ALL;
                handler.sendEmptyMessage(0);
            }
            break;
            case R.id.other_tab_doc: {
                mTabAll.setBackgroundResource(R.drawable.other_tab_left_normal);
                mTabDOC.setBackgroundResource(R.drawable.other_tab_center_checked);
                mTabPPT.setBackgroundResource(R.drawable.other_tab_center_normal);
                mTabXLS.setBackgroundResource(R.drawable.other_tab_center_normal);
                mTabPDF.setBackgroundResource(R.drawable.other_tab_right_normal);

                mTabAll.setTextColor(nRgbColorNor);
                mTabDOC.setTextColor(nRgbColorSel);
                mTabPPT.setTextColor(nRgbColorNor);
                mTabXLS.setTextColor(nRgbColorNor);
                mTabPDF.setTextColor(nRgbColorNor);

                filetype = FileInfoL.FILE_TYPE_DOC;
                handler.sendEmptyMessage(0);
            }
            break;
            case R.id.other_tab_pdf: {
                mTabAll.setBackgroundResource(R.drawable.other_tab_left_normal);
                mTabDOC.setBackgroundResource(R.drawable.other_tab_center_normal);
                mTabPPT.setBackgroundResource(R.drawable.other_tab_center_normal);
                mTabXLS.setBackgroundResource(R.drawable.other_tab_center_normal);
                mTabPDF.setBackgroundResource(R.drawable.other_tab_right_checked);

                mTabAll.setTextColor(nRgbColorNor);
                mTabDOC.setTextColor(nRgbColorNor);
                mTabPPT.setTextColor(nRgbColorNor);
                mTabXLS.setTextColor(nRgbColorNor);
                mTabPDF.setTextColor(nRgbColorSel);

                filetype = FileInfoL.FILE_TYPE_PDF;
                handler.sendEmptyMessage(0);
            }
            break;
            case R.id.other_tab_ppt: {
                mTabAll.setBackgroundResource(R.drawable.other_tab_left_normal);
                mTabDOC.setBackgroundResource(R.drawable.other_tab_center_normal);
                mTabPPT.setBackgroundResource(R.drawable.other_tab_center_checked);
                mTabXLS.setBackgroundResource(R.drawable.other_tab_center_normal);
                mTabPDF.setBackgroundResource(R.drawable.other_tab_right_normal);

                mTabAll.setTextColor(nRgbColorNor);
                mTabDOC.setTextColor(nRgbColorNor);
                mTabPPT.setTextColor(nRgbColorSel);
                mTabXLS.setTextColor(nRgbColorNor);
                mTabPDF.setTextColor(nRgbColorNor);

                filetype = FileInfoL.FILE_TYPE_PPT;
                handler.sendEmptyMessage(0);
            }
            break;
            case R.id.other_tab_xls: {
                mTabAll.setBackgroundResource(R.drawable.other_tab_left_normal);
                mTabDOC.setBackgroundResource(R.drawable.other_tab_center_normal);
                mTabPPT.setBackgroundResource(R.drawable.other_tab_center_normal);
                mTabXLS.setBackgroundResource(R.drawable.other_tab_center_checked);
                mTabPDF.setBackgroundResource(R.drawable.other_tab_right_normal);
                mTabAll.setTextColor(nRgbColorNor);
                mTabDOC.setTextColor(nRgbColorNor);
                mTabPPT.setTextColor(nRgbColorNor);
                mTabXLS.setTextColor(nRgbColorSel);
                mTabPDF.setTextColor(nRgbColorNor);
                filetype = FileInfoL.FILE_TYPE_XLS;
                handler.sendEmptyMessage(0);
            }
            break;
            default:
                break;
        }
    }

    /**
     * 下载
     *
     * @param checkList
     */
    private void download(final ArrayList<FileInfo> checkList) {
        DownloadManager manager = DownloadManager.getInstance(mAct);
        final MaterialDialog.Builder builder = new MaterialDialog.Builder(OtherActivity.this);
        builder.content("第1个文件正在下载");
        builder.cancelable(false);
        builder.progress(true, 100);
        final MaterialDialog build = builder.build();
        build.show();
        count = 0;
        for (int i = 0; i < checkList.size(); i++) {
            FileInfo f = checkList.get(i);
            f.setFtype(FTYPE.NORMAL);
            File outFile = new File(path + "/" + f.objid);
            manager.downloadFile(f, outFile, new ProgressBarAware(build, i + 1), new OnDownloadingListener() {
                @Override
                public void onDownloadFailed(FileDownloadTask task, int errorType, String msg) {
                    ToastUtils.toast(OtherActivity.this, "下载失败");
                    build.dismiss();
                }

                @Override
                public void onDownloadSucc(FileDownloadTask task, File outFile) {
                    count++;
                    if (count == checkList.size()) {
                        build.dismiss();
                        ToastUtils.toast(OtherActivity.this, "下载成功");
                        setResult(RESULT_OK);
                        onBackPressed();
                    }
                }
            }, new OnDownloadProgressListener() {
                @Override
                public void onProgressUpdate(FileDownloadTask downloadInfo, long current, long totalSize) {

                }
            });
        }

    }

    /**
     * 上传
     *
     * @param checkList
     */
    private void upload(final ArrayList<FileInfo> fileLocals) {
     /*
       final ArrayList<FileInfo0> fileLocals = new ArrayList<>();
        for (FileInfo f :
                checkList) {
            FileInfo0 fileInfo0 = new FileInfo0(f);
            fileLocals.add(fileInfo0);
        }
        */
        FileUploadManager manager = FileUploadManager.getInstance();
        UploadOptions options = new UploadOptions(true, true);
        final MaterialDialog.Builder builder = new MaterialDialog.Builder(OtherActivity.this);
        builder.content("第1个文件正在上传");
        builder.cancelable(false);
        builder.progress(true, 100);
        final MaterialDialog build = builder.build();
        build.show();
        count = 0;
        for (int i = 0; i < fileLocals.size(); i++) {
            FileInfo0 f = (FileInfo0) fileLocals.get(i);
            f.setFtype(FTYPE.NORMAL);
            manager.uploadFile(new ProgressBarAware(build, i + 1), null, f, new OnUploadListener() {
                @Override
                public void onError(FileUploadInfo uploadData, int errorType, String msg) {
                    ToastUtils.toast(OtherActivity.this, "上传失败");
                    build.dismiss();
                }

                @Override
                public void onSuccess(FileUploadInfo uploadData, Object data) {
                    count++;
                    if (count == fileLocals.size()) {
                        build.dismiss();
                        ToastUtils.toast(OtherActivity.this, "上传成功");
                        setResult(RESULT_OK);
                        onBackPressed();
                    }
                }
            }, options);
        }

    }

}
