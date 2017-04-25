package com.chd.video;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chd.TClient;
import com.chd.base.UILActivity;
import com.chd.base.backend.SyncTask;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.service.RPCchannel.download.DownloadManager;
import com.chd.service.RPCchannel.download.FileDownloadTask;
import com.chd.service.RPCchannel.download.listener.OnDownloadProgressListener;
import com.chd.service.RPCchannel.download.listener.OnDownloadingListener;
import com.chd.service.RPCchannel.upload.progressaware.ProgressBarAware;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.view.ActionSheetDialog;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoPlayActivity extends UILActivity implements OnPreparedListener {


    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_center)
    TextView tvCenter;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.video_view)
    EMVideoView videoView;

    @BindView(R.id.video_preview)
    ImageView ivPreview;

    FileInfo fileInfo = null;
    private String videoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        ButterKnife.bind(this);
        tvCenter.setText("视频播放");
        tvRight.setText("编辑");
        fileInfo = (FileInfo) getIntent().getSerializableExtra("bean");
        int sysid = UILApplication.getFilelistEntity().queryLocalSysid(fileInfo.getObjid());
        String url = null;
        videoPath = new ShareUtils(this).getVideoFile().getPath();
        if (sysid > 0) {
            SyncTask syncTask = new SyncTask(this, FTYPE.VIDEO);
            FileInfo0 fileInfo0 = syncTask.queryLocalInfo(sysid);
            url = fileInfo0.getFilePath();
            syncTask = null;
        }
        if (url == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final String s = TClient.getinstance().CreateShare(FTYPE.VIDEO, fileInfo.getObjid());
                        Log.d("liumj","地址："+s);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                videoView.setVideoURI(Uri.parse(s));
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            videoView.setVideoPath(url);
        }
//        url="http://221.7.13.207:8080/netdisk/nd1101video/aaabbcss.mp4";
//        url = "http://221.7.13.207:8080/netdisk/nd1101video/aaabbcss.mp4";
//        HashMap<String,String> map=new HashMap<>();
//        map.put("torrent","");
//        map.put("AWSAccessKeyId","KHC1BRIC6D8YBNC30UMF");
//        map.put("Expires","259200");
//        map.put("Signature","QoXSBZZFpnRZBadTgtYMej%2B0IKs%3D");
        MediaController mediaController = new MediaController(this);
        videoView.setOnPreparedListener(this);
        //For now we just picked an arbitrary item to play.  More can be found at
        //https://archive.org/details/more_animation

//		videoView.setVideoURI(Uri.parse("https://archive.org/download/Popeye_forPresident/Popeye_forPresident_512kb.mp4"));
//        videoView.setVideoURI(Uri.parse(url));
//        videoView.setMediaController(mediaController);
        mediaController.show();
    }


    @OnClick({R.id.iv_left, R.id.tv_right})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                onBackPressed();
                break;
            case R.id.tv_right:
                //从本地添加，视频拍照
                editVideo();
                break;
        }


    }

    private void editVideo() {
        new ActionSheetDialog(this).builder().addSheetItem("下载视频", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                //下载视频操作
                DownloadManager manager = DownloadManager.getInstance(VideoPlayActivity.this);
                File file = new File(videoPath + fileInfo.getObjid());
                MaterialDialog.Builder builder = new MaterialDialog.Builder(VideoPlayActivity.this);
                builder.progress(false, 100);
                builder.title("正在下载：0%");
                final MaterialDialog build = builder.build();
                build.show();
                manager.downloadFile(fileInfo, file, new ProgressBarAware(build), new OnDownloadingListener() {
                    @Override
                    public void onDownloadFailed(FileDownloadTask task, int errorType, String msg) {
                        //下载失败
                    }

                    @Override
                    public void onDownloadSucc(FileDownloadTask task, File outFile) {
                        //下载成功
                    }
                }, new OnDownloadProgressListener() {
                    @Override
                    public void onProgressUpdate(FileDownloadTask downloadInfo, long current, long totalSize) {
                        //当前进度， 总进度
                        int l = (int) (current / totalSize) * 100;
                        Log.d("liumj", "当前进度：" + current + "总进度：" + totalSize + "进度：" + l);
                        build.setContent("正在下载:" + l + "%");
                        build.setProgress(l);
                    }
                });


            }
        }).addSheetItem("删除视频", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                //删除视频操作
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SyncTask syncTask = new SyncTask(VideoPlayActivity.this, FTYPE.VIDEO);
                        syncTask.DelRemoteObj(fileInfo);
                        setResult(RESULT_OK, null);
                        VideoPlayActivity.this.finish();
                    }
                }).start();
            }
        }).setCanceledOnTouchOutside(true).setCancelable(true).show();
    }


    @Override
    public void onPrepared() {

    }
}
