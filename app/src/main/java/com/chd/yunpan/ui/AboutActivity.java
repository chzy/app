package com.chd.yunpan.ui;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.chd.yunpan.R;

public class AboutActivity extends Activity {

	private Button button = null;

	private View back = null;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.about_layout);
		this.overridePendingTransition(R.anim.activity_right_in,
				R.anim.activity_left_out);

		initView();
	}

	private void initView() {

		button = (Button) this.findViewById(R.id.aboutUserProtocol);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		back = this.findViewById(R.id.aboutBack);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AboutActivity.this.finish();
			}
		});
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		return super.onKeyDown(keyCode, event);
	}

}
