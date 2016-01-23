package com.chd.other.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.MediaMgr.utils.MFileFilter;
import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.Ui.ActiveProcess;
import com.chd.base.backend.SyncTask;
import com.chd.contacts.vcard.StringUtils;
import com.chd.other.adapter.OtherListAdapter;
import com.chd.other.entity.FileInfoL;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;

import java.util.ArrayList;
import java.util.List;

public class OtherActivity extends ActiveProcess implements OnClickListener {

    List<FileInfo0> tmpFileInfo = new ArrayList<FileInfo0>();
    private ImageView mIvLeft;
    private TextView mTvCenter;
    private TextView mTvRight;
    private int nRgbColorNor = Color.rgb(248, 184, 45);
    private int nRgbColorSel = Color.rgb(255, 255, 255);
    private TextView mTabAll, mTabDOC, mTabXLS, mTabPPT, mTabPDF;
    private ListView mListView;
    private List<FileInfo0> mFileInfoList = new ArrayList<FileInfo0>();
    private String filetype = "";
    private OtherListAdapter adapter;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            tmpFileInfo.clear();
            if (!StringUtils.isNullOrEmpty(filetype)&&!FileInfoL.FILE_TYPE_ALL.equals(filetype)) {
                for (FileInfo0 fileInfo : mFileInfoList) {
                    if (fileInfo.getObjid().contains(filetype)) {
                        tmpFileInfo.add(fileInfo);
                    }
                }
            } else {
                tmpFileInfo.addAll(mFileInfoList);
            }
            adapter.notifyDataSetChanged();
        }
    };
    private TextView mTabAllTextView;
    private TextView mTabDocTextView;
    private TextView mTabXlsTextView;
    private TextView mTabPptTextView;
    private TextView mTabPdfTextView;
    private LinearLayout mFreedownTabLayoutLinearLayout;
    private ListView mListviewListView;
    private Button mEditDownButton;
    private Button mEditDelButton;
    private RelativeLayout mEditRlRelativeLayout;
    private Button mEditCancelButton;
    private SyncTask syncTask;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		/*StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
*/
        setContentView(R.layout.activity_other);




        initTitle();
        initResourceId();
        initListener();
        syncTask = new SyncTask(OtherActivity.this, FTYPE.NORMAL);
        path=new ShareUtils(this).getOtherFile().getPath();
        adapter = new OtherListAdapter(OtherActivity.this, tmpFileInfo);
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

                final List<FileInfo0> cloudUnits = syncTask.getCloudUnits(0, 100);
                runOnUiThread(new Runnable() {
                    public void run() {
                        initData(cloudUnits);
                    }
                });
            }
        });
        thread.start();
    }

    private void initData(List<FileInfo0> cloudUnits) {
        if (cloudUnits == null) {
            System.out.print("query remote failed");
        }

        FilelistEntity filelistEntity = syncTask.analyUnits(cloudUnits);
//        cloudUnits  是网存的文件
        cloudUnits.clear();
        cloudUnits = null;
        List<FileLocal> fileLocals = filelistEntity.getLocallist();
        cloudUnits=filelistEntity.getBklist();
        //显示的时候过滤文件类型
        MFileFilter fileFilter = new MFileFilter();
        fileFilter.setCustomCategory(new String[]{FileInfoL.FILE_TYPE_DOC, FileInfoL.FILE_TYPE_PDF, FileInfoL.FILE_TYPE_PPT, FileInfoL.FILE_TYPE_XLS}, true);

		for(FileInfo0 item:cloudUnits)
		{
            mFileInfoList.add(item);
			//FileInfo0 item=new FileInfo0(finfo);
			if(!fileFilter.contains(item.getObjid()))
				continue;
			//已备份文件
			if (syncTask.haveLocalCopy(item))
			{
				String path=item.getFilePath();
			}
//			else
//			{
//				String savepath= new ShareUtils(this).getStorePathStr()+item.getFilename();
//				item.setFilePath(savepath);
//				//param1  object ,param2 progressBar, param 3  beeque
//				syncTask.download(item,null,false);
//			}
		}
//        if (fileLocals != null) {
//            for (FileLocal fileLocal : fileLocals) {
//                if (fileLocal.bakuped)
//                    continue;
//
//                FileInfo0 fileInfo0 = syncTask.queryLocalInfo(fileLocal.sysid);
//                if (fileInfo0 == null) {
//                    continue;
//                }
//
//                if (fileFilter.contains(fileInfo0.getFilePath())) {
//                    mFileInfoList.add(fileInfo0);
//                }
//            }
//        }
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


        mListView = (ListView) findViewById(R.id.other_listview);
    }

    private void initTitle() {
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mTvCenter = (TextView) findViewById(R.id.tv_center);
        mTvRight = (TextView) findViewById(R.id.tv_right);

        mTvCenter.setText("免流量应用");
        mTvRight.setText("编辑");
    }
    ArrayList<FileInfo0> checkList;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.other_edit_cancel:
                //取消
                adapter.showCB(false);
                adapter.notifyDataSetChanged();
                mTvRight.setText("编辑");
                mEditRlRelativeLayout.setVisibility(View.GONE);
                break;
            case R.id.other_edit_del:
                //删除
               checkList=adapter.getCheckList();
                for (final FileInfo0 info:
                        checkList) {
                    if (syncTask != null && info != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean delS = syncTask.DelRemoteObj(info);
                                if (delS) {

                                    handler.post(new Runnable() {
                                        @Override

                                        public void run() {
                                            Toast.makeText(OtherActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                            tmpFileInfo.remove(info);
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
                        }).start();
                    }
                }

                break;
            case R.id.other_edit_down:
                //下载
                checkList=adapter.getCheckList();

                for (final FileInfo0 info:
                        checkList) {
                    if(info.getSysid()<=0){
                        info.setFilePath(path+"/"+info.getObjid());
                    }

                    if (syncTask != null && info != null) {
                        syncTask.download(info, OtherActivity.this, false);
                    }
                }








                //上传


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

}
