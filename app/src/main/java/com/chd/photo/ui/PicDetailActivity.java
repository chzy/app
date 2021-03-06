package com.chd.photo.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.UILActivity;
import com.chd.base.backend.SyncTask;
import com.chd.contacts.vcard.StringUtils;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.utils.PhotoView;
import com.chd.yunpan.utils.ToastUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PicDetailActivity extends UILActivity implements OnClickListener {

    private ImageView mIvLeft;
    private TextView mTvCenter;
    private PhotoView mImgView;
    private Button mBtnSaveLocal;
    private Button mBtnCancel;
    private Button mBtnDelete;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
    private boolean IsLocal;
    private int pos;
    private SyncTask syncTask;
    private FileInfo0 item ;
    private FilelistEntity filelistEntity;
    private final String TAG = this.getClass().getName();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 997:
                    //下载成功
                    Log.d("liumj", "下载成功");
                    Toast.makeText(PicDetailActivity.this, "保存到本地成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
//	private TextView mTvRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pic_detail);
        filelistEntity=UILApplication.getFilelistEntity();

        IsLocal = getIntent().getBooleanExtra("islocal", false);
        if (IsLocal) {
            findViewById(R.id.pic_detail_btm_layout).setVisibility(View.GONE);
        }

        pos = getIntent().getIntExtra("pos", 0);
        options = new DisplayImageOptions.Builder()
//		.showImageOnLoading(R.drawable.pic_test1)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .considerExifParams(true)
//		.extraForDownloader(new ShareUtils(this).getStorePath())  //增加保存路径
                .build();

        initTitle();
        initResourceId();
        initListener();
        initData();
    }



    private void initData() {

       if (syncTask == null)
            syncTask = new SyncTask(this, FTYPE.PICTURE);

        FileInfo fileInfo = (FileInfo) getIntent().getSerializableExtra("bean");

        /*if (IsLocal) {
            *//*
            ipc 传递 可能回影响速度
             *//*
            FileInfo fileInfo = (FileInfo) getIntent().getSerializableExtra("bean");
            *//*
            只能传成功基类. 不知道原因
             *//*

            item=(FileInfo0) fileInfo;
            item.setFilePath(filelistEntity.getDirPath(item.pathid));
        }
        else {
            FileInfo fileInfo = (FileInfo) getIntent().getSerializableExtra("bean");
            */


            {
                List<FileInfo0> list=syncTask.QueryLocalFile(fileInfo.objid);
                boolean matched=false;
                if (list!=null && !list.isEmpty()) {
                    for (FileInfo0 it:list)
                    {
                        if (it.getFilesize()==fileInfo.getFilesize())
                        {
                            item=it;
                            matched=true;
                            IsLocal=true;
                            mBtnSaveLocal.setVisibility(View.INVISIBLE);
                            break;
                        }
                    }

                }
                if (!matched)
                {
                    item=new FileInfo0(fileInfo);
                }
            }
      //  }
        /*if (fileInfo instanceof FileInfo0 && ((FileInfo0) fileInfo).islocal) {
            item=(FileInfo0) fileInfo;
            item.setFilePath(filelistEntity.getDirPath(item.pathid));
            //url = "file://" + UILApplication.getFilelistEntity().getDirPath(item.pathid) + "/" + item.getObjid();
        } else */


        String url=item.getUrl();

        if (StringUtils.isNullOrEmpty(url)) {
            return;
        }

        Log.d("liumj", "---" + url);
        if (url != null) {

            Log.d("liumj", "" + url);
            imageLoader.displayImage(url, mImgView,
                    options, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            showWaitDialog();
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                                    FailReason failReason) {
                            /*vh.progressBar.setVisibility(View.GONE);*/
                            dismissWaitDialog();
                            Toast.makeText(PicDetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view,
                                                      Bitmap loadedImage) {
							/*vh.progressBar.setVisibility(View.GONE);*/
                            dismissWaitDialog();
                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view,
                                                     int current, int total) {
//							/*vh.progressBar.setProgress(Math.round(100.0f * current
//									/ total));*/
//							int i = (current / total) * 100;
//							setParMessage("正在加载中");
//							updateProgress(i);
                        }
                    });
        }
		/*else
		{
			mImgView.setImageResource(R.drawable.pic_test1);
		}*/
    }

    private void initListener() {
        mIvLeft.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mBtnSaveLocal.setOnClickListener(this);
    }

    private void initResourceId() {
        mImgView = (PhotoView) findViewById(R.id.pic_detail_img);
        mBtnSaveLocal = (Button) findViewById(R.id.pic_detail_savelocal);
        mBtnDelete = (Button) findViewById(R.id.pic_detail_delete);
        mBtnCancel = (Button) findViewById(R.id.pic_detail_cancel);
    }

    private void initTitle() {
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mTvCenter = (TextView) findViewById(R.id.tv_center);

        mTvCenter.setText("照片");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
            case R.id.pic_detail_cancel: {
                finish();
            }
            break;
            case R.id.pic_detail_delete: {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (item != null) {
                            final boolean bSucc = syncTask.DelRemoteObj(item);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String strData = bSucc ? "删除成功" : "删除失败";
                                    ToastUtils.toast(PicDetailActivity.this, strData);
                                    if (bSucc && IsLocal) {
                                        if (!StringUtils.isNullOrEmpty(item.getFilePath())) {
                                            new MaterialDialog.Builder(PicDetailActivity.this).title("温馨提示").content("是否将本地资源同步删除").positiveText("删除").negativeText("取消").onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    try {
                                                        File f = new File(item.getFilePath());
                                                        f.delete();
                                                    } catch (Exception ignored) {

                                                    } finally {
                                                        Intent intent = new Intent();
                                                        intent.putExtra("pos", pos);
                                                        setResult(RESULT_OK, intent);
                                                        finish();
                                                    }

                                                }
                                            }).onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    Intent intent = new Intent();
                                                    intent.putExtra("pos", pos);
                                                    setResult(RESULT_OK, intent);
                                                    finish();
                                                }
                                            }).show();
                                        } else {
                                            Intent intent = new Intent();
                                            intent.putExtra("pos", pos);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                    }


                                }
                            });
                        }
                    }
                });
                thread.start();
            }
            break;
            case R.id.pic_detail_savelocal: {
                if (item != null) {
                    List<FileInfo> info0s = new ArrayList<>();
                    if (StringUtils.isNullOrEmpty(item.getFilePath())) {
                        String objid = item.getObjid();
                        if (objid.contains("//")) {
                            objid = objid.split("//")[1];
                        }
                        item.setFilePath(new ShareUtils(this).getPhotoFile().getPath() + "/" + objid);
                    }
                    info0s.add(item);
                    /**
                     * TODO 要改成 downloadmgr 下载
                     */

                    if (syncTask != null) {
                        syncTask.downloadList(info0s, PicDetailActivity.this, handler);
                    }
                }

            }
            break;
            default:
                break;
        }
    }
}
