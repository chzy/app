package com.chd.yunpan.ui.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
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
import android.widget.Toast;

import com.chd.Entity.FilesListEntity;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.db.DBManager;
import com.chd.yunpan.net.NetworkUtils;
import com.chd.yunpan.parse.entity.PhotoFileEntity;
import com.chd.yunpan.ui.UpLoadActivity;
import com.chd.yunpan.ui.dialog.PhotoLoadDialog;
import com.chd.yunpan.ui.dialog.PhotoLoadDialog.OnConfirmListener1;
import com.chd.yunpan.ui.fragment.PhotoUploadingFragment;
import com.chd.yunpan.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*import com.chd.yunpan.parse.entity.FileDataDBEntity;
import com.chd.yunpan.parse.entity.FilesListEntity;*/

public class PhotoPopMenuLoad implements OnClickListener {
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
	private List<PhotoFileEntity> fileEntities;
	private ImageView imageView;
	private Context context;
	private Handler handler;
	private PhotoUploadingFragment fragment;
	private int curcid = 0;

	public FilesListEntity<FileInfo0> entity;
	private Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {

				imageView.setImageResource(R.drawable.cloud_dir_icon);
				path.setText("我的云盘");
				curcid = 0;

				// else{
				// imageView.setImageResource(R.drawable.photo_album_icon);
				// path.setText("云盘相册");
				// curcid=0;
				// }
			} else if (msg.what == -1) {

			} else {
				imageView.setImageResource(R.drawable.cloud_dir_icon);
				curcid = msg.what;
				path.setText((String) msg.obj);
			}

		}
	};

	private View v = null;

	private Handler handler3 = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1024:

				check(fileEntities);
				dis();
				disDialog();
				Toast.makeText(context, "成功添加到下载列表", Toast.LENGTH_SHORT).show();
				((UpLoadActivity) context).finish();
				break;

			default:
				break;
			}
		}
	};

	public void check(List<PhotoFileEntity> fileEntities) {
		int j = 0;
		for (int i = 0; i < fileEntities.size(); i++) {

			if (fileEntities.get(i).isPitch()) {
				j++;
			}
		}
		if (j > 0) {
			title.setText("以选" + j + "项");
			onload.setText("开始上传(" + j + ")");
			showPopupWindows(v);
		} else if (j == 0) {
			title.setText("请选择文件");
			onload.setText("开始上传");
			dis();
		}
	}

	public PhotoPopMenuLoad(Context context,
			List<PhotoFileEntity> fileEntities,
			PhotoUploadingFragment fragment, View v) {
		/*if (entity!=null)
			path.setText(entity.getList().get(entity.getCount()-1).getFilename());
		else*/
			path.setText("图片");

		entity = (FilesListEntity) fragment.getActivity().getIntent()
				.getSerializableExtra("entity");
		this.fileEntities = fileEntities;
		this.v = v;
		this.context = context;
		this.fragment = fragment;
		menuViewDown = LayoutInflater.from(context).inflate(
				R.layout.photo_load_menu_down, null);

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
		if (entity!=null)
			path.setText(entity.getList().get(entity.getCount()-1).getFilename());
		else
			path.setText("null");
	}

	private int getWindowHeight() {
		DisplayMetrics dm = new DisplayMetrics();
		fragment.getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		return dm.heightPixels;
	}

	public void showPopupWindows(View v) {
		menuTop.showAtLocation(v, Gravity.TOP, 0, 50);
		menuDown.showAtLocation(v, Gravity.BOTTOM, 0, 0);
		handler1.sendEmptyMessageDelayed(1, 200);

		fragment.setFlag(true);
		havePop = true;
	}

	private Handler handler1 = new Handler() {
		public void handleMessage(Message msg) {
			fragment.setMainPad(menuViewDown.getBottom());
		}
	};

	public boolean havePop() {
		return havePop;
	}

	public void dis() {
		menuTop.dismiss();
		menuDown.dismiss();
		fragment.setFlag(false);
		fragment.setMainPad(0);
		havePop = false;
	}

	public void sethandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void onClick(View v) {
		NetworkUtils nUtils = new NetworkUtils();
		switch (v.getId()) {
		case R.id.photo_load_menu_top_quit:
			quit();
			break;
		case R.id.photo_load_menu_top_all:
			selsvtAll();
			break;
		case R.id.photo_load_menu_lin:
			PhotoLoadDialog loadDialog = new PhotoLoadDialog(context,
					fragment.getActivity());
			loadDialog.setOnConfirmListener(new OnConfirmListener1() {
				public void confirm(FilesListEntity entity) {
					PhotoPopMenuLoad.this.entity = entity;
					FileInfo0 f= (FileInfo0) entity.getList().get(entity.getCount() - 1);
					path.setText(f.getFilename());
				}
			});
			loadDialog.showMyDialog();
			break;
		case R.id.photo_load_menu_bn:
			if(NetworkUtils.isNetworkAvailable(context)){
				DBManager db = new DBManager(context);
				db.open();
			for (int i = 0; i < fileEntities.size(); i++) {
				if (fileEntities.get(i).isPitch()) {
//					if(!canLoaded(fileEntities.get(i))){
//						alert();
//						return;
//					}
					FileInfo0 dataDBEntity = new FileInfo0();
					dataDBEntity.setLastModified( (int)System.currentTimeMillis()/1000);
					File file=fileEntities.get(i).getFile();
					dataDBEntity.setFilePath(file.getAbsolutePath());
					dataDBEntity.setFilesize(file.length());
					dataDBEntity.setFilename(file.getName());
					db.addUpLoadingFile(dataDBEntity);
				}
			}
			db.close();
			ToastUtils.toast(context, "添加成功，自动上传中");
			
//			new Thread(new AddFileNeedUpLoadRightNow2(loadFiles, curcid,
//					context, handler3)).start();
			dis();
			
			}else {
				Toast.makeText(context, "网络不可用",Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}

	}

	public void alert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("警告!!");
		builder.setMessage("里面有重名文件，是否覆盖?");
		builder.setNegativeButton("取消", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("确认", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				List<File> loadFiles = new ArrayList<File>();
				for (int i = 0; i < fileEntities.size(); i++) {
					if (fileEntities.get(i).isPitch()) {
						loadFiles.add(fileEntities.get(i).getFile());
					}
				}
				// new Thread(new AddFileNeedUpLoadRightNow2(loadFiles, curcid,
				// context, handler3)).start();
				dialog.dismiss();
				showDialog();
			}
		});
		builder.create().show();
	}

	public boolean canLoaded(PhotoFileEntity localFile) {
		return false;
		// SQLUtils sqlUtils = new SQLUtils(context);
		// if (sqlUtils.findByNPid(localFile.getFile().getName(), curcid) !=
		// null) {
		// return false;
		// } else {
		// return true;
		// }
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
		textView.setText("正在处理");
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

	public void quit() {
		dis();
		for (int i = 0; i < fileEntities.size(); i++) {
			fileEntities.get(i).setPitch(false);
		}
		flag = false;
		all.setText("全选");
		message = new Message();
		message.what = 1;
		message.obj = fileEntities;
		handler.sendMessage(message);
	}

	public void selsvtAll() {
		if (!flag) {
			flag = true;
			for (int i = 0; i < fileEntities.size(); i++) {
				fileEntities.get(i).setPitch(true);
			}
			all.setText("全不选");
		} else {
			flag = false;
			all.setText("全选");
			for (int i = 0; i < fileEntities.size(); i++) {
				fileEntities.get(i).setPitch(false);
			}
		}
		message = new Message();
		message.what = 2;
		message.obj = fileEntities;
		handler.sendMessage(message);
	}

}
