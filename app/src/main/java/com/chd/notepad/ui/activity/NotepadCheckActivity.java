package com.chd.notepad.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chd.notepad.ui.adapter.NineAdapter;
import com.chd.notepad.ui.db.FileDBmager;
import com.chd.notepad.ui.item.NoteItem;
import com.chd.notepad.ui.item.NoteItemtag;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.utils.Base64Utils;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

public class NotepadCheckActivity extends Activity {

    private TextView contentText = null;
    private TextView timeText = null;
    private ImageView mIvLeft;
    private TextView mTvCenter;
    Gson gson;

    private final String TAG = this.getClass().getName();
    private RecyclerView nineGrid;
    private ArrayList<String> eatPath;
    private Context mContext;
    private int FOOD_IMAGE = 0xAF;
    private int DELFOODIMG = 0xAE;
    private NineAdapter adapter;
    private TextView checkTitle;
    private ArrayList<String> localPath;
    private TextView mTvRight;
    private NoteItemtag noteItemtag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notepad_check);

        gson = new Gson();
        mContext = this;
        initTitle();
        contentText = (TextView) findViewById(R.id.checkContent);
        checkTitle = (TextView) findViewById(R.id.checkTitle);
        timeText = (TextView) findViewById(R.id.checkTime);
        nineGrid = (RecyclerView) findViewById(R.id.editNineGrid);
        nineGrid.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        nineGrid.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(mContext, PhotoBrowseActivity.class);
                intent.putStringArrayListExtra("PhotoPath", localPath);
                intent.putExtra("PhotoPosition", position);
                startActivityForResult(intent, DELFOODIMG);
            }
        });


        Intent intent = getIntent();//获取启动该Activity的intent对象

        //String id = intent.getStringExtra("hashcode");
        //String time= intent.getStringExtra("time");
        //String content = intent.getStringExtra("content");
        //String fname=intent.getStringExtra("fname");
        //long t = Long.parseLong(time);
        noteItemtag = (NoteItemtag) intent.getSerializableExtra("item");
        String path = new ShareUtils(this.getApplicationContext()).getStorePathStr();
        FileDBmager fileDBmager = new FileDBmager(this);
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeInMillis();
        String datetime = DateFormat.format("yyyy-MM-dd HH:mm:ss", noteItemtag.getStamp() * 1000L).toString();
        String content = "";
        content = fileDBmager.readFile(noteItemtag.get_fname());
        try {
            NoteItem noteItem = gson.fromJson(content, NoteItem.class);
            this.contentText.setText(noteItem.getContent());
            this.checkTitle.setText(noteItem.getTitle());
            eatPath = noteItem.getPicList();
            if (eatPath != null && eatPath.size() > 0) {
                localPath = new ArrayList<>();
                for (String item :
                        eatPath) {
                    String s = getCacheDir() + "/" + System.currentTimeMillis() + ".jpeg";
                    Base64Utils.base64ToFile(item, s);
                    localPath.add("file:" + s);
                }
                adapter = new NineAdapter(eatPath);
                nineGrid.setAdapter(adapter);
            } else {
                nineGrid.setVisibility(View.GONE);
            }


        } catch (Exception e) {
            this.contentText.setText(content);
        }


        this.timeText.setText(datetime);

    }

    public void editNotePad(View v) {
        //跳转编辑
        //用于Activity之间的通讯
        Intent intent = new Intent();
        //intent.putExtra("id", nt.id);
        intent.putExtra("state", 2);
        //intent.putExtra("fname", /*cursor.getLong(cursor.getColumnIndex("time"))*/nt.get_fname());
        // intent.putExtra("content", /*cursor.getString(cursor.getColumnIndex("content"))*/nt.content);
        intent.putExtra("item", noteItemtag);
        intent.setClass(this, NotepadEditActivity.class);
        startActivityForResult(intent, 0x1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case 0x1001:
                    setResult(resultCode, data);
                    finish();
                    break;


            }
        }
    }

    private void initTitle() {
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mTvCenter = (TextView) findViewById(R.id.tv_center);
        mTvRight = (TextView) findViewById(R.id.tv_right);
        mTvRight.setText("删除");
        mTvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(0x111, getIntent());
                finish();
            }
        });
        mTvCenter.setText("心事详情");
        mIvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (localPath != null && localPath.size() > 0) {
            for (String s :
                    localPath) {
                File f = new File(s.replace("file:", ""));
                if (f.exists()) {
                    boolean delete = f.delete();
                    Log.d("liumj", "删除成功");
                }
            }
        }
    }
}
