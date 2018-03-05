package com.chd.photo.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.Entity.PicFile;
import com.chd.base.UILActivity;
import com.chd.base.backend.SyncTask;
import com.chd.listener.DataCallBack;
import com.chd.other.ui.OtherActivity;
import com.chd.photo.adapter.PicInfoAdapter2;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
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
import com.chd.yunpan.view.ActionSheetDialog;
import com.chd.yunpan.view.SuperRefreshRecyclerView;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.PictureConfig;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public class PicActivity extends UILActivity implements OnClickListener {

    private static final int REQUEST_CODE_SETTING = 300;
    private static final int REQUEST_CODE_CAMERA = 1;
    private ImageView mIvLeft;
    private RelativeLayout rlBottom;
    private TextView mTvCenter;
    private TextView mTvRight;
    private TextView mTvNumber;
    private SuperRefreshRecyclerView mLvPic;
    private boolean bIsUbkList;
    private ImageLoader imageLoader;
    private SyncTask syncTask;
    private FilelistEntity filelistEntity;
    private PicInfoAdapter2<FileInfo> adapter;
    private ArrayList<PicFile<FileInfo>> cloudList = new ArrayList<>();
    private List<FileInfo> cloudUnits /*= new ArrayList<>()*/;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            dismissDialog();
            int size = 0;
            size = filelistEntity.getUnbak_idx_lst().size();
            mTvNumber.setText(String.format("上传未备份照片（%d）", size));
            adapter.notifyDataSetChanged();
            rlBottom.setVisibility(View.GONE);
            mTvRight.setText("编辑");
        }

    };
    private TextView tvUpload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncTask = new SyncTask(PicActivity.this, FTYPE.PICTURE);
        filelistEntity = UILApplication.getFilelistEntity();
        setContentView(R.layout.activity_pic);
        bIsUbkList = false;
        UILApplication.ClearFileEntity();
        imageLoader = ImageLoader.getInstance();
        initTitle();
        initResourceId();
        initListener();
        onNewThreadRequest();
    }


    private void onNewThreadRequest0(final boolean bIsUbkList) {
        showWaitDialog();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                filelistEntity = UILApplication.getFilelistEntity();
                if (syncTask == null)
                    syncTask = new SyncTask(PicActivity.this, FTYPE.PICTURE);
                //未备份文件 ==  backedlist . removeAll(localist);
                if (cloudUnits == null || cloudUnits.isEmpty()) {
                    // 0-100 分批取文件
                    cloudUnits = syncTask.getCloudUnits(0, 10000);
                    if (cloudUnits == null) {
                        System.out.print("query cloudUnits remote failed");
                        return;
                    }
                    syncTask.analyPhotoUnits(cloudUnits, filelistEntity);

                    List<FileInfo0> localUnits = filelistEntity.getLocallist();

                    if (cloudUnits != null && !cloudUnits.isEmpty()) {
                        Collections.sort(cloudUnits, new Comparator<FileInfo>() {
                            @Override
                            public int compare(FileInfo fileLocal, FileInfo t1) {
                                int lastModified = fileLocal.getLastModified();
                                int lastModified1 = t1.getLastModified();
                                if(lastModified<lastModified1){
                                    return 1;
                                }else if(lastModified>lastModified1){
                                    return -1;
                                }
                                return 0;
                            }
                        });
                        PicFile<FileInfo> heads = new PicFile<>(true, "");
                        int time = TimeUtils.getZeroTime(cloudUnits.get(0).getLastModified());
                        int index = 0;
                        cloudList.add(heads);
                        cloudList.add(new PicFile<FileInfo>(cloudUnits.get(0)));
                        for (int i = 1; i < cloudUnits.size(); i++) {
                            FileInfo fileInfo = cloudUnits.get(i);
                            if (Math.abs(fileInfo.lastModified - time) <= (3 * 24 * 3600)) {
                                cloudList.add(new PicFile<FileInfo>(fileInfo));
                                if(i==cloudUnits.size()-1){
                                    PicFile<FileInfo> fileLocalPicFile = cloudList.get(index);
                                    String start = TimeUtils.getDay(time);
                                    String end = TimeUtils.getDay(fileInfo.getLastModified());
                                    if (start.equals(end)) {
                                        fileLocalPicFile.header = start;
                                    } else {
                                        fileLocalPicFile.header = end + "至" + start;
                                    }
                                    cloudList.set(index, fileLocalPicFile);
                                }
                            } else {
                                PicFile<FileInfo> fileLocalPicFile = cloudList.get(index);
                                String start = TimeUtils.getDay(time);
                                String end = TimeUtils.getDay(localUnits.get(i - 1).getLastModified());
                                if (start.equals(end)) {
                                    fileLocalPicFile.header = start;
                                } else {
                                    fileLocalPicFile.header = end + "至" + start;
                                }
                                cloudList.set(index, fileLocalPicFile);
                                time = TimeUtils.getZeroTime(fileInfo.getLastModified());
                                heads = new PicFile<>(true, "");
                                index = cloudList.size();
                                cloudList.add(heads);
                                cloudList.add(new PicFile<FileInfo>(fileInfo));
                            }
                        }
                    }
                }
                if (bIsUbkList) {
                    initLocal();
                } /*else {
                    initData();
                }*/
            }
        });
        thread.start();
    }

    private void onNewThreadRequest() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                 cloudUnits = syncTask.getCloudUnits(0, 10000);
                filelistEntity.setBklist(cloudUnits);
                initData(cloudUnits);
            }
        }).start();
    }
    /*private void initData() {
        //要有提示用户等待的画面
        handler.sendEmptyMessage(0);
    }*/


    private void initData(final List<FileInfo> cloudUnits) {

        if (cloudUnits == null) {
            System.out.print("query remote failed");
        }


        // 找到10个以后 先返回, 剩下的 在线程里面继续找
        syncTask.dbManager.GetLocalFiles0(new String[]{"jpg", "png", "gif"}, true, filelistEntity, new DataCallBack(10) {
            @Override
            /*
            * @count 当前list的最后下标
            * */
            public void success(List<FileInfo0> datas, int offset, int count) {
                //接收到的数据
                //syncTask.dbManager.anlayLocalUnits(cloudUnits, filelistEntity, offset, count);
                List<PicFile> list=new ArrayList<>();
                int unbak=GetUnbakSubitem(offset,count,list);
                refreshData(unbak);
            }
        }
        );

        //按时间重新排序
        /*Collections.sort(cloudUnits, new Comparator<FileInfo>() {
            @Override
            public int compare(FileInfo fileLocal, FileInfo t1) {
                int lastModified = fileLocal.getLastModified();
                int lastModified1 = t1.getLastModified();
                if(lastModified<lastModified1){
                    return 1;
                }else if(lastModified>lastModified1){
                    return -1;
                }
                return 0;
            }
        });*/

        Log.i("ddddddddd", "initData: ");
    }


    /**
     *
     * @param lastoffset 向list 添加元素的起始位置
     * @param Exceptnumber 希望添加元素的个数
     * @param list 容器对象,应该初始化为线程安全对象
     * @return 实际添加元素的个数
     */
    public int  GetUnbakSubitem(int lastoffset, int Exceptnumber ,List<PicFile> list) {
        int count = 0;
        if (list == null)
            return count;
        FileInfo0 item;
        //int min=0;
        int idx = lastoffset;
        List<FileInfo0> locallst=filelistEntity.getLocallist();
        int len=locallst.size();
        for (;idx<len;idx++)
        {
            item =locallst.get(idx);
            if (syncTask.isBacked(item))
            {
                count++;
                PicFile<FileInfo0> f = new PicFile<FileInfo0>(item);
                list.add(f);
            }
        }

        /*
        * sort array by time if you would
        * */
        /*Collections.sort(list, (Comparator<? super PicFile>) new Comparator<PicFile<FileInfo0>>() {
            @Override
             public int compare(PicFile<FileInfo0> t0, PicFile<FileInfo0> t1) {

                        int lastModified = t0.t.lastModified;
                        int lastModified1 = t1.t.lastModified;
                        if (lastModified < lastModified1) {
                            return 1;
                        } else if (lastModified > lastModified1) {
                            return -1;
                        }
                        return 0;
                    }
                }
        );*/
        return  count;
    }


    private void refreshData(final int count) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvNumber.setText("未备份文件" + Integer.valueOf(mTvNumber.getText().toString())+count + "个");
            }
        });
        handler.sendEmptyMessage(0);
    }

    private void initResourceId() {
        mTvNumber = (TextView) findViewById(R.id.tv_pic_number);
        mLvPic = (SuperRefreshRecyclerView) findViewById(R.id.lv_pic);
        rlBottom = (RelativeLayout) findViewById(R.id.rl_pic_bottom);
        tvUpload = (TextView) findViewById(R.id.tv_pic_upload);
        mTvNumber.setText("未备份照片0张");
        mTvRight.setVisibility(View.VISIBLE);
        mTvRight.setText("编辑");
        adapter = new PicInfoAdapter2<>(cloudList, false);
        mLvPic.setAdapter(adapter);
        mLvPic.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                PicFile<FileInfo> file = cloudList.get(position);
                if (file.isHeader) {
                    return 4;
                }
                return 1;
            }
        });
        mLvPic.setLayoutManager(manager);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //非视频，即图片进去
                PicFile<FileInfo> file = cloudList.get(position);
                Intent intent = new Intent(mAct, PicDetailActivity.class);
                intent.putExtra("bean", file.t);
                intent.putExtra("pos", position);
                intent.putExtra("ubklist", false);
                startActivityForResult(intent, 0x12);
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.iv_pic_edit_item_photo_check) {
                    PicFile<FileInfo> file = cloudList.get(position);
                    if (file.isSelect) {
                        file.isSelect = false;
                    } else {
                        file.isSelect = true;
                    }
                    adapter.notifyItemChanged(position);
                }
            }
        });
    }

    private void initListener() {
        mIvLeft.setOnClickListener(this);
        mTvRight.setOnClickListener(this);
        mTvNumber.setOnClickListener(this);
        mLvPic.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true));
    }

    private void initTitle() {
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mTvCenter = (TextView) findViewById(R.id.tv_center);
        mTvRight = (TextView) findViewById(R.id.tv_right);
        mTvCenter.setText("照片");
//		mTvRight.setText("编辑");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                onBackPressed();
                break;
            case R.id.tv_right: // 编辑
                tvUpload.setText("下载");
                if ("编辑".equals(mTvRight.getText())) {
                    rlBottom.setVisibility(View.VISIBLE);
                    adapter.setShowEdit(true);
                    adapter.notifyDataSetChanged();
                    mTvRight.setText("取消");
                } else {
                    rlBottom.setVisibility(View.GONE);
                    mTvRight.setText("编辑");
                    adapter.setShowEdit(false);
                    adapter.notifyDataSetChanged();
                }
//                }
                break;
            case R.id.tv_pic_number:
                initLocal();
                break;
        }
    }

    private void initLocal() {
        Intent intent = new Intent(this, PicBackActivity.class);
        startActivityForResult(intent, 0x123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case 0x11:
                    onNewThreadRequest();
                    break;
                case FunctionConfig.CAMERA_RESULT:
                    if (data != null) {
                        List<LocalMedia> selectMedia = (List<LocalMedia>) data.getSerializableExtra(FunctionConfig.EXTRA_RESULT);
                        uploadCamera(selectMedia.get(0));
                    }
                    break;
                case 0x12:
                    //删除成功
                    if (data != null) {
                        int pos1 = data.getIntExtra("pos", -1);
                        adapter.notifyItemRemoved(pos1);
                        setResult(RESULT_OK);
                    }
                    break;
                case 0x123:
                    //刷新
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cloudUnits.clear();
                            cloudList.clear();
                            onNewThreadRequest();
                        }
                    }, 500);
                    break;
            }
        }

    }


    public static final int REQUEST_CODE_PERMISSION_CAMERA = 100;

    public void addPic(View v) {
        //添加图片
        //从本地添加，视频拍照
        new ActionSheetDialog(this).builder().addSheetItem("现拍照片", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                //视频拍照
                AndPermission.with(PicActivity.this)
                        .requestCode(REQUEST_CODE_PERMISSION_CAMERA)
                        .permission(Manifest.permission.CAMERA)
                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
                        .rationale(new RationaleListener() {
                            @Override
                            public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                            }
                        })
                        .callback(listener)
                        .start();
            }
        }).addSheetItem("从本地添加", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                showMultiChoose();
            }
        }).setCanceledOnTouchOutside(true).setCancelable(true).show();
    }

    public void showMultiChoose() { // 多选图片，带预览功能
        PictureConfig.getInstance().openPhoto(this, new PictureConfig.OnSelectResultCallback() {
            @Override
            public void onSelectSuccess(List<LocalMedia> list) {
                // 多选回调
                FileUploadManager manager = FileUploadManager.getInstance();
                final MaterialDialog.Builder builder = new MaterialDialog.Builder(PicActivity.this);
                builder.progress(false, 100);
                final MaterialDialog build = builder.build();
                ArrayList<String> images = new ArrayList<String>();
                for (LocalMedia media :
                        list) {
                    String path = "";
                    if (media.isCut() && !media.isCompressed()) {
                        // 裁剪过
                        path = media.getCutPath();
                    } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                        // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                        path = media.getCompressPath();
                    } else {
                        // 原图地址
                        path = media.getPath();
                    }
                    images.add(path);
                }

                for (int i = 0; i < images.size(); i++) {
                    try {
                        File tmpFile = new File(images.get(i));
                        FileLocal fileLocal = new FileLocal();
                        int pathid = UILApplication.getFilelistEntity().addFilePath(tmpFile.getParent());
                        fileLocal.setPathid(pathid);
                        fileLocal.setFtype(FTYPE.PICTURE);
                        fileLocal.setObjid(tmpFile.getName());
                        boolean overwrite = true;
                        boolean resume = true;
                        build.setContent((i + 1) + "/" + images.size() + "正在上传:0%");
                        build.show();
                        UploadOptions options = new UploadOptions(overwrite, resume);

                        manager.uploadFile(new ProgressBarAware(build), null, fileLocal, new OnUploadListener() {

                            @Override
                            public void onError(FileUploadInfo uploadData, int errorType, String msg) {
                                build.dismiss();
                                ToastUtils.toast(PicActivity.this, "上传失败");
                            }

                            @Override
                            public void onSuccess(FileUploadInfo uploadData, Object data) {
                                build.dismiss();
                                ToastUtils.toast(PicActivity.this, "上传成功");
                                onNewThreadRequest();
                            }
                        }, options);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onSelectSuccess(LocalMedia media) {
                // 单选回调
                //一张图片上传
                uploadCamera(media);
            }
        });
    }

    private void uploadCamera(LocalMedia media) {
        FileUploadManager manager = FileUploadManager.getInstance();
        String path = "";
        if (media.isCut() && !media.isCompressed()) {
            // 裁剪过
            path = media.getCutPath();
        } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
            // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
            path = media.getCompressPath();
        } else {
            // 原图地址
            path = media.getPath();
        }
        try {
            File tmpFile = new File(path);
            FileLocal fileLocal = new FileLocal();
            int pathid = UILApplication.getFilelistEntity().addFilePath(tmpFile.getParent());
            fileLocal.setPathid(pathid);
            fileLocal.setFtype(FTYPE.PICTURE);
            fileLocal.setObjid(tmpFile.getName());
            boolean overwrite = true;
            boolean resume = true;
            UploadOptions options = new UploadOptions(overwrite, resume);
            final MaterialDialog.Builder builder = new MaterialDialog.Builder(PicActivity.this);
            builder.content("正在上传:0%");
            builder.progress(false, 100);
            final MaterialDialog build = builder.build();
            build.show();
            manager.uploadFile(new ProgressBarAware(build), null, fileLocal, new OnUploadListener() {
                @Override
                public void onError(FileUploadInfo uploadData, int errorType, String msg) {
                    build.dismiss();
                    ToastUtils.toast(PicActivity.this, "上传失败");
                }

                @Override
                public void onSuccess(FileUploadInfo uploadData, Object data) {
                    build.dismiss();
                    ToastUtils.toast(PicActivity.this, "上传成功");
                    onNewThreadRequest();
                }
            }, options);
        } catch (Exception e) {

        }

    }


    public void uploadPic(View v) {
        //上传图片

        uploadPic();

    }

    public void uploadPic() {
        //多张上传下载
        ArrayList<FileInfo> info0s = new ArrayList<>();
        for (PicFile<FileInfo> s : cloudList) {
            if (s.isSelect) {
                if (bIsUbkList) {
                    FileLocal local = (FileLocal) s.t;
                    FileInfo0 f = new FileInfo0(local);
                    String s1 = "file://" + UILApplication.getFilelistEntity().getFilePath(local.getPathid()) + "/" + f.getObjid();
                    f.setFilePath(s1);
                    f.setFtype(FTYPE.PICTURE);
                    info0s.add(f);
                } else {
                    FileInfo0 f = new FileInfo0(s.t);
                    f.setFtype(FTYPE.PICTURE);
                    info0s.add(f);
                }
            }
        }
        if (info0s.isEmpty()) {
            ToastUtils.toast(this, "暂无选中图片");
            return;
        }
        if (bIsUbkList) {
            syncTask.uploadList(info0s, PicActivity.this, handler);
        } else {
            syncTask.downloadList(info0s, PicActivity.this, handler);
        }

    }

    public void deletePic(View v) {
        //删除图片

        delete();
    }

    private void delete() {
        //多选删除
        ArrayList<FileInfo> info0s = new ArrayList<>();
        for (PicFile<FileInfo> s :
                cloudList) {
            if (s.isSelect) {
                info0s.add(s.t);
            }
        }
        if (info0s.isEmpty()) {
            toastMain("暂无选中图片");
            return;
        }
        syncTask.delList(info0s, PicActivity.this, handler, bIsUbkList);
    }


    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            if (requestCode == 100) {
                // TODO 相应代码。录制视频
                PictureConfig.getInstance().startOpenCamera(PicActivity.this);
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(PicActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(PicActivity.this, REQUEST_CODE_SETTING).show();

                // 第二种：用自定义的提示语。
                // AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING)
                // .setTitle("权限申请失败")
                // .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                // .setPositiveButton("好，去设置")
                // .show();

                // 第三种：自定义dialog样式。
                // SettingService settingService =
                //    AndPermission.defineSettingDialog(this, REQUEST_CODE_SETTING);
                // 你的dialog点击了确定调用：
                // settingService.execute();
                // 你的dialog点击了取消调用：
                // settingService.cancel();
            }
        }
    };


    @Override
    public void onStop() {
        super.onStop();
    }


}
