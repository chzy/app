package com.chd.music.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.base.Entity.FilelistEntity;
import com.chd.base.backend.SyncTask;
import com.chd.music.adapter.MusicAdapter;
import com.chd.music.entity.MusicBean;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends Activity implements OnClickListener, OnItemClickListener {

    SyncTask syncTask;
    private ImageView mIvLeft;
    private TextView mTvCenter;
    private TextView mTvRight;
    private TextView mTvNumber;
    private GridView mGvMusic;
    private View mViewNumber;
    private MusicAdapter adapter;
    private List<MusicBean> mMusicList = new ArrayList<MusicBean>();
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            adapter.notifyDataSetChanged();
        }
    };
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_music);

        initTitle();
        initResourceId();
        initListener();
        adapter = new MusicAdapter(MusicActivity.this,
                mMusicList);
        mGvMusic.setAdapter(adapter);
        syncTask = new SyncTask(MusicActivity.this, FTYPE.MUSIC);
        onNewThreadRequest();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void onNewThreadRequest() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                final List<FileInfo0> cloudUnits = syncTask.getCloudUnits(0, 10000);
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
        FilelistEntity filelistEntity = syncTask.analyMusicUnits(cloudUnits);
        cloudUnits.clear();
        cloudUnits = null;
        cloudUnits = filelistEntity.getBklist();
	

        for (FileInfo0 item : cloudUnits) {
            //FileInfo0 item=new FileInfo0(finfo);


            //已备份文件
            String path = item.getFilePath();
            String name = item.getFilename();
            if(item.getFilePath()==null){
                if(item.getSysid()>0){
                    item =syncTask.queryLocalInfo(item.getSysid());
                }else{
                    item.setFilePath(new ShareUtils(this).getMusicFile().getPath()+ "/"+item.getObjid());
                }
            }
            /*if (syncTask.haveLocalCopy(item)) {

            }*/
            MusicBean musicBean = new MusicBean(name, path);
            musicBean.setFileInfo0(item);

            //	syncTask.download(item, null, false);
            mMusicList.add(musicBean);
        }

        mTvNumber.setText(String.format("未备份音乐%d首", filelistEntity.getUnbakNumber()));

        handler.sendEmptyMessage(0);

    }

    private void initResourceId() {
        mTvNumber = (TextView) findViewById(R.id.tv_music_number);
        mGvMusic = (GridView) findViewById(R.id.gv_music);
        mViewNumber = findViewById(R.id.iv_music_num_layout);

        mTvNumber.setText("未备份音乐0首");
    }

    private void initListener() {
        mIvLeft.setOnClickListener(this);
        mTvRight.setOnClickListener(this);
        mGvMusic.setOnItemClickListener(this);
        mViewNumber.setOnClickListener(this);
    }

    private void initTitle() {
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mTvCenter = (TextView) findViewById(R.id.tv_center);
        mTvRight = (TextView) findViewById(R.id.tv_right);

        mTvCenter.setText("音乐");
        mTvRight.setText("编辑");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_right: // 编辑
                // TODO
                break;
            case R.id.iv_music_num_layout:
                Intent intent = new Intent(this, MusicBackupActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent intent = new Intent(this, MusicDetailActivity.class);
        intent.putExtra("file", mMusicList.get(arg2));
        pos = arg2;
        startActivityForResult(intent, 0x11);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0x11:
                    //删除成功了
                    mMusicList.remove(pos);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
