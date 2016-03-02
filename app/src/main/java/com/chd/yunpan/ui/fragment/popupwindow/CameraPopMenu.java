package com.chd.yunpan.ui.fragment.popupwindow;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.net.DownloadRun;
import com.chd.yunpan.net.NetworkUtils;
/*import com.chd.yunpan.parse.entity.FileDataEntity;*/
import com.chd.yunpan.ui.dialog.PhotoDeleteDialog;
import com.chd.yunpan.ui.dialog.PhotoDownloadDialog;
import com.chd.yunpan.ui.dialog.PhotoShareDialog;
import com.chd.yunpan.ui.fragment.PhotoListFragment;

public class CameraPopMenu implements OnClickListener {
	private PopupWindow menuTop;
	private PopupWindow menuDown;
	private TextView cancel;
	private TextView delete;
	private TextView download;
	private TextView share;
	private boolean flag = false;
	private Handler handler ;
	private Message message;
	private Context context;
	private Activity activity;
	private PhotoListFragment fragment;
	private boolean havePop=false;
	public CameraPopMenu(Context context,Activity activity,PhotoListFragment fragment) {
		this.context = context;
		this.fragment=fragment;
		this.activity=activity;
		View menuViewDown = LayoutInflater.from(context).inflate(
				R.layout.camera_pop_menu, null);
		menuDown = new PopupWindow(menuViewDown, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		View menuViewTop = LayoutInflater.from(context).inflate(
				R.layout.camera_pop2_menu, null);
		menuTop = new PopupWindow(menuViewTop, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		cancel = (TextView) menuViewTop.findViewById(R.id.camera_top2_cancel);
		cancel.setOnClickListener(this);
		delete = (TextView) menuViewDown.findViewById(R.id.camera_pop_delete);
		delete.setOnClickListener(this);
		share = (TextView) menuViewDown.findViewById(R.id.camera_pop_share);
		share.setOnClickListener(this);
		download = (TextView) menuViewDown
				.findViewById(R.id.camera_pop_download);
		download.setOnClickListener(this);

	}
	//显示PopUpWindows
	public void showPopupWindows(View v) {
		menuTop.showAtLocation(fragment.getView(), Gravity.TOP, 0, 50);
		menuDown.showAtLocation(fragment.getView(), Gravity.BOTTOM, 0, 0);
		havePop=true;
	}
	public boolean havePop() {
		return havePop;
	}
   //取消PopUpWindows
	public void dis() {
		menuTop.dismiss();
		menuDown.dismiss();
		havePop=false;
		fragment.dis();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.camera_top2_cancel:
			for (int i = 0; i < fragment.getEntity().size(); i++) {
				fragment.getEntity().get(i).setIsChecked(false);
			}
			dis();
			fragment.refresh();
			break;
		case R.id.camera_pop_share:
			if(NetworkUtils.isNetworkAvailable(activity)){
				share();
			}else {
				Toast.makeText(context, "网络不可用，请稍后重试", Toast.LENGTH_SHORT)
				.show();
			}
			
			break;
		case R.id.camera_pop_delete:
			if (!NetworkUtils.isNetworkAvailable(context)) {
				Toast.makeText(context, "网络不可用，请稍后重试", Toast.LENGTH_SHORT)
						.show();
			} else {
//				dis();
				PhotoDeleteDialog deldialog = new PhotoDeleteDialog(context,
						fragment.getEntity(),fragment);
			}
			break;
		case R.id.camera_pop_download:
			if (fragment.getEntity().size() > 0) {
				List<FileInfo0> downPictures = new ArrayList<FileInfo0>();
				for (int i = 0; i < fragment.getEntity().size(); i++) {
					if (fragment.getEntity().get(i).isSelected()) {
						downPictures.add(fragment.getEntity().get(i));
					}
				}
				if (!NetworkUtils.isNetworkAvailable(context)) {
					Toast.makeText(context, "网络不可用，请稍后重试", Toast.LENGTH_SHORT)
							.show();
				} else if (NetworkUtils.isWifi(context)) {
					
					new Thread(){
						public void run() {
							down();
						}
					}.start();
				} else {
					dis();
					if (downPictures.size() == 0) {
						Toast.makeText(context, "文件已下载完成", Toast.LENGTH_SHORT)
								.show();
					}else{
						PhotoDownloadDialog downdialog = new PhotoDownloadDialog(
								context,fragment,fragment.getEntity());
					}
					
				}
			}

			break;
		default:
			break;
		}
	}
	
	private void down(){
		List<FileInfo0> downPictures = new ArrayList<FileInfo0>();
		for (int i = 0; i < fragment.getEntity().size(); i++) {
			if (fragment.getEntity().get(i).isSelected()) {
				downPictures.add(fragment.getEntity().get(i));
			}
		}
		DownloadRun downloadRun = DownloadRun.getDownLoadRun();
		downloadRun.addToDB( context, handler2, downPictures);

		handler2.sendEmptyMessage(1);
	}
	
	
	private Handler handler2  = new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what==1){
				fragment.refresh();
				fragment.downFileFork();
				Toast.makeText(context, "照片已下载完成", Toast.LENGTH_SHORT)
				.show();
				dis();
			}else if(msg.what==300||msg.what==3){
				fragment.refresh();
			}
			
		}
	};
	
	
	
	private void share(){
		int j = 0;
		for (int i = 0; i < fragment.getEntity().size(); i++) {
			if(fragment.getEntity().get(i).isSelected()){
				j++;
			}
			
		}
		
//		if(j==0&&a==1){
//			menu_other_share.showContextMenu();
//		}
		
		if(j==1){
			PhotoShareDialog dialog=new PhotoShareDialog(activity,fragment.getEntity(),fragment);
			dialog.showDialog();
		}
	
		if(j>=2){
			Toast.makeText(activity, "请选择一个照片分享", 0).show();
		}
		
	}
	
	
}
