package com.chd.yunpan.ui.fragment.popupwindow;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.chd.yunpan.R;
import com.chd.yunpan.ui.DownloadED;
import com.chd.yunpan.ui.MainFragmentActivity;
import com.chd.yunpan.ui.dialog.DirCreateDialog;
import com.chd.yunpan.ui.dialog.FileSearchDialog;
import com.chd.yunpan.ui.dialog.FileSortDialog;
import com.chd.yunpan.ui.fragment.FileListFragment;

public class MenuMore implements OnClickListener {
	private PopupWindow window;
	private View view;

	private View soon_uploand;
	private View soon_download;
	private View newfile;
	private View sort;
	private View search;
	private View refrsh;
	private View all;


	private FileListFragment  fragment;

	public MenuMore(FileListFragment fragment) {
	this.fragment = fragment;
		view = fragment.getActivity().getLayoutInflater()
				.inflate(R.layout.jd_menu_more, null);
		window = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		window.setBackgroundDrawable(new BitmapDrawable());
		window.setOutsideTouchable(true);
		window.setFocusable(true);
		soon_uploand = view.findViewById(R.id.jd_menu_more_up);
		soon_download = view.findViewById(R.id.jd_menu_more_dow);
		newfile = view.findViewById(R.id.jd_menu_more_newfile);
		refrsh = view.findViewById(R.id.jd_menu_more_refrsh);
		search = view.findViewById(R.id.jd_menu_more_search);
		sort = view.findViewById(R.id.jd_menu_more_sort);
		all=view.findViewById(R.id.jd_menu_more_all);

		soon_uploand.setOnClickListener(this);
		soon_download.setOnClickListener(this);
		newfile.setOnClickListener(this);
		refrsh.setOnClickListener(this);
		search.setOnClickListener(this);
		sort.setOnClickListener(this);
		all.setOnClickListener(this);
	}

	public void setOnDismissListener(OnDismissListener dismissListener) {
		window.setOnDismissListener(dismissListener);
	}

	public void show(View view) {
		window.setAnimationStyle(R.style.mypopwindow_anim_style);
		window.showAtLocation(view, Gravity.BOTTOM, 0, view.getHeight());

	}

	public void dismiss() {
		window.dismiss();
	}

	public PopupWindow getWindow() {
		return window;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.jd_menu_more_dow:
			Intent i = new Intent(fragment.getActivity(), DownloadED.class);
			i.putExtra("type", 1);
			fragment.getActivity().startActivity(i);
		
			break;
		case R.id.jd_menu_more_up:
			Intent i1 = new Intent(fragment.getActivity(), DownloadED.class);
			i1.putExtra("type", 2);
			fragment.getActivity().startActivity(i1);
			break;
		case R.id.jd_menu_more_newfile:
			new DirCreateDialog(fragment);
			dismiss();
			break;
		case R.id.jd_menu_more_sort:
			new FileSortDialog(fragment);
			dismiss();
			break;
		case R.id.jd_menu_more_search:
			new FileSearchDialog(fragment);
			dismiss();
			break;
		case R.id.jd_menu_more_refrsh:
			fragment.refresh();
			break;
		case R.id.jd_menu_more_all:
			Intent i2 = new Intent(fragment.getActivity(), MainFragmentActivity.class);
			i2.putExtra("callpage", 2);
			fragment.getActivity().startActivity(i2);
			break;

		}

	}
}
