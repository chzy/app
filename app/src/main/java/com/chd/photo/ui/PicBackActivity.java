package com.chd.photo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.Entity.PicFile;
import com.chd.base.UILActivity;
import com.chd.base.backend.SyncTask;
import com.chd.photo.adapter.PicInfoAdapter2;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.service.RPCchannel.upload.FileUploadInfo;
import com.chd.service.RPCchannel.upload.FileUploadManager;
import com.chd.service.RPCchannel.upload.UploadOptions;
import com.chd.service.RPCchannel.upload.listener.OnUploadListener;
import com.chd.service.RPCchannel.upload.progressaware.ProgressBarAware;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.utils.TimeUtils;
import com.chd.yunpan.utils.ToastUtils;
import com.chd.yunpan.view.SuperRefreshRecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PicBackActivity extends UILActivity implements View.OnClickListener {

    private SuperRefreshRecyclerView mPicRecyclerView;
    private TextView mPicUploadTextView;
    private RelativeLayout mPicBottomRelativeLayout;
    private PicInfoAdapter2 adapter;
    private ArrayList<PicFile<FileInfo0>> picFiles=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unbackup);
        mPicRecyclerView = (SuperRefreshRecyclerView) findViewById(R.id.lv_pic);
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
        adapter = new PicInfoAdapter2<FileInfo0>(picFiles,false);
        adapter.setShowEdit(true);
        mPicRecyclerView.setAdapter(adapter);
        mPicRecyclerView.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                PicFile<FileInfo0> file = picFiles.get(position);
               if(file.isHeader){
                   return 4;
               }
               return 1;
            }
        });
        mPicRecyclerView.setLayoutManager(manager);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    //非视频，即图片进去
                PicFile<FileInfo0> file = picFiles.get(position);
                Intent intent = new Intent(mAct, PicDetailActivity.class);
                    intent.putExtra("bean", (Serializable) file.t);
                    intent.putExtra("pos", position);
                    intent.putExtra("islocal", true);
                    startActivityForResult(intent, 0x12);
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.iv_pic_edit_item_photo_check) {
                        PicFile<FileInfo0> file = picFiles.get(position);
                    if (file.isSelect) {
                        file.isSelect=false;
                    }else{
                        file.isSelect=true;
                    }
                    adapter.notifyItemChanged(position);
                }
            }
        });
//       adapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
//           @Override
//           public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
//               PicFile<FileLocal> file = picFiles.get(position);
//               if(file.isHeader){
//                   return 4;
//               }
//               return 1;
//           }
//       });
        onNewThreadRequest();
    }

    TextView right;


    private SyncTask syncTask;
    private FilelistEntity filelistEntity;

    private void onNewThreadRequest() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                picFiles.clear();
                filelistEntity = UILApplication.getFilelistEntity();
                if (syncTask == null)
                    syncTask = new SyncTask(PicBackActivity.this, FTYPE.PICTURE);
                /*
                //未备份文件 ==  backedlist . removeAll(localist);
                List<FileInfo> cloudUnits = syncTask.getCloudUnits(0, 10000);
                syncTask.analyPhotoUnits(cloudUnits, filelistEntity);*/
                /***
                 * 不需要 再次跟云端文件列表比较
                 */
                List<FileInfo0> localUnits = filelistEntity.getLocallist();
                if (localUnits != null && !localUnits.isEmpty()) {
                    picFiles.clear();
                    PicFile<FileInfo0> heads=new PicFile<>(true,"");
                    int time= TimeUtils.getZeroTime(localUnits.get(0).getLastModified());
                    int index=0;
                    picFiles.add(heads);
                   // picFiles.add(new PicFile<FileInfo0>(localUnits.get(0)));
                    /**
                     *
                     * 每次都 上次的 位置开始找
                     *
                     */
                    int offset=0;
                    for (int i = offset; i < localUnits.size(); i++) {
                        FileInfo0 fileInfo =localUnits.get(i);
                        if (fileInfo.getObjid().equalsIgnoreCase("magazine-unlock-05-2.3.921-_768aa36f7f554c2891784c6ed7a84cd4.jpg"))
                            Log.i("+++++++++", "GetUnbakSubitem: ");

                        if (!syncTask.isBacked(fileInfo))
                        {
                            if (Math.abs(fileInfo.lastModified-time) <= ( 3 * 24 * 3600 )) {
                                picFiles.add(new PicFile<FileInfo0>(fileInfo));
                                {
                                    PicFile<FileInfo0> fileLocalPicFile = picFiles.get(index);
                                    String start = TimeUtils.getDay(time);
                                    String end = TimeUtils.getDay(fileInfo.getLastModified());
                                    if (start.equals(end)) {
                                        fileLocalPicFile.header = start;
                                    } else {
                                        fileLocalPicFile.header = end + "至" + start;
                                    }
                                    picFiles.set(index, fileLocalPicFile);
                                }
                            } else {
                                PicFile<FileInfo0> fileLocalPicFile = picFiles.get(index);
                                String start = TimeUtils.getDay(time);
                                String end = TimeUtils.getDay(localUnits.get(i-1).getLastModified());
                                if (start.equals(end)) {
                                    fileLocalPicFile.header=start;
                                } else {
                                    fileLocalPicFile.header=end + "至" + start;
                                }
                                picFiles.set(index,fileLocalPicFile);
                                time = TimeUtils.getZeroTime(fileInfo.getLastModified());
                                heads=new PicFile<>(true,"");
                                index=picFiles.size();
                                picFiles.add(heads);
                                picFiles.add(new PicFile<FileInfo0>(fileInfo));
                            }
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });
        thread.start();
    }

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
                boolean isSelect="取消全选".equals(right.getText().toString());
                adapter.setShowEdit(true);
                for (PicFile<FileInfo0> file:
                        picFiles) {
                    file.isSelect=isSelect;
                }
                adapter.notifyDataSetChanged();
                if (!isSelect) {
                    right.setText("全选");
                } else {
                    right.setText("取消全选");
                }
                break;
            case R.id.tv_pic_upload:
                //上传
                final ArrayList<FileInfo0> fileLocals=new ArrayList<>();
                for (PicFile<FileInfo0> f:
                     picFiles) {
                    if(f.isSelect){
                        fileLocals.add(f.t);
                    }
                }
                if (fileLocals.isEmpty()) {
                    ToastUtils.toast(this, "请选择上传文件");
                    return;
                }
                FileUploadManager manager = FileUploadManager.getInstance();
                UploadOptions options = new UploadOptions(true, true);
                final MaterialDialog.Builder builder = new MaterialDialog.Builder(PicBackActivity.this);
                builder.content("第1个文件正在上传");
                builder.cancelable(false);
                builder.progress(true, 100);
                final MaterialDialog build = builder.build();
                build.show();
                count = 0;
                for (int i = 0; i < fileLocals.size(); i++) {
                    FileInfo0 f = fileLocals.get(i);
                    f.setFtype(FTYPE.PICTURE);
                    manager.uploadFile(new ProgressBarAware(build,i+1),null,f, new OnUploadListener() {
                        @Override
                        public void onError(FileUploadInfo uploadData, int errorType, String msg) {
                            ToastUtils.toast(PicBackActivity.this, "上传失败");
                            build.dismiss();
                        }

                        @Override
                        public void onSuccess(FileUploadInfo uploadData, Object data) {
                            count++;
                            if (count == fileLocals.size()) {
                                build.dismiss();
                                ToastUtils.toast(PicBackActivity.this, "上传成功");
                                setResult(RESULT_OK);
                                onBackPressed();
                            }
                        }
                    }, options);
                }


                break;
        }
    }
}
