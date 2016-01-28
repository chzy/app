package com.chd.photo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chd.MediaMgr.utils.MediaFileUtil;
import com.chd.base.Ui.ActiveProcess;
import com.chd.base.Ui.DownListActivity;
import com.chd.base.backend.SyncTask;
import com.chd.photo.adapter.PicEditAdapter;
import com.chd.photo.entity.PicEditBean;
import com.chd.photo.entity.PicEditItemBean;
import com.chd.photo.entity.PicInfoBean;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PicEditActivity extends ActiveProcess implements OnClickListener {

    private final String TAG = this.getClass().getName();
    SyncTask syncTask;
    List<FileInfo0> cloudUnits;
    private ImageView mIvLeft;
    private TextView mTvCenter;
    private TextView mTvRight;
    private ListView mLvPic;
    private View mLvPicEditDel;
    private View mLvPicEditUpDown;
    private View mLvPicEditView;
    private int month, year;
    private List<PicEditBean> mPicList = new ArrayList<PicEditBean>();
    private List<PicInfoBean> mPicList0;//数组对象传递到 这个变量里面了
    private PicEditAdapter picEditAdapter;
    private boolean bIsUbkList;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (picEditAdapter == null) {
                picEditAdapter.setData(mPicList);
            } else {
                picEditAdapter.notifyDataSetChanged();
            }
        }
    };
    private Button mBtnDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_edit);
        bIsUbkList = getIntent().getBooleanExtra("ubklist", false);
        month = getIntent().getIntExtra("month", -1);
        mPicList0 = (List) getIntent().getSerializableExtra("listUnits");
        //List<PicInfoBean> list=(List)getIntent().getSerializableExtra("ubklistUnits");
        /*String sYear = getIntent().getStringExtra("year");
		if (sYear != null && sYear.length() > 0)
		{
			year = Integer.valueOf(sYear);
		}*/

        initTitle();
        initResourceId();
        initListener();
        picEditAdapter = new PicEditAdapter(PicEditActivity.this, mPicList);
        mLvPic.setAdapter(picEditAdapter);
        onNewThreadRequest();
    }

    private void onNewThreadRequest() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (syncTask == null)
                    syncTask = new SyncTask(PicEditActivity.this, FTYPE.PICTURE);

                runOnUiThread(new Runnable() {
                    public void run() {
                        initData();
                    }
                });
            }
        });
        thread.start();
    }

    private void initData() {

        if (cloudUnits == null) {
            System.out.print("query remote failed");
        }

		/*FilelistEntity filelistEntity=syncTask.analyPhotoUnits(cloudUnits);
		cloudUnits.clear();
		cloudUnits=null;
		cloudUnits=filelistEntity.getBklist();*/
        //显示的时候过滤文件类型
		/*MFileFilter fileFilter=new MFileFilter();
		fileFilter.setCustomCategory(new String[]{"jpg"},true);*/

        //批量加载下载任务 调用 dbmnager addUpLoadingFile 方法

        //实时上传 需要 在当前 Activity 里面  增加一个 public的 updateProgress 函数 将该 Activity 传入后台的 Activity .线程里调用  setProcess 显示进度 . .
        //调用 SyncTask 里面的 upload


        Map<String, List<PicEditItemBean>> tmpDayMap = new HashMap<String, List<PicEditItemBean>>();
        for (PicInfoBean item : mPicList0) {
            //if (bIsUbkList)

            String tmpDay = item.getDay();
            PicEditItemBean picInfoBean = new PicEditItemBean();

            picInfoBean.setUrl(item.getUrl());
            picInfoBean.setSelect(false);
            picInfoBean.setbIsUbkList(bIsUbkList);
            //picInfoBean.setFileInfo0(info);

            if (tmpDayMap.get(tmpDay) != null) {
                tmpDayMap.get(tmpDay).add(picInfoBean);
            } else {
                List<PicEditItemBean> dayItemBeans = new ArrayList<PicEditItemBean>();
                dayItemBeans.add(picInfoBean);
                tmpDayMap.put(tmpDay, dayItemBeans);
            }


        }


        if (tmpDayMap.size() > 0) {

            for (Map.Entry<String, List<PicEditItemBean>> entryDay : tmpDayMap.entrySet()) {
                PicEditBean picBean = new PicEditBean(String.valueOf(entryDay.getKey()), entryDay.getValue());
                picBean.setbIsUbkList(bIsUbkList);
                mPicList.add(picBean);
            }
        }

        handler.sendEmptyMessage(0);
    }

    private void initListener() {
        mIvLeft.setOnClickListener(this);
        mTvRight.setOnClickListener(this);
        mTvRight.setTag(false);
        mLvPicEditDel.setOnClickListener(this);
        mLvPicEditUpDown.setOnClickListener(this);
        mBtnDown.setOnClickListener(this);
    }

    private void initResourceId() {
        mLvPic = (ListView) findViewById(R.id.lv_pic_edit);
        mLvPicEditDel = findViewById(R.id.lv_pic_edit_del);
        mLvPicEditView = findViewById(R.id.lv_pic_edit_layout);
        mLvPicEditUpDown = findViewById(R.id.lv_pic_edit_updown);
        mBtnDown = (Button) findViewById(R.id.pic_btn_down);

        TextView textView = (TextView) findViewById(R.id.lv_pic_edit_updown_text);
        if (bIsUbkList) {
            textView.setText("上传");
        } else {
            textView.setText("下载");
        }
    }

    private void initTitle() {
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mTvCenter = (TextView) findViewById(R.id.tv_center);
        mTvRight = (TextView) findViewById(R.id.tv_right);

        mTvCenter.setText("照片");
        mTvRight.setText("编辑");
        mTvRight.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pic_btn_down:
                //任务列表
                Intent intent = new Intent(PicEditActivity.this, DownListActivity.class);
                startActivity(intent);
                break;

            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_right:
                boolean bTag = (Boolean) mTvRight.getTag();
                mLvPicEditView.setVisibility(bTag ? View.GONE : View.VISIBLE);
                mTvRight.setText(bTag ? "编辑" : "取消");
                mTvRight.setTag(!bTag);
                for (PicEditBean picEditBean : mPicList) {
                    picEditBean.setEdit(!bTag);
                    picEditBean.setSelect(false);
                    for (PicEditItemBean picEditItemBean : picEditBean.getList()) {
                        picEditItemBean.setEdit(!bTag);
                        picEditBean.setSelect(false);
                    }
                }
                handler.sendEmptyMessage(0);
                break;
            case R.id.lv_pic_edit_del: {
            }
            break;
            case R.id.lv_pic_edit_updown: {
                for (PicEditBean picEditBean : mPicList) {
				/*if (!picEditBean.isSelect())
				{
					continue;
				}*/

                    for (PicEditItemBean picEditItemBean : picEditBean.getList()) {
                        if (!picEditItemBean.isSelect()) {
                            continue;
                        }

                        ///FileInfo fileInfo = syncTask.queryLocalInfo(picEditItemBean.getPicid());
					/*if (fileInfo==null) {
						Log.e(TAG, "query localfile fail!!!!");
						continue;
					}*/
                        final FileInfo0 fileInfo0 = new FileInfo0();
                        int idx = picEditItemBean.getUrl().indexOf("://");
                        if (idx < 0) {
                            Log.e(TAG, "error file path fail!!!!");
                            continue;
                        }
                        idx += 3;
                        String uri = picEditItemBean.getUrl().substring(idx);
                        if (picEditItemBean.isbIsUbkList()) {
                            fileInfo0.setFilePath(uri);
                            fileInfo0.setObjid(MediaFileUtil.getFnameformPath(uri));
                        } else
                            fileInfo0.setObjid(uri);
                        //fileInfo0.setFilePath(ThumUtil.splitFileName(picEditItemBean.getPicpath()));
                        //fileInfo0.setFilesize(fileInfo.getFilesize());
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (bIsUbkList) {
                                    syncTask.upload(fileInfo0, PicEditActivity.this, false);
                                } else {
                                    syncTask.download(fileInfo0, null, false);
                                }
                            }
                        });
                        thread.start();
                    }
                }
            }
            break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 99) {
            switch (requestCode) {
                case 0x12:
                    //删除成功
                    if (data != null) {
                        int pos = data.getIntExtra("pos", -1);
                        int pos2 = data.getIntExtra("pos2", -1);
                        mPicList.get(pos).getList().remove(pos2);
                        picEditAdapter.notifyDataSetChanged();
                        setResult(RESULT_OK);
                    }
                    break;
            }
        }
    }
}
