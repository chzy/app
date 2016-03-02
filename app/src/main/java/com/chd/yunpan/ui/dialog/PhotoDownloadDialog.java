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
import com.chd.yunpan.net.DownloadRun;
import com.chd.yunpan.ui.fragment.PhotoListFragment;

import java.util.ArrayList;
import java.util.List;

//import com.chd.yunpan.parse.entity.FileDataEntity;

public class PhotoDownloadDialog {
	private Dialog d = null;

	private View view = null;

	private Button confirm = null;

	private Button cancel = null;

	private Context context = null;

	private PhotoListFragment fragment = null;

	private List<FileInfo0> entities = null;

	public PhotoDownloadDialog(Context context, PhotoListFragment fragment,
			List<FileInfo0> entities) {
		super();
		this.context = context;
		this.fragment = fragment;
		this.entities = entities;
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
				// for (int i = 0; i < entities.size(); i++) {
				// if (entities.get(i).isPitch()&&entities.get(i).getFlag()!=1)
				// {
				// //下载事件
				// entities.get(i).setFlag(3);
				// }
				//
				// //下载完成改变
				// }

				new Thread() {
					public void run() {
						downFile();
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

	private void downFile() {
		List<FileInfo0> downPictures = new ArrayList<FileInfo0>();
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i).isSelected() ) {
				FileInfo0 localFile = new FileInfo0();
				localFile.setObjid(entities.get(i).getObjid());
				downPictures.add(localFile);
			}
		}
		DownloadRun downloadRun = DownloadRun.getDownLoadRun();
		downloadRun.addToDB( context, handler2, downPictures);
		handler2.sendEmptyMessage(1);
	}

	private Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				fragment.refresh();
				fragment.downFileFork();
				Toast.makeText(context, "照片已下载完成", Toast.LENGTH_SHORT).show();
			} else if (msg.what == 300 || msg.what == 3) {
				// fragment.setEntities(entities);
				d.dismiss();
				fragment.refresh();
			}
		}
	};
}
