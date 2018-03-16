package com.chd.music.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.Entity.PicFile;
import com.chd.base.Ui.ActiveProcess;
import com.chd.base.backend.SyncTask;
import com.chd.contacts.vcard.StringUtils;
import com.chd.listener.DataCallBack;
import com.chd.music.adapter.MusicAdapter;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.share.ShareUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends ActiveProcess implements OnClickListener, OnItemClickListener {

    SyncTask syncTask;
    private ImageView mIvLeft;
    private TextView mTvCenter;
    private TextView mTvRight;
    private TextView mTvNumber;
    private GridView mGvMusic;
    private View mViewNumber;
    private MusicAdapter adapter;
    private List<FileInfo> cloudUnits = new ArrayList<>();
    private List<FileInfo0> cloudList = new ArrayList<>();
    FilelistEntity filelistEntity;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            dismissDialog();
            adapter = new MusicAdapter(MusicActivity.this,
                    cloudList);
            mGvMusic.setAdapter(adapter);
            mTvNumber.setText("未备份音乐" + /*filelistEntity.getUnbakNumber()*/0 + "首");
        }
    };
    private String musicPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        initTitle();
        initResourceId();
        initListener();
        UILApplication.ClearFileEntity();
        musicPath = new ShareUtils(this).getMusicFile().getPath();
        syncTask = new SyncTask(MusicActivity.this, FTYPE.MUSIC);
        filelistEntity=UILApplication.getFilelistEntity();
        onNewThreadRequest();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void onNewThreadRequest() {
        showDialog("正在获取");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                cloudUnits = syncTask.getCloudUnits(0, 10000);
                initData(cloudUnits);
            }
        });
        thread.start();
    }



    private void initData(final List<FileInfo> cloudUnits) {

        if (cloudUnits == null) {
            System.out.print("query remote failed");
        }
         final List<Integer> lst=filelistEntity.getUnbak_idx_lst();
        lst.clear();
        // 找到10个以后 先返回, 剩下的 在线程里面继续找
        syncTask.dbManager.GetLocalFiles0(FTYPE.MUSIC,new String[]{"mp3", "mid", "wav", "flv"}, true, filelistEntity, new DataCallBack(10) {
            @Override
            /*
            * @count 当前list的最后下标
            */
            public void success(List<FileInfo0> datas, int offset, int count) {
                //接收到的数据
                int unbak=GetUnbakSubitem(offset,count,/*list*/lst);
                refreshData(unbak);
            }
        });
    }

    /**
     *
     * @param lastoffset 向list 添加元素的起始位置
     * @param Exceptnumber 希望添加元素的个数
     * @param list 容器对象,应该初始化为线程安全对象
     * @return 实际添加元素的个数
     */
    public int  GetUnbakSubitem(int lastoffset, int Exceptnumber ,List<Integer> list) {
        int count = 0;
        boolean addflag=false;
        if (list != null)
            addflag=true;
            /*return count;*/
        FileInfo0 item;
        //int min=0;
        int idx = lastoffset;
        List<FileInfo0> locallst=filelistEntity.getLocallist();
        int len=locallst.size();
        for (;idx<len;idx++)
        {
            item =locallst.get(idx);
            if (!syncTask.isBacked(item))
            {
                count++;
                if (addflag) {
                    list.add(idx);
                }
            }
            else
            {
                Log.i("GetUnbakSubitem:",item.getObjid()+"  backuped");
            }
        }
        return  count;
    }

    /*private void initData() {

        filelistEntity = UILApplication.getFilelistEntity();
        if (cloudUnits == null) {
            System.out.print("query remote failed");
        }
        syncTask.analyMusicUnits(cloudUnits, filelistEntity);
        for (FileInfo finfo : cloudUnits) {
            FileInfo0 item = new FileInfo0(finfo);
            //已备份文件
            String path = item.getDirPath();
            String name = item.getFilename();
            if (StringUtils.isNullOrEmpty(path)) {
                *//*
                int sysid = filelistEntity.queryLocalSysid(item.getObjid());
                if (sysid > 0) {
                    item.setFilePath(filelistEntity.getDirPath(sysid));
                } else {
                    item.setFilePath(musicPath + "/" + item.getObjid());
                }*//*
            }
            if (name == null) {
                item.setFilename(item.getObjid());
            }
            cloudList.add(item);
        }


        handler.sendEmptyMessage(0);

    }
*/
    int unbks=0;
    private void refreshData(final int count) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                unbks+=count;
                mTvNumber.setText("未备份文件" + unbks + "个");
            }
        });
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
//        mTvRight.setText("编辑");
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
               // ArrayList<FileInfo0> fileLocals = new ArrayList<>(filelistEntity.getLocallist());
               // intent.putExtra("locallist", fileLocals);
                startActivityForResult(intent, 0x02);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent intent = new Intent(this, MusicDetailActivity.class);
        intent.putExtra("file", cloudList.get(arg2));
        startActivityForResult(intent, 0x99);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0x99:
                    //删除成功了
                    cloudList.clear();
                    cloudUnits.clear();
                    mTvNumber.setText("未备份音乐0首");
                    onNewThreadRequest();
                    break;
                case 0x02:
                    //有备份问题
                    cloudList.clear();
                    cloudUnits.clear();
                    mTvNumber.setText("未备份音乐0首");
                    onNewThreadRequest();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
