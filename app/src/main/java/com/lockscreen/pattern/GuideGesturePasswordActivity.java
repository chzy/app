package com.lockscreen.pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;

public class GuideGesturePasswordActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gesturepassword_guide);
		findViewById(R.id.gesturepwd_guide_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UILApplication.getInstance().getLockPatternUtils().clearLock();
				Intent intent = new Intent(GuideGesturePasswordActivity.this,
						CreateGesturePasswordActivity.class);
				// 打开新的Activity
				startActivity(intent);
				finish();
			}
		});
	}

}
