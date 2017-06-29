package com.chd.photo.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chd.base.Entity.FileLocal;
import com.chd.base.UILActivity;
import com.chd.photo.adapter.PicAdapter;
import com.chd.proto.FTYPE;
import com.chd.service.RPCchannel.upload.FileUploadInfo;
import com.chd.service.RPCchannel.upload.FileUploadManager;
import com.chd.service.RPCchannel.upload.UploadOptions;
import com.chd.service.RPCchannel.upload.listener.OnUploadListener;
import com.chd.service.RPCchannel.upload.progressaware.ProgressBarAware;
import com.chd.yunpan.R;
import com.chd.yunpan.utils.ToastUtils;

import java.util.ArrayList;

public class PicBackActivity extends UILActivity implements View.OnClickListener {

    private RecyclerView mPicRecyclerView;
    private TextView mPicUploadTextView;
    private RelativeLayout mPicBottomRelativeLayout;
    private ArrayList<ArrayList<FileLocal>> localList = new ArrayList();
    private PicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unbackup);
        mPicRecyclerView = (RecyclerView) findViewById(R.id.lv_pic);
        mPicUploadTextView = (TextView) findViewById(R.id.tv_pic_upload);
        mPicBottomRelativeLayout = (RelativeLayout) findViewById(R.id.rl_pic_bottom);
        TextView title = (TextView) findViewById(R.id.tv_center);
        ImageView iv_back = (ImageView) findViewById(R.id.iv_left);
        title.setText("选择未备份照片");
        iv_back.setOnClickListener(this);
        right = (TextView) findViewById(R.id.tv_right);
        right.setText("全选");
        right.setOnClickListener(this);
        mPicUploadTextView.setOnClickListener(this);
        localList.addAll((ArrayList<ArrayList<FileLocal>>) getIntent().getSerializableExtra("locallist"));
        adapter = new PicAdapter(PicBackActivity.this, localList, null, false, true);
        mPicRecyclerView.setAdapter(adapter);
        mPicRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    TextView right;

    @Override
    public void onEventMainThread(Object obj) {
        super.onEventMainThread(obj);
        mPicUploadTextView.setText(String.format("备份（%d）", obj));

    }

    int count = 0;

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_left:
                onBackPressed();
                break;
            case R.id.tv_right:
                //全选
                if ("取消全选".equals(right.getText().toString())) {
                    ArrayList<String> lists = new ArrayList<>();
                    adapter.setSelectList(lists);
                    right.setText("全选");
                } else {
                    ArrayList<String> lists = new ArrayList<>();
                    for (int i = 0; i < localList.size(); i++) {
                        ArrayList<FileLocal> fileLocals = localList.get(i);
                        for (int j = 0; j < fileLocals.size(); j++) {
                            lists.add(i + " " + j);
                        }
                    }
                    adapter.setSelectList(lists);
                    right.setText("取消全选");
                }
                break;
            case R.id.tv_pic_upload:
                //上传
                final ArrayList<String> selectData = adapter.getSelectData();
                FileUploadManager manager = FileUploadManager.getInstance();
                boolean overwrite = true;
                boolean resume = true;
                UploadOptions options = new UploadOptions(overwrite, resume);
                final MaterialDialog.Builder builder = new MaterialDialog.Builder(PicBackActivity.this);
                builder.content("正在上传");
                builder.progress(true, 100);
                final MaterialDialog build = builder.build();
                build.show();
                count = 0;
                for (String s :
                        selectData) {
                    String[] split = s.split(" ");
                    int pos1 = Integer.parseInt(split[0]);
                    int pos2 = Integer.parseInt(split[1]);
                    final FileLocal local = localList.get(pos1).get(pos2);
                    local.setFtype(FTYPE.PICTURE);
                    manager.uploadFile(new ProgressBarAware(build), null, local, new OnUploadListener() {
                        @Override
                        public void onError(FileUploadInfo uploadData, int errorType, String msg) {
                            ToastUtils.toast(PicBackActivity.this, "上传失败");
                            build.dismiss();
                        }

                        @Override
                        public void onSuccess(FileUploadInfo uploadData, Object data) {
                            build.dismiss();
                            count++;
                            if (count == selectData.size()) {
                                ToastUtils.toast(PicBackActivity.this, "上传成功");
                                build.dismiss();
                                for (String s :
                                        selectData) {
                                    String[] split = s.split(" ");
                                    int pos1 = Integer.parseInt(split[0]);
                                    int pos2 = Integer.parseInt(split[1]);
                                    ArrayList<FileLocal> fileLocals = localList.get(pos1);
                                    fileLocals.remove(pos2);
                                    if (fileLocals.isEmpty()) {
                                        localList.remove(pos1);
                                    }
                                }
                                adapter.setSelectList(new ArrayList<String>());
                                adapter.notifyDataSetChanged();
                                setResult(RESULT_OK);
                            }

                        }
                    }, options);
                }


                break;
        }
    }
}
