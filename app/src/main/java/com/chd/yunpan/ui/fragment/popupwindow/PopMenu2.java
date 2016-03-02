package com.chd.yunpan.ui.fragment.popupwindow;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chd.yunpan.R;
import com.chd.yunpan.myclass.FileWithBoolean;
import com.chd.yunpan.myclass.WyfItem;
import com.chd.yunpan.ui.UpLoadActivity;

public class PopMenu2 implements OnClickListener {

	/** 上方菜单 */
	private PopupWindow menuTop;

	/** 下方菜单 */
	private PopupWindow menuDown;

	/** 全选 or 全不选 */
	private TextView all = null;

	/** 头--》显示选中数目 */
	private TextView title = null;

	/** 退出选择模式 取消所有选中 */
	private TextView quit = null;

	private boolean f = false;

	private boolean havePop = false;

	/** 数据 */
	private List<FileWithBoolean> fileList = new ArrayList<FileWithBoolean>();

	private View menuViewDown;
	private View menuViewTop;
	private View v;
	/** 上传目标文件夹 */
	private TextView up_file;

	/** 按钮---显示选中文件数目 */
	private TextView up_account;

	/** 数据对应的adapter 刷新用 */
	private BaseAdapter adapter;

	// private NetDistProgressBar bar;

	private Fragment fragment = null;

	public PopMenu2(Fragment fragment,
			List<FileWithBoolean> fileList) {
		this.fileList = fileList;
		// this.bar = bar;
		this.fragment = fragment;

		menuViewDown = LayoutInflater.from(fragment.getActivity()).inflate(
				R.layout.yf_pop3_menu, null);

		menuDown = new PopupWindow(menuViewDown, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		menuViewTop = LayoutInflater.from(fragment.getActivity()).inflate(
				R.layout.pop2_menu, null);
		menuTop = new PopupWindow(menuViewTop, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		all = (TextView) menuViewTop.findViewById(R.id.pop_top_all);
		title = (TextView) menuViewTop.findViewById(R.id.pop_top_title);
		quit = (TextView) menuViewTop.findViewById(R.id.pop_top_quit);
		all.setOnClickListener(this);
		quit.setOnClickListener(this);

		v = menuViewDown.findViewById(R.id.first);
		up_file = (TextView) menuViewDown.findViewById(R.id.pop_file);
		up_account = (TextView) menuViewDown.findViewById(R.id.pop_upload);

	}

	// 显示上下两个menu
	public void showPopupWindows(View v) {
		int[] position = new int[2];
		((UpLoadActivity) fragment.getActivity()).getBack()
				.getLocationOnScreen(position);
		menuTop.showAtLocation(v, Gravity.TOP, 0, position[1]);
		menuDown.showAtLocation(v, Gravity.BOTTOM, 0, 0);
		havePop = true;
	}

	// public void downloadShow() {
	// bar.show();
	// }
	//
	// public void downloadDis() {
	// bar.dis();
	// }

	public void dis() {
		menuTop.dismiss();
		menuDown.dismiss();
		havePop = false;
	}

	public boolean havePop() {
		return havePop;
	}

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
	}
//
//	private boolean m = true;
//	private boolean a = false;

//	public void setA() {
//		a = true;
//	}

//	private WyfDialogDistStop stop;
//	private int i;

//	public void stop(final List<FileWithBoolean> items, final int i) {
//		stop = new WyfDialogDistStop(fragment.getActivity(), items, i);
//		stop.setOnConfirmListener(new OnConfirmListener() {
//
//			@Override
//			public void confirm() {
//				stopDis(items, i);
//			}
//		});
//	}

	public void check() {
		int j = 0;
		for (int i = 0; i < fileList.size(); i++) {
			if (fileList.get(i).isCheck()) {
				j++;
			}
		}
		if (j == 0) {
			title.setText("请选择文件");
			up_account.setText("开始上传");
		} else {
			title.setText("以选" + j + "项");
			up_account.setText("开始上传(" + j + ")");
		}
		adapter.notifyDataSetChanged();
	}

	public void stopDis(List<WyfItem> items, int i) {
//		stop.dis();
		// int t = 0;
		items.get(i).setDownload(false);
		for (int j = 0; j < items.size(); j++) {
			if (items.get(j).isDownload() && items.get(j).getPyte() == 1) {
				// t++;
			}
		}
		// if (t == 0) {
		// bar.dis();
		// }
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pop_top_all:
			selsvtAll();
			break;
		case R.id.pop_top_quit:
			quit();
			break;

		}
	}

	private void selsvtAll() {
		if (!f) {
			f = true;
			for (int i = 0; i < fileList.size(); i++) {
				fileList.get(i).setCheck(true);
			}
			all.setText("全不选");
		} else {
			f = false;
			all.setText("全选");
			for (int i = 0; i < fileList.size(); i++) {
				fileList.get(i).setCheck(false);
			}
		}
		adapter.notifyDataSetChanged();
	}

	private void quit() {
		dis();
		for (int i = 0; i < fileList.size(); i++) {
			fileList.get(i).setCheck(false);
		}
		f = false;
		all.setText("全选");
		adapter.notifyDataSetChanged();
	}
}
