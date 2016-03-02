package com.chd.yunpan.ui.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.chd.yunpan.ui.fragment.BaseFragment;
import com.chd.yunpan.ui.fragment.FileListFragment;
import com.chd.yunpan.ui.fragment.MusicListFragment;
import com.chd.yunpan.ui.fragment.PhotoListFragment;
import com.chd.yunpan.ui.fragment.SettingFragment;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

	public BaseFragment[] fragments = null;
	
	private FragmentManager fm;

	public MainFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		fragments = new BaseFragment[6];
		fragments[1] = new FileListFragment();
		fragments[0] = new PhotoListFragment();
		fragments[5] = new SettingFragment();
		fragments[4] = new MusicListFragment();

		this.fm = fm;

	}

	@Override
	public android.support.v4.app.Fragment getItem(int arg0) {
		return fragments[arg0];
	}

	@Override
	public int getCount() {
		return fragments.length;
	}
	
	
}
