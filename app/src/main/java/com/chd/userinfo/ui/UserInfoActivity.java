package com.chd.userinfo.ui;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.TClient;
import com.chd.base.Ui.ActiveProcess;
import com.chd.contacts.vcard.StringUtils;
import com.chd.proto.Errcode;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.proto.RetHead;
import com.chd.proto.UserInfo;
import com.chd.service.SyncLocalFileBackground;
import com.chd.userinfo.ui.entity.UserInfoFlag;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.ui.LoginActivity;
import com.chd.yunpan.ui.dialog.IconSelectWindow;
import com.chd.yunpan.utils.PictureUtil;
import com.chd.yunpan.view.circleimage.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class UserInfoActivity extends ActiveProcess implements OnClickListener {
    private ImageView mIvLeft;
    private TextView mTvCenter;
    private View mViewHead, mViewName, mViewSex, mViewAge, mViewMobile, mViewPwd;
    private TextView mTextName, mTextSex, mTextAge, mTextMobile, mTextPwd;
    private CircleImageView mImgHead;
    private Button mBtnLogout;
    private UserInfo userInfo;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (userInfo != null) {
                        Log.d("userinfo", userInfo.toString());
                        mTextName.setText(StringUtils.isNullStr(userInfo.aliasname));
                        if (userInfo.isMale()) {
                            mTextSex.setText("男");
                        } else {
                            mTextSex.setText("女");
                        }
                        mTextAge.setText(userInfo.age + "");
                        mTextMobile.setText(StringUtils.isPhoneStr(userInfo.mobile));
                        mTextPwd.setText("******");
                    }
                    break;
                case 1:
                    Toast.makeText(UserInfoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(UserInfoActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private IconSelectWindow iconWindow;
    private View root_view;
    private File icon_file;
    public Runnable savePic = new Runnable() {
        @Override
        public void run() {
            FileInfo0 info0 = new FileInfo0();
            info0.setObjid("netdiskportrait");
            info0.setFilePath(icon_file.getPath());
            info0.setFtype(FTYPE.PICTURE);
            setParMessage("头像上传中");
            boolean b = new SyncLocalFileBackground(UserInfoActivity.this).uploadFileOvWrite(info0, UserInfoActivity.this, null,null);
            if (b) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(UserInfoActivity.this, "上传头像成功", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(UserInfoActivity.this, "上传头像失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    };

    //以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_userinfo);
        root_view = LayoutInflater.from(this).inflate(R.layout.activity_userinfo, null, false);

        initTitle();
        initResourceId();
        initListener();
        iconWindow = new IconSelectWindow(this);
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            userInfo = TClient.getinstance().QueryUserInfo();
                            if (userInfo == null) {
                                userInfo = new UserInfo();
                            }

                          /*  runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageLoader.displayImage("trpc://netdiskportrait", mImgHead);
                                }
                            });*/

//                            }
                            mHandler.sendEmptyMessage(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).start();
        initData();
    }


    private void initData() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true)
                .considerExifParams(true)
                .showImageOnLoading(R.drawable.pic_test1).showImageOnFail(R.drawable.pic_test1)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(false)
                .extraForDownloader(new ShareUtils(UserInfoActivity.this).getStorePathStr())
                .displayer(new FadeInBitmapDisplayer(0)).build();


        imageLoader.displayImage("trpc://netdiskportrait", mImgHead,options);
    }

    private void initListener() {
        mIvLeft.setOnClickListener(this);
        mViewHead.setOnClickListener(this);
        mViewName.setOnClickListener(this);
        mViewSex.setOnClickListener(this);
        mViewAge.setOnClickListener(this);
        //TODO 手机号屏蔽
//        mViewMobile.setOnClickListener(this);
        mViewPwd.setOnClickListener(this);
        mBtnLogout.setOnClickListener(this);
    }

    private void initResourceId() {
        mViewHead = findViewById(R.id.userinfo_head_layout);
        mViewName = findViewById(R.id.userinfo_name_layout);
        mViewSex = findViewById(R.id.userinfo_sex_layout);
        mViewAge = findViewById(R.id.userinfo_age_layout);
        mViewMobile = findViewById(R.id.userinfo_mobile_layout);
        mViewPwd = findViewById(R.id.userinfo_pwd_layout);

        mImgHead = (CircleImageView) findViewById(R.id.userinfo_head_img);
        mTextName = (TextView) findViewById(R.id.userinfo_name_txt);
        mTextSex = (TextView) findViewById(R.id.userinfo_sex_txt);
        mTextAge = (TextView) findViewById(R.id.userinfo_age_txt);
        mTextMobile = (TextView) findViewById(R.id.userinfo_mobile_txt);
        mTextPwd = (TextView) findViewById(R.id.userinfo_pwd_txt);

        mBtnLogout = (Button) findViewById(R.id.userinfo_logout);
    }

    private void initTitle() {
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mTvCenter = (TextView) findViewById(R.id.tv_center);

        mTvCenter.setText("个人中心");
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, UserInfoEditActivity.class);
        int edit_type = -1;
        String edit_value = "";
        switch (v.getId()) {
            case R.id.iv_left:
                onBackPressed();
                break;
            case R.id.userinfo_head_layout: {
                iconWindow.showPopupWindow(root_view);
            }
            break;
            case R.id.userinfo_name_layout: {
                edit_type = UserInfoFlag.FLAG_EDIT_TYPE_NAME;
                edit_value = mTextName.getText().toString();
            }
            break;
            case R.id.userinfo_sex_layout: {
                edit_type = UserInfoFlag.FLAG_EDIT_TYPE_SEX;
                edit_value = mTextSex.getText().toString();
            }
            break;
            case R.id.userinfo_age_layout: {
                edit_type = UserInfoFlag.FLAG_EDIT_TYPE_AGE;
                edit_value = mTextAge.getText().toString();
            }
            break;
            case R.id.userinfo_mobile_layout: {
                edit_type = UserInfoFlag.FLAG_EDIT_TYPE_MOBILE;
                edit_value = mTextMobile.getText().toString();
            }
            break;
            case R.id.userinfo_pwd_layout: {
                edit_type = UserInfoFlag.FLAG_EDIT_TYPE_PWD;
                edit_value = mTextPwd.getText().toString();
            }
            break;
            case R.id.userinfo_logout: {
                ShareUtils shareUtils = new ShareUtils(UserInfoActivity.this);
                shareUtils.setLoginEntity(null);
                shareUtils.setURL("");
                shareUtils.setAutoLogin(false);
                shareUtils.setPwd("");
                Intent startIntent = new Intent(UserInfoActivity.this, LoginActivity.class);
                startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startIntent);
                finish();
            }
            return;
            default:
                break;
        }
        if (edit_type != -1) {
            intent.putExtra(UserInfoFlag.FLAG_EDIT_TYPE, edit_type);
            intent.putExtra(UserInfoFlag.FLAG_EDIT_VALUE, edit_value);
            startActivityForResult(intent, 1000);
        }
    }

    public void saveUser(final UserInfo info) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean b = TClient.getinstance().SetUserInfo(info);
                    Log.d("lmj-保存", b + "");
                    if (b) {
                        mHandler.sendEmptyMessage(1);
                    } else {
                        mHandler.sendEmptyMessage(2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(2);
                }
            }
        })

                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        Uri data2 = null;
        if (data == null) {
            data2 = PictureUtil.getImageUri();
        } else {
            data2 = data.getData();
        }
        if (data != null && requestCode == 1000) {
            int edit_type = data.getIntExtra(UserInfoFlag.FLAG_EDIT_TYPE, -1);
            String edit_value = data.getStringExtra(UserInfoFlag.FLAG_EDIT_VALUE);
            if (edit_value == null) {
                edit_value = "";
            }
            switch (edit_type) {
                case UserInfoFlag.FLAG_EDIT_TYPE_NAME: {
                    userInfo.setAliasname(edit_value);
                    mTextName.setText(edit_value);
                    saveUser(userInfo);
                }
                break;
                case UserInfoFlag.FLAG_EDIT_TYPE_SEX: {
                    if ("男".equals(edit_value)) {
                        userInfo.setMale(true);
                    } else {
                        userInfo.setMale(false);
                    }
                    mTextSex.setText(edit_value);
                    saveUser(userInfo);
                }
                break;
                case UserInfoFlag.FLAG_EDIT_TYPE_AGE: {
                    int age = Integer.valueOf(edit_value);
                    userInfo.setAge(age);
                    mTextAge.setText(edit_value);
                    saveUser(userInfo);

                }
                break;
                case UserInfoFlag.FLAG_EDIT_TYPE_MOBILE: {
                    userInfo.setMobile(edit_value);
                    mTextMobile.setText(edit_value);
                    saveUser(userInfo);
                }
                break;
                case UserInfoFlag.FLAG_EDIT_TYPE_PWD: {
//                    mTextPwd.setText(edit_value);
                    mTextPwd.setText("******");
                    final String oldPass = data.getStringExtra("oldPass");

                    final String finalEdit_value = edit_value;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final RetHead retHead = TClient.getinstance().ChangePwd(oldPass, finalEdit_value);
                                if (Errcode.SUCCESS == retHead.getRet()) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ShareUtils shareUtils = new ShareUtils(UserInfoActivity.this);
                                            shareUtils.setPwd(finalEdit_value);
                                            Toast.makeText(UserInfoActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String msg = retHead.getMsg();
                                            Toast.makeText(UserInfoActivity.this, msg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                            }
                        }
                    }).start();
                }
                break;
                default:
                    break;
            }

        }

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureUtil.PHOTO_PICKED_WITH_DATA:
                    intent.setDataAndType(data2, "image/*");
                    intent.putExtra("crop", true);
                    // 设置裁剪尺寸
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 160);
                    intent.putExtra("outputY", 130);
                    intent.putExtra("return-data", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            PictureUtil.getImageCaiUri());
                    startActivityForResult(intent, PictureUtil.PHOTO_CROP);
                    break;
                case PictureUtil.CAMERA_WITH_DATA:

                    Uri ur = Uri.fromFile(PictureUtil.getmCurrentPhotoFile());
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        String url = getPath(this, ur);
                        intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
                    } else {
                        intent.setDataAndType(ur, "image/*");
                    }
                    intent.putExtra("crop", true);
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 160);
                    intent.putExtra("outputY", 130);
                    intent.putExtra("return-data", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, ur);
                    startActivityForResult(intent, PictureUtil.PHOTO_CROP);
                    break;
                case PictureUtil.PHOTO_CROP:
                    String fileName = PictureUtil.getCharacterAndNumber();
                    if (data.getData() != null) {
                        Bitmap pho = BitmapFactory.decodeFile(data.getData().getPath());
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        pho.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                        File file = new File(PictureUtil.PHOTO_DIR, fileName + ".png");
                        PictureUtil.saveMyBitmap(pho, file);
                        icon_file = file;
                        mImgHead.setImageBitmap(pho);
                        iconWindow.dismiss();
                    } else {
                        Bundle bundle = data.getExtras();
                        Bitmap myBitmap = (Bitmap) bundle.get("data");
                        Bitmap bitImage = PictureUtil.comp(myBitmap);
                        File file = new File(PictureUtil.PHOTO_DIR, fileName + ".png");
                        PictureUtil.saveMyBitmap(bitImage, file);
                        icon_file = file;
                        mImgHead.setImageBitmap(bitImage);
                        iconWindow.dismiss();
                    }
                    if (icon_file == null) {
                        Toast.makeText(UserInfoActivity.this, "头像不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        new Thread(savePic).start();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
