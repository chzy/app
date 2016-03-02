package com.chd.music.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.base.Ui.ActiveProcess;
import com.chd.base.backend.SyncTask;
import com.chd.music.backend.MediaUtil;
import com.chd.music.entity.MusicBean;
import com.chd.photo.adapter.RoundImageView;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.File;
import java.io.FileNotFoundException;

public class MusicDetailActivity extends ActiveProcess implements OnClickListener {

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
    FileInfo0 fileInfo0;
    private ImageView mIvLeft;
    private TextView mTvCenter;
    private TextView mTvRight;
    private TextView mTvTotalTime;
    private TextView mTvMusicName;
    private TextView mTvMusicDestrip;
    private ImageView mBtnDownload;
    private ImageView mBtnPlay;
    private ImageView mBtnDelete;
    private RoundImageView mRoundImageView;
    private SyncTask syncTask;
    private MusicBean bean;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_music_detail);

        initTitle();
        initResourceId();
        initListener();
        syncTask = new SyncTask(this, FTYPE.MUSIC);
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
        bean = (MusicBean) getIntent().getSerializableExtra("file");
        fileInfo0 = bean.getFileInfo0();
        if (fileInfo0 == null) {
            return;
        }


        mTvMusicName.setText(fileInfo0.getFilename());
        String url = fileInfo0.getFilePath();
        String albumArt = MediaUtil.getAlbumArt(this, url);
        if (albumArt == null) {
//			albumImage.setBackgroundResource(R.drawable.audio_default_bg);
        } else {
            Bitmap bm = BitmapFactory.decodeFile(albumArt);
            BitmapDrawable bmpDraw = new BitmapDrawable(bm);
            mRoundImageView.setImageDrawable(bmpDraw);
        }

    }

    private void initListener() {
        mIvLeft.setOnClickListener(this);
        mBtnDownload.setOnClickListener(this);
        mBtnPlay.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
    }

    private void initResourceId() {
        mBtnDownload = (ImageView) findViewById(R.id.music_detail_download);
        mBtnPlay = (ImageView) findViewById(R.id.music_detail_play);
        mBtnDelete = (ImageView) findViewById(R.id.music_detail_delete);

        mTvTotalTime = (TextView) findViewById(R.id.music_detail_totaltime);
        mTvMusicName = (TextView) findViewById(R.id.music_detail_musicname);
        mTvMusicDestrip = (TextView) findViewById(R.id.music_detail_musicdestrip);
        mRoundImageView = (RoundImageView) findViewById(R.id.music_detail_pic);


    }

    private void initTitle() {
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mTvCenter = (TextView) findViewById(R.id.tv_center);
        mTvRight = (TextView) findViewById(R.id.tv_right);

        mTvCenter.setText("音乐");
        mTvRight.setText("取消");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right:
            case R.id.iv_left:
                finish();
                break;
            case R.id.music_detail_download:
                if (syncTask != null && fileInfo0 != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            syncTask.download(fileInfo0, MusicDetailActivity.this, false);
                        }
                    }).start();

                }
                break;
            case R.id.music_detail_play:


                try {
                    File f=new File(fileInfo0.getFilePath());
                    if(!f.exists()){
                        throw new FileNotFoundException("文件未找到");
                    }
                    Uri uri = Uri.parse("file://"+fileInfo0.getFilePath());
                    //调用系统自带的播放器
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "video/mp4");
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MusicDetailActivity.this, "请先下载", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.music_detail_delete:
                if (syncTask != null && fileInfo0 != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean delS = syncTask.DelRemoteObj(fileInfo0);
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
