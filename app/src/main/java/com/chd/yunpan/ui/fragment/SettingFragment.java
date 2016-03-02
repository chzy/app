package com.chd.yunpan.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.TClient;
import com.chd.proto.Errcode;
import com.chd.proto.LoginResult;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.net.ExecRunable;
import com.chd.yunpan.net.NetworkUtils;
import com.chd.yunpan.net.TouXiangUpLoad;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.ui.AutoPhotoBackupsActivity;
import com.chd.yunpan.ui.LoginActivity;
import com.chd.yunpan.ui.SettingActivity;
import com.chd.yunpan.ui.dialog.ReLoginDialog;
import com.chd.yunpan.utils.TimeAndSizeUtil;
import com.lockscreen.pattern.OffUnlockPasswordActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SettingFragment extends BaseFragment implements OnClickListener  {

	private View otherAutoPhoto = null;

	private View otherLockPhone = null;

	private View otherSetting = null;

	private View otherReLogin = null;

	private Context context = null;

	private TextView photoText = null;

	private TextView lockText = null;

	private ImageView userPhoto = null;

	private TextView userID = null;

	private TextView userPhoneNumber = null;

	private ProgressBar pb = null;

	private TextView netDiskCapacity = null;

	
	DisplayImageOptions options;

	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	@Override
	public View createView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
		return inflater.inflate(R.layout.other_explorer_fragment_layout, null);
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		otherAutoPhoto = getView().findViewById(R.id.otherAutoPhoto);
		otherLockPhone = getView().findViewById(R.id.otherLockPhone);
		otherSetting = getView().findViewById(R.id.otherSetting);
		otherReLogin = getView().findViewById(R.id.otherReLogin);

		userPhoto = (ImageView) getView().findViewById(R.id.otherFragmentPhoto);
		userPhoto.setOnClickListener(this);
		otherSetting.setOnClickListener(this);
		userID = (TextView) getView().findViewById(R.id.otherUserInfoId);
		userPhoneNumber = (TextView) getView()
				.findViewById(R.id.otherUserInfoCellPhoneNumber);
		pb = (ProgressBar) getView().findViewById(R.id.otherUserInfoDiskState);
		netDiskCapacity = (TextView) getView()
				.findViewById(R.id.otherUserInfoDiskCapacity);

		photoText = (TextView) getView().findViewById(R.id.otherAutoPhotoText);
		lockText = (TextView) getView().findViewById(R.id.otherAutoLockText);
		otherAutoPhoto.setOnClickListener(this);
		pb.setMax(100);
		
		ShareUtils shareUtils = new ShareUtils(getActivity());
		LoginResult entity = shareUtils.getLoginEntity();
		imageLoader.displayImage("", userPhoto, options, animateFirstListener);
		userID.setText(entity.getUserid());
		userPhoneNumber.setText(entity.getUserid());
		pb.setProgress((int)(entity.getUspace()*100/entity.getSpace()));
		netDiskCapacity.setText("网盘容量："+TimeAndSizeUtil.getSize(entity.getUspace()+"")+"/"+TimeAndSizeUtil.getSize(entity.getSpace()+""));
		createNewFile();
		progressDialog = new ProgressDialog(getActivity());
	}

	@Override
	public void setLogic() {
		otherLockPhone.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getActivity(),OffUnlockPasswordActivity.class);
				startActivity(i);
			}
		});
		otherReLogin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ReLoginDialog reLoginDialog = new ReLoginDialog(getActivity());
				reLoginDialog.setOnConfirmListener(new com.chd.yunpan.ui.dialog.ReLoginDialog.OnConfirmListener() {
					@Override
					public void confirm() {
						Intent i = new Intent(getActivity(),LoginActivity.class);
						startActivity(i);
						getActivity().finish();
					}
				});
				reLoginDialog.showMyDialog();
			}
		});
	}

	@Override
	public void setAdapter() {
		
	}
	
	@Override
	public void onResume() {
		if (UILApplication.getInstance().getLockPatternUtils().savedPatternExists()) {
			lockText.setText("已开启");
			lockText.setTextColor(Color.rgb(106, 208, 44));
		} else {
			lockText.setText("未开启");
			lockText.setTextColor(Color.rgb(170, 170, 170));
		}
		
		ShareUtils utils = new ShareUtils(getActivity());
		if(utils.getAutoPhotoBack()){
			photoText.setText("已开启");
			photoText.setTextColor(Color.rgb(106, 208, 44));
		} else {
			photoText.setText("未开启");
			photoText.setTextColor(Color.rgb(170, 170, 170));
		}
		
		super.onResume();
	}
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
	private int crop = 100;
	private Dialog dialog = null;
	private File sdcardTempFile;


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.otherAutoPhoto:
			Intent i1 = new Intent(getActivity(),AutoPhotoBackupsActivity.class);
			startActivity(i1);
			break;
		case R.id.otherSetting:
			Intent i = new Intent(getActivity(), SettingActivity.class);
			startActivity(i);
			break;
		case R.id.otherFragmentPhoto:
			if (dialog == null) {
				dialog = new AlertDialog.Builder(getActivity()).setItems(new String[] { "相机", "相册" }, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
//							Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
////							intent.putExtra("output", Uri.fromFile(sdcardTempFile));
//							intent.putExtra("crop", "true");
//							intent.setType("image/*");
//						     intent.putExtra("data", data);
//							intent.putExtra("aspectX", 1);// 裁剪框比例
//							intent.putExtra("aspectY", 1);
//							intent.putExtra("outputX", crop);// 输出图片大小
//							intent.putExtra("outputY", crop);
//							intent.putExtra("return-data", true);
//							startActivityForResult(intent, 101);
							Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							 intent.putExtra("return-data", true); 
				              startActivityForResult(intent, CAMERA_WITH_DATA);
						} else {
							Intent intent = new Intent("android.intent.action.PICK");
							intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
//							intent.putExtra("output", Uri.fromFile(sdcardTempFile));
							intent.putExtra("crop", "true");
							intent.putExtra("aspectX", 1);// 裁剪框比例
							intent.putExtra("aspectY", 1);
							intent.putExtra("outputX", crop);// 输出图片大小
							intent.putExtra("outputY", crop);
							intent.putExtra("return-data", true);
							startActivityForResult(intent, 100);
						}
					}
				}).create();
				}
				if (!dialog.isShowing()) {
					dialog.show();
				}
			break;

		default:
			break;
		}
		
	}
	
	public static final int PHOTO_PICKED_WITH_DATA = 3021;
	public static final int CAMERA_WITH_DATA = 3023;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if(resultCode==0)
		        return;
		 if (resultCode ==Activity.RESULT_OK) {
			 
			 if(requestCode==CAMERA_WITH_DATA){
				 final Bitmap photo = data.getParcelableExtra("data");
		           if(photo!=null){
		               doCropPhoto(photo);
		               return;
		           }
			 }
			 try {
				  Bitmap photo = data.getParcelableExtra("data");
				 OutputStream out = new FileOutputStream(sdcardTempFile);
				boolean flag =  photo.compress(CompressFormat.JPEG, 100, out);
				 out.close();
				if(flag){
					  showDialog(null);
				}
			 
			} catch (IOException e) {
				e.printStackTrace();
			}
	            
	          
	      
			
		}
		 
	}
	protected void doCropPhoto(Bitmap data){
	       Intent intent = getCropImageIntent(data);
	       startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
	   }
	public static Intent getCropImageIntent(Bitmap data) {
	    Intent intent = new Intent("com.android.camera.action.CROP");
	    intent.setType("image/*");
	    intent.putExtra("data", data);
	    intent.putExtra("crop", "true");
	    intent.putExtra("aspectX", 1);
	    intent.putExtra("aspectY", 1);
	    intent.putExtra("outputX", 128);
	    intent.putExtra("outputY", 128);
	    intent.putExtra("return-data", true);
	    return intent;
	}
	
	ProgressDialog progressDialog ;
	
	AlertDialog d;
	
	protected void showDialog(Bitmap b) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle("头像设置");
		builder.setMessage("头像数据已经截取，确定上传？");
		if(d==null){
	
		builder.setNegativeButton("取消",new Dialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				d.dismiss();
			}
		});
		builder.setPositiveButton("确认",new Dialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				progressDialog.show();
				 Toast.makeText(getActivity(), "开始上传头像", Toast.LENGTH_SHORT).show();
				
				upload();
					
			}
		} );
		d = builder.create();
		}
		d.show();
	}

	public void upload(){
		if(NetworkUtils.isNetworkAvailable(getActivity())){

	        	new Thread(){
	        		public void run() {
						int what=1;
	        			ShareUtils su = new ShareUtils(getActivity());

						try {
							TouXiangUpLoad.uploadBigFileMethod(sdcardTempFile, su, context);
						} catch (Exception e) {
					 		what = 2 ;
							e.printStackTrace();
							Toast.makeText(context, "上传失败", Toast.LENGTH_SHORT).show();
						}
						handler.sendEmptyMessage(what);
	        		}
				}.start();
	        	d.dismiss();
	        
		        }else {
		        	Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
		        }
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				
				if(msg.obj!=null){
					LoginResult entity = (LoginResult) msg.obj;
					ShareUtils shareUtils = new ShareUtils(getActivity());
					shareUtils.setLoginEntity(entity);
					imageLoader.displayImage("trpc://netdiskportrait", userPhoto, options, animateFirstListener);
					userID.setText(entity.getUserid());
					userPhoneNumber.setText(entity.getUserid());
					pb.setProgress((int)(entity.getUspace()*100/entity.getSpace()));
					netDiskCapacity.setText("网盘容量："+TimeAndSizeUtil.getSize(entity.getUspace()+"")+"/"+TimeAndSizeUtil.getSize(entity.getSpace()+""));
				}
				progressDialog.dismiss();
				break;
			case 1:
				
				ExecRunable.execRun(new Thread(){
					@Override
					public void run() {
						ShareUtils su = new ShareUtils(getActivity());
						Message msg = new Message();
						try {
							try {
								LoginResult entity = TClient.getinstance().loginAuth(su.getUsername(), su.getPwd(), 1111);
								if(entity.getResult().getRet()== Errcode.SUCCESS){
									msg.what = 0;
									msg.obj = entity;
								}else {
									msg.what=-1;
									msg.obj ="账号或密码错误";
								}
								
							} catch (Exception e) {
								msg.what = -1;
								msg.obj = "账号或密码错误";
							}
							
						} catch (Exception e){
							msg.what = -1;
							msg.obj = "网络错误";
						}
						sendMessage(msg);
						
					}
				});
				break;
			case 2:
				// msg = new Message();
				//msg.what = -1;
				//msg.obj = "上传错误";
				//sendMessage(msg);
				break;
			}

		}
	};
	
	// 复制文件
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }
	
	public void createNewFile(){
		File cacheDir = StorageUtils.getCacheDirectory(getActivity());
		sdcardTempFile = new File(cacheDir, "avator.jpg");
		
		if(!sdcardTempFile.exists()){
			try {
				sdcardTempFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			sdcardTempFile.delete();
			try {
				sdcardTempFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onBack() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void pageChange() {
		// TODO Auto-generated method stub
		
	}
	

}
