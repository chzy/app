package com.chd.yunpan.ui.fragment.popupwindow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chd.Entity.FilesListEntity;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.net.NetworkUtils;
import com.chd.yunpan.parse.entity.FileDataFatherEntity;
import com.chd.yunpan.ui.fragment.FileListFragment;
import com.chd.yunpan.ui.fragment.popupwindow.PhotoLoadDialog2.OnConfirmListener1;
import com.chd.yunpan.utils.ToastUtils;

import java.util.List;

/*import com.chd.yunpan.parse.entity.FileDataFatherEntity;*/

/*import com.chd.yunpan.parse.entity.FileDataEntity;
import com.chd.yunpan.parse.entity.FileDataFatherEntity;
import com.chd.yunpan.parse.entity.FileDirDataEntity;
import com.chd.yunpan.parse.entity.FilesListEntity;*/

public class OtherFilePopMenuLoad2 implements OnClickListener {
	private PopupWindow menuTop;
	private PopupWindow menuDown;
	private TextView all, title, quit, path;
	private boolean flag = false;
	private boolean havePop = false;
	private Message message;
	private View menuViewDown;
	private View menuViewTop;
	private Button onload;
	private LinearLayout layout;
	private List<FileDataFatherEntity> booleans;
	private ImageView imageView;
	private Context context;

	private View v = null;

	private FileListFragment fragment;

	private FilesListEntity<FileInfo0> entity;

	ProgressDialog progressDialog;

	AlertDialog dialog2;

