package com.chd.yunpan.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ListView;

import com.chd.yunpan.R;
import com.chd.yunpan.myclass.FileWithBoolean;
import com.chd.yunpan.myclass.OtherFilePopMenuLoad;
import com.chd.yunpan.myclass.StorageUtils;
import com.chd.yunpan.myclass.StorageUtils.StorageInfo;
import com.chd.yunpan.parse.entity.FileDataFatherEntity;
import com.chd.yunpan.ui.adapter.UpLoadOtherAdapter;
import com.chd.yunpan.ui.fragment.popupwindow.PopMenu2;
import com.chd.yunpan.ui.myviews.FileDirLocationBar3;
import com.chd.yunpan.ui.myviews.FileDirLocationBar3.OnFilePathChange;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UpLoadOtherFileFragment extends Fragment {

	private ListView listView = null;

	private List<FileWithBoolean> fileList = new ArrayList<FileWithBoolean>();

	private FileDirLocationBar3 fdlb = null;

	private HorizontalScrollView hor = null;

	private ViewGroup vg = null;

	private UpLoadOtherAdapter adapter = null;

	private PopMenu2 menu;

	private boolean isRoot = true;

	private boolean flag = false;

	private OtherFilePopMenuLoad load = null;

	private View v = null;

	// 0:正常状态 1:选择状态
	private int state = 0;

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				fileList = (List<FileWithBoolean>) msg.obj;
				for (int i = 0; i < fileList.size(); i++) {
				}
				adapter.notifyDataSetChanged();
				break;
			case 2:
				fileList = (List<FileWithBoolean>) msg.obj;
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}

		}
	};

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	List<StorageInfo> infos;

	@Override
	public View onCreateView(android.view.LayoutInflater inflater,
			android.view.ViewGroup container,
			android.os.Bundle savedInstanceState) {
		// contentView
		v = inflater.inflate(R.layout.lz_upload_other_fragment, container, false);

		infos = StorageUtils.getStorageList();
		initView(v);

		return v;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	private void initView(View v) {

		listView = (ListView) v.findViewById(R.id.lzUpLoadOtherListView);

		hor = (HorizontalScrollView) v.findViewById(R.id.lzUpLoadOtherHor);

		vg = (ViewGroup) v.findViewById(R.id.lzUpLoadOtherFrame);

		fdlb = new FileDirLocationBar3(getActivity(), vg, hor, "全部文件");
		fdlb.setOnFilePathChange(new OnFilePathChange() {

			@Override
			public void filePathChange(File currentPath) {
				changeDiraction(currentPath);
				if (load.havePop()) {
					for (int i = 0; i < fileList.size(); i++) {
						fileList.get(i).setCheck(false);
					}
					adapter.notifyDataSetChanged();
					load.check(fileList);
					checkFileCheck();
					load.dis();

				}
			}
		});

		adapter = new UpLoadOtherAdapter(this, fileList);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FileWithBoolean current = fileList.get(position);
				if (state == 1) {
					current.setCheck(!current.isCheck());
					adapter.notifyDataSetChanged();
					checkFileCheck();
					return;
				}

				if (!current.getFile().isDirectory()) {
					current.setCheck(!current.isCheck());
					adapter.notifyDataSetChanged();
					checkFileCheck();
					return;
				}
				fdlb.changeCurrentPath(current.getFile());
				changeDiraction(current.getFile());
			}
		});
		// changeDiraction(Environment.getExternalStorageDirectory());
		changeRoot();
		load = new OtherFilePopMenuLoad(getActivity(), fileList, this, v);
		load.sethandler(handler);
	}

	public void checkFileCheck() {
		int count = 0;
		load.check(fileList);
		for (FileWithBoolean fwb : fileList) {
			if (fwb.isCheck()) {
				count++;

			}

		}
		if (count > 0) {
			// 选择状态
			state = 1;
			load.showPopupWindows(v);

		} else {
			state = 0;
			System.out.println(getActivity().getCurrentFocus());
		}
	}

	public List<FileDataFatherEntity> files = null;

	private void changeDiraction(File file) {

		File[] files = null;

		FileFilter filter = new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return !"lost+found".equalsIgnoreCase(pathname.getName());
			}
		};
		if (file == null || !file.exists()) {
			changeRoot();
			return;
		}

		if (file != null) {
			files = file.listFiles(filter);
		}

		fileList.clear();
		if (files != null && files.length > 0) {
			for (File f : files) {
				fileList.add(new FileWithBoolean(f, false));
			}
		}
		Collections.sort(fileList, new Comparator<FileWithBoolean>() {
			public int compare(FileWithBoolean o1, FileWithBoolean o2) {
				if (o1.getFile().isDirectory() && o2.getFile().isFile())
					return -1;
				if (o1.getFile().isFile() && o2.getFile().isDirectory())
					return 1;
				return o1.getFile().getName().compareTo(o2.getFile().getName());
			}
		});

		isRoot = fdlb.isRootDir;
		adapter.notifyDataSetChanged();
	}

	private void changeRoot() {

		fileList.clear();
		for (int i = 0; i < infos.size(); i++) {
			fileList.add(new FileWithBoolean(new File(infos.get(i).path), false));
		}
		isRoot = fdlb.isRootDir;
		adapter.notifyDataSetChanged();
	}

	// private Handler handler = new Handler() {
	// public void handleMessage(android.os.Message msg) {
	//
	// };
	// };

	public int onKeyDown(KeyEvent event, int keyCode) {
		if (flag) {
			for (int i = 0; i < fileList.size(); i++) {
				fileList.get(i).setCheck(false);
				adapter.notifyDataSetChanged();
				load.check(fileList);
			}
			return 1;
		} else {
			if (!isRoot) {
				// 返回上一级文件夹
				changeDiraction(fdlb.backOneClass());
				return 1;
			}
			return 0;
		}

		// 返回1 处理

	}

	// {
	// listView = (ListView) v.findViewById(R.id.listView);
	//
	// sdCardUtils = new SdCardUtils();
	//
	// if (Environment.getExternalStorageState().equals(
	// Environment.MEDIA_MOUNTED)) {
	// file = Environment.getExternalStorageDirectory();
	//
	// lists = sdCardUtils.getDirMessage(file.getParentFile()
	// .getParentFile());
	// menu = new PopMenu2(activity, lists, getActivity(), null);
	// adapter = new DistListViewAdapter2(activity, lists, menu);
	// listView.setAdapter(adapter);
	// menu.setAdapter(adapter);
	// listView.setOnItemClickListener(new OnItemClickListener() {
	// public void onItemClick(AdapterView<?> arg0, View arg1,
	// int arg2, long arg3) {
	// WyfItem item = lists.get(arg2);
	// file = new File(item.getfPath());
	// if (item.getPyte() == 0) {
	// lists = sdCardUtils.getDirMessage(file);
	// adapter = new DistListViewAdapter2(activity, lists,
	// menu);
	// listView.setAdapter(adapter);
	// }
	// }
	// });
	// } else {
	//
	// }
	// }



}
