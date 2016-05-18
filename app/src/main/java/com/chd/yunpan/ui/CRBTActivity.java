package com.chd.yunpan.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.yunpan.R;

public class CRBTActivity extends Activity implements OnClickListener {
	private ImageView mIvLeft;
	private TextView mTvCenter;
//	private WebView crbt_webview;
	private Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // Request progress circle
//		setProgressBarIndeterminateVisibility(true); // Show progress circle

		setContentView(R.layout.activity_crbt);
		initTitle();
		initListener();
//		String url = "http://box.10155.com/";
//		crbt_webview.loadUrl(url);
//		activity=this;
//
//		crbt_webview.setWebChromeClient(new WebChromeClient(){
//			@Override
//			public void onProgressChanged(WebView view, int newProgress) {
//				activity.setTitle("Loading...");
//				activity.setProgress(newProgress * 100);
//				if(newProgress == 100)
//					setProgressBarIndeterminateVisibility(false); // Hide progress circle when page loaded
//				activity.setTitle("My title");
//			}
//		});
//
//		WebSettings webSettings = crbt_webview.getSettings();
//		webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
//		webSettings.setLoadWithOverviewMode(true);
//		webSettings.setLoadsImagesAutomatically(true);
//		webSettings.setJavaScriptEnabled(true);
//		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);


	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if ((keyCode == KeyEvent.KEYCODE_BACK) && crbt_webview.canGoBack()) {
//			crbt_webview.goBack();
//			return true;
//		}
		return super.onKeyDown(keyCode, event);
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
//		crbt_webview = (WebView) findViewById(R.id.crbt_webview);

		mTvCenter.setText("更多精彩");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_left: {
				onBackPressed();
			}
			break;
			default:
				break;
		}
	}



}
