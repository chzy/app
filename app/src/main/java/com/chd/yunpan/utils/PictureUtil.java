package com.chd.yunpan.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Description 调用系统拍照或进入图库中选择照片,再进行裁剪,压缩.
 * @author 疯尘丶
 */
public class PictureUtil {
	/** 用来请求照相功能的常量 */
	public static final int CAMERA_WITH_DATA = 168;
	/** 用来请求图片选择器的常量 */
	public static final int PHOTO_PICKED_WITH_DATA = CAMERA_WITH_DATA + 1;
	/** 用来请求图片裁剪的 */
	public static final int PHOTO_CROP = PHOTO_PICKED_WITH_DATA + 1;
	/**	拍照的照片存储位置 */
	public static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/");

	private static File mCurrentPhotoFile;// 照相机拍照得到的图片

	public File file;		// 截图后得到的图片
	public static Uri imageUri;	// 拍照后的图片路径
	public static Uri imageCaiUri;	// 裁剪后的图片路径


	static{
		if (!PHOTO_DIR.exists()){
			PHOTO_DIR.mkdirs();		// 创建照片的存储目录
		}
	}
	
	/**
	 * 得到当前图片文件的路径
	 * @return
	 */
	public static File getmCurrentPhotoFile() {
		if (mCurrentPhotoFile == null) {
			if (!PHOTO_DIR.exists()){
				PHOTO_DIR.mkdirs();		// 创建照片的存储目录
			}
			mCurrentPhotoFile = new File(PHOTO_DIR, getCharacterAndNumber()+".png"/*此处可更换文件后缀*/);
			if (!mCurrentPhotoFile.exists())	// 判断存储文件是否存在>不存在则创建
				try {
					mCurrentPhotoFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return mCurrentPhotoFile;
	}

	/**
	 * 开始启动照片选择框
	 * @param context
	 * @param iscrop
	 * 是否裁剪
	 */
	public static void doPickPhotoAction(final Activity context) {
		final Context dialogContext = new ContextThemeWrapper(context,android.R.style.Theme_Light);
		String[] choices;
		choices = new String[2];
		choices[0] = "拍照"; 			// 拍照
		choices[1] = "从相册中选择" ;	// 从相册中选择
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,	android.R.layout.simple_list_item_1, choices);

		final AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
		builder.setTitle("选择上传图片方式");
		builder.setSingleChoiceItems(adapter, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0: {
							String status = Environment
									.getExternalStorageState();
							if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
								doTakePhoto(context);// 用户点击了从照相机获取
							} else {
								Toast.makeText(dialogContext, "没有找到SD卡", Toast.LENGTH_SHORT).show();
							}
							break;
						}
						case 1:
							doPickPhotoFromGallery(context);// 从相册中去获取
							break;
						}
					}
				});
		builder.setNegativeButton(
				"取消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	/**
	 * 调用拍照功能
	 */
	public static void doTakePhoto(Activity context) {
		try {
			if (!PHOTO_DIR.exists()){
				PHOTO_DIR.mkdirs();	// 创建照片的存储目录
			}
			imageUri = Uri.fromFile(getmCurrentPhotoFile());
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			context.startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(context, "没有找到照相机", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 调用相册程序
	 * @param context
	 */
	public static void doPickPhotoFromGallery(Context context) {
		try {
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			((Activity) context).startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(context, "没有找到相册", Toast.LENGTH_SHORT).show();
		}
	}
	
	public static Uri getImageUri(){
		File temporaryFile = new File(PHOTO_DIR, getCharacterAndNumber()+".png");
		imageUri = Uri.fromFile(temporaryFile);
		return imageUri;
	}
	
	public static Uri getImageCaiUri(){
		File temporaryFile = new File(PHOTO_DIR, getCharacterAndNumber()+".png");
		imageCaiUri = Uri.fromFile(temporaryFile);
		return imageCaiUri;
	}

	/**
	 * 得到系统当前时间并转化为字符串
 	 */

	@SuppressLint("SimpleDateFormat")
	public static String getCharacterAndNumber() {
//        String rel="";
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//        Date curDate = new Date(System.currentTimeMillis());
//        rel = formatter.format(curDate);
        return "netdiskportrait";
    }

	public static void saveMyBitmap(Bitmap mBitmap, File file) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			mBitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
			byte[] bitmapData = baos.toByteArray();

			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bitmapData);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//计算图片的缩放值
	public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	// 根据路径获得图片并压缩，返回bitmap用于显示
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 480, 800);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}


	// 压缩图片(第一次)
	public static Bitmap comp(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, baos);
		if (baos.toByteArray().length / 1024 > 200) {// 判断如果图片大于200KB,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.PNG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 160f;// 这里设置高度为800f
		float ww = 130f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}
	// 压缩图片(第二次)
	private static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.PNG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}



}