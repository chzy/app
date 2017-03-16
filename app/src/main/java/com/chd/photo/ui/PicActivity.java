package com.chd.photo.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.UILActivity;
import com.chd.base.backend.SyncTask;
import com.chd.photo.adapter.PicAdapter;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.view.ActionSheetDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import cn.iterlog.xmimagepicker.PickerActivity;


public class PicActivity extends UILActivity implements OnClickListener {

	private static final int REQUEST_CODE_SETTING = 300;
	private static final int REQUEST_CODE_CAMERA = 1;
	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	private TextView mTvNumber;
	private RecyclerView mLvPic;
	private boolean bIsUbkList;
	private List<FileInfo> cloudUnits;  //云端文件
	private List<FileLocal> localUnits; //未备份的本地文件
	private ImageLoader imageLoader;
	private SyncTask syncTask;
	//private List<PicBean<PicInfoBeanMonth>> localList = new ArrayList();
	private FilelistEntity filelistEntity;

	private Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(Message msg) {

			if (bIsUbkList) {

				dismissDialog();
				dismissWaitDialog();
				//Collections.sort(localList);
				findViewById(R.id.rl_pic_ubk_layout).setVisibility(View.GONE);
				mTvCenter.setText("未备份照片");

				mLvPic.setAdapter(new PicAdapter(PicActivity.this, localUnits, bIsUbkList, imageLoader));
			} else {

				dismissWaitDialog();
				dismissDialog();
				//Collections.sort(mPicList);
				int size = 0;
				size = filelistEntity.getUnbakNumber();
				Log.d("liumj", "数量" + size);
				mTvNumber.setText(String.format("未备份照片%d张", size));
				mLvPic.setAdapter(new PicAdapter(PicActivity.this, cloudUnits, bIsUbkList, imageLoader));
			}
		}

	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pic);

		bIsUbkList = false;
		imageLoader = ImageLoader.getInstance();
		initTitle();
		initResourceId();
		initListener();
		onNewThreadRequest(false);
	}

	private void onNewThreadRequest(final boolean bIsUbkList) {

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

//					cloudUnits = filelistEntity.getBklist();
					localUnits = filelistEntity.getLocallist();

				}
				if (bIsUbkList) {
					initLocal();
				} else {
					initData();
				}
			}
		});
		thread.start();
	}

	/*void add2YearMap(FileInfo01 info) {

		int year = TimeUtils.getYearWithTimeMillis(info.getLastModified() * 1000L);
		int month = TimeUtils.getMonthWithTimeMillis(info.getLastModified() * 1000L);
		Map<Integer, List<PicInfoBean>> tmpMonthMap;

		if (YearMap.containsKey(year)) {
			tmpMonthMap = YearMap.get(year);
		} else {
			tmpMonthMap = new HashMap();
		}

		PicInfoBean picInfoBean = new PicInfoBean();


		if (info.getSysid() > 0) {
			picInfoBean.setSysid(info.getSysid());
			String uri = info.getUri();
			picInfoBean.setUrl(uri);
			if (uri == null *//*&& syncTask.haveLocalCopy(info)*//*)
				picInfoBean.setUrl("file://" + info.getFilePath());

		} else {
			picInfoBean.setUrl("ttrpc://" + info.getObjid());
		}
		picInfoBean.setTimeStamp(info.getLastModified());
		picInfoBean.setDay(TimeUtils.getTime(info.getLastModified() * 1000L, new SimpleDateFormat("MM月dd日")));

		if (tmpMonthMap.containsKey(month)) {
			tmpMonthMap.get(month).add(picInfoBean);
		} else {
			List<PicInfoBean> monthPicInfoBeans = new ArrayList<PicInfoBean>();
			monthPicInfoBeans.add(picInfoBean);
			tmpMonthMap.put(month, monthPicInfoBeans);
		}
		YearMap.put(year, tmpMonthMap);
	}*/


	private void initData() {

		if (filelistEntity != null) {
/*
			if (YearMap == null)
				YearMap = new HashMap<>();
			for (FileInfo info : cloudUnits) {
				add2YearMap(info);
			}
*/


			//mPicList = initData(mPicList);
			//YearMap.clear();
		}
		//要有提示用户等待的画面
		handler.sendEmptyMessage(0);
	}


	/*private List<PicBean<PicInfoBeanMonth>> initData(List<PicBean<PicInfoBeanMonth>> data) {
		if (!YearMap.isEmpty()) {
			data.clear();
			for (Map.Entry<Integer, Map<Integer, List<PicInfoBean>>> entryYear : YearMap.entrySet()) {

				int midx = 0;
				int year = entryYear.getKey();
				for (Map.Entry<Integer, List<PicInfoBean>> entryMonth :
						entryYear.getValue().entrySet()) {
					PicInfoBeanMonth<PicInfoBean> monthInfoBean = new PicInfoBeanMonth();
					midx = entryMonth.getKey();
					List<PicInfoBean> value = entryMonth.getValue();
					//Collections.sort(value);
					int index = value.size() - 1;
					monthInfoBean.setUrl(value.get(index).getUrl());//第一张图片
					monthInfoBean.setPicunits(value);
					if (!monthInfoBean.getPicunits().isEmpty()) {
						PicBean<PicInfoBeanMonth> picBean = new PicBean(String.valueOf(year), monthInfoBean);
						picBean.setMonth(midx);
						data.add(picBean);
					}
				}
			}
		}
		return data;

	}
*/
	private void initResourceId() {
		mTvNumber = (TextView) findViewById(R.id.tv_pic_number);
		mLvPic = (RecyclerView) findViewById(R.id.lv_pic);
		mTvNumber.setText("未备份照片0张");
		mLvPic.setLayoutManager(new LinearLayoutManager(this));
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
				finish();
				break;
			case R.id.tv_right: // 编辑
				break;
			case R.id.tv_pic_number:
				initLocal();
				break;
		}
	}

	private void initLocal() {
		waitDialog.show();
		new Thread() {
			@Override
			public void run() {
				List<FileLocal> locallist = filelistEntity.getLocallist();
				for (FileLocal fileLocal : locallist) {
					//if (fileLocal.getSysid()>0)
					//	continue;
//				/*	FileInfo0 info = syncTask.queryLocalInfo(fileLocal.getSysid());
					//filelistEntity.
//					if (info == null) {
//						Log.d("PicActity", fileLocal.getSysid() + " not found in mediaStore");
//						continue;
//					}
//					if (StringUtils.isNullOrEmpty(info.getFilename())) {
//						info.setFilename(fileLocal.getFilename());
//					}*/
					//add2YearMap(info);
				}
				//localList = initData(localList);
				//YearMap.clear();
				bIsUbkList = true;
				handler.sendEmptyMessage(0);
			}
		}.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
				case 0x11:
					Log.i("lmj", "返回执行了");
					onNewThreadRequest(bIsUbkList);
					break;
				case 1:
					Bundle bundle = data.getExtras();
					// 获取相机返回的数据，并转换为Bitmap图片格式，这是缩略图
					Bitmap bitmap = (Bitmap) bundle.get("data");
					Log.d("LMJ",saveImageToGallery(this,bitmap));

					break;
			}
		}
	}

	public  String saveImageToGallery(Context context, Bitmap bitmap) {
		File appDir = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath(), "DCIM");
		if (!appDir.exists()) {
			// 目录不存在 则创建
			appDir.mkdirs();
		}
		String fileName = System.currentTimeMillis() + ".jpg";
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // 保存bitmap至本地
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			ScannerByReceiver(context, file.getAbsolutePath());

			if (!bitmap.isRecycled()) {
				// bitmap.recycle(); 当存储大图片时，为避免出现OOM ，及时回收Bitmap
				System.gc(); // 通知系统回收
			}
			return file.getPath();
			// Toast.makeText(context, "图片保存成功" ,
			// Toast.LENGTH_SHORT).show();
		}
	}

	/** Receiver扫描更新图库图片 **/

	private static void ScannerByReceiver(Context context, String path) {
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.parse("file://" + path)));
	}

	public static final int REQUEST_CODE_PERMISSION_CAMERA = 100;

	public void addPic(View v){
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
						.send();
			}
		}).addSheetItem("从本地添加", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
					showMultiChoose();
			}
		}).setCanceledOnTouchOutside(true).setCancelable(true).show();
	}

	public void showMultiChoose() { // 多选图片，带预览功能
		PickerActivity.chooseMultiPicture(this, 12, 6);
	}

	public void deletePic(View v){
		//删除图片
	}



	private PermissionListener listener = new PermissionListener() {
		@Override
		public void onSucceed(int requestCode, List<String> grantedPermissions) {
			// 权限申请成功回调。
			if(requestCode == 100) {
				// TODO 相应代码。录制视频
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, REQUEST_CODE_CAMERA);
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
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		// 只需要调用这一句，其它的交给AndPermission吧，最后一个参数是PermissionListener。
		AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
	}



	@Override
	public void onStop() {
		super.onStop();
	}


	/*private class ComparatorByDate implements Comparator {

		public int compare(Object arg0, Object arg1) {
			FileInfo  item0=(FileInfo) arg0;
			FileInfo item1=(FileInfo) arg1;
			int flag=item0.getLastModified()-item1.getLastModified();
			return flag;
		}
	}*/
}
