package com.chd.yunpan.ui.myviews;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;

import com.chd.yunpan.R;

public class FileDirLocationBar3 implements OnClickListener {

	private List<File> allPath = new ArrayList<File>();

	List<Button> buttons = new ArrayList<Button>();

	private Context context = null;

	private ViewGroup viewGroup = null;

	private HorizontalScrollView hor = null;

	private Handler handler = new Handler();

	public boolean isRootDir = true;

	private String rootName = null;

	/**
	 * @param context
	 *            所在的上下文
	 * @param viewGroup
	 *            所使用的布局
	 * @param horizontalScrollView
	 *            需要一个horizontalScrollView装载viewGroup
	 * 
	 * 
	 *            需要的布局: <HorizontalScrollView android:id="@+id/hor"
	 *            android:layout_width="match_parent"
	 *            android:layout_height="40dp" android:scrollbars="none" >
	 * 
	 *            <FrameLayout android:id="@+id/locationID"
	 *            android:layout_width="wrap_content"
	 *            android:layout_height="40dp" android:orientation="horizontal"
	 *            > </FrameLayout> </HorizontalScrollView>
	 */
	public FileDirLocationBar3(Context context, ViewGroup viewGroup,
			HorizontalScrollView horizontalScrollView,String rootName) {

		this.context = context;

		this.rootName =rootName;
		
		this.viewGroup = viewGroup;

		this.hor = horizontalScrollView;

		allPath.add(new File(rootName));

		Button b = this.createNewButton(this.rootName);
		FrameLayout.LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.LEFT);
		params.leftMargin = 0;
		b.setLayoutParams(params);
		b.setTag(new File(rootName));
		b.setOnClickListener(this);
		viewGroup.addView(b, 0);
		buttons.add(b);

	}

	private void initButtons() {

		for (int i = 0; i < buttons.size(); i++) {
			viewGroup.addView(buttons.get(i), 0);
		}
		handler.post(new MoveToEnd());
	}

	/**
	 * 传入当前目录的路径
	 * */
	public String changeCurrentPath(File file) {
		allPath.add(file);
		isRootDir = allPath.size() <= 1;

		String displayName = null;

		addButton(file);
		return displayName;
	}

	public File backOneClass() {
		isRootDir = allPath.size() <= 1;
		if (isRootDir) {
			return null;
		}
		int index = allPath.size() - 2;

		pathChange(index);

		return allPath.get(index);
	}

	private void addButton(File file) {
		Button b = createNewButton(file.getName());
		b.setOnClickListener(this);
		addButton2View(b);
		buttons.add(b);
		b.setTag(file);
	}

	private void addButton2View(Button b) {
		FrameLayout.LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.LEFT);
		params.leftMargin = buttons.get(buttons.size() - 1).getRight() - 50;

		b.setLayoutParams(params);
		viewGroup.addView(b, 0);
		handler.post(new MoveToEnd());
	}

	private Button createNewButton(String title) {

		Button b = new Button(context);
		b.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		b.setSingleLine();
		b.setText(title);
		b.setBackgroundResource(R.drawable.location_btn_last);

		return b;

	}

	@Override
	public void onClick(View v) {

		int index = allPath.indexOf(v.getTag());

		pathChange(index);
	}

	private void pathChange(int index) {

		if (index < (buttons.size() - 1)) {
			onFilePathChange.filePathChange(allPath.get(index));

			for (int i = 0; i < buttons.size(); i++) {
				viewGroup.removeView(buttons.get(i));
			}

			List<Button> bs = new ArrayList<Button>();
			List<File> str = new ArrayList<File>();

			for (int i = 0; i <= index; i++) {
				bs.add(buttons.get(i));
				str.add(allPath.get(i));
			}
			buttons = bs;
			allPath = str;
			isRootDir = allPath.size() <= 1;
			initButtons();
		}
	}

	class MoveToEnd implements Runnable {

		@Override
		public void run() {
			hor.scrollTo(buttons.get(buttons.size() - 1).getRight(), 0);
		}

	}

	private OnFilePathChange onFilePathChange = null;

	public void setOnFilePathChange(OnFilePathChange onFilePathChange) {
		this.onFilePathChange = onFilePathChange;
	}

	public interface OnFilePathChange {
		void filePathChange(File currentPath);
	}

	public boolean isRootDir() {
		return isRootDir;
	}

}