	public OtherFilePopMenuLoad2(FileListFragment fragment, View v) {
		this.fragment = fragment;
		entity = fragment.getFilesListEntity();
		context = fragment.getActivity();
		this.v = v;
		/*menuViewDown = LayoutInflater.from(context).inflate(
				R.layout.photo_load_menu_down2, null);*/

		menuDown = new PopupWindow(menuViewDown, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		menuViewTop = LayoutInflater.from(context).inflate(
				R.layout.photo_load_menu_top, null);
		menuTop = new PopupWindow(menuViewTop, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		layout = (LinearLayout) menuViewDown
				.findViewById(R.id.photo_load_menu_lin);
		imageView = (ImageView) menuViewDown
				.findViewById(R.id.photo_load_menu_image);
		path = (TextView) menuViewDown.findViewById(R.id.photo_load_menu_text);
		FileInfo0 f= fragment.getFilesListEntity().getList().get(fragment.getFilesListEntity().getCount() - 1);
		path.setText( f.getFilename() );
		layout = (LinearLayout) menuViewDown
				.findViewById(R.id.photo_load_menu_lin);
		onload = (Button) menuViewDown.findViewById(R.id.photo_load_menu_bn);
		all = (TextView) menuViewTop.findViewById(R.id.photo_load_menu_top_all);
		title = (TextView) menuViewTop
				.findViewById(R.id.photo_load_menu_top_title);
		quit = (TextView) menuViewTop
				.findViewById(R.id.photo_load_menu_top_quit);
		all.setOnClickListener(this);
		quit.setOnClickListener(this);
		onload.setOnClickListener(this);
		layout.setOnClickListener(this);
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		dialog2 = new ProgressDialog(context);
		dialog2.setTitle("有重名文件，是否覆盖");
		dialog2.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
				new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mainHandler.sendEmptyMessage(4);
						dialog2.dismiss();
					}
				});
		dialog2.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
				new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mainHandler.sendEmptyMessage(1);
						dialog2.dismiss();
					}
				});
		
		new MyMoveThread().start();
	}

	public void showPopupWindows(View v) {
		menuTop.showAtLocation(v, Gravity.TOP, 0, 50);
		menuDown.showAtLocation(v, Gravity.BOTTOM, 0, 0);
		havePop = true;
	}

	public boolean havePop() {
		return havePop;
	}

	Dialog dialog = null;

	public void showDialog() {

		dialog = new Dialog(context);
		Window dialogWindow = dialog.getWindow();
		dialogWindow
				.setBackgroundDrawableResource(R.drawable.background_dialog);
		dialog.setContentView(R.layout.photo_delete_dialog);
		TextView textView = (TextView) dialog
				.findViewById(R.id.photo_delete_diaog_textView);
		textView.setText("正在移动");
		ImageView imageView = (ImageView) dialog
				.findViewById(R.id.photo_delete_diaog_image);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				disDialog();
			}
		});

		dialog.show();
	}

	public void disDialog() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	public void dis() {
		menuTop.dismiss();
		menuDown.dismiss();
		havePop = false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.photo_load_menu_top_quit:
			// quit();
			dis();
			break;
		case R.id.photo_load_menu_top_all:
			if (flag) {
				fragment.checkAll(false);
				flag = false;
				all.setText("全选");
			} else {
				fragment.checkAll(true);
				flag = true;
				all.setText("全不选");
			}

			break;
		case R.id.photo_load_menu_lin:
			PhotoLoadDialog2 loadDialog = new PhotoLoadDialog2(fragment);
			loadDialog.setOnConfirmListener(new OnConfirmListener1() {
				public void confirm(FilesListEntity entity) {
					path.setText(/*entity.getFilePathEntities()
							.get(entity.getFilePathEntities().size() - 1)
							.getName()*/"其他文件");
					OtherFilePopMenuLoad2.this.entity = entity;
				}
			});
			loadDialog.showMyDialog();
			break;
		case R.id.photo_load_menu_bn:
			if (NetworkUtils.isNetworkAvailable(context)) {
				progressDialog.show();
				mainHandler.sendEmptyMessage(1);
			}
			break;
		}
	}

	private int movesize = -1;

	public Handler mainHandler = new Handler() {
		public void handleMessage(Message msg) {
			Message msg1 = new Message();
			switch (msg.what) {
			case -1:

				ToastUtils.toast(
						context,
						"移动"
								+ fragment.getCheckedFileDataFatherEntities()
										.get(movesize).getFilename() + "失败");
				movesize++;
				msg1.what = 1;
				if (movesize >= fragment.getCheckedFileDataFatherEntities()
						.size()) {
					ToastUtils.toast(context, "移动结束");
					progressDialog.dismiss();
					fragment.refresh();
					return;
				} else {
					msg1.obj = fragment.getCheckedFileDataFatherEntities().get(
							movesize);
				}
				threadHandler.sendMessage(msg1);
				break;

			case 1:
				msg1.what = 1;
				movesize++;
				if (movesize >=fragment.getCheckedFileDataFatherEntities()
						.size()) {
					ToastUtils.toast(context, "移动结束");
					progressDialog.dismiss();
					fragment.refresh();
					return;
				} else {
					msg1.obj = fragment.getCheckedFileDataFatherEntities().get(
							movesize);
				}
				threadHandler.sendMessage(msg1);
				break;
			case 2:
				dialog2.show();

				break;
			case 3:
/*
				ToastUtils.toast(context, "目录重名，无法移动");
				sendEmptyMessage(1);
*/
				assert (false);
				break;
			case 4:
				msg1.what = 2;
				if (movesize>= fragment.getCheckedFileDataFatherEntities()
						.size()) {
					ToastUtils.toast(context, "移动结束");
					progressDialog.dismiss();
					fragment.refresh();
					return;
				} else {
					msg1.obj = fragment.getCheckedFileDataFatherEntities().get(
							movesize);
				}
				threadHandler.sendMessage(msg1);
				break;
			}

		}

	};

	public Handler threadHandler = null;

	public class MyMoveThread extends Thread {
		@Override
		public void run() {
			Looper.prepare();
			threadHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
						case 1:
							break;
						case 2:
							assert (false);
							break;
					}
				}

			};
			Looper.loop();
		}
	}
}