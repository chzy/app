package com.chd.music.ui;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.TClient;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.Ui.ActiveProcess;
import com.chd.base.backend.SyncTask;
import com.chd.contacts.vcard.StringUtils;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.utils.ToastUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import co.mobiwise.library.MusicPlayerView;

public class MusicDetailActivity extends ActiveProcess implements OnClickListener {

    DisplayImageOptions options;
    FileInfo fileInfo;
    private ImageView mIvLeft;
    private TextView mTvCenter;
    private TextView mTvRight;
    private TextView mTvTotalTime;
    private TextView mTvMusicName;
    private TextView mTvMusicDestrip;
    private ImageView mBtnDownload;
    private ImageView mBtnPlay;
    private ImageView mBtnDelete;
    private MusicPlayerView mpv;
    private SyncTask syncTask;
    private Handler mHandler = new Handler();
    private MediaPlayer mMediaPlayer = null;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean mRun;
    private boolean isPrepared;
    private FilelistEntity filelistEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_music_detail);

        initTitle();
        initResourceId();
        initListener();
        syncTask = new SyncTask(this, FTYPE.MUSIC);
        filelistEntity= UILApplication.getFilelistEntity();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.pic_test1)
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .extraForDownloader(new ShareUtils(this).getMusicFile())  //增加保存路径
                .build();
        initData();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initData() {
        //模拟数据
        int idx= (int) getIntent().getSerializableExtra("idx");
       // fileInfo =new FileInfo0(filelistEntity.getBklist().get(idx));
        fileInfo=filelistEntity.getBklist().get(idx);
        mTvMusicName.setText(fileInfo.getObjid());
        //String url = fileInfo.getFilePath();
        mMediaPlayer = new MediaPlayer();
        showDialog("正在加载中");
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                dismissDialog();
                isPrepared = true;
                int maxProgress = mMediaPlayer.getDuration() / 1000;
                if(mMediaPlayer.getDuration()%1000>0){
                    maxProgress+=1;
                }
                mpv.setMax(maxProgress);
                mpv.setProgress(0);
                mpv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isPrepared) {
                            if (mMediaPlayer.isPlaying()) {
                                mMediaPlayer.pause();
                                mBtnPlay.setImageResource(R.drawable.music_detail_btn_play);

                            } else {
                                if (!mRun) {
                                    startTimer();
                                }
                                mMediaPlayer.start();
                                mBtnPlay.setImageResource(R.drawable.icon_pause);

                            }
                            mpv.toggle();
                        }
                    }
                });
            }
        });

        /*String albumArt = MediaUtil.getAlbumArt(this, url);
        if (albumArt == null) {
			//albumImage.setBackgroundResource(R.drawable.audio_default_bg);
        } else {
            Bitmap bm = BitmapFactory.decodeFile(albumArt);
            BitmapDrawable bmpDraw = new BitmapDrawable(bm);
            mpv.setCoverDrawable(bmpDraw);
        }*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    remote = TClient.getinstance().CreateUrl(FTYPE.MUSIC, fileInfo.getObjid());
                    if(StringUtils.isNullOrEmpty(remote)){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.toast(mAct,"远程路径错误，请重试");
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mMediaPlayer.setDataSource(remote);
                                    mMediaPlayer.prepareAsync();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    String remote = "";

    private void initListener() {
        mIvLeft.setOnClickListener(this);
        mBtnDownload.setOnClickListener(this);
        mBtnPlay.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null)
            mTimer.cancel();
        if (mTimerTask != null)
            mTimerTask.cancel();
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mTimerTask = null;
        mTimer = null;
    }

    private void initResourceId() {
        mBtnDownload = (ImageView) findViewById(R.id.music_detail_download);
        mBtnPlay = (ImageView) findViewById(R.id.music_detail_play);
        mBtnDelete = (ImageView) findViewById(R.id.music_detail_delete);

        mTvTotalTime = (TextView) findViewById(R.id.music_detail_totaltime);
        mTvMusicName = (TextView) findViewById(R.id.music_detail_musicname);
        mTvMusicDestrip = (TextView) findViewById(R.id.music_detail_musicdestrip);
        mpv = (MusicPlayerView) findViewById(R.id.mpv);


    }

    private void startTimer() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (!mMediaPlayer.isPlaying()) {
                    return;
                }
                mpv.setProgress(mMediaPlayer.getCurrentPosition() / 1000);
            }
        };
        mRun = true;
        mTimer.schedule(mTimerTask, 0, 10);
    }

    private void initTitle() {
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mTvCenter = (TextView) findViewById(R.id.tv_center);
        mTvRight = (TextView) findViewById(R.id.tv_right);

        mTvCenter.setText("音乐");
        mTvRight.setText("取消");
        mTvRight.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right:
                break;
            case R.id.iv_left:
                finish();
                break;
            case R.id.music_detail_download:
                /**
                 * TODO 改成downloadmgr 下载
                 */

                if (syncTask != null && fileInfo != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            syncTask.download(fileInfo, MusicDetailActivity.this, false, null);
                        }
                    }).start();

                }
                break;
            case R.id.music_detail_play:
                if (isPrepared) {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                        mBtnPlay.setImageResource(R.drawable.music_detail_btn_play);

                    } else {
                        if (!mRun) {
                            startTimer();
                        }
                        mMediaPlayer.start();
                        mBtnPlay.setImageResource(R.drawable.icon_pause);

                    }
                    mpv.toggle();
                }
                break;
            case R.id.music_detail_delete:
                if (syncTask != null && fileInfo != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean delS = syncTask.DelRemoteObj(fileInfo);
                            if (delS) {

                                mHandler.post(new Runnable() {
                                    @Override

                                    public void run() {
                                        setResult(RESULT_OK);
                                        Toast.makeText(MusicDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                        MusicDetailActivity.this.finish();
                                    }
                                });
                            } else {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MusicDetailActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }).start();


                }
                break;
        }
    }


}
