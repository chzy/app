package com.chd.yunpan.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;

import java.util.ArrayList;
import java.util.List;

/*import com.chd.yunpan.parse.entity.FileDataEntity;*/

public class PhotoDownloadDialog2 {
	private Dialog d = null;

	private View view = null;

	private Button confirm = null;

	private Button cancel = null;

	private Context context = null;
	
	private FileInfo0 entity=null;


	public PhotoDownloadDialog2(Context context,FileInfo0 entity) {
		super();
		this.context = context;
		this.entity= entity;
		d = new Dialog(context);

		view = View.inflate(context, R.layout.photo_pop_down, null);

		d.setContentView(view);

		confirm = (Button) d.findViewById(R.id.photo_pop_downgo);

		cancel = (Button) d.findViewById(R.id.photo_pop_downcancel);

		init();
	}

	private void init() {

		Window dialogWindow = d.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		lp.width = LayoutParams.MATCH_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		// lp.x = 0;
		// lp.y = getWindowHeight();
		dialogWindow
				.setBackgroundDrawableResource(R.drawable.background_dialog);

		final int height = lp.height;

		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogAnimation(d, view, height, getWindowHeight(), true);
				new Thread(){
					public void run() {
						down();
					}
				}.start();
				
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogAnimation(d, view, height, getWindowHeight(), true);
			}
		});
		d.show();
		dialogAnimation(d, view, getWindowHeight(), height, false);

	}
	
	private void down(){
		List<FileInfo0> downPictures = new ArrayList<FileInfo0>();
		FileInfo0 localFile = new FileInfo0();
		
//		DownloadRun downloadRun = DownloadRun.getDownLoadRun();
//		downloadRun.addToDB(0, 0, context, handler2, downPictures);
	}
	
	
	private Handler handler2  = new Handler(){
		public void handleMessage(Message msg) {
			 if(msg.what==300||msg.what==3){
				Toast.makeText(context, "照片已下载完成", Toast.LENGTH_SHORT).show();
				
			}
			
		}
	};

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
