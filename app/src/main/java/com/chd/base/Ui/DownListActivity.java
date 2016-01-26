package com.chd.base.Ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chd.base.adapter.DownListAdapter;
import com.chd.base.backend.SyncTask;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @description
 * @FileName: com.chd.base.Ui.DownListActivity
 * @author: liumj
 * @date:2016-01-20 20:17
 * OS:Mac 10.10
 * Developer Kits:AndroidStudio 1.5
 */

public class DownListActivity extends ActiveProcess implements View.OnClickListener {


    private ListView listView;
    private TextView tv_center;
    private ImageView iv_left;
    private SyncTask syncTask;
    private DownListAdapter adapter;
    private List<FileInfo0> mDownList = new ArrayList<>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downlist);
        listView = (ListView) findViewById(R.id.DOWNLOADLIST_LV);
        tv_center = (TextView) findViewById(R.id.tv_center);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        tv_center.setText("任务列表");
        iv_left.setOnClickListener(this);
        syncTask = new SyncTask(this, FTYPE.NORMAL);
        adapter = new DownListAdapter(mDownList, this);
        initData();
    }
    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDownList = syncTask.getDownList(1000);
                Log.d("lmj","下载数量:"+mDownList.size());
                mHandler.sendEmptyMessage(0);
            }
        }).start();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                onBackPressed();
                break;
        }
    }
}
