package com.chd.yunpan.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.chd.yunpan.R;

public class ReLoginDialog extends Dialog {

	//private Dialog d = null;

	private View view = null;

	private View confirm = null; 

	private View cancel = null;

	private Context context = null;

	public ReLoginDialog(Activity activity) {
		super(activity);
		this.context = activity;

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.relogin_dialog);
		confirm = findViewById(R.id.settingConfirmRelogin);
		cancel = findViewById(R.id.settingCancelRelogin);
		confirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(onConfirmListener!=null)
				onConfirmListener.confirm();
				//dialogAnimation(d, view, height, getWindowHeight(), true);
					ReLoginDialog.this.dismiss();
					//reLogin();
			}

		});
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//dialogAnimation(d, view, height, getWindowHeight(), true);
				ReLoginDialog.this.dismiss();
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
		window
				.setBackgroundDrawableResource(R.drawable.background_dialog);
	}
	
	private OnConfirmListener onConfirmListener = null;

	public interface OnConfirmListener {
		void confirm();
	}

	public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
		this.onConfirmListener = onConfirmListener;
	}

}
