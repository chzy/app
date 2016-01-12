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

import com.chd.yunpan.R;

public class FindPwdActivity extends Activity {

	private WebView web = null;

	private WebSettings ws = null;

	private View back = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		this.setContentView(R.layout.lz_find_password);

		back = this.findViewById(R.id.findPwdBack);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FindPwdActivity.this.finish();

			}
		});
		web = (WebView) this.findViewById(R.id.findPwdWebView);
		ws = web.getSettings();
		ws.setJavaScriptEnabled(true);
		ws.setDefaultTextEncodingName("utf-8");
		web.setWebChromeClient(new WebChromeClient());
		web.setWebViewClient(new WebViewClient());
		web.loadUrl("http://fyimail.vicp.net:1080/pwdapp.html");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (web.canGoBack()) {
				web.goBack();
			} else {
				this.finish();
			}
		}

		return false;
	}

}
