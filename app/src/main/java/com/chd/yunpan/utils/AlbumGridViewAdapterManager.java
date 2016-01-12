package com.chd.yunpan.utils;

import android.content.Context;
import android.os.Handler;

import com.chd.yunpan.myclass.Album;
import com.chd.yunpan.ui.adapter.AlbumGridAdapter;

import java.util.ArrayList;
import java.util.List;

public class AlbumGridViewAdapterManager {

	private AlbumGridAdapter adapter = null;

	private List<Album> path = new ArrayList<Album>();

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			System.out.println("AlbumGridViewAdapterManager-----Handler");
			path = (List<Album>) msg.obj;
			
			adapter.setAlbums(path);
			adapter.notifyDataSetChanged();
		}

	};
	
	

	public AlbumGridViewAdapterManager(Context context) {

		new LocalImageFind(handler, context);
		adapter = new AlbumGridAdapter(context, path);
	}

	public AlbumGridAdapter getAdapter() {
		return adapter;
	}
}
