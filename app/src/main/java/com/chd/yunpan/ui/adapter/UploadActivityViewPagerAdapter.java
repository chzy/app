package com.chd.yunpan.ui.adapter;

import com.chd.yunpan.ui.fragment.PhotoUploadingFragment;
import com.chd.yunpan.ui.fragment.UpLoadOtherFileFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class UploadActivityViewPagerAdapter extends FragmentPagerAdapter {

	private Fragment[] fragments = null;

	public UploadActivityViewPagerAdapter(FragmentManager fm) {
		super(fm);
		fragments = new Fragment[2];
		fragments[0] = new PhotoUploadingFragment();
		fragments[1] = new UpLoadOtherFileFragment();
	

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
