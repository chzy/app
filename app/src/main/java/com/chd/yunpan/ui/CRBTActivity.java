package com.chd.yunpan.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.yunpan.R;

public class CRBTActivity extends Activity implements OnClickListener {
	private ImageView mIvLeft;
	private TextView mTvCenter;
	private WebView crbt_webview;
	private Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // Request progress circle
//		setProgressBarIndeterminateVisibility(true); // Show progress circle

		setContentView(R.layout.activity_crbt);
		initTitle();
		initListener();
		String url = "http://more.i8855.cn:8080/";
		crbt_webview.loadUrl(url);
		activity=this;
		crbt_webview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{ //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}
		});

		crbt_webview.setWebChromeClient(new WebChromeClient(){

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				activity.setTitle("Loading...");
				activity.setProgress(newProgress * 100);
				if(newProgress == 100)
					setProgressBarIndeterminateVisibility(false); // Hide progress circle when page loaded
				activity.setTitle("更多精彩");
			}
		});

		WebSettings webSettings = crbt_webview.getSettings();
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setJavaScriptEnabled(true);
		//自适应屏幕
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setDomStorageEnabled(true);

		webSettings.setAppCacheMaxSize(1024*1024*8);

		String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();

		webSettings.setAppCachePath(appCachePath);

		webSettings.setAllowFileAccess(true);

		webSettings.setAppCacheEnabled(true);


	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && crbt_webview.canGoBack()) {
			crbt_webview.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
		crbt_webview = (WebView) findViewById(R.id.crbt_webview);

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
