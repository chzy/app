package com.chd.yunpan.ui.fragment;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chd.yunpan.R;
import com.chd.yunpan.myclass.Album;
import com.chd.yunpan.parse.entity.PhotoFileEntity;
import com.chd.yunpan.ui.adapter.PhotoListAdapter;
import com.chd.yunpan.ui.adapter.PhotoLoadGridAdapter;
import com.chd.yunpan.ui.adapter.PhotoPopMenuLoad;
import com.chd.yunpan.ui.myviews.FileDirLocationBar3;
import com.chd.yunpan.ui.myviews.FileDirLocationBar3.OnFilePathChange;
import com.chd.yunpan.utils.LocalImageFind;
import com.chd.yunpan.utils.MyComparetor2;
import com.chd.yunpan.utils.TimeUtils;


@SuppressLint("ValidFragment")
public class PhotoUploadingFragment extends Fragment {
	private ListView listView = null;
	private List<Album> albums = new ArrayList<Album>();
	private ScrollView scrollView = null;
	private List<PhotoFileEntity> fileEntities = null;
	private List<String> times = null;
	private Map<String, List<PhotoFileEntity>> maps = null;
	private LinearLayout layout = null;
	private PhotoLoadGridAdapter adapterGrid = null;
	private PhotoPopMenuLoad loadl = null;
	private FileDirLocationBar3 f = null;
	private List<PhotoLoadGridAdapter> adapters;
	private boolean flag = false;
	private View main;
	private List<PhotoFileEntity> tEntities=new ArrayList<PhotoFileEntity>();
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1000:
				albums = (List<Album>) msg.obj;
				PhotoListAdapter adapter = new PhotoListAdapter(albums,
						getActivity());
				listView.setAdapter(adapter);
				break;
			case 1:
				fileEntities = (List<PhotoFileEntity>) msg.obj;
				for (int i = 0; i < fileEntities.size(); i++) {
				}
				notifyChecked();
				break;
			case 2:
				fileEntities = (List<PhotoFileEntity>) msg.obj;
				notifyChecked();
				break;
			default:
				break;
			}

		}
	};

	private View v = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		adapters = new ArrayList<PhotoLoadGridAdapter>();
		v = inflater.inflate(R.layout.photo_load_main, null);
		main=v.findViewById(R.id.photo_load_mian_main);
		ViewGroup group = (ViewGroup) v
				.findViewById(R.id.photo_load_main_locationID);
		HorizontalScrollView horizontalScrollView = (HorizontalScrollView) v
				.findViewById(R.id.photo_load_main_hs);
		f = new FileDirLocationBar3(getActivity(), group, horizontalScrollView,
				"全部图片");
		f.setOnFilePathChange(new OnFilePathChange() {

			@Override
			public void filePathChange(File currentPath) {
			
				if (loadl.havePop()) {
					for (int i = 0; i < fileEntities.size(); i++) {
						fileEntities.get(i).setPitch(false);
					}
					loadl.check(fileEntities);
					notifyChecked();
					loadl.dis();
					
				}
				if ("全部图片".equals(currentPath.getName())) {
					scrollView.setVisibility(View.GONE);
					listView.setVisibility(View.VISIBLE);
					tEntities.clear();
					fileEntities.clear();
					times.clear();
					maps.clear();
					adapters.clear();
					//scrollView.removeAllViews();
					layout.removeAllViews();
					notifyChecked();
				}
			}

		});

		listView = (ListView) v.findViewById(R.id.photo_load_main_list);
		scrollView = (ScrollView) v
				.findViewById(R.id.Photo_load_main_scrollView1);
		layout = (LinearLayout) v
				.findViewById(R.id.Photo_load_main_linearLayout1);
		LocalImageFind find = new LocalImageFind(handler, getActivity());
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				listView.setVisibility(View.GONE);
				File[] files = getAllFiles(albums.get(position));
				fileEntities = new ArrayList<PhotoFileEntity>();
				List<File> list = new ArrayList<File>();
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					PhotoFileEntity entity = new PhotoFileEntity(file, false,
							false);
					fileEntities.add(entity);
					System.out.println(file.getName());
				}
				
				
				init();

			}
		});
		return v;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	@SuppressWarnings("unchecked")
	private void init() {

		// Item item = new Item();
		//
		// f.changeCurrentPath();s
		scrollView.setVisibility(View.VISIBLE);
		maps = new HashMap<String, List<PhotoFileEntity>>();
		times = new ArrayList<String>();
		loadl = new PhotoPopMenuLoad(getActivity(), fileEntities, this, v);
		loadl.sethandler(handler);
		Collections.sort(fileEntities, new MyComparetor2());
		for (int i = 0; i < fileEntities.size(); i++) {
			String time = TimeUtils.getFileDate(fileEntities.get(i).getFile());
			if (times == null || !times.contains(time)) {
				times.add(time);
			}
		}
		System.out.println(times.size()+"----");
		Collections.sort(times);
		Collections.reverse(times);
		for (int i = 0; i < times.size(); i++) {
			tEntities = new ArrayList<PhotoFileEntity>();
			String time = times.get(i);
			for (int j = 0; j < fileEntities.size(); j++) {
				String etime = TimeUtils.getFileDate(fileEntities.get(j)
						.getFile());
				System.out.println(etime);
				System.out.println(time);
				if (time.equals(etime)) {
					tEntities.add(fileEntities.get(j));
				}
			}
			View linearLayout = View.inflate(this.getActivity(),
					R.layout.photo_load_lin_item, null);
			TextView tv = (TextView) linearLayout
					.findViewById(R.id.photo_load__li_textView);
			GridView gv = (GridView) linearLayout
					.findViewById(R.id.photo_load__li_gridView);
			String cunTime = TimeUtils.getDate(System.currentTimeMillis());
			// 2010-05-12
			String cunYear = cunTime.substring(0, 4);
			String cunMonth = cunTime.substring(5, 7);
			String cunDay = cunTime.substring(8);
			String year = time.substring(0, 4);
			String month = time.substring(5, 7);
			String day = time.substring(8);
			if (cunTime.equals(time)) {
				tv.setText("今天");
			} else if (cunYear.equals(year)
					&& cunMonth.equals(month)
					&& ((Integer.valueOf(cunDay)) - (Integer.valueOf(day))) == 1) {
				tv.setText("昨天");
			} else if (cunYear.equals(year)) {
				tv.setText(month + "月" + day + "日");
			} else {
				tv.setText(time);
			}
			adapterGrid = new PhotoLoadGridAdapter(getActivity(), tEntities,
					this, loadl);
			adapters.add(adapterGrid);
			gv.setAdapter(adapterGrid);
			layout.addView(linearLayout);
			maps.put(time, tEntities);
		}
	}
	public void setMainPad(int s){
		main.setPadding(0, 0, 0, s);
	}
	
	public int onKeyDown(KeyEvent event, int keyCode) {
		if (flag) {
			for (int i = 0; i < fileEntities.size(); i++) {
				fileEntities.get(i).setPitch(false);
				notifyChecked();
			}
			return 1;
		} else {
			return 0;
		}
	}

	private File[] getAllFiles(Album album) {
		File file = new File(album.getFirstImagePath());
		File dir = file.getParentFile();
		f.changeCurrentPath(dir);
		File[] images = dir.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				if(pathname.isDirectory()){
					return false;
				}
				return pathname.getName().endsWith(".png")
						|| pathname.getName().endsWith(".jpg")
						|| pathname.getName().endsWith(".gif")
						|| pathname.getName().endsWith(".bmp")
						|| pathname.getName().endsWith(".jpeg");
			}
		});
		for (File i : images) {
			Log.v("wang", i.getAbsolutePath());
		}
		return images;
	}

	public List<PhotoFileEntity> getFileEntities() {
		return fileEntities;
	}

	public boolean isChecked() {
		for (int i = 0; i < times.size(); i++) {
			for (String time : maps.keySet()) {
				for (int j = 0; j < maps.get(times.get(i)).size(); j++) {
					if (maps.get(times.get(i)).get(j).isPitch()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void notifyChecked() {
		for (int i = 0; i < adapters.size(); i++) {
			adapters.get(i).notifyDataSetChanged();
		}
		loadl.check(fileEntities);
	}

	public void notifyChecked2(int position) {
		loadl.check(fileEntities);
	}

	
}
