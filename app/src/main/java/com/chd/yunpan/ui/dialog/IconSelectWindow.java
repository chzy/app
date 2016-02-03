package com.chd.yunpan.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.chd.yunpan.R;
import com.chd.yunpan.utils.PictureUtil;


/**
 * 
 * @类名称: UserBaseInfoActivity
 * @类描述: TODO 用户照片上传
 * @创建人：liuml
 * @创建时间：2015-5-6 上午10:47:22
 * @备注：
 * @version V1.0
 */
public class IconSelectWindow extends PopupWindow implements View.OnClickListener{

	public static final String TAG = "IconSelectWindow";

	//取消
	private Button btn_cancel;

	//拍照
	private Button btn_photo;

	//本地图片
	private Button btn_picLibrary;

	public IconSelectWindow() {
	}

	private Activity mycontext;
	private View pop_View;

	public IconSelectWindow(final Activity context) {
		super(context);
		this.mycontext = context;
		pop_View= LayoutInflater.from(context).inflate(R.layout.activity_userupdatephoto,null,false);
		initView();
		setContentView(pop_View);
		this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		this.setOutsideTouchable(true);
		this.setBackgroundDrawable(new BitmapDrawable());
		this.setFocusable(true);
		// 设置背景颜色变暗
		setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = context.getWindow().getAttributes();
				lp.alpha = 1f;
				context.getWindow().setAttributes(lp);
			}
		});
		super.update();
	}


	private void initView() {
		//初始化视图
		btn_cancel= (Button) pop_View.findViewById(R.id.btn_cancel);
		btn_photo= (Button) pop_View.findViewById(R.id.btn_photo);
		btn_picLibrary= (Button) pop_View.findViewById(R.id.btn_picLibrary);
		btn_cancel.setOnClickListener(this);
		btn_picLibrary.setOnClickListener(this);
		btn_photo.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel: // 取消
			dismiss();
			break;
		case R.id.btn_photo: // 拍照
			dismiss();
			PictureUtil.doTakePhoto(mycontext);
			break;
		case R.id.btn_picLibrary: // 图片库
			dismiss();
			PictureUtil.doPickPhotoFromGallery(mycontext);
			break;
		}
	}

	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			WindowManager.LayoutParams lp = mycontext.getWindow().getAttributes();
			lp.alpha = 0.7f;
			mycontext.getWindow().setAttributes(lp);
			hideSoftInput(parent);
			this.showAtLocation(parent, Gravity.BOTTOM,0,0);
		} else {
			this.dismiss();
		}
	}

	private void hideSoftInput(View view){
		try {
			((InputMethodManager) mycontext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mycontext.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}catch (Exception e){

		}
		}



}