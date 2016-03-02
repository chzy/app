package com.chd.yunpan.myclass;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
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
import android.widget.Toast;

import com.chd.Entity.FilesListEntity;
import com.chd.Entity.LocalFileEntity;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.db.DBManager;
import com.chd.yunpan.net.NetworkUtils;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.ui.UpLoadActivity;
import com.chd.yunpan.ui.dialog.PhotoLoadDialog;
import com.chd.yunpan.ui.dialog.PhotoLoadDialog.OnConfirmListener1;
import com.chd.yunpan.ui.fragment.UpLoadOtherFileFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*import com.chd.yunpan.parse.entity.FileDataDBEntity;
import com.chd.yunpan.parse.entity.FilesListEntity;*/

public class OtherFilePopMenuLoad implements OnClickListener {
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
	private List<FileWithBoolean> booleans;
	private ImageView imageView;
	private Context context;
	private Handler handler;
	private int curcid = 0;
	private UpLoadOtherFileFragment fragment;
	private Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {

				imageView.setImageResource(R.drawable.cloud_dir_icon);
				path.setText("wo云盘");
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
				// showDialog();
//				context.stopService(new Intent(context, UpLoadService.class));
//				context.startService(new Intent(context, UpLoadService.class));
				for (int i = 0; i < booleans.size(); i++) {
					booleans.get(i).setCheck(false);
				}
				check(booleans);
				dis();
				disDialog();
				Toast.makeText(context, "添加添加到上传列表", Toast.LENGTH_SHORT)
						.show();
				((UpLoadActivity)context).finish();

				break;

			default:
				break;
			}
		}
	};

	public void check(List<FileWithBoolean> booleans) {
		int j = 0;
		for (int i = 0; i < booleans.size(); i++) {

			if (booleans.get(i).isCheck()) {
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

	public OtherFilePopMenuLoad(Context context,
			List<FileWithBoolean> booleans, UpLoadOtherFileFragment fragment,
			View v) {
		entity = (FilesListEntity) fragment.getActivity().getIntent().getSerializableExtra("entity");
		this.booleans = booleans;
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
		if (entity!=null) {
			FileInfo0 f = entity.getList().get(entity.getCount()-1);
			path.setText(/*entity.getFilePathEntities().get(entity.getFilePathEntities().size()-1).getName()*/f.getFilename());
		}
	}

	public void showPopupWindows(View v) {
		menuTop.showAtLocation(v, Gravity.TOP, 0, 50);
		menuDown.showAtLocation(v, Gravity.BOTTOM, 0, 0);
		fragment.setFlag(true);
		havePop = true;
	}

	public boolean havePop() {
		return havePop;
	}

	Dialog dialog = null;
	private  FilesListEntity<FileInfo0> entity;

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

	public void dis() {
		menuTop.dismiss();
		menuDown.dismiss();
		fragment.setFlag(false);
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
					OtherFilePopMenuLoad.this.entity = entity;
					FileInfo0 f =(FileInfo0) entity.getList().get( entity.getCount()-1 );
					path.setText(f.getFilename());
				}
			});
			loadDialog.showMyDialog();
			break;
		case R.id.photo_load_menu_bn:
			DBManager dbManager = new DBManager(context);
			dbManager.open();
			ShareUtils utils = new ShareUtils(context);
			dbManager.open();
			if(NetworkUtils.isNetworkAvailable(context)){
				for (int i = 0; i < booleans.size(); i++) {
					if (booleans.get(i).isCheck()) {
						/*if(booleans.get(i).getFile().isDirectory()){
							utils.addDownPath(booleans.get(i).getFile().getAbsolutePath());
						}else
						*/
						{
							/*FileDataDBEntity*/ LocalFileEntity dataDBEntity = new /*FileDataDBEntity*/LocalFileEntity();
							//dataDBEntity.setFid((int)System.currentTimeMillis());
							File f=booleans.get(i).getFile();
							dataDBEntity.setPath(booleans.get(i).getFile().getParent());
							dataDBEntity.setFname(f.getName());
							dataDBEntity.setSize(f.length());
							//dataDBEntity.setPid(entity.getCid());

							dbManager.addUpLoadingFile(dataDBEntity);
						}
					}
				}
//			new Thread(new AddFileNeedUpLoadRightNow2(loadFiles, curcid,
//					context, handler3)).start();
				dbManager.close();
			}else {
				Toast.makeText(context, "网络不可用",Toast.LENGTH_SHORT).show();
			}
			// System.out.println("上传文件Cid" + curcid);
			// System.out.println("上传文件" + loadbooleans);
			// 上传方法，curcid是目录cid loadbooleans是要上传的
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
				if(NetworkUtils.isNetworkAvailable(context)){
				List<File> loadFiles = new ArrayList<File>();
				for (int i = 0; i < booleans.size(); i++) {
					if (booleans.get(i).isCheck()) {
						loadFiles.add(booleans.get(i).getFile());
					}
				}
//				new Thread(new AddFileNeedUpLoadRightNow2(loadFiles, curcid,
//						context, handler3)).start();
				dialog.dismiss();
				showDialog();
				}else {
						Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
				}
			}
		});
		builder.create().show();
	}

	public boolean canLoaded(File localFile) {
		return flag;
		//SQLUtils sqlUtils = new SQLUtils(context);
//		if (sqlUtils.findByNPid(localFile.getName(), curcid) != null) {
//			return false;
//		} else {
//			return true;
//		}
	}


	public void quit() {
		dis();
		for (int i = 0; i < booleans.size(); i++) {
			booleans.get(i).setCheck(false);
		}
		flag = false;
		check(booleans);
		all.setText("全选");
		message = new Message();
		message.what = 1;
		message.obj = booleans;
		handler.sendMessage(message);

	}

	public void selsvtAll() {
		if (!flag) {
			flag = true;
			for (int i = 0; i < booleans.size(); i++) {
				booleans.get(i).setCheck(true);
			}
			all.setText("全不选");
		} else {
			flag = false;
			all.setText("全选");
			for (int i = 0; i < booleans.size(); i++) {
				booleans.get(i).setCheck(false);
			}
		}
		check(booleans);
		message = new Message();
		message.what = 2;
		message.obj = booleans;
		handler.sendMessage(message);
	}

}
