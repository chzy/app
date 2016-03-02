package com.chd.yunpan.ui;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.yunpan.R;
import com.chd.yunpan.ui.adapter.UploadActivityViewPagerAdapter;

public class UpLoadActivity extends FragmentActivity implements OnClickListener {

	private ImageView line1 = null;

	private ImageView line2 = null;

	private TextView title = null;

	private TextView head1 = null;

	private TextView head2 = null;

	private ViewPager vp = null;

	private View back = null;

	private UploadActivityViewPagerAdapter adapter = null;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.lz_activity_upload);
		this.overridePendingTransition(R.anim.activity_right_in,
				R.anim.activity_left_out);
		initAllView();
	}

	private void initAllView() {
		line1 = (ImageView) this.findViewById(R.id.upLoadAvtivity_line1);

		line2 = (ImageView) this.findViewById(R.id.upLoadAvtivity_line2);

		title = (TextView) this.findViewById(R.id.upLoadAvtivity_title);

		head1 = (TextView) this.findViewById(R.id.upLoadAvtivity_choose1);
		head1.setOnClickListener(this);

		head2 = (TextView) this.findViewById(R.id.upLoadAvtivity_choose2);
		head2.setOnClickListener(this);
//
		adapter = new UploadActivityViewPagerAdapter(
				getSupportFragmentManager());
		vp = (ViewPager) this.findViewById(R.id.upLoadAvtivity_viewPager);
		vp.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				changePage(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				//adapter.changeCurrentItem(arg0);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		vp.setAdapter(adapter);

		back = this.findViewById(R.id.upLoadAvtivity_back);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.upLoadAvtivity_choose1:
			vp.setCurrentItem(0);
			break;
		case R.id.upLoadAvtivity_choose2:
			vp.setCurrentItem(1);

			break;
		case R.id.upLoadAvtivity_back:
			back2Main();
			break;
		}

	}

	private void changePage(int page) {

		switch (page) {
		case 0:
			head1.setTextColor(Color.rgb(68, 134, 232));
			line1.setVisibility(View.VISIBLE);

			head2.setTextColor(Color.GRAY);
			line2.setVisibility(View.INVISIBLE);

			title.setText("全部图片");

			break;
		case 1:
			head2.setTextColor(Color.rgb(68, 134, 232));
			line2.setVisibility(View.VISIBLE);

			head1.setTextColor(Color.GRAY);
			line1.setVisibility(View.INVISIBLE);

			title.setText("其他文件");

			break;
		}

	}



	private void back2Main() {
		this.finish();
	}

	public View getBack() {
		return back;
	}

	public void setBack(View back) {
		this.back = back;
	}
	
	

}
