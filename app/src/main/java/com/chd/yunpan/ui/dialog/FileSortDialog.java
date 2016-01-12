package com.chd.yunpan.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;

import com.chd.yunpan.R;
import com.chd.yunpan.ui.fragment.BaseFragment;
import com.chd.yunpan.ui.fragment.FileListFragment;

public class FileSortDialog implements OnClickListener {

	private Dialog d = null;

	private View view = null;

	private Button yes = null;

	private View quit = null;

	private ImageView A;
	private ImageView B;
	private ImageView C;
	private ImageView D;

	private View az;
	private View za;
	private View no;
	private View on;

	private int s;

	private FileListFragment fragment;

	public FileSortDialog(FileListFragment fragment) {

		this.fragment = fragment;

		d = new Dialog(fragment.getActivity());

		view = View.inflate(fragment.getActivity(), R.layout.jd_menu_more_sort,
				null);

		d.setContentView(view);

		quit = view.findViewById(R.id.jd_menu_sort_quit);

		A = (ImageView) view.findViewById(R.id.imageA);
		B = (ImageView) view.findViewById(R.id.imageB);
		C = (ImageView) view.findViewById(R.id.ImageC);
		D = (ImageView) view.findViewById(R.id.ImageD);

		az = view.findViewById(R.id.s_az);
		za = view.findViewById(R.id.s_za);
		no = view.findViewById(R.id.s_no);
		on = view.findViewById(R.id.s_on);

		az.setOnClickListener(this);
		za.setOnClickListener(this);
		on.setOnClickListener(this);
		no.setOnClickListener(this);

		init();
		setView();
	}

	public void setView() {
		int flag = 0;
		if (BaseFragment.FILE_NAME.equals(fragment.getSortName())) {
			if (BaseFragment.ASC == fragment.getAscOrDesc()) {
				flag = 2;
			} else {
				flag = 1;
			}

		} else if (BaseFragment.USER_PTIME.equals(fragment.getSortName())) {
			if (BaseFragment.ASC == fragment.getAscOrDesc()) {
				flag = 3;
			} else {
				flag = 4;
			}
		}

		switch (flag) {
		case 0:
			A.setVisibility(View.VISIBLE);
			B.setVisibility(View.GONE);
			C.setVisibility(View.GONE);
			D.setVisibility(View.GONE);
			break;
		case 1:
			A.setVisibility(View.VISIBLE);
			B.setVisibility(View.GONE);
			C.setVisibility(View.GONE);
			D.setVisibility(View.GONE);
			break;
		case 2:
			A.setVisibility(View.GONE);
			B.setVisibility(View.VISIBLE);
			C.setVisibility(View.GONE);
			D.setVisibility(View.GONE);
			break;
		case 3:
			A.setVisibility(View.GONE);
			B.setVisibility(View.GONE);
			C.setVisibility(View.VISIBLE);
			D.setVisibility(View.GONE);
			break;
		case 4:
			A.setVisibility(View.GONE);
			B.setVisibility(View.GONE);
			C.setVisibility(View.GONE);
			D.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}

	private void init() {

		Window dialogWindow = d.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		lp.width = getWindowHeight() - 100;
		lp.height = LayoutParams.WRAP_CONTENT;
		dialogWindow
				.setBackgroundDrawableResource(R.drawable.background_dialog);

		final int height = lp.height;

		// yes.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (onConfirmListener != null)
		// onConfirmListener.confirm();
		// }
		// });
		quit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				d.dismiss();
			}
		});
		d.show();

	}

	private int getWindowHeight() {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) fragment.getActivity()).getWindowManager()
				.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	private OnConfirmListener onConfirmListener = null;

	public interface OnConfirmListener {
		void confirm();
	}

	public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
		this.onConfirmListener = onConfirmListener;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.s_az:

			d.dismiss();
			fragment.setSort(BaseFragment.FILE_NAME, BaseFragment.DESC);
			break;
		case R.id.s_za:
			fragment.setSort(BaseFragment.FILE_NAME, BaseFragment.ASC);
			d.dismiss();
			break;
		case R.id.s_no:

			d.dismiss();
			fragment.setSort(BaseFragment.USER_PTIME, BaseFragment.ASC);
			break;
		case R.id.s_on:

			d.dismiss();
			fragment.setSort(BaseFragment.USER_PTIME, BaseFragment.DESC);
			break;

		default:
			break;
		}
	}

	public void setSort() {
	}
}
