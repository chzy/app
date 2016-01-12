package com.chd.yunpan.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.chd.Entity.FilesListEntity;
import com.chd.yunpan.R;
//import com.chd.yunpan.ui.dialog.PhotoLoadDialogOther2.OnConfirmListener;

/*
import com.chd.yunpan.parse.entity.FilePathEntity;
import com.chd.yunpan.parse.entity.FilesListEntity;
*/

public class PhotoLoadDialog extends Dialog implements OnClickListener {

	private Dialog d = null;

	private View view = null;

	private LinearLayout one, two, three, four;

	private Context context;

	private Handler handler = null;

	private int height = 0;
	private Activity activity;
	private int curcid = 0;
	private String curname=null;
	private Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Message message3=new Message();
			if (msg.what==-1) {
				handler.sendEmptyMessage(-1);
			}else{
				message3.what=msg.what;
				message3.obj=msg.obj;
				handler.sendMessage(message3);
			}
			
			
		}
	};
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_load_dialog);
		two = (LinearLayout) findViewById(R.id.photo_load_dialog_two);
		three = (LinearLayout) findViewById(R.id.photo_load_dialog_three);
		four = (LinearLayout) findViewById(R.id.photo_load_dialog_four);
//		one.setOnClickListener(this);
		two.setOnClickListener(this);
		three.setOnClickListener(this);
		four.setOnClickListener(this);
//		init();
	}

	public PhotoLoadDialog(Context context, Activity activity) {
		super(context);
		this.activity = activity;
		this.context = context;
		//d = new Dialog(context);

		//view = View.inflate(context, R.layout.photo_load_dialog, null);
		

		//d.setContentView(view);

//		one = (LinearLayout) d.findViewById(R.id.photo_load_dialog_one);
		
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void showMyDialog() {
		Window window = getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.mystyle); // 添加动画
		show();
		WindowManager windowManager = activity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = display.getWidth();
		getWindow().setAttributes(params);
	
		window
				.setBackgroundDrawableResource(R.drawable.background_dialog);
	}

	// Dialog dialog = null;
	// public void showDialog() {
	//
	// dialog = new Dialog(context);
	// Window dialogWindow = dialog.getWindow();
	// dialogWindow
	// .setBackgroundDrawableResource(R.drawable.background_dialog);
	// dialog.setContentView(R.layout.photo_delete_dialog);
	// ImageView imageView = (ImageView) dialog
	// .findViewById(R.id.photo_delete_diaog_image);
	// imageView.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// disDialog();
	// }
	// });
	// dialog.show();
	// }
	//
	// public void disDialog() {
	// if (dialog != null) {
	// dialog.dismiss();
	// }
	// }

	private int getWindowHeight() {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		return dm.widthPixels;
	}

	private void dialogAnimation(final Dialog d, View v, int from, int to,
			final boolean needDismiss) {
//
//		Animation anim = new TranslateAnimation(0, 0, from, to);
//		anim.setFillAfter(true);
//		anim.setDuration(1000);
//		
//		anim.setAnimationListener(new AnimationListener() {
//
//				public void onAnimationStart(Animation animation) {
//				}
//
//				public void onAnimationRepeat(Animation animation) {
//				}
//
//				public void onAnimationEnd(Animation animation) {
//					
//				}
//			});
//		if (needDismiss) {
//			d.dismiss();
//		}else {
//			d.show();
//		}
//		v.startAnimation(anim);
		if(needDismiss){
//			d.getWindow().setWindowAnimations(R.anim.dialog_dis);
			dismiss();
		}else {
//			d.getWindow().setWindowAnimations(R.anim.dialog_show);
			show();
		}
			
	}

	// confirm.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// showDialog();
	// dialogAnimation(d, view, height, getWindowHeight(), true);
	//
	// }
	// });
	// cancel.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// dialogAnimation(d, view, height, getWindowHeight(), true);
	// }
	// });
	// d.show();
	// dialogAnimation(d, view, getWindowHeight(), height, false);

	@Override
	public void onClick(View v) {
		dialogAnimation(d, view, height, getWindowHeight(), true);
		switch (v.getId()) {
	/*	case R.id.photo_load_dialog_one:
			Message message = new Message();
			message.what = 0;
			message.obj="云盘相册";
			handler.sendMessage(message);
			break;*/
	/*	case R.id.photo_load_dialog_two:
			if (onConfirmListener!=null) {
				FilesListEntity entity = new FilesListEntity();
				entity.setCid(0);
				List<FilePathEntity> entities = new ArrayList<FilePathEntity>();
				entities.add(new FilePathEntity("网盘文件", -1, 0, -1));
				entity.setFilePathEntities(entities);
				onConfirmListener.confirm(entity);
			}
			break;*/
		case R.id.photo_load_dialog_three:
		
			/*new PhotoLoadDialogOther2(context,(FilesListEntity) activity.getIntent().getSerializableExtra("entity")).setOnConfirmListener(new OnConfirmListener() {
				public void confirm(FilesListEntity entity) {
					if (onConfirmListener!=null) {
						onConfirmListener.confirm(entity);
					}
				}
			});;*/
			
			
			break;
		case R.id.photo_load_dialog_four:
			Message message4 = new Message();
			message4.what = -1;
			handler.sendMessage(message4);
			break;

		default:
			break;
		}

	}
	
	private OnConfirmListener1 onConfirmListener = null;

	public interface OnConfirmListener1 {
		void confirm(FilesListEntity entity);
	}

	public void setOnConfirmListener(OnConfirmListener1 onConfirmListener) {
		this.onConfirmListener = onConfirmListener;
	}

}
