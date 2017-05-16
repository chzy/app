package com.chd.video;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoPlayActivity extends UILActivity implements UniversalVideoView.VideoViewCallback{


    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_center)
    TextView tvCenter;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.videoView)
    UniversalVideoView mVideoView;

    @BindView(R.id.video_layout)
    FrameLayout mVideoLayout;

    @BindView(R.id.media_controller)
    UniversalMediaController mMediaController;



    FileInfo fileInfo = null;
    private String videoPath = "";
    private int mSeekPosition;
    private int cachedHeight;
    private boolean isFullscreen;

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
                        videoPath=s;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mVideoView.setVideoURI(Uri.parse(s));
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            videoPath=url;
            mVideoView.setVideoPath(url);
        }

        mVideoView.setMediaController(mMediaController);
//        setVideoAreaSize();
        mVideoView.setVideoViewCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null && mVideoView.isPlaying()) {
            mSeekPosition = mVideoView.getCurrentPosition();
            mVideoView.pause();
        }
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

    /**
     * 置视频区域大小
     */
    private void setVideoAreaSize() {
        mVideoLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = mVideoLayout.getWidth();
                cachedHeight = (int) (width * 405f / 720f);
//                cachedHeight = (int) (width * 3f / 4f);
//                cachedHeight = (int) (width * 9f / 16f);
                ViewGroup.LayoutParams videoLayoutParams = mVideoLayout.getLayoutParams();
                videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoLayoutParams.height = cachedHeight;
                mVideoLayout.setLayoutParams(videoLayoutParams);
                mVideoView.requestFocus();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
    }

    @Override
    public void onScaleChange(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        if (isFullscreen) {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoLayout.setLayoutParams(layoutParams);

        } else {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = this.cachedHeight;
            mVideoLayout.setLayoutParams(layoutParams);
        }

    }


    @Override
    public void onPause(MediaPlayer mediaPlayer) {
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {
    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {
    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {
    }

    @Override
    public void onBackPressed() {
        if (this.isFullscreen) {
            mVideoView.setFullscreen(false);
        } else {
            super.onBackPressed();
        }
    }

}
