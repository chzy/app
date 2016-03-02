package com.chd.yunpan.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

import com.chd.yunpan.R;

public class DialogPopupFromBottom3 extends Dialog {

	//private Dialog d = null;

	private View view = null;

	private View confirm = null; 

	private View cancel = null;

	private Context context = null;

	public DialogPopupFromBottom3(Activity activity) {
		super(activity);
		
		this.context = activity;

		//d = new Dialog(context);

//		view = View.inflate(context, res[0], null);
		
//		d.setContentView(view);
//
//		confirm = d.findViewById(res[1]);
//
//		cancel = d.findViewById(res[2]);

//		init();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lz_relogin3);
		confirm = findViewById(R.id.settingConfirmRelogin);
		cancel = findViewById(R.id.settingCancelRelogin);
		confirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(onConfirmListener!=null)
				onConfirmListener.confirm();
				//dialogAnimation(d, view, height, getWindowHeight(), true);
					DialogPopupFromBottom3.this.dismiss();
					//reLogin();
			}

		});
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//dialogAnimation(d, view, height, getWindowHeight(), true);
				DialogPopupFromBottom3.this.dismiss();
			}
		});
	}
	
	public void showMyDialog(){
		Window window = getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.mystyle); // 添加动画
		show();
		WindowManager windowManager = ((Activity)context).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = display.getWidth();
		getWindow().setAttributes(params);
		// lp.x = 0;
		// lp.y = getWindowHeight();
		window
				.setBackgroundDrawableResource(R.drawable.background_dialog);
	}
	
	
//
//	private void init() {
//		
//		Window dialogWindow = d.getWindow();
//		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
//		lp.width = LayoutParams.MATCH_PARENT;
//		lp.height = LayoutParams.WRAP_CONTENT;
//		
//		dialogWindow
//				.setBackgroundDrawableResource(R.drawable.background_dialog);
//
//		final int height = lp.height;

//		confirm.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if(onConfirmListener!=null)
//				onConfirmListener.confirm();
//				dialogAnimation(d, view, height, getWindowHeight(), true);
//			}
//		});
//		cancel.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dialogAnimation(d, view, height, getWindowHeight(), true);
//
//			}
//		});
//		d.show();
//		dialogAnimation(d, view, getWindowHeight(), height, false);

//	}

	private int getWindowHeight() {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		return dm.widthPixels;
	}

	private void dialogAnimation(final Dialog d, View v, int from, int to,
			boolean needDismiss) {

		Animation anim = new TranslateAnimation(0, 0, from, to);
		anim.setFillAfter(true);
		anim.setDuration(600);
		if (needDismiss) {
			anim.setAnimationListener(new AnimationListener() {

				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					d.dismiss();
				}
			});

		}

		v.startAnimation(anim);

	}

	private OnConfirmListener onConfirmListener = null;

	public interface OnConfirmListener {
		void confirm();
	}

	public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
		this.onConfirmListener = onConfirmListener;
	}

}
