package com.chd.notepad.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.notepad.ui.adapter.NineAdapter;
import com.chd.notepad.ui.db.FileDBmager;
import com.chd.notepad.ui.item.NoteItem;
import com.chd.notepad.ui.item.NoteItemtag;
import com.chd.yunpan.R;
import com.chd.yunpan.utils.Base64Utils;
import com.chd.yunpan.utils.TimeUtils;
import com.chd.yunpan.view.NineGridlayout;
import com.google.gson.Gson;
import com.multi_image_selector.MultiImageSelectorActivity;

import java.util.ArrayList;


public class NotepadEditActivity extends Activity {

    public static final int CHECK_STATE = 0;
    public static final int EDIT_STATE = 1;
    public static final int ALERT_STATE = 2;
    public ArrayList<String> eatPhotoData = new ArrayList<>();
    private int state = -1;
    private ImageView mIvLeft;
    private TextView mTvCenter;
    private TextView mTvRight;
    private EditText title;
    private EditText content;
    //private DatabaseManage dm = null;
    private FileDBmager fileDBmager;
    private String fname = "";
    private String titleText = "";
    private String contentText = "";
    private String timeText = "";
    private NineGridlayout nineGrid;
    private NineAdapter mAdapter;
    private ArrayList<String> eatPath = new ArrayList<>();
    private Context mContext;
    private int FOOD_IMAGE = 0xAF;
    private int DELFOODIMG = 0xAE;
    private ArrayList<String> delList = new ArrayList<>();
    private Gson gson;
   

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notepad_edit);
        mContext = this;
        gson = new Gson();
        initTitle();
        fileDBmager = new FileDBmager(this);
        Intent intent = getIntent();
        state = intent.getIntExtra("state", EDIT_STATE);

        //赋值控件对象
        title = (EditText) findViewById(R.id.editTitle);
        content = (EditText) findViewById(R.id.editContent);
        nineGrid = (NineGridlayout) findViewById(R.id.editNineGrid);


        nineGrid.setOnItemClickListerner(new NineGridlayout.OnItemClickListerner() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == (eatPath.size() - 1)) {
                    Intent intent = new Intent(mContext, MultiImageSelectorActivity.class);
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                    if (eatPath.contains("assets://add_photo.png")) {
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 5 - eatPath.size() + 1);
                    } else {
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 5 - eatPath.size());
                    }
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                    startActivityForResult(intent, FOOD_IMAGE);
                } else {
                    Intent intent = new Intent(mContext, PhotoBrowseActivity.class);
                    intent.putStringArrayListExtra("PhotoPath", eatPhotoData);
                    intent.putExtra("PhotoPosition", position);
                    startActivityForResult(intent, DELFOODIMG);
                }
            }
        });


        content.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                content.setSelection(content.getText().toString().length());
                return false;
            }

        });

        if (state == ALERT_STATE) {//修改状态,赋值控件
            //id = intent.getStringExtra("id");
            //titleText = intent.getStringExtra("title");
            //long time = intent.getLongExtra("time",0);
            //if (time<1000)
            //	time=System.currentTimeMillis();
            //contentText = intent.getStringExtra("content");
            //timeText = intent.getStringExtra("time");
            NoteItemtag itemtag = (NoteItemtag) intent.getSerializableExtra("item");

            title.setText(TimeUtils.getTime(itemtag.getStamp()));
            String contentStr = fileDBmager.readFile(itemtag.get_fname());
            try {
                NoteItem noteItem = gson.fromJson(contentStr, NoteItem.class);
                content.setText(noteItem.getContent());
                content.setSelection(noteItem.getContent().length());
                title.setText(noteItem.getTitle());
                title.setSelection(noteItem.getTitle().length());
                eatPath = noteItem.getPicList();
                if(eatPath.size()<5){
                    eatPath.add("assets://add_photo.png");
                }
                eatPhotoData=eatPath;
                mAdapter = new NineAdapter(this, eatPath);
                nineGrid.setAdapter(mAdapter);
            } catch (Exception e) {
                content.setText(contentStr);
            }
            fname = itemtag.get_fname();
        } else {
            eatPath.add("assets://add_photo.png");
            mAdapter = new NineAdapter(this, eatPath);
            nineGrid.setAdapter(mAdapter);
        }

        //dm = new DatabaseManage(this);
    }

    private void initTitle() {
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mTvCenter = (TextView) findViewById(R.id.tv_center);
        mTvRight = (TextView) findViewById(R.id.tv_right);


        mTvCenter.setText("添加心事");
        mTvRight.setText("发布");
        mIvLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvRight.setOnClickListener(new EditCompleteListener());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FOOD_IMAGE) {
            if (resultCode == RESULT_OK) {
                // Get the result list of select image paths
//                eatPath.clear();
                eatPath.remove("assets://add_photo.png");
                eatPhotoData = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                for (int i = 0; i < eatPhotoData.size(); i++) {
                    eatPath.add("file://" + eatPhotoData.get(i));
//					LogUtils.e(eatPhotoData.get(i)+":路径");
                }
                eatPath.add("assets://add_photo.png");
                //eatPath.addAll(data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT));
                //eatPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                //Log.w(TAG, eatPath.toString());
                //mPhotoAdapter.notifyDataSetChanged();
                //eatPath.add("assets://add_photo.png");
                nineGrid.setAdapter(new NineAdapter(this, eatPath));

            }
        }
        if (resultCode == 996) {
            delList = data.getStringArrayListExtra("imgList");
        }
        if (requestCode == DELFOODIMG) {
            eatPath.clear();
            eatPhotoData = delList;
            for (int i = 0; i < delList.size(); i++) {
                String path=eatPhotoData.get(i);
//                if(path.contains("storage")){
//                    eatPath.add("file://" + eatPhotoData.get(i));
//                }else{
                    eatPath.add(path);
//                }
            }
            if(!eatPath.contains("assets://add_photo.png")&&eatPath.size()<5){
            eatPath.add("assets://add_photo.png");
            }
            nineGrid.setAdapter(new NineAdapter(this, eatPath));
        }

    }


    /**
     * 监听完成按钮
     *
     * @author mao
     */
    public class EditCompleteListener implements OnClickListener {

        public void onClick(View v) {
            titleText = title.getText().toString();
            contentText = content.getText().toString();


            ArrayList<String> food = new ArrayList<>();

            for (int i = 0; i < eatPhotoData.size(); i++) {
                String jpeg = Base64Utils.imgToBase64(eatPhotoData.get(i), null, "JPEG");
                food.add(jpeg);
            }

            NoteItem item = new NoteItem();
            item.setTitle(titleText);
            item.setContent(contentText);
            item.setPicList(food);
            String content = gson.toJson(item);


            try {
                //dm.open();

                if (state == EDIT_STATE)//新增状态
                    fileDBmager.writeFile(System.currentTimeMillis() + "", content);
                //dm.insert(titleText, contentText);
                if (state == ALERT_STATE)//修改状态
                    fileDBmager.editFile(fname, content);
                setResult(RESULT_OK);
                //dm.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            finish();
        }

    }




}
