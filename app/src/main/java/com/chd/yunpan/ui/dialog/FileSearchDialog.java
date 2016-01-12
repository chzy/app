package com.chd.yunpan.ui.dialog;

import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

import com.chd.yunpan.R;
import com.chd.yunpan.ui.fragment.FileListFragment;

public class FileSearchDialog {

	private Dialog d = null;

	private View view = null;

	private Button yes = null;

	private Button no = null;

	private EditText ed;

	private FileListFragment fragment;

	public FileSearchDialog(FileListFragment fragment) {
		this.fragment = fragment;

		d = new Dialog(fragment.getActivity());

		view = View.inflate(fragment.getActivity(),
				R.layout.jd_menu_more_search, null);

		d.setContentView(view);

		yes = (Button) d.findViewById(R.id.jd_menu_more_search_sou);

		no = (Button) d.findViewById(R.id.jd_menu_more_search_quit);

		ed = (EditText) d.findViewById(R.id.jd_menu_more_search_et);

		init();
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

		/*yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = ed.getText().toString();
				if (name == null || name.trim().equals("")) {
					Toast.makeText(fragment.getActivity(), "搜索名字不能为空", 0)
							.show();
					return;
				}
				fragment.search(ed.getText().toString());
				d.dismiss();
			}
		});*/
		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				d.dismiss();
			}
		});
		d.show();

	}

	private int getWindowHeight() {
		DisplayMetrics dm = new DisplayMetrics();
		(fragment.getActivity()).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		return dm.widthPixels;
	}

	private OnConfirmListener onConfirmListener = null;

	public interface OnConfirmListener {
		void confirm();
	}

	public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
		this.onConfirmListener = onConfirmListener;
	}

}
