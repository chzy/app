package com.chd.video;

import android.Manifest;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chd.TClient;
import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.UILActivity;
import com.chd.base.backend.SyncTask;
import com.chd.contacts.vcard.StringUtils;
import com.chd.photo.adapter.PicAdapter;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
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
import com.gturedi.views.StatefulLayout;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.PictureConfig;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * User: Liumj(liumengjie@365tang.cn)
 * Date: 2017-03-06
 * Time: 10:11
 * describe:
 */
public class VideoListActivity extends UILActivity {


    private static final int RECORD_VIDEO = 1000;
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_center)
    TextView tvCenter;
    @BindView(R.id.rv_video_list_content)
    RecyclerView rvVideoListContent;
    @BindView(R.id.sl_video_list_layout)
    StatefulLayout slVideoListLayout;


    private PicAdapter picAdapter = null;

    private ImageLoader imageLoader;
    private List<FileInfo> cloudUnits = new ArrayList<>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    waitDialog.dismiss();
                    boolean showSelect = picAdapter.isShowSelect();
                    picAdapter = new PicAdapter(VideoListActivity.this, cloudList, imageLoader, true,showSelect);
                    rvVideoListContent.setAdapter(picAdapter);
                    break;
            }
        }
    };
    private List<List<FileInfo>> cloudList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        ButterKnife.bind(this);
        imageLoader = ImageLoader.getInstance();
        tvCenter.setText("视频");
        File f = new File(
                getCacheDir() + "/video");
        if (!f.exists()) {
            f.mkdir();
        }
        rvVideoListContent.setLayoutManager(new LinearLayoutManager(this));
        referData();
    }

    private void referData() {
        waitDialog.show();
        filelistEntity = UILApplication.getFilelistEntity();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 0-100 分批取文件
                cloudList.clear();
                SyncTask syncTask = new SyncTask(VideoListActivity.this, FTYPE.VIDEO);
                cloudUnits = syncTask.getCloudUnits(0, 10000);
                if (cloudUnits == null) {
                    System.out.print("query cloudUnits remote failed");
                    return;
                }
                syncTask.analyVideoUnits(cloudUnits, filelistEntity);
                if (cloudUnits != null && !cloudUnits.isEmpty()) {
                    List<FileInfo> local = new ArrayList<>();
                    int time = TimeUtils.getZeroTime(cloudUnits.get(0).getLastModified());
                    local.add(cloudUnits.get(0));
                    for (int i = 1; i < cloudUnits.size(); i++) {
                        FileInfo fileInfo = cloudUnits.get(i);
                        if (fileInfo.lastModified <= ((cloudList.size() + 1) * 3 * 24 * 3600 + time)) {
                            local.add(fileInfo);
                        } else {
                            cloudList.add(local);
                            local = new ArrayList<>();
                            local.add(fileInfo);
                            time = TimeUtils.getZeroTime(fileInfo.getLastModified());
                        }
                    }
                    cloudList.add(local);
                    local = null;
                }

                mHandler.sendEmptyMessage(0);

            }
        }).start();
    }


    private FilelistEntity filelistEntity;
    private static final int REQUEST_CODE_SETTING = 300;
    private static final int REQUEST_CODE_PERMISSION_VIDEO = 100;

    public void addVideo(View v) {
        //从本地添加，视频拍照
        new ActionSheetDialog(this).builder().addSheetItem("现拍视频", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                //视频拍照
                AndPermission.with(VideoListActivity.this)
                        .requestCode(REQUEST_CODE_PERMISSION_VIDEO)
                        .permission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
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
                showVideoChoose();

            }
        }).setCanceledOnTouchOutside(true).setCancelable(true).show();
    }

    public void showVideoChoose() {
        PictureConfig.getInstance().options.setType(FunctionConfig.TYPE_VIDEO);
        PictureConfig.getInstance().options.setPreviewVideo(true); // 是否预览视频(播放) mode or 多选有效
        PictureConfig.getInstance().options.setSelectMode(FunctionConfig.MODE_SINGLE); // 单选 or 多选 FunctionConfig.MODE_SINGLE FunctionConfig.MODE_MULTIPLE

        PictureConfig.getInstance().openPhoto(this, new PictureConfig.OnSelectResultCallback() {
            @Override
            public void onSelectSuccess(List<LocalMedia> list) {

            }

            @Override
            public void onSelectSuccess(LocalMedia localMedia) {
                FileUploadManager manager = FileUploadManager.getInstance();
                //一张视频上传
                try {
                    final File tmpFile = new File(localMedia.getPath());
                    FileLocal fileLocal = new FileLocal();
                    int pathid = UILApplication.getFilelistEntity().addFilePath(tmpFile.getParent());
                    fileLocal.setPathid(pathid);
                    fileLocal.setFtype(FTYPE.VIDEO);
                    fileLocal.setObjid(tmpFile.getName());
                    boolean overwrite = true;
                    boolean resume = true;
                    UploadOptions options = new UploadOptions(overwrite, resume);
                    final MaterialDialog.Builder builder = new MaterialDialog.Builder(VideoListActivity.this);
                    builder.content("正在上传:0%");
                    builder.progress(false, 100);
                    final MaterialDialog build = builder.build();
                    build.show();
                    manager.uploadFile(new ProgressBarAware(build), null, fileLocal, new OnUploadListener() {
                        @Override
                        public void onError(FileUploadInfo uploadData, int errorType, String msg) {
                            build.dismiss();
                            ToastUtils.toast(VideoListActivity.this, "上传失败");
                        }

                        @Override
                        public void onSuccess(FileUploadInfo uploadData, Object data) {
                            build.dismiss();
                            ToastUtils.toast(VideoListActivity.this, "上传成功");
                            deleteDefaultFile(true, false,Uri.parse(tmpFile.getPath()));

                        }
                    }, options);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void deleteVideo(View v) {
        //删除视频

    }

    @OnClick({R.id.iv_left})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                //退出
                onBackPressed();
                break;
        }
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            if (requestCode == 100) {
                // TODO 相应代码。录制视频

                startrecord();
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(VideoListActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(VideoListActivity.this, REQUEST_CODE_SETTING).show();

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

    /**
     * 录制视频
     **/
    public void startrecord() {
        Intent mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);
        startActivityForResult(mIntent, RECORD_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case RECORD_VIDEO:
                // 录制视频完成
                MaterialDialog.Builder md = new MaterialDialog.Builder(VideoListActivity.this);
                final EditText editText = new EditText(VideoListActivity.this);

                editText.setText("新视频" + (cloudUnits.size() + 1));
                editText.setHint("输入文件名");
                md.title("编辑文件名上传视频")
                        .customView(editText, true)
                        .positiveText("上传")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                String s = editText.getText().toString();
                                if (StringUtils.isNullOrEmpty(s) || StringUtils.isNullOrEmpty(s.trim())) {
                                    ToastUtils.toast(VideoListActivity.this, "文件名不允许为空");
                                    return;
                                }
                                uploadVideoFile(editText.getText().toString(), data);
                            }
                        }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // 文件写完之后删除/sdcard/dcim/CAMERA/XXX.MP4
                        deleteDefaultFile(false, true, data.getData());
                    }
                }).show();
                break;
            case 12:
                //添加本地视频 上传

            case 0x13:
                //进入了详情,删除了视频
                referData();
                break;
        }
    }

    private void uploadVideoFile(final String name, final Intent data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AssetFileDescriptor videoAsset = getContentResolver()
                            .openAssetFileDescriptor(data.getData(), "r");
                    FileInputStream fis = videoAsset.createInputStream();
                    final HashMap<String, String> param = new HashMap<>();
                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
                            String format = sdf.format(new Date(duration));
                            param.put("duration", format);//视频时长
                        }
                        cursor.close();
                    }
                    final File tmpFile = new File(
                            getCacheDir() + "/video",
                            name + ".mp4");

                    FileOutputStream fos = new FileOutputStream(tmpFile);
                    final byte[] buf = new byte[1024];
                    int len;
                    while ((len = fis.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                    }
                    fis.close();
                    fos.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FileUploadManager manager = FileUploadManager.getInstance();
                            FileLocal fileLocal = new FileLocal();
                            int pathid = UILApplication.getFilelistEntity().addFilePath(tmpFile.getParent());
                            fileLocal.setPathid(pathid);
                            fileLocal.setFtype(FTYPE.VIDEO);
                            fileLocal.setObjid(tmpFile.getName());
                            objid = tmpFile.getName();
                            objid = "yunpan_thumb_" + objid.replace("mp4", "jpg");
                            param.put("thumbnail", objid);
                            boolean overwrite = true;
                            boolean resume = true;
                            UploadOptions options = new UploadOptions(overwrite, resume);
                            final MaterialDialog.Builder builder = new MaterialDialog.Builder(VideoListActivity.this);
                            builder.content("正在上传:0%");
                            builder.progress(false, 100);
                            builder.canceledOnTouchOutside(false);
                            final MaterialDialog build = builder.build();
                            build.show();
                            manager.uploadFile(new ProgressBarAware(build), param, fileLocal, new OnUploadListener() {
                                @Override
                                public void onError(FileUploadInfo uploadData, int errorType, String msg) {
                                    build.dismiss();
                                    ToastUtils.toast(VideoListActivity.this, "上传失败");
                                }

                                @Override
                                public void onSuccess(FileUploadInfo uploadData, Object data2) {
                                    build.dismiss();
                                    ToastUtils.toast(VideoListActivity.this, "上传成功");
                                    deleteDefaultFile(true, true, data.getData());
//
                                    // 文件写完之后删除/sdcard/dcim/CAMERA/XXX.MP4

                                }
                            }, options);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private String objid;
    private Bitmap bitmap = null;


    // 删除在/sdcard/dcim/Camera/默认生成的文件
    private void deleteDefaultFile(final boolean isUpload, final boolean isDel, Uri uri) {
        String fileName = null;
        if (uri != null) {
            // content
            Log.d("Scheme", uri.getScheme());
            if (uri.getScheme().equals("content")) {
                Cursor cursor = this.getContentResolver().query(uri, null,
                        null, null, null);
                if (cursor != null && cursor.moveToNext()) {
                    int columnIndex = cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    fileName = cursor.getString(columnIndex);
                    bitmap = ThumbnailUtils.createVideoThumbnail(fileName, MediaStore.Video.Thumbnails.MINI_KIND);

                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    final String finalFileName = fileName;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (isUpload) {
                                    ByteBuffer byteBuffer = ByteBuffer.wrap(baos.toByteArray());
                                    FileInfo fileInfo = new FileInfo();
                                    fileInfo.setObjid(objid);
                                    fileInfo.setFtype(FTYPE.THUMB);
                                    String s = TClient.getinstance().AllocObj(fileInfo, null);
                                    TClient.getinstance().AppendObj(objid, byteBuffer, FTYPE.PICTURE, 0L);
                                    TClient.getinstance().CommitObj(objid, FTYPE.PICTURE, null);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                // 删除文件
                                if (isDel) {
                                    File file = new File(finalFileName);
                                    if (file.exists()) {
                                        file.delete();
                                        Log.d("delete", "删除成功");
                                    }
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        referData();
                                    }
                                });
                            }
                        }
                    }).start();


                    //                    //获取缩略图id
                    //                    int id = cursor.getInt(cursor
                    //                            .getColumnIndex(MediaStore.Video.VideoColumns._ID));
                    //                    //获取缩略图
                    //                    bitmap = MediaStore.Video.Thumbnails.getThumbnail(
                    //                            getContentResolver(), id, MediaStore.Video.Thumbnails.MICRO_KIND,
                    //                            null);


                    //                    Log.d("fileName", fileName);
                    //					if (!fileName.startsWith("/mnt")) {
                    //						fileName = "/mnt" + fileName;
                    //					}
                    //					Log.d("fileName", fileName);
                }
            }
        }

    }
}
