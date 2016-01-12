package com.chd.yunpan.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.TextView;

import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.db.DBManager;
import com.chd.yunpan.ui.DownloadED;

import java.util.List;

public class JDDialogDownedStop extends Dialog {


	private View view = null;

	private View yes = null;

	private View no = null;

	private DownloadED context = null;

	private List<FileInfo0> items;

	private TextView size;
	
	private int i;
	
	
	
DBManager dbManager = null;
	public JDDialogDownedStop(DownloadED activity) {
		super(activity);
		this.context = activity;
		dbManager = new DBManager(activity);
		dbManager.open();
		this.i = activity.getFileDataDBEntities().size();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jd_uped_clear);


		size = (TextView) findViewById(R.id.trans_dia_text);

		size.setText("还有" + i + "个文件,清空列表吗？");

		yes = findViewById(R.id.trans_dia_yes);

		no = findViewById(R.id.trans_dia_no);
		yes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onConfirmListener != null)
					onConfirmListener.confirm();
				int type = context.getIntent().getIntExtra("type", 0);
				switch (type) {
				case 0:
					for (int i = 0; i < context.getFileDataDBEntities().size(); i++) {
						dbManager.deleteDownloadingFile(context.getFileDataDBEntities().get(i).getObjid());
						dbManager.deleteUpLoadingFile(context.getFileDataDBEntities().get(i).getObjid());
					}
					break;

				case 1:
					for (int i = 0; i < context.getFileDataDBEntities().size(); i++) {
						dbManager.deleteDownloadedFile(context.getFileDataDBEntities().get(i).getObjid());
					}
					break;
				case 2:
					for (int i = 0; i < context.getFileDataDBEntities().size(); i++) {
						dbManager.deleteUpLoadedFile(context.getFileDataDBEntities().get(i).getObjid());
					}
					break;
				}
				//context.updateAdapter();
			//	dialogAnimation(d, view, height, getWindowHeight(), true);
				context.refresh();
				dismiss();
			}
		});
		no.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//dialogAnimation(d, view, height, getWindowHeight(), true);
				dismiss();

			}
		});
	}

	public void showMyDialog() {

//		Window dialogWindow = d.getWindow();
//		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		
		Window window = getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.mystyle); // 添加动画
		show();
		WindowManager windowManager = context.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = display.getWidth();
		getWindow().setAttributes(params);
		// lp.x = 0;
		// lp.y = getWindowHeight();
		window
				.setBackgroundDrawableResource(R.drawable.background_dialog);

		
//		d.show();
		
//		dialogAnimation(d, view, getWindowHeight(), height, false);
	}
	


	private int getWindowHeight() {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay()
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
